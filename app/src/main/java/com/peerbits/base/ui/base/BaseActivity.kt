package com.peerbits.base.ui.base

import android.annotation.TargetApi
import android.app.Dialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.nlgic.insurance.utils.NetworkUtils
import com.peerbits.base.AppClass
import com.peerbits.base.R
import com.peerbits.base.ui.dialog.CustomProgressDialog
import com.peerbits.base.ui.dialog.MessageDialog
import com.peerbits.base.utils.GlideLoader
import com.peerbits.base.utils.LocaleHelper
import com.peerbits.base.utils.pref.SessionManager
import com.peerbits.base.utils.visible
import kotlinx.android.synthetic.main.toolbar.tvTitle
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity(),
    BaseNavigator {

    // TODO
    // this can probably depend on isLoading variable of BaseViewModel,
    // since its going to be common for all the activities
    private var mProgressDialog: Dialog? = null
    var isBackArrow = false
    /*public void openActivityOnTokenExpire() {
        startActivity(LoginActivity.getStartIntent(this));
        finish();
    }*/

    lateinit var glideLoader: GlideLoader
    var permissionListener: setPermissionListener? = null

    // Progress
    private var progressDialog: CustomProgressDialog? = null
    lateinit var session: SessionManager
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null

    /**
     * Override for set view model
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

    val isNetworkConnected: Boolean
        get() = NetworkUtils.isNetworkConnected(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        // performDependencyInjection()
        super.onCreate(savedInstanceState)
        viewModel.appContext = this
        session = SessionManager(this)
//        val w = window
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
        performDataBinding()
        setStatusBarGradiant()
        glideLoader = GlideLoader(applicationContext)
        initialization(savedInstanceState)
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        this.mViewModel = if (mViewModel == null) viewModel else mViewModel
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * hide soft keyboard
     */
    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * All initialization will be done on this method
     * This method execute after the data binding
     */
    abstract fun initialization(savedInstance: Bundle?)

    protected fun getCompatColor(color: Int): Int {

        return ContextCompat.getColor(this, color)
    }

    /**
     * Setting up toolbar accross the app with back arrow
     * @param toolbar Toolbar
     * @param strTitle String
     * @param isBackArrow Boolean
     * @param icon Int
     */
    fun setUpToolbarWithBackArrow(
        toolbar: Toolbar,
        strTitle: String,
        isBackArrow: Boolean, @DrawableRes icon: Int = R.drawable.ic_back,
        @DrawableRes background: Int = R.color.colorPrimary
    ) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(icon)
            toolbar.setBackgroundResource(background)
            tvTitle.text = strTitle
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))

