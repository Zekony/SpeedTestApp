package com.zekony.speed.ooklaSpeedtest

import kotlinx.coroutines.*
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL

class TestUploader constructor(builder: Builder) {
    companion object {
        var mUploadedBytes: Int = 0
        private const val THREADS_COUNT_DEFAULT = 4
        private val THREADS_COUNT_MAX = 10
        private val TIME_OUT_DEFAULT = 10
        private var mShouldStop = false
        var THREAD_SINGLE = 1
        var THREAD_MULTIPLE = 2
    }

    private var mStartTime: Long = 0
    private var mEndTime: Long = 0
    private var mUploadElapsedTime: Double = 0.0
    private var mInstantUploadRate: Double = 0.0
    private var mFinalUploadRate: Double = 0.0
    private var mTimeOut = TIME_OUT_DEFAULT
    private var mListener: TestUploadListener? = null
    private var mUrl: String
    private var mThreadCount = THREADS_COUNT_DEFAULT

    class Builder(val url: String) {
        private var mTimeOut = TIME_OUT_DEFAULT
        private var mListener: TestUploadListener? = null
        private var mThreadCount = THREADS_COUNT_DEFAULT

        fun setTimeOUt(timeout: Int) = apply { this.mTimeOut = timeout }
        fun getTimeout(): Int = this.mTimeOut

        fun addListener(listener: TestUploadListener) = apply {
            mListener = listener
        }

        fun getListener(): TestUploadListener? = this.mListener

        fun setThreadsCount(count: Int) = apply {
            mThreadCount = when (count) {
                THREAD_SINGLE -> 1
                THREAD_MULTIPLE -> THREADS_COUNT_DEFAULT
                else -> THREADS_COUNT_MAX
            }
        }

        fun getThreadsCount() = mThreadCount
        fun build(): TestUploader = TestUploader(this)
    }

    init {
        this.mTimeOut = builder.getTimeout()
        this.mListener = builder.getListener()
        this.mUrl = builder.url
        this.mThreadCount = builder.getThreadsCount()
    }

    // запускаем несколько функций task() которые будут загружать данные и мы будем считать вес данных и высчитывать скорость отдачи
    fun start() {
        mShouldStop = false
        val exp = CoroutineExceptionHandler { _, throwable ->
            stop()
            throwable.message?.let {
                mListener?.onError(it)
            }
        }
        CoroutineScope(Dispatchers.IO + exp).launch {
            mUploadedBytes = 0
            mStartTime = System.currentTimeMillis()
            mListener?.onStart()
            for (i in 1..mThreadCount) {
                task()
            }
            while (true) {
                delay(100)
                mEndTime = System.currentTimeMillis()
                mUploadElapsedTime = ((mEndTime.minus(mStartTime)).div(1000.0))
                mListener?.onProgress(getInstantUploadRate(), mUploadElapsedTime)
                if (mShouldStop || mUploadElapsedTime >= mTimeOut) {
                    break
                }
            }
            mEndTime = System.currentTimeMillis()
            mUploadElapsedTime = ((mEndTime.minus(mStartTime)).div(1000.0))
            mFinalUploadRate =
                roundNow((((mUploadedBytes / 1000.0) * 8) / mUploadElapsedTime), 2)
            mListener?.onFinished(
                mFinalUploadRate,
                mUploadedBytes / 1000,
                mUploadElapsedTime
            )
            stop()
        }
    }

    // налаживаем связь с выбранной ссылкой и загружаем по ней файлы, подсчитывая сколько мы отдали, по истечении времени останавливаем загрузку
    fun task() {
        val url = URL(getUrl())
        val exp = CoroutineExceptionHandler { coroutineContext, throwable ->
            stop()
            throwable.message?.let {
                mListener?.onError(it)
            }
        }
        CoroutineScope(Dispatchers.IO + exp).launch {
            delay(100)
            var mHttpConn: HttpURLConnection?

            val buffer = ByteArray(150 * 1024)

            var outputStream: OutputStream?

            while (true) {
                mHttpConn = url.openConnection() as HttpURLConnection
                mHttpConn.doOutput = true
                mHttpConn.requestMethod = "POST"
                mHttpConn.setRequestProperty("Connection", "Keep-Alive")
                mHttpConn.connect()
                outputStream = mHttpConn.outputStream
                outputStream?.write(buffer, 0, buffer.size)
                outputStream?.flush()
                val responsecode = mHttpConn.responseCode
                var elapsedtime = 0.0
                val endtime = System.currentTimeMillis()
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    mUploadedBytes += buffer.size / 1024
                    elapsedtime = ((endtime.minus(mStartTime)).div(1000.0))
                    setInstantUploadRate(mUploadedBytes, elapsedtime)
                } else {
                    stop()
                    mListener?.onError(mHttpConn.responseMessage.toString())
                }
                if (elapsedtime > mTimeOut || mShouldStop) {
                    break
                }
            }
            outputStream?.close()
            mHttpConn?.disconnect()
        }
    }

    private fun getUrl(): String = this.mUrl
    private fun getInstantUploadRate(): Double {
        return mInstantUploadRate
    }

    private fun setInstantUploadRate(uploadedbyte: Int, elapsedtime: Double) {
        if (uploadedbyte >= 0) {
            mInstantUploadRate = roundNow((((uploadedbyte / 1000.0) * 8) / elapsedtime), 2)
        } else {
            mInstantUploadRate = 0.0
        }
    }

    private fun roundNow(value: Double, places: Int): Double {
        require(places >= 0)
        var bd: BigDecimal = try {
            BigDecimal(value)
        } catch (ex: Exception) {
            return 0.0
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun removeListener() {
        mListener = null
    }

    fun stop() {
        mShouldStop = true
    }

    interface TestUploadListener {
        fun onStart()
        fun onProgress(progress: Double, elapsedTimeMillis: Double)
        fun onFinished(finalprogress: Double, datausedinkb: Int, elapsedTimeMillis: Double)
        fun onError(msg: String)
    }
}