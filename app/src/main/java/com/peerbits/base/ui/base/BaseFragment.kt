package com.peerbits.base.ui.base

import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.peerbits.base.AppClass
import com.peerbits.base.R
import com.peerbits.base.databinding.ToolbarBinding
import com.peerbits.base.interfaces.OnSnackbarActionListener
import com.peerbits.base.utils.GlideLoader
import com.peerbits.base.utils.pref.SessionManager
import com.peerbits.base.ui.dialog.CustomProgressDialog
import com.peerbits.base.ui.dialog.MessageDialog
import com.peerbits.base.utils.visible
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by ak on 09/07/18.
 */

@Suppress("UNCHECKED_CAST")
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<*>> : Fragment(), BaseNavigator {

    var baseActivity: BaseActivity<*, *>? = null
    var mActivity: Activity? = null
    private var snackbar: Snackbar? = null
        private set
    lateinit var mContext: Context
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    private var mRootView: View? = null
    private var mProgressDialog: Dialog? = null
    lateinit var glideLoader: GlideLoader
    lateinit var session: SessionManager
    var permissionListener: setPermissionListener? = null

    // Progress
    private var progressDialog: CustomProgressDialog? = null

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

    var imei = ""
    var isBuzz = false
    var isLight = false
    lateinit var bluetoothManager: BluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        //  performDependencyInjection()
        super.onCreate(savedInstanceState)
        mViewModel = viewModel
        mViewModel?.appContext = baseActivity!!
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
            mRootView = viewDataBinding!!.root
            showToolbar(true)
            initialization(savedInstanceState)
        }
        onSetupToolbar()
        return mRootView
    }

    fun showToolbar(showToolbar : Boolean) {
        baseActivity?.toolbar?.visible(showToolbar)
    }

    open fun onSetupToolbar() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            val activity = context
            this.baseActivity = activity
            mActivity = getActivity()
            this.mContext = activity
            glideLoader = GlideLoader(mContext)
            session = SessionManager(mContext)
        }
    }

    fun onBackPressed(): Boolean {
        return false
    }

    interface setPermissionListener {
        fun onPermissionGranted(requestCode: Int)

        fun onPermissionDenied(requestCode: Int)

        fun onPermissionNeverAsk(requestCode: Int)
    }

    fun requestAppPermissions(
        requestedPermissions: Array<String>,
        requestCode: Int,
        listener: setPermissionListener
    ) {
        this.permissionListener = listener
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in requestedPermissions) {
            permissionCheck =
                permissionCheck + ContextCompat.checkSelfPermission(requireActivity(), permission)
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), requestedPermissions, requestCode)
        } else {
            if (permissionListener != null) permissionListener!!.onPermissionGranted(requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permissionListener != null) permissionListener?.onPermissionGranted(requestCode)
                break
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!,
                    permission
                )
            ) {
                if (permissionListener != null) permissionListener?.onPermissionDenied(requestCode)
                break
            } else {
                if (permissionListener != null) {
                    permissionListener?.onPermissionNeverAsk(requestCode)
                    break
                }
            }
        }
    }

    fun showPermissionSettingDialog(message: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.need_permission)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.app_settings) { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + requireActivity().packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    fun <T : ViewDataBinding> getActivityBinding(): T = baseActivity?.viewDataBinding as T

    fun setUpToolbarWithBackArrow(
        toolbar: ToolbarBinding,
        strTitle: String,
        isBackArrow: Boolean, @DrawableRes icon: Int = R.drawable.ic_back
    ) {
        baseActivity?.setSupportActionBar(toolbar.toolbar)
        val actionBar = baseActivity?.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(icon)
            toolbar.tvTitle.text = strTitle
        }
    }

    fun setupToolbar(
        toolbar: ToolbarBinding,
        showBackArrow: Boolean,
        title: String,
        fragmentPosition: String?
    ) {

        baseActivity?.setSupportActionBar(toolbar.toolbar)

        val mActionBar = baseActivity?.supportActionBar

        if (mActionBar != null) {

            mActionBar.setDisplayShowTitleEnabled(false)

            if (showBackArrow) {

                mActionBar.setDisplayHomeAsUpEnabled(true)
                mActionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            } else {
                mActionBar.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        baseActivity = null
    }

    /**
     * Hide soft keyboard from screen
     */
    fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity!!.hideKeyboard()
        }
    }

    fun showSnackbar(
        view: View?,
        msg: String,
        LENGTH: Int,
        action: String,
        actionListener: OnSnackbarActionListener?
    ) {
        if (view == null) return
        snackbar = Snackbar.make(view, msg, LENGTH)
        snackbar!!.setActionTextColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.colorPrimary
            )
        )
        if (actionListener != null) {
            snackbar!!.setAction(action) {
                snackbar!!.dismiss()
                actionListener.onAction()
            }
        }
        val sbView = snackbar!!.getView()
        val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snackbar!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        //        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()

        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    abstract fun initialization(savedInstance: Bundle?)

    /**
     * @see BaseActivity.showLoading
     */
    override fun showLoading() {
        hideKeyboard()
        /*hideLoading()
        mProgressDialog = CommonUtils.showLoadingDialog(context)*/
        showProgress()
    }

    override fun showSnackBar(message: String, isError: Boolean) {
        val snackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view

        val params = snackBarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        params.setMargins(
            params.leftMargin,
            (params.topMargin + resources.getDimension(R.dimen._15sdp)).toInt(),
            params.rightMargin,
            params.bottomMargin
        )

        snackBarView.layoutParams = params

        if (isError) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity().findViewById<View>(android.R.id.content).context,
                    R.color.snackbar_error_color
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity().findViewById<View>(android.R.id.content).context,
                    R.color.snackbar_success_color
                )
            )
        }
        snackbar.show()
    }

    fun showSnackBar(context: Context, message: String, isError: Boolean) {
        val snackbar =
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
        val snackBarView = snackbar.view

        val params = snackBarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.setMargins(
            params.leftMargin,
            (params.topMargin + resources.getDimension(R.dimen._15sdp)).toInt(),
            params.rightMargin,
            params.bottomMargin
        )

        snackBarView.layoutParams = params

        if (isError) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity().findViewById<View>(android.R.id.content).context,
                    R.color.snackbar_error_color
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity().findViewById<View>(android.R.id.content).context,
                    R.color.snackbar_success_color
                )
            )
        }
        snackbar.show()
    }

    /**
     * @see BaseActivity.hideLoading
     */
    override fun hideLoading() {

        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            /*if (!EspressoIdlingResource.idlingResource.isIdleNow()) {
                EspressoIdlingResource.decrement() // Set app as idle.
            }*/
            mProgressDialog!!.dismiss()
        }
        stopProgress()
    }

    /**
     * @see BaseActivity.showToast
     */
    override fun showToast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Show Progress Dailog without any message
     */
    fun showProgress() {
        showProgress(null)
    }

    /**
     * Show Progress dialog with Message
     *
     * @param message Message to show under Progress
     */
    fun showProgress(message: String?) {
        if (progressDialog != null) {
            progressDialog!!.show(message)
        } else {
            progressDialog = CustomProgressDialog(mContext, message)
            progressDialog!!.show()
        }
    }

    /**
     * Stop Progress if running and dismiss progress Dialog
     */
    fun stopProgress() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    /**
     * @see BaseActivity.showAlert
     */
    override fun showAlert(title: String, msg: String?) {

        val dialog = MessageDialog(mContext)
            .setTitle(title)
            .setMessage(msg).cancelable(true)
            .setPositiveButton(getString(R.string.ok)) { d1, i ->
                d1.dismiss()
            }
        dialog.show()
    }

    fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            requireActivity().window.statusBarColor = color
        }
    }

    override fun finishActivity() {
        requireActivity().finish()
    }

    override fun gotoLogin() {
        baseActivity?.finish()
        /* val intent = LoginActivity.newIntent(mContext).apply {

             flags = Intent.FLAG_ACTIVITY_NEW_TASK
             flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
             flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
         }

         startActivity(intent)*/
    }

    fun hasGPSDevice(context: Context): Boolean {
        val mgr =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    override fun onPause() {
        super.onPause()
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    protected val app: AppClass
        get() {
            return context?.applicationContext as AppClass
        }
}
