package com.peerbits.base.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.peerbits.base.utils.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding, V : BaseViewModel<*>> :
    BottomSheetDialogFragment(),
    BaseNavigator {

    var baseActivity: BaseActivity<*, *>? = null
        private set
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    private var mRootView: View? = null
    private var mProgressDialog: Dialog? = null

    val isNetworkConnected: Boolean
        get() = baseActivity != null && baseActivity!!.isNetworkConnected

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    val activityBinding: ViewDataBinding?
        get() = baseActivity!!.viewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = viewDataBinding!!.root
        initialization(savedInstanceState)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    override fun onAttach(attachedContext: Context) {
        super.onAttach(attachedContext)
        if (attachedContext is BaseActivity<*, *>) {
            val activity = attachedContext as BaseActivity<*, *>?
            this.baseActivity = activity
        }
    }


    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity!!.hideKeyboard()
        }
    }


    private fun performDependencyInjection() {
//        AndroidSupportInjection.inject(this)
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    abstract fun initialization(savedInstance: Bundle?)

    fun addFragment(fragment: Fragment, @IdRes layout: Int) {

        childFragmentManager
            .beginTransaction()
            .add(layout, fragment)
            .addToBackStack(null)
            .commit()

    }

    fun replaceFragment(fragment: Fragment, @IdRes layout: Int) {


        childFragmentManager
            .beginTransaction()
            .replace(layout, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun showLoading() {
        hideLoading()
        mProgressDialog = CommonUtils.showLoadingDialog(context)
    }

    override fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }


}
