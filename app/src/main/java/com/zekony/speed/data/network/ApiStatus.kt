package com.zekony.speed.data.network

sealed class ApiStatus{
    object Success: ApiStatus()
    object Loading : ApiStatus()
    data class Error(val error:String): ApiStatus()
}
