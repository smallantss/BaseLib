package com.xwy.baselib.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.xwy.baselib.R

/**
 * 加载框
 */
class LoadingDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        hideNavigation()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        hideNavigation()
    }

    //隐藏虚拟导航栏
    private fun hideNavigation() {
    }

    private var mLoadingText: String? = null

    fun setText(text: String) {
        mLoadingText = text
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}