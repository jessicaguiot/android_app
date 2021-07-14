package com.example.tempestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.util.Log
import android.widget.Toast

class RegisterActivity2 : AppCompatActivity(), View.OnClickListener  {

    private val activity = this@RegisterActivity2

    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var textViewUsername: AppCompatTextView
    private lateinit var textViewPassword: AppCompatTextView
    private lateinit var textViewConfirmPassword: AppCompatTextView

    private lateinit var editTextUsername: AppCompatEditText
    private lateinit var editTextPassword: AppCompatEditText
    private lateinit var editTextConfirmPassword: AppCompatEditText

    private lateinit var buttonRegister: AppCompatButton
    private lateinit var textViewLoginLink: AppCompatTextView

    private lateinit var databaseHelper: DBHelper
    private lateinit var inputValidation: InputValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)
        supportActionBar!!.hide()
        initViews()

        initListeners()

        initObjects()
        getDataFromIntent()
    }

    private fun initViews() {
        nestedScrollView = findViewById(R.id.registerNestedScrollView) as NestedScrollView

        textViewUsername = findViewById(R.id.registerTextViewUsername) as AppCompatTextView
        textViewPassword = findViewById(R.id.registerTextViewPassword) as AppCompatTextView
        textViewConfirmPassword = findViewById(R.id.registerTextViewConfirmPassword) as AppCompatTextView

        editTextUsername = findViewById(R.id.registerEditTextUsername) as AppCompatEditText
        editTextPassword = findViewById(R.id.registerEditTextPassword) as AppCompatEditText
        editTextConfirmPassword = findViewById(R.id.registerEditTextConfirmPassword) as AppCompatEditText

        buttonRegister = findViewById(R.id.buttonRegister) as AppCompatButton

        textViewLoginLink = findViewById(R.id.registerTextViewLoginLink) as AppCompatTextView
    }

    private fun initListeners() {
        buttonRegister.setOnClickListener(this)
        textViewLoginLink.setOnClickListener(this)
    }

    private fun initObjects() {
        databaseHelper = DBHelper(activity)
        inputValidation = InputValidation(activity)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.buttonRegister -> postDataToSQLite()
            R.id.registerTextViewLoginLink -> {
                val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }

    private fun postDataToSQLite() {
        if(!inputValidation.isEditTextFilled(editTextUsername, textViewUsername, getString(R.string.error_message_username))) {
            return
        }
        if(!inputValidation.isEditTextFilled(editTextPassword, textViewPassword, getString(R.string.error_message_password))) {
            return
        }
        if(!inputValidation.isEditTextMatches(editTextPassword, editTextConfirmPassword, textViewConfirmPassword, getString(R.string.error_message_password_match))) {
            return
        }

        if (!databaseHelper.checkUser(editTextUsername.text.toString().trim())) {
            var user = User(name = editTextUsername.text.toString().trim(), password = editTextPassword.text.toString().trim())
            databaseHelper.addUser(user)

            Snackbar.make(nestedScrollView, getString(R.string.success_message_register), Snackbar.LENGTH_LONG).show()
            val loginIntent = Intent(applicationContext, LoginActivity::class.java)
            emptyEditTexts()
            startActivity(loginIntent)
        } else {
            Snackbar.make(nestedScrollView, getString(R.string.error_message_username_exists), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getDataFromIntent() {
        val username = intent.getStringExtra("username")
        editTextUsername.setText(username)
    }

    private fun emptyEditTexts() {
        editTextUsername.text = null
        editTextPassword.text = null
        editTextConfirmPassword.text = null
    }
}