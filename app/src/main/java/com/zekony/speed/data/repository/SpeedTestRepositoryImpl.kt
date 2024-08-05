package com.zekony.speed.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import okhttp3.ResponseBody
import com.zekony.speed.data.network.SpeedTestServices
import com.zekony.speed.domain.SpeedTestRepository
import retrofit2.Response

class SpeedTestRepositoryImpl(
    private val appServices: SpeedTestServices
) : SpeedTestRepository {

    override suspend fun getServersPublic(): Flow<Response<ResponseBody>> = flow {
        emit(appServices.getServersPublic())
    }.flowOn(Dispatchers.IO)

    override suspend fun getProvider(): Flow<Response<ResponseBody>> = flow {
        emit(appServices.getProvider())
    }.flowOn(Dispatchers.IO)
}