//            tvSubTitle.text = strSubTitle
//            if (strSubTitle.isNotEmpty()) {
//                tvSubTitle.visibility = VISIBLE
//            } else {
//                tvSubTitle.visibility = GONE
//            }
        }
    }

    fun setUpToolbar(
        toolbar: Toolbar,
        showBackArrow: Boolean,
        title: String
    ) {

        setSupportActionBar(toolbar)

        val mActionBar = supportActionBar

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

    /**
     * Setting up toolbar accross the app with back arrow
     * @param toolbar Toolbar
     * @param strTitle String
     * @param isBackArrow Boolean
     * @param icon Int
     */
    fun setUpToolbarWithCustomBackArrow(
        toolbar: Toolbar,
        strTitle: String,
        isBackArrow: Boolean,
        @DrawableRes icon: Int = R.drawable.ic_back,
        @DrawableRes background: Int = R.color.colorPrimary
    ) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(isBackArrow)
            actionBar.setHomeAsUpIndicator(icon)
            toolbar.setBackgroundResource(background)
            tvTitle.text = strTitle
        }
    }

    /**
     * setting up toolbar with drawer menu
     * @param toolbar Toolbar
     * @param strTitle String
     */
    fun setUpToolbarWithMenu(
        toolbar: Toolbar,
        strTitle: String,
        @DrawableRes navMenu: Int = R.drawable.ic_menu,
        textColorInt: Int = ContextCompat.getColor(this, R.color.black)
    ) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(navMenu)
            tvTitle.text = strTitle
            tvTitle.visible(true)
            tvTitle.setTextColor(textColorInt)
        }
    }

    /**
     * adds fragment into given container id with/without backStack and animation
     *
     */
    open fun addFragment(
        containerRes: Int,
        fragment: Fragment,
        addToBackStack: Boolean,
        animate: Boolean = true
    ) {
        var t = supportFragmentManager.beginTransaction()
        t.replace(containerRes, fragment, fragment.javaClass.simpleName)
        if (addToBackStack) {
            t = t.addToBackStack(fragment.javaClass.simpleName)
        }
        if (animate) {
            t = t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
        fragment.retainInstance = true
        t.commitAllowingStateLoss()
    }

    /**
     * set status bar gradient for O@
     */
    fun setStatusBarGradiant() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getCompatColor(R.color.white)
            //            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }

    /**
     * On back press handle
     * @param item MenuItem
     * @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    protected fun getFragmentByTag(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    /**
     * show loader "Please wait"
     */
    override fun showLoading() {
        hideKeyboard()
        /*hideLoading()
        mProgressDialog = CommonUtils.showLoadingDialog(this)*/
        showProgress()
    }

    /**
     * Stop loader
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
            progressDialog = CustomProgressDialog(this, message)
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
     * show short toast message
     * @param msg String
     */
    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * show Alert Dialog with single ok button
     * @param title String
     * @param msg String? message to show
     */
    override fun showAlert(title: String, msg: String?) {

        val dialog = MessageDialog(this)
            .setTitle(title)
            .setMessage(msg).cancelable(true)
            .setPositiveButton(getString(R.string.ok)) { d1, i ->
                d1.dismiss()
            }
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        SESSION_EXPIRED_RECEIVER.unregister(this)

        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun finishActivity() {
        finish()
    }

    override fun gotoLogin() {
        /*val intent = SplashActivity.newIntent(this).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }*/
        startActivity(intent)
    }

    fun requestAppPermissions(
        requestedPermissions: Array<String>,
        requestCode: Int, listener: setPermissionListener
    ) {
        this.permissionListener = listener
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission)
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, requestedPermissions, requestCode)
        } else {
            if (permissionListener != null) permissionListener?.onPermissionGranted(requestCode)
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
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permissionListener != null) permissionListener?.onPermissionGranted(requestCode)
                break
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
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

    interface setPermissionListener {
        fun onPermissionGranted(requestCode: Int)

        fun onPermissionDenied(requestCode: Int)

        fun onPermissionNeverAsk(requestCode: Int)
    }

    fun showPermissionSettingDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.need_permission)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.app_settings) { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    protected val app: AppClass
        get() {
            return applicationContext as AppClass
        }

    /**
     * set full screen view for splash screen
     */
    fun setFullScreenView() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, mainContainer).let { controller ->
                controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {*/
        //noinspection
        @Suppress("DEPRECATION")
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
        // }
    }

    private class SessionExpiredBroadcastReceiver : BroadcastReceiver() {
        private var listener: BaseActivity<*, *>? = null

        override fun onReceive(context: Context, intent: Intent) {
            listener?.onSessionExpired()
        }

        fun register(listener: BaseActivity<*, *>, context: Context) {
            this.listener = listener
            LocalBroadcastManager.getInstance(context)
                .registerReceiver(this, IntentFilter(SESSION_EXPIRED))
        }

        fun unregister(context: Context) {
            this.listener = null
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
        }
    }

    companion object {
        private val SESSION_EXPIRED_RECEIVER = SessionExpiredBroadcastReceiver()
        val SESSION_EXPIRED = "SESSION_EXPIRED"
    }

    override fun onResume() {
        super.onResume()
        SESSION_EXPIRED_RECEIVER.register(this, this)
    }

    override fun showSnackBar(message: String, isError: Boolean) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
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
                    findViewById<View>(android.R.id.content).context,
                    R.color.snackbar_error_color
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    findViewById<View>(android.R.id.content).context,
                    R.color.snackbar_success_color
                )
            )
        }
        snackbar.show()
    }

    override fun attachBaseContext(newBase: Context?) {

//        val mSessionManager = SessionManager.getInstance(newBase!!)
//
//        var language = ""
//        when (mSessionManager.getDataByKey(PreferenceKeys.SELECTED_LANGUAGE)) {
//
//            AppConstants.ENGLISH_LANGUAGE -> {
//
//                language = AppConstants.ENGLISH_LANGUAGE
//            }
//            AppConstants.ARABIC_LANGUAGE -> {
//
//                language = AppConstants.ARABIC_LANGUAGE
//            }
//        }
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    fun displayCamera(onUriSet: (uri: Uri?, intent: Intent?) -> Unit) {
        var uriSavedImage: Uri? = null
        val imagesFolder = File(
            cacheDir,
            resources.getString(R.string.app_name)
        )
        try {
            if (!imagesFolder.exists()) {
                val isCreated: Boolean = imagesFolder.mkdirs()
                if (!isCreated) {
                    showToastShort(getString(R.string.storage_not_found))
                    onUriSet(null, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // -------- Start of v24 FileProvider concept ----------
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            val image: File = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            uriSavedImage =
                FileProvider.getUriForFile(this, "com.nlgic.insurance.fileprovider", image)
            // -------- End v24 FileProvider concept ----------
            val intent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
            try {
                onUriSet(uriSavedImage, intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onUriSet(null, null)
        }
    }

    fun downloadFile(url: String?) {
        // Download here using downloadUrl
        if (url != null && url.startsWith("http")) {
            val request: DownloadManager.Request = DownloadManager.Request(
                Uri.parse(url)
            )
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val dm: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        } else {
            showToastShort(getString(R.string.str_document_cant_download))
        }
    }

    fun onSessionExpired() {
        Timber.d(javaClass.simpleName, "onSessionExpired()")

        val dialog = MessageDialog(this)
            .setTitle(getString(R.string.str_session_expried_msg))
            .cancelable(false)
            .setMessage(getString(R.string.str_session_expried_msg)).cancelable(false)
            .setPositiveButton(getString(R.string.relogin)) { d1, i ->
                //                Preference.IS_LOGGED_IN.setValue(this, false)
                d1.dismiss()
                gotoLogin()
            }

        dialog.show()
    }
}


