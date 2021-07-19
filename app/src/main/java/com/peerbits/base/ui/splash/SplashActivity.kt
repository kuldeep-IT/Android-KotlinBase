package com.peerbits.base.ui.splash

import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import com.peerbits.base.BR
import com.peerbits.base.R
import com.peerbits.base.ViewModelProviderFactory
import com.peerbits.base.databinding.ActivitySplashBinding
import com.peerbits.base.ui.base.BaseActivity
import com.peerbits.base.ui.login.LoginActivity
import com.peerbits.base.ui.main.MainActivity

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    override val viewModel: SplashViewModel
        get() = ViewModelProvider(
            this, ViewModelProviderFactory<SplashViewModel>(
                SplashViewModel(app)
            )
        ).get(SplashViewModel::class.java)
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun initialization(savedInstance: Bundle?) {
        viewModel.navigator = this
        setFullScreenView()
        Handler().postDelayed({ setNavigation() }, 2000)
    }

    private fun setNavigation() {
            startActivity(LoginActivity.newIntent(this).apply {
                putExtra(LoginActivity.NAME, "Shahnavaz")
            })
    }
}