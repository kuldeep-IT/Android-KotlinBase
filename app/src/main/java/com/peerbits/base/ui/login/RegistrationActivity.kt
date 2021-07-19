package com.peerbits.base.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.peerbits.base.R
import com.peerbits.base.ui.home.HomeActivity
import com.peerbits.base.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_registration.btnSignup
import kotlinx.android.synthetic.main.activity_registration.etContactNo
import kotlinx.android.synthetic.main.activity_registration.etEmail
import kotlinx.android.synthetic.main.activity_registration.etName
import kotlinx.android.synthetic.main.activity_registration.etPassword

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setClickListener()
    }

    fun setClickListener() {
        btnSignup.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation() {
        if (etName.text.isNullOrEmpty()) {
            Toast.makeText(
                this,
                resources.getString(R.string.valid_Name),
                Toast.LENGTH_SHORT
            ).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text as CharSequence)
                .matches() || etEmail.text.isNullOrEmpty()
        ) {
            Toast.makeText(
                this,
                resources.getString(R.string.valid_Email),
                Toast.LENGTH_SHORT
            ).show()
        } else if (etContactNo.text.isNullOrEmpty() || etContactNo.text.length < 10) {
            Toast.makeText(
                this,
                resources.getString(R.string.valid_Contact_Digit),
                Toast.LENGTH_SHORT
            ).show()
        } else if (etPassword.text.isNullOrEmpty() || etContactNo.text.length < 8) {
            Toast.makeText(
                this,
                resources.getString(R.string.valid_Password),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val i = Intent(applicationContext, HomeActivity::class.java)
            startActivity(i)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, RegistrationActivity::class.java)
            return intent
        }
    }
}