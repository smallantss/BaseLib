package com.xwy.baselib.bean

open class BaseConfig internal constructor(builder: Builder) {

    constructor() : this(Builder())

    val debug: Boolean = builder.debug
    val countTime = builder.countTime
    val net: Boolean = builder.net

    class Builder constructor() {
        internal var debug: Boolean = false
        internal var countTime = 60
        internal var net: Boolean = true

        internal constructor(config: BaseConfig) : this() {
            this.debug = config.debug
            this.countTime = config.countTime
            this.net = config.net
        }


        //是否开启日志打印
        fun enableDebug(debug: Boolean) = apply {
            this.debug = debug
        }

        //无操作返回时长
        fun countTime(time: Int) = apply {
            this.countTime = time
        }

        //是否开启网络监听
        fun enableNetListener(net: Boolean) = apply {
            this.net = net
        }

        fun build() = BaseConfig(this)

    }

}