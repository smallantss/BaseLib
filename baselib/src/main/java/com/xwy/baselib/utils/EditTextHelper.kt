package com.xwy.baselib.utils

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * EditText的辅助类
 */
class EditTextHelper(private val owner: AppCompatActivity) : LifecycleObserver {

    var etList: MutableList<EditText>? = null
        set(value) {
            field = value
            value?.forEach {
                it.addTextChangedListener(watcher)
            }
        }

    private val watcher = TextInputWatcher({
        //变化的时候
        stop()
    }, {
        //停止输入
        start()
    })

    private val mEtList = ArrayList<EditText>()

    init {
        owner.lifecycle.addObserver(this)
        val root = (owner.window.decorView as FrameLayout)
        addEt(root)
        etList = mEtList
    }

    private fun addEt(root: View) {
        if (root is ViewGroup) {
            val iterator = root.children.iterator()
            while (iterator.hasNext()) {
                val i = iterator.next()
                addEt(i)
            }
        } else if (root is EditText) {
            mEtList.add(root)
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        start()
    }

    private fun start(){
        CountDownHelper.getInstance().startCountDown()
    }

    private fun stop(){
        CountDownHelper.getInstance().releaseTimeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        etList?.forEach {
            it.removeTextChangedListener(watcher)
        }
        owner.lifecycle.removeObserver(this)
    }

}