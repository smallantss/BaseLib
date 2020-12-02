package com.xwy.baselib.base

import android.os.Bundle
import androidx.lifecycle.*
import com.xwy.baselib.ext.loge
import kotlinx.coroutines.*

/**
 * 封装Repo
 */
open class BaseViewModel : ViewModel(), BaseLifecycleObserver {

    val startActivityEvent = MutableLiveData<Class<*>>()
    val startActivityPair = MutableLiveData<Pair<Class<*>, Bundle>>()
    val toastEvent = MutableLiveData<String>()
    val loadingEvent = MutableLiveData<Boolean>()
    val finishEvent = MutableLiveData<Boolean>()
    val hideInput = MutableLiveData<Boolean>()

    private var mErrorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        loge("launchUI exception:${throwable.message}")
        onError(throwable.message)
    }

    open fun onError(msg:String?) {
    }

    fun jumpActivity(clazz: Class<*>) {
        startActivityEvent.value = clazz
    }

    fun jumpActivity(clazz: Class<*>, bundle: Bundle) {
        val pair = Pair(clazz, bundle)
        startActivityPair.value = pair
    }

    fun showToast(msg: String) {
        toastEvent.value = msg
    }

    fun showLoading(loading: Boolean) {
        loadingEvent.value = loading
    }

    fun finishCurrent() {
        finishEvent.value = true
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(mErrorHandler) {
            block()
        }

    fun <R> multiRequest2(block: suspend CoroutineScope.() -> R): R? {
        var result: R? = null
        launchUI {
            result = coroutineScope {
                block()
            }
        }
        return result
    }

    /***************再优化*****************/

//    fun <T> apiCall(
//        call: suspend () -> BaseResponse<T>,
//        onSuccess: (t: T) -> Unit
//    ) {
//        apiCall(call, onSuccess, ::onError)
//    }
//
//    fun async(call: () -> Unit) {
//        viewModelScope.launch(mErrorHandler) {
//            withContext(Dispatchers.IO) {
//                call.invoke()
//            }
//        }
//    }
//
//    suspend fun async(call: () -> Unit, error: (String?) -> Unit) {
//        try {
//            withContext(Dispatchers.IO) {
//                call.invoke()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            error(e.message)
//        }
//    }
//
//    fun <T> apiCall(
//        call: suspend () -> BaseResponse<T>,
//        onSuccess: (t: T) -> Unit,
//        onError: (e: ErrorBean) -> Unit
//    ) {
//        try {
//            viewModelScope.launch(mErrorHandler) {
//                val result = withContext(Dispatchers.IO) {
//                    call.invoke()
//                }
//                if (result.status == CODE_SUCCESS) {
//                    onSuccess.invoke(result.result)
//                } else {
//                    onError.invoke(ErrorFactory.getErrorBean(result))
//                }
//            }
//        } catch (e: Exception) {
//            onError.invoke(ErrorFactory.getErrorBean(e))
//        }
//    }
//
//    fun apiCall(call: suspend () -> Unit) {
//        try {
//            viewModelScope.launch(mErrorHandler) {
//                withContext(Dispatchers.IO) {
//                    call.invoke()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            loge("apiCall:${e.message}")
//        }
//    }
}