package com.xwy.baselib.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import java.lang.reflect.ParameterizedType
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SpDelegate<T>(private val name: String, private val default: T) : ReadWriteProperty<Any?, T> {

    companion object {

//        lateinit var sharedPreferences: SharedPreferences

        fun setContext(context: Context, name: String = context.packageName) {
//            sharedPreferences =
//                context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)

            MMKV.initialize(context.applicationContext)
        }

        fun clear() {
//            sharedPreferences.edit().clear().apply()
            MMKV.defaultMMKV().clearAll()
        }

        fun clear(key: String) {
//            sharedPreferences.edit().remove(key).apply()
            MMKV.defaultMMKV().removeValueForKey(key)
        }
    }

    //    private val sp = sharedPreferences
    private val gson = Gson()

    private fun getSp(name: String, default: T): T = with(MMKV.defaultMMKV()) {
        when (default) {
            is Int -> getInt(name, default)
            is Float -> getFloat(name, default)
            is Long -> getLong(name, default)
            is Boolean -> getBoolean(name, default)
            is String -> {
                getString(name, default)
            }
            else -> {
                val type = javaClass.genericSuperclass
                val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<Parcelable>
                decodeParcelable(name, clazz)
            }
        } as T
    }

    private fun putSp(name: String, default: T) {
        with(MMKV.defaultMMKV()) {
            when (default) {
                is Int -> encode(name, default)
                is Float -> encode(name, default)
                is Long -> encode(name, default)
                is Boolean -> encode(name, default)
                is String -> encode(name, default)
                else -> throw IllegalArgumentException("SharedPreference can't be get this type")
            }
        }
    }


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSp(name, value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getSp(name, default)


//    fun firstLaunch(): Boolean {
//        val first = getBoolean(FIRST_LAUNCH)
//        putBoolean(FIRST_LAUNCH, false)
//        return first
//    }

//    fun putString(key: String, value: String) {
//        editor.putString(key, value).apply()
//    }
//
//    fun getString(key: String) = sp.getString(key, "")
//
//    fun putBoolean(key: String, b: Boolean) {
//        editor.putBoolean(key, b).apply()
//    }
//
//    fun getBoolean(key: String, default: Boolean = true) = sp.getBoolean(key, default)
//
//    fun putInt(key: String, b: Int) {
//        editor.putInt(key, b).apply()
//    }
//
//    fun getInt(key: String, default: Int = -1) = sp.getInt(key, default)

//    //put必须要手动调用apply，用于多个数据保存
//    fun put(key: String, value: Any): SpUtils {
//        when (value) {
//            is Int -> editor.putInt(key, value)
//            is String -> editor.putString(key, value)
//            is Boolean -> editor.putBoolean(key, value)
//            is Float -> editor.putFloat(key, value)
//            is Long -> editor.putLong(key, value)
//        }
//        return this
//    }

//    //保存Json字符串
//    fun putJson(key: String, jsonString: String) {
//        putString(key, jsonString)
//    }
//
//    //保存Bean对象
//    fun putBean(key: String, bean: Any) {
//        val jsonString = gson.toJson(bean)
//        putJson(key, jsonString)
//    }
//
//    //获取Bean对象
//    fun <T> getBean(key: String): T {
//        val json = getString(key)
//        val type = object : TypeToken<T>() {}.type
//        return gson.fromJson(json, type)
//    }
//
//    fun apply() {
//        editor.apply()
//    }
//
//    fun clear() {
//        editor.clear().apply()
//    }

}

interface IDataStore<T> {
    fun initialize(context: Context)

    fun clear()

    fun clear(name: String)

    fun put(name: String, value: T)

    fun get(name: String): T?
}

class SpData<T>(val name: String) : IDataStore<T> {

    lateinit var sp: SharedPreferences

    override fun initialize(context: Context) {
        sp = context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun clear() {
        sp.edit().clear().apply()
    }

    override fun clear(name: String) {
        sp.edit().remove(name).apply()
    }

    override fun put(name: String, value: T) {
        putData(name, value)
    }

    override fun get(name: String): T? {
        return null
    }

    private fun putData(key: String, value: T) {
        with(sp.edit()) {
            when (value) {
                is Int -> putInt(key, value)
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("SharedPreference can't be get this type")
            }
        }.apply()
    }
}
