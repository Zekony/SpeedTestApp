package com.zekony.speed.ooklaSpeedtest

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL

class TestDownloader private constructor(builder: Builder){
    companion object {
        private var mDownloadedByte: Int = 0
        private const val THREADS_COUNT_DEFAULT = 4
        private const val TIME_OUT_DEFAULT = 10
        private var mShouldStop = false
        private var mIsTestRunning = false
        var THREAD_SINGLE = 1
        var THREAD_MULTIPLE = 2
    }

    private var mStartTime: Long = 0
    private var mEndTime: Long = 0
    private var mDownloadElapsedTime: Double = 0.0
    private var mFinalDownloadRate: Double = 0.0
    private var mInstantDownloadRate: Double = 0.0
    private var mTimeout = TIME_OUT_DEFAULT
    private  var mUrl:String
    private var mListener: TestDownloadListener? = null
    private var mThreadCount = THREADS_COUNT_DEFAULT

    class Builder(val url:String) {
        private var mTimeOut = TIME_OUT_DEFAULT
        private var mListener: TestDownloadListener? = null
        private var mThreadCount = THREADS_COUNT_DEFAULT

        fun setTimeOUt(timeout:Int) = apply { this.mTimeOut = timeout }
        fun getTimeout():Int = this.mTimeOut

        fun addListener(listener: TestDownloadListener) = apply {
            mListener = listener
        }
        fun getListener(): TestDownloadListener? = this.mListener

        fun setThreadsCount(count:Int) = apply {
            mThreadCount = when(count){
                THREAD_SINGLE -> 1
                THREAD_MULTIPLE -> THREADS_COUNT_DEFAULT
                else -> THREADS_COUNT_DEFAULT
            }
        }
        fun getThreadsCount() = mThreadCount
        fun build(): TestDownloader = TestDownloader(this)
    }

    init {
        this.mTimeout = builder.getTimeout()
        this.mListener = builder.getListener()
        this.mUrl = builder.url
        this.mThreadCount = builder.getThreadsCount()
    }

    // запускаем несколько функций task() которые будут загружать данные и мы будем считать вес данных и высчитывать скорость загрузки
    fun start() {
        mShouldStop = false
       if(!mIsTestRunning){
           val exp = CoroutineExceptionHandler { coroutineContext, throwable ->
               stop()
               throwable.message?.let {
                   mListener?.onError(it)
               }
           }
           CoroutineScope(Dispatchers.IO + exp).launch {
               mListener?.onStart()
               mDownloadedByte = 0
               mDownloadElapsedTime = 0.0

               mStartTime = System.currentTimeMillis()
               for (i in 1..mThreadCount) {
                   task()
               }
               while(true){
                   delay(100)
                   mIsTestRunning = true
                   mEndTime = System.currentTimeMillis()
                   mDownloadElapsedTime = ((mEndTime.minus(mStartTime)).div(1000.0))
                   mListener?.onProgress(mInstantDownloadRate, mDownloadElapsedTime)
                   if(mDownloadElapsedTime >= mTimeout || mShouldStop){
                       mIsTestRunning = false
                       break
                   }
               }

               mEndTime = System.currentTimeMillis()
               mDownloadElapsedTime = ((mEndTime.minus(mStartTime)).div(1000.0))
               mFinalDownloadRate =
                   roundNow(((mDownloadedByte * 8) / (1000 * 1000.0)) / mDownloadElapsedTime)
               mListener?.onFinished(mFinalDownloadRate,
                   mDownloadedByte / 1000,
                   mDownloadElapsedTime)
               stop()
               mIsTestRunning = false
           }
       }
    }

    // налаживаем связь с выбранной ссылкой и загружаем по ней файлы, подсчитывая сколько мы скачали, по истечении времени останавливаем загрузку
    private fun task() {
        val exp = CoroutineExceptionHandler { coroutineContext, throwable ->
            stop()
            throwable.message?.let {
                mListener?.onError(it)
            }
        }
        CoroutineScope(Dispatchers.IO + exp).launch {
            var mHttpConn: HttpURLConnection? = null
            val starttime = System.currentTimeMillis()
            var mUrl: URL
            var responseCode: Int
            val fileUrls = listOf(
                "${getUrl()}random4000x4000.jpg",
                "${getUrl()}random4000x4000.jpg",
                "${getUrl()}random4000x4000.jpg",
                "${getUrl()}random4000x4000.jpg",
                "${getUrl()}random3000x3000.jpg",
                "${getUrl()}random2000x2000.jpg",
                "${getUrl()}random1000x1000.jpg"
            )
            var mInputStream: InputStream? = null

            outerloop@ for (link in fileUrls) {
                mUrl = URL(link)
                mHttpConn = mUrl.openConnection() as HttpURLConnection
                mHttpConn.connect()
                responseCode = mHttpConn.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val buffer = ByteArray(10240)
                    mInputStream = mHttpConn.inputStream
                    var len = 0
                    while (true) {
                        if (mInputStream != null) {
                            len = mInputStream.read(buffer)
                        }

                        mDownloadedByte += len
                        val endtime = System.currentTimeMillis()
                        val elapsedtime = ((endtime.minus(starttime)).div(1000.0))
                        setInstantDownloadRate(mDownloadedByte, elapsedtime)

                        if (mDownloadElapsedTime > mTimeout || mShouldStop) {
                            break@outerloop
                        }
                    }
                } else {
                    stop()
                    mHttpConn.responseMessage?.let {
                        mListener?.onError(it)
                    }
                }
            }
            mInputStream?.close()
            mHttpConn?.disconnect()
        }
    }

    private fun getUrl():String = this.mUrl

    private fun setInstantDownloadRate(downloadedbyte: Int, elapsedtime: Double) {
        if (downloadedbyte >= 0) {
            mInstantDownloadRate =
                roundNow((((downloadedbyte * 8) / (1000 * 1000)).toDouble() / elapsedtime))
        } else {
            mInstantDownloadRate = 0.0
        }
    }


    fun removeListener() {
        mListener = null
    }

    fun stop() {
        mShouldStop = true
    }

    private fun roundNow(value: Double): Double {
        var bd: BigDecimal = try {
            BigDecimal(value)
        } catch (ex: Exception) {
            return 0.0
        }
        bd = bd.setScale(2, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    interface TestDownloadListener {
        fun onStart()
        fun onProgress(progress: Double, elapsedTimeMillis: Double)
        fun onFinished(finalprogress: Double, datausedinkb: Int, elapsedTimeMillis: Double)
        fun onError(msg: String)
    }
}