package com.xwy.baselib.base

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.xwy.baselib.bean.BaseConfig
import com.xwy.baselib.ext.hideNavigation
import com.xwy.baselib.ext.loge
import com.xwy.baselib.ext.openWifi
import com.xwy.baselib.receiver.NetReceiver
import com.xwy.baselib.utils.CleanLeakUtils
import com.xwy.baselib.utils.CountDownHelper
import com.xwy.baselib.utils.EditTextHelper
import org.greenrobot.eventbus.EventBus

/**
 * 所有基类的顶级父类
 */
abstract class BaseRootActivity : AppCompatActivity(),
    NetReceiver.NetworkCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EditTextHelper(this)
        NetReceiver.instance.register(this, this)
//        hideNavigation()
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        beforeSetContentView()
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
//        hideNavigation()
    }

    open fun beforeSetContentView() {

    }

    abstract fun initView()

    //隐藏虚拟导航栏
    private fun hideNavigation() {
        window.decorView.hideNavigation()
    }

    open fun initData(savedInstanceState: Bundle?) {

    }

    override fun onBackPressed() {
    }

    /**
     * 是否要使用EventBus
     */
    open fun useEventBus(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        CleanLeakUtils.fixInputMethodManagerLeak(this)
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    open fun startActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }

    open fun startActivityWithData(clazz: Class<*>, bundle: Bundle) {
        startActivity(Intent(this, clazz).apply {
            putExtras(bundle)
        })
    }

//    private var loadingDialog: LoadingDialog? = null
//
//    open fun loading(loading: Boolean) {
//        if (loadingDialog == null) {
//            loadingDialog = LoadingDialog().apply {
//                setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_full_screen)
//            }
//        }
//        if (loading) {
//            if (loadingDialog?.isAdded == true) {
//                return
//            }
//            loadingDialog?.show(supportFragmentManager, "loading")
//        } else {
////            dismissLoading()
//        }
//    }
//
//    fun dismissLoading() {
//        loadingDialog?.let {
//            if (it.dialog?.isShowing == true) {
//                it.dismiss()
//            }
//        }
//        loadingDialog = null
//    }


    override fun onNetChanged(hasNet: Boolean) {
        if (hasNet) {
            loge("网络已连接")
        } else {
            loge("网络已断开")
            openWifi(this)
        }
    }

    //在该页面是否停止计时
    var blockCount: Boolean = false
        set(value) {
            CountDownHelper.getInstance().blockCount = value
            field = value
        }

    //抬起时开始计时，其他停止计时
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (blockCount) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev?.actionMasked) {
            MotionEvent.ACTION_UP -> {
                CountDownHelper.getInstance().startCountDown()
            }
            else -> {
                CountDownHelper.getInstance().releaseTimeDisposable()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}