package com.xwy.baselib.ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.ToastUtils
import com.xwy.baselib.utils.FileProvider7
import java.io.File
import java.lang.reflect.Method
import kotlin.math.min

/**
 * 获取颜色
 */
fun Context.getColorRes(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.dp2px(dp: Float): Int {
    return (resources.displayMetrics.density * dp + 0.5f).toInt()
}

fun toast(msg: CharSequence) {
    ToastUtils.showShort(msg)
}

var debug: Boolean = true

fun loge(msg: String) {
    if (debug) {
        Log.e("TAG", msg)
    }
}

fun logd(msg: String) {
    if (debug) {
        Log.d("TAG", msg)
    }
}

fun logw(msg: String) {
    if (debug) {
        Log.w("TAG", msg)
    }
}

fun logv(msg: String) {
    if (debug) {
        Log.v("TAG", msg)
    }
}

fun logi(msg: String) {
    if (debug) {
        Log.i("TAG", msg)
    }
}

fun logThread(tag: String) {
    loge(Thread.currentThread().name + ":" + tag)
}

fun View.hideNavigation() {
    var flags =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    var visibility = systemUiVisibility
    visibility = visibility or flags
    systemUiVisibility = visibility
}

/**
 * 禁用系统导航栏
 */
fun Context.hideSystemNavigation() {
    val intent = Intent()
    intent.action = "com.outform.hidebar"
    sendBroadcast(intent)
}

/**
 * 启用系统导航栏
 */
fun Context.showSystemNavigation() {
    val intent = Intent()
    intent.action = "com.outform.unhidebar"
    sendBroadcast(intent)
}

fun View.hideInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        this.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

@SuppressLint("PrivateApi")
fun Context.silenceInstall(apkPath: String): Boolean {
    logd("apk:$apkPath")
    val managerClass: Class<*> = packageManager.javaClass
    try {
        if (Build.VERSION.SDK_INT >= 21) {
            val pio = Class.forName("android.app.PackageInstallObserver")
            val constructor = pio.getDeclaredConstructor()
            constructor.isAccessible = true
            val installObserver = constructor.newInstance()
            val method = managerClass.getDeclaredMethod(
                "installPackage",
                Uri::class.java, pio,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            method.isAccessible = true
            method.invoke(
                packageManager, FileProvider7.getUriForFile(this, File(apkPath)),
                installObserver,
                2,
                null
            )
        } else {
            val method = managerClass.getDeclaredMethod(
                "installPackage",
                Uri::class.java,
                Class.forName("android.content.pm.IPackageInstallObserver"),
                Int::class.javaPrimitiveType,
                String::class.java
            )
            method.isAccessible = true
            method.invoke(
                packageManager,
                FileProvider7.getUriForFile(this, File(apkPath)),
                null,
                2,
                null
            )
        }
        loge("安装成功")
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        loge("安装出错:${e.message}")
    }
    return false
}

private var lastTime = 0L

fun doubleClick(confirm: () -> Unit, cancel: () -> Unit) {
    val curTime = System.currentTimeMillis()
    if (curTime - lastTime > 2000) {
        cancel()
    } else {
        confirm()
    }
    lastTime = curTime
}

/**
 * 秒转时：分
 */
fun secondFormat(second: Long, containHour: Boolean = true): String {
    val result = StringBuilder()
    val hour = second / 3600
    if (containHour) {
        val hs = if (hour < 10) {
            "0$hour:"
        } else {
            "$hour:"
        }
        result.append(hs)
    }
    val m = second % 3600
    val min = m / 60
    val sec = second % 60
    val ms = if (min < 10) {
        "0$min:"
    } else {
        "$min:"
    }
    val ss = if (sec < 10) {
        "0$sec"
    } else {
        "$sec"
    }
    return result.append(ms).append(ss).toString()
}


/**
 * 静音切换
 */
fun Context.toggleMicrophoneMute(change: Boolean = false): Boolean {
    val mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    mAudioManager.mode = AudioManager.MODE_NORMAL
    if (change) {
        mAudioManager.isMicrophoneMute = !mAudioManager.isMicrophoneMute
    }
    return mAudioManager.isMicrophoneMute
}

/**
 * 麦克风打开
 * true 麦克风静音  false 麦克风打开
 */
fun Context.microphoneOpen() {
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
        mode = AudioManager.MODE_NORMAL
        isMicrophoneMute = false
    }
}

fun Context.microPhoneState() =
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).isMicrophoneMute

fun Context.microphoneClose() {
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
        mode = AudioManager.MODE_NORMAL
        isMicrophoneMute = true
    }
}

/**
 * 扬声器切换
 */
fun Context.toggleSpeakerphone(): Boolean {
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
        mode = AudioManager.MODE_NORMAL
        isSpeakerphoneOn = !isSpeakerphoneOn
        return isSpeakerphoneOn
    }
}

/**
 * 打开扬声器
 */
fun Context.speakerOn() {
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
        mode = AudioManager.MODE_NORMAL
        isSpeakerphoneOn = true
    }
}

fun Context.closeSpeaker() {
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
        mode = AudioManager.MODE_IN_COMMUNICATION
        isSpeakerphoneOn = false
    }
}

fun Context.toggleSpeaker(change: Boolean = false): Boolean {
    val mAudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    try {
        if (change) {
            if (mAudioManager.isSpeakerphoneOn) {
                //关闭
                mAudioManager.isSpeakerphoneOn = false
                mAudioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                mAudioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.FX_KEY_CLICK
                )
            } else {
                //打开
                mAudioManager.isSpeakerphoneOn = true
                mAudioManager.mode = AudioManager.MODE_NORMAL
                mAudioManager.setStreamVolume(
                    AudioManager.STREAM_SYSTEM,
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
                    AudioManager.FX_KEY_CLICK
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return mAudioManager.isSpeakerphoneOn
}

/**
 * 尝试打开WIFI
 * 若未开启,直接打开;若已开启,尝试关闭后重新打开
 */
fun openWifi(context: Context) {
    try {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiState = wifiManager.wifiState
        if (wifiState != WifiManager.WIFI_STATE_ENABLED && wifiState != WifiManager.WIFI_STATE_ENABLING) {
            wifiManager.isWifiEnabled = true
            toast("尝试打开WIFI")
        } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            wifiManager.isWifiEnabled = false
            Handler().postDelayed({ openWifi(context) }, 2000)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

/**
 * 判断版本是否比参数大
 * 0代表相等，1代表version1大于version2，-1代表version1小于version2
 */
fun compareVersion(version1: String, version2: String): Int {
    if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version2)) { // 判断数据是否存在问题，如果存在直接返回
        return 0
    }
    if (version1 == version2) {
        return 0
    }
    val version1Array = version1.split("\\.".toRegex()).toTypedArray()
    val version2Array = version2.split("\\.".toRegex()).toTypedArray()
    var index = 0
    // 获取最小长度值
    val minLen = min(version1Array.size, version2Array.size)
    var diff = 0
    // 循环判断每位的大小
    while (index < minLen && version1Array[index].toInt() - version2Array[index].toInt()
            .also { diff = it } == 0
    ) {
        index++
    }
    return if (diff == 0) {
        // 如果位数不一致，比较多余位数
        for (i in index until version1Array.size) {
            if (version1Array[i].toInt() > 0) {
                return 1
            }
        }
        for (i in index until version2Array.size) {
            if (version2Array[i].toInt() > 0) {
                return -1
            }
        }
        0
    } else {
        if (diff > 0) 1 else -1
    }
}

fun Any?.isNull(nullMethod: () -> Unit, nonNullMethod: () -> Unit) {
    if (this == null) {
        nullMethod()
    } else {
        nonNullMethod()
    }
}

fun String.getHost() = run {
    if (contains(":")) {
        split(":")[0]
    } else {
        this
    }
}

fun String.getPort() = run {
    if (contains(":")) {
        split(":")[1]
    } else {
        "80"
    }
}

//关闭应用验证
fun Context.closeVerify() {
    try {
        val check = Settings.Secure.getInt(contentResolver, "package_verifier_enable", 1)
        if (check == 1) {
            Settings.Secure.putInt(contentResolver, "package_verifier_enable", 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getSerialNo() = try {
    PhoneUtils.getSerial()
} catch (e: Exception) {
    e.printStackTrace()
    ""
}

fun getSsid(context: Context): String? {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val wifiInfo = wifiManager!!.connectionInfo
    return wifiInfo.ssid
}

fun getCurrentChannel(context: Context): Int {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo // 当前wifi连接信息
    val scanResults =
        wifiManager.scanResults
    for (result in scanResults) {
        if (result.BSSID.equals(wifiInfo.bssid, ignoreCase = true)
            && result.SSID.equals(
                wifiInfo.ssid
                    .substring(1, wifiInfo.ssid.length - 1), ignoreCase = true
            )
        ) {
            return getChannelByFrequency(result.frequency)
        }
    }
    return -1
}

fun getChannelByFrequency(frequency: Int): Int {
    var channel = -1
    when (frequency) {
        2412 -> channel = 1
        2417 -> channel = 2
        2422 -> channel = 3
        2427 -> channel = 4
        2432 -> channel = 5
        2437 -> channel = 6
        2442 -> channel = 7
        2447 -> channel = 8
        2452 -> channel = 9
        2457 -> channel = 10
        2462 -> channel = 11
        2467 -> channel = 12
        2472 -> channel = 13
        2484 -> channel = 14
        5745 -> channel = 149
        5765 -> channel = 153
        5785 -> channel = 157
        5805 -> channel = 161
        5825 -> channel = 165
    }
    return channel
}

fun getRssi(context: Context): Int {
    val wifiManager = context.applicationContext
        .getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectionInfo = wifiManager.connectionInfo
    return connectionInfo.rssi
}

fun String?.buryName() = this?.let {
    when (it.length) {
        0, 1 -> it
        2 -> it.first().plus("*")
        else -> it.first().plus("*".repeat(it.length - 2))
            .plus(it.last())
    }
}

fun Context.sendBroadcastAction(msg: String) {
    sendBroadcast(Intent().apply {
        action = msg
    })
}