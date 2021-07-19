package com.peerbits.base.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.peerbits.base.BR
import com.peerbits.base.R
import com.peerbits.base.ViewModelProviderFactory
import com.peerbits.base.databinding.ActivityMainBinding
import com.peerbits.base.ui.base.BaseActivity
import kotlinx.android.synthetic.main.toolbar.toolbar

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel
        get() = ViewModelProvider(
            this, ViewModelProviderFactory<MainViewModel>(MainViewModel(app))
        ).get(MainViewModel::class.java)
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initialization(savedInstance: Bundle?) {
        viewModel.navigator = this
        setUpToolbar(toolbar,true,"HOME")
    }

    companion object {
        const val FRAGMENT_CONTAINER_ID = R.id.fragment_container
        const val HOME = "Home"

        fun newIntent(context: Context, isFinish: Boolean = true): Intent {
            val intent = Intent(context, MainActivity::class.java).apply {
                if (isFinish) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            }
            return intent
        }
    }
}