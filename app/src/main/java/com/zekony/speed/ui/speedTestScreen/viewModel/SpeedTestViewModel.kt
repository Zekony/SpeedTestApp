package com.zekony.speed.ui.speedTestScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zekony.speed.data.datastore.SettingsHelper
import com.zekony.speed.data.network.ApiStatus
import com.zekony.speed.ooklaSpeedtest.Servers
import com.zekony.speed.ooklaSpeedtest.TestDownloader
import com.zekony.speed.ooklaSpeedtest.TestUploader
import com.zekony.speed.ooklaSpeedtest.models.ServersResponse
import com.zekony.speed.ooklaSpeedtest.models.TestType
import com.zekony.speed.ooklaSpeedtest.models.TestingStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SpeedTestViewModel(
    private val mSettings: SettingsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(SpeedTestState())
    val state = _state.asStateFlow()

    init {
        getTestsToRun()
        loadServers()
    }

    private fun getTestsToRun() = viewModelScope.launch(Dispatchers.IO) {
        mSettings.tests.collect { testsSet ->
            _state.update {
                it.copy(
                    testsToRun = testsSet
                )
            }
        }
    }

    private var mBuilderUpload: TestUploader? = null
    private var mBuilderDownload: TestDownloader? = null

    private val exp = CoroutineExceptionHandler { _, throwable ->
        val msg = throwable.message
        msg?.let { notNullMsg ->
            _state.update {
                it.copy(
                    downloadStatus = ApiStatus.Error(notNullMsg)
                )
            }
        }
    }


    // загружаем список провайдеров
    private fun loadServers() = viewModelScope.launch(Dispatchers.IO) {
        val serversBuilder = Servers.Builder()
            .build()
        serversBuilder.listServers(object : Servers.ServerStatusListener {
            override fun onLoading() {
                _state.update {
                    it.copy(
                        downloadStatus = ApiStatus.Loading
                    )
                }
            }

            override fun onSuccess(response: ServersResponse) {
                response.servers?.let { list ->
                    _state.update { state ->
                        state.copy(
                            serversList = list,
                            downloadStatus = ApiStatus.Success
                        )
                    }
                }
            }

            override fun onError(error: String) {
                _state.update {
                    it.copy(
                        downloadStatus = ApiStatus.Error(error)
                    )
                }
            }
        })
    }

    // по нажатию на кнопку начинаем тест скорости загрузки с помощью TestDownloader
    fun startDownloadTest() = viewModelScope.launch(Dispatchers.IO + exp) {
        if (state.value.testsToRun.contains(TestType.DownloadTest)) {
            _state.update {
                it.copy(
                    downloadSpeed = 0f
                )
            }
            var mUrl = state.value.selectedSTServer?.url ?: ""
            mUrl = mUrl.replace(
                mUrl.split("/").toTypedArray()[mUrl.split("/")
                    .toTypedArray().size - 1],
                ""
            )

            mBuilderDownload =
                TestDownloader.Builder(mUrl)
                    .addListener(object : TestDownloader.TestDownloadListener {
                        override fun onStart() {
                            _state.update {
                                it.copy(
                                    testingStatus = TestingStatus.Testing(
                                        TestType.DownloadTest
                                    )
                                )
                            }
                        }

                        override fun onProgress(progress: Double, elapsedTimeMillis: Double) {
                            _state.update {
                                it.copy(
                                    downloadSpeed = progress.toFloat(),
                                    downloadSpeedList = state.value.downloadSpeedList.toMutableList()
                                        .apply {
                                            add(progress.toFloat())
                                        }
                                )
                            }
                        }

                        override fun onFinished(
                            finalprogress: Double,
                            datausedinkb: Int,
                            elapsedTimeMillis: Double
                        ) {
                            if (state.value.testsToRun.contains(TestType.UploadTest)) startUploadTest() else {
                                _state.update {
                                    it.copy(
                                        testingStatus = TestingStatus.Finished
                                    )
                                }
                            }
                        }

                        override fun onError(msg: String) {
                            _state.update {
                                it.copy(
                                    testingStatus = TestingStatus.Error(msg)
                                )
                            }
                        }

                    })
                    .setTimeOUt(10)
                    .setThreadsCount(SettingsHelper.CONNECTION_TYPE_MULTIPLE)
                    .build()
            mBuilderDownload?.start()
        } else if (state.value.testsToRun.contains(TestType.UploadTest)) startUploadTest()
    }

    // по нажатию на кнопку начинаем тест скорости отдачи с помощью TestUploader
    fun startUploadTest() = viewModelScope.launch(Dispatchers.IO + exp) {
        _state.update {
            it.copy(
                testingStatus = TestingStatus.Testing(testtype = TestType.UploadTest)
            )
        }
        delay(1500)
        val fullurl = (state.value.selectedSTServer?.url ?: "")

        mBuilderUpload = TestUploader.Builder(fullurl)
            .addListener(object : TestUploader.TestUploadListener {
                override fun onStart() {
                    _state.update {
                        it.copy(
                            testingStatus = TestingStatus.Testing(TestType.UploadTest)
                        )
                    }
                }

                override fun onProgress(progress: Double, elapsedTimeMillis: Double) {
                    _state.update {
                        it.copy(
                            uploadSpeed = progress.toFloat(),
                            uploadSpeedList = state.value.uploadSpeedList.toMutableList()
                                .apply {
                                    add(progress.toFloat())
                                }
                        )
                    }
                }

                override fun onFinished(
                    finalprogress: Double,
                    datausedinkb: Int,
                    elapsedTimeMillis: Double
                ) {
                    _state.update {
                        it.copy(
                            testingStatus = TestingStatus.Finished
                        )
                    }
                }

                override fun onError(msg: String) {
                    _state.update {
                        it.copy(
                            testingStatus = TestingStatus.Error(msg)
                        )
                    }
                }

            })
            .setTimeOUt(10)
            .setThreadsCount(SettingsHelper.CONNECTION_TYPE_MULTIPLE)
            .build()
        mBuilderUpload?.start()
    }

    // по желанию проверяем скорость по ссылке другого сервера
    fun chooseServer(serverUrl: String) {
        _state.update {
            it.copy(
                selectedSTServer = state.value.serversList.find { it.url == serverUrl }
            )
        }
    }

    fun restart() {
        _state.update {
            SpeedTestState().copy(
                testsToRun = it.testsToRun
            )
        }
        loadServers()
    }

    private fun stopTesting() {
        _state.update {
            it.copy(
                testingStatus = TestingStatus.Finished
            )
        }
        mBuilderUpload?.stop()
        mBuilderUpload?.removeListener()

        mBuilderDownload?.stop()
        mBuilderDownload?.removeListener()
    }

    override fun onCleared() {
        super.onCleared()
        stopTesting()
    }
}