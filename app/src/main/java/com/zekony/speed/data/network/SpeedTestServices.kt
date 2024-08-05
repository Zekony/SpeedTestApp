package com.zekony.speed.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface SpeedTestServices {

    @GET("speedtest-servers-static.php")
    suspend fun getServersPublic():Response<ResponseBody>

    @GET("speedtest-config.php")
    suspend fun getProvider():Response<ResponseBody>
}