package com.zekony.speed.ooklaSpeedtest

import com.zekony.speed.ooklaSpeedtest.models.STProvider
import com.zekony.speed.ooklaSpeedtest.models.STServer
import com.zekony.speed.ooklaSpeedtest.models.ServersResponse
import com.zekony.speed.data.network.ApiStatus
import com.zekony.speed.data.network.SpeedTestServices
import com.zekony.speed.data.repository.SpeedTestRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Servers private constructor() {
    companion object {
        const val BASE_URL_SPEEDTEST = "https://www.speedtest.net/"
    }


    class Builder {
        fun build(): Servers = Servers()
    }



    fun listServers(listener: ServerStatusListener) {
        listener.onLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val okHttpClient = OkHttpClient.Builder()
                .build()
            val services = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL_SPEEDTEST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpeedTestServices::class.java)
            val repo = SpeedTestRepositoryImpl(services)

                    var provider: STProvider? = null
                    val differed = CoroutineScope(Dispatchers.IO).async {
                        repo.getProvider()
                            .catch {
                                provider = null
                            }
                            .collect { response ->
                                provider = if (response.isSuccessful) {
                                    val body = response.body()?.string()
                                    val doc = Jsoup.parse(body, Parser.xmlParser())
                                    val client = doc.select("client")
                                    STProvider(
                                        client.attr("isp"),
                                        client.attr("isp"),
                                        client.attr("lat"),
                                        client.attr("lon")
                                    )
                                } else {
                                    null
                                }
                            }
                        provider
                    }
                    provider = differed.await()
                    //End collecting provider

                    //Getting server list
                    repo.getServersPublic()
                        .catch { ex ->
                            listener.onError(ex.message.toString())
                        }
                        .collect { response ->
                            if (response.isSuccessful) {
                                val body =
                                    response.body()?.string() ?: throw Exception("body is null")
                                val doc = Jsoup.parse(body, Parser.xmlParser())
                                val client = doc.select("client")
                                val servers = doc.getElementsByTag("server")
                                if (servers.isNotEmpty()) {
                                    val list = getServers(servers, provider)
                                    val resp = ServersResponse(
                                        provider,
                                        list
                                    )
                                    listener.onSuccess(resp)
                                } else {
                                    listener.onError("No servers found")
                                }
                            } else {
                                listener.onError(response.message().toString())
                            }
                        }
                    //End Getting Server List
                }

            ApiStatus.Loading
        }


    private fun getServers(servers: Elements, stProvider: STProvider?): List<STServer> {
        val list = mutableListOf<STServer>()
        for (item in servers) {
            val server = item.select("server")
            var url = server.attr("url")
            if (!url.contains("8080")) {
                url = url.replace(":80", ":8080")
            }
            val stserver = STServer(
                url,
                server.attr("lat"),
                server.attr("lon"),
                server.attr("name"),
                server.attr("sponsor")
            )
            stProvider?.let {
               stserver.distance = 0
            }
            list.add(stserver)
        }
        return list
    }

    interface ServerStatusListener {
        fun onLoading()
        fun onSuccess(response: ServersResponse)
        fun onError(error: String)
    }
}