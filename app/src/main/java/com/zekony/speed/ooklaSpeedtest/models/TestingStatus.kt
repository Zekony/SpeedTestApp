package com.zekony.speed.ooklaSpeedtest.models

sealed class TestingStatus{
    object Idle: TestingStatus()
    data class Testing(val testtype:TestType): TestingStatus()
    data class Error(val error:String): TestingStatus()
    object Finished: TestingStatus()
}

enum class TestType {
    DownloadTest, UploadTest
}
