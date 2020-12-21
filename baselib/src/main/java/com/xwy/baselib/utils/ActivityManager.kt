package com.xwy.baselib.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.xwy.baselib.receiver.NetReceiver
import java.lang.Exception
import java.util.*
import kotlin.system.exitProcess

/**
 * Activity堆栈管理
 */
object ActivityManager {

    private val activityStack = Stack<Activity>()

    fun addActivity(activity: Activity) {
        activityStack.push(activity)
    }

    fun removeActivity(activity: Activity) {
        if (activityStack.contains(activity)) {
            activityStack.remove(activity)
        }
    }

    fun currentActivity(): Activity? {
        return try {
            activityStack.lastElement()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun finishActivity(activity: Activity) {
        if (activity.isFinishing.not()) {
            activity.finish()
            removeActivity(activity)
        }
    }

    fun finishActivity(clazz: Class<Activity>) {
        finishActivity(activityStack.first {
            it.javaClass == clazz
        })
    }

    //销毁所有Activity，执行正常onDestroy的流程
    fun finishAllActivity() {
        while (activityStack.size > 0) {
            finishActivity(currentActivity()!!)
        }
    }

    fun exitApp(activity: Activity?) {
        try {
            if (activity == null) return
            NetReceiver.instance.unRegister(activity)
            finishAllActivity()
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(activity.packageName)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun listActivity() = activityStack.toList()

    fun isEmpty() = activityStack.isEmpty()
}