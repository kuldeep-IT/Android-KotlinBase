package com.peerbits.base.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peerbits.base.R
import com.peerbits.base.ui.home.HomeActivity
import com.peerbits.base.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.btnLogin
import kotlinx.android.synthetic.main.activity_login.btnRegistration
import kotlinx.android.synthetic.main.activity_login.etEmail
import kotlinx.android.synthetic.main.activity_login.etPassword

class LoginActivity : AppCompatActivity() {
    private var userName: String? = ""
    private val userId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getIntentData()
        setClickListeners()
    }

    fun setClickListeners() {
        btnLogin.setOnClickListener {
            checkValidation()
        }
        btnRegistration.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(RegistrationActivity.newIntent(this@LoginActivity))
            }
        })
    }

    fun checkValidation() {
        //Validations...
        val email: String = etEmail.text.toString().trim { it <= ' ' }
        if (!Patterns.EMAIL_ADDRESS.matcher(email as CharSequence)
                .matches() || email.isNullOrEmpty()
        ) {
            Toast.makeText(
                this,
                resources.getString(R.string.valid_Email),
                Toast.LENGTH_SHORT
            ).show()
        } else if (etPassword.text.isNullOrEmpty()) {
            Toast.makeText(
                this,
                resources.getString(R.string.valid_Email),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            startActivity(HomeActivity.newIntent(this))
        }
    }

    fun getIntentData() {
        userName = intent.getStringExtra(NAME)
    }

    companion object {
        const val NAME = "NAME"

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            return intent
        }
    }
}