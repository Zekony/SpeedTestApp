package com.zekony.speed.domain

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response


interface SpeedTestRepository {

    suspend fun getServersPublic(): Flow<Response<ResponseBody>>

    suspend fun getProvider(): Flow<Response<ResponseBody>>
}