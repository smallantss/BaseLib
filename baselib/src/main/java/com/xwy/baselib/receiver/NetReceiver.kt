package com.xwy.baselib.receiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.NetworkUtils

/**
 * 监听反馈当前网络连接状态
 */
class NetReceiver private constructor() : BroadcastReceiver() {
    private var networkCallback: NetworkCallback? = null
    private var callback: ConnectivityManager.NetworkCallback? = null

    @Volatile
    private var registered = false
    fun register(activity: Activity, networkCallback: NetworkCallback) {
        if (registered) {
            return
        }
        this.networkCallback = networkCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Handler(Looper.getMainLooper()).post {
                        networkCallback.onNetChanged(true)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Handler(Looper.getMainLooper()).post {
                        networkCallback.onNetChanged(false)
                    }
                }
            }
            connManager.registerDefaultNetworkCallback(callback!!)
        } else {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            activity.registerReceiver(receiver, intentFilter)
        }
        registered = true
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            Handler(Looper.getMainLooper()).post {
                networkCallback?.onNetChanged(NetworkUtils.isConnected())
            }
        }
    }

    /**
     * 取消网络监听
     */
    fun unRegister(activity: Activity) {
        registered = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val connManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (callback == null) {
                return
            }
            connManager.unregisterNetworkCallback(callback!!)
            callback = null
        } else {
            activity.unregisterReceiver(receiver)
        }
    }

    interface NetworkCallback {
        /**
         * 网络是否可用
         */
        fun onNetChanged(hasNet: Boolean)
    }

    companion object {
        @Volatile
        private var receiver: NetReceiver? = null
        val instance: NetReceiver
            get() {
                if (receiver == null) {
                    synchronized(NetReceiver::class.java) {
                        if (receiver == null) {
                            receiver = NetReceiver()
                        }
                    }
                }
                return receiver!!
            }
    }
}