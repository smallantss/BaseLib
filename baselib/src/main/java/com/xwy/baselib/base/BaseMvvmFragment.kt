package com.xwy.baselib.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xwy.baselib.BR
import com.xwy.baselib.ext.hideInput
import com.xwy.baselib.ext.toast
import com.xwy.baselib.utils.CleanLeakUtils
import java.lang.reflect.ParameterizedType
import kotlin.properties.Delegates

open class BaseMvvmFragment<T : ViewDataBinding, VM : BaseViewModel>(val layoutId: Int) :
    Fragment() {

    var binding: T by Delegates.notNull()
    var viewModel: VM by Delegates.notNull()
    private var viewModelId = BR.viewModel
    private var useHost = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeView()
    }

    //在onCreate的时候做操作
    open fun beforeView() {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewDataBinding()
        initUIChange()
        initView(view, savedInstanceState)
    }

    //初始化DataBinding
    private fun initViewDataBinding() {
        viewModelId = initViewModelId()
        val vm = initViewMode()
        if (vm == null) {
            val modelClass: Class<BaseViewModel>?
            val type = javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<BaseViewModel>
            } else {
                BaseViewModel::class.java
            }
            val hostViewModel = (activity as BaseMvvmActivity<*, *>).viewModel
            viewModel = if (modelClass == hostViewModel.javaClass) {
                useHost = true
                hostViewModel as VM
            } else {
                useHost = false
                createViewModel(this, modelClass) as VM
            }
        } else {
            viewModel = vm
        }
        binding.apply {
            setVariable(viewModelId, viewModel)
            executePendingBindings()
            lifecycleOwner = this@BaseMvvmFragment
        }
        if (useHost.not()) {
            lifecycle.addObserver(viewModel)
        }
    }

    /**
     * 获取ViewModel的实例
     */
    private fun createViewModel(
        fragment: Fragment,
        clazz: Class<BaseViewModel>
    ): BaseViewModel {
        return ViewModelProvider(fragment).get(clazz)
    }

    /**
     * 注册事件监听
     */
    private fun initUIChange() {
        if (useHost) return
        viewModel.apply {
            startActivityEvent.observe(this@BaseMvvmFragment, Observer {
                startActivity(it)
            })
            startActivityPair.observe(this@BaseMvvmFragment, Observer {
                startActivityWithData(it.first, it.second)
            })
            loadingEvent.observe(this@BaseMvvmFragment, Observer {
//                loading(it)
            })
            toastEvent.observe(this@BaseMvvmFragment, Observer {
                toast(it)
            })
            finishEvent.observe(this@BaseMvvmFragment, Observer {
                finish()
            })
            hideInput.observe(this@BaseMvvmFragment, Observer {
                this@BaseMvvmFragment.activity?.window?.decorView?.hideInput()
            })
        }
    }

    open fun startActivity(clazz: Class<*>) {
        activity ?: return
        startActivity(Intent(activity, clazz))
    }

    open fun startActivityWithData(clazz: Class<*>, bundle: Bundle) {
        activity ?: return
        startActivity(Intent(activity, clazz).apply {
            putExtras(bundle)
        })
    }

    open fun finish() {
        activity?.finish()
    }

//    private var loadingDialog: LoadingDialog? = null
//
//    open fun loading(loading: Boolean) {
//        activity ?: return
//        if (loadingDialog == null) {
//            loadingDialog = LoadingDialog().apply {
//                setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_full_screen)
//            }
//        }
//        if (loading) {
//            if (loadingDialog?.isAdded == true) {
//                return
//            }
//            loadingDialog?.show(activity!!.supportFragmentManager, "loading")
//        } else {
////            dismissLoading()
//        }
//    }
//
//    fun dismissLoading(){
//        loadingDialog?.let {
//            if (it.dialog?.isShowing == true) {
//                it.dismiss()
//            }
//        }
//        loadingDialog = null
//    }

    open fun initView(view: View, savedInstanceState: Bundle?) {

    }

    /**
     * 初始化ViewModelId
     * 可通过重写重新设置
     */
    open fun initViewModelId(): Int {
        return BR.viewModel
    }

    /**
     * 获取ViewModel的实例
     * 可通过重写获取
     */
    open fun initViewMode(): VM? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
//        dismissLoading()
        CleanLeakUtils.fixInputMethodManagerLeak(context)
        binding.unbind()
    }
}