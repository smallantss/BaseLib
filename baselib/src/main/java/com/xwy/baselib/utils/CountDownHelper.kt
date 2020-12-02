package com.xwy.baselib.utils

import com.xwy.baselib.ext.logd
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class CountDownHelper private constructor() {

    companion object {
        @Volatile
        private var instance: CountDownHelper? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CountDownHelper().also {
                    instance = it
                }
            }
    }

    //停止计数，如果未设置完整参数，需要全部停止计数
    var blockCount = false

    //无操作,跳转到首页
    private fun withoutOperate() {
        logd("停止计时:$blockCount")
        if (blockCount) {
            return
        }
        operate()
    }

    private var operate: () -> Unit = {}

    fun withoutOperate(method: () -> Unit) {
        operate = method
    }

    //检测事件
    private var detectTime: Int? = null

    private var timeObservable: Disposable? = null

    //开始计时,每秒计时一次
    fun startCountDown(total: Int = 60) {
        releaseTimeDisposable()
        detectTime = total
        logd("开始计时:$total")
        timeObservable = Observable.intervalRange(0, total.toLong(), 0, 1, TimeUnit.SECONDS)
            .map {
                total - it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
            }
            .doOnComplete {
                withoutOperate()
            }
            .subscribe()
    }

    //释放
    fun releaseTimeDisposable() {
        logd("取消计时")
        if (timeObservable?.isDisposed == false) {
            timeObservable?.dispose()
        }
    }
}