package com.xwy.baselib.bean

//异常的数据类型
data class ErrorBean(
    var status: Int,
    var msg: String,
    var errorDesc: String
)
