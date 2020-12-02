package com.xwy.baselib.utils

import android.text.Editable
import android.text.TextWatcher

//输入的监听
class TextInputWatcher(val textChanged: (CharSequence?) -> Unit, val afterInput:()->Unit) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        afterInput()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textChanged(s)
    }


}