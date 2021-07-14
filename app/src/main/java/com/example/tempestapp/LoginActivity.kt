package com.example.tempestapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.material.snackbar.Snackbar
import okhttp3.Authenticator
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.security.SecureRandom
import java.security.cert.Certificate
import java.util.*

class LoginActivity: AppCompatActivity(), View.OnClickListener {

    private val activity = this@LoginActivity
    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var textViewUsername: AppCompatTextView
    private lateinit var textViewPassword: AppCompatTextView

    private lateinit var editTextUsername: AppCompatEditText
    private lateinit var editTextPassword: AppCompatEditText

    private lateinit var buttonLogin: AppCompatButton
    private lateinit var textViewRegisterLink: AppCompatTextView

    private lateinit var databaseHelper: DBHelper
    private lateinit var inputValidation: InputValidation

    private lateinit var sessionManager: SessionManager

    private val SAFETY_NET_CHECK_API_KEY = "API_KEY"
    private var isDeviceRooted = false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        initViews()
        initListeners()
        initObjects()
        sessionManager()
        ifGooglePlayServicesValid()

        if (isRooted()) {
            Toast.makeText(applicationContext, "DEVICE IS ROOTED - METHOD 2", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "DEVICE IS NOT ROOTED - METHOD 2", Toast.LENGTH_LONG).show()
        }
    }

    private fun sessionManager() {
        if (sessionManager.isLoggedIn()) {
            val helloIntent = Intent(applicationContext, HelloWorldActivity2::class.java)
            startActivity(helloIntent)
        }
    }

    private fun initViews() {

        nestedScrollView = findViewById(R.id.nestedScrollView) as NestedScrollView

        textViewUsername = findViewById(R.id.textViewUsername) as AppCompatTextView
        textViewPassword = findViewById(R.id.textViewPassword) as AppCompatTextView


        editTextUsername = findViewById(R.id.editTextUsername) as AppCompatEditText
        editTextPassword = findViewById(R.id.editTextPassword) as AppCompatEditText


        buttonLogin = findViewById(R.id.buttonLogin) as AppCompatButton

        textViewRegisterLink = findViewById(R.id.textViewRegisterLink) as AppCompatTextView
    }

    private fun initListeners() {
        buttonLogin.setOnClickListener(this)
        textViewRegisterLink.setOnClickListener(this)
    }

    private fun initObjects() {
        databaseHelper = DBHelper(activity)
        inputValidation = InputValidation(activity)
        sessionManager = SessionManager(activity)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonLogin -> verifyFromSQLite()
            R.id.textViewRegisterLink -> {
                val intentRegister  = Intent(applicationContext, RegisterActivity2::class.java)
                startActivity(intentRegister)
                finish()
            }
        }
    }

    private fun verifyFromSQLite() {

        if(!inputValidation.isEditTextFilled(editTextUsername, textViewUsername, getString(R.string.error_message_username))) {
            return
        }
        if(!inputValidation.isEditTextFilled(editTextPassword, textViewPassword, getString(R.string.error_message_password))) {
            return
        }

        if (databaseHelper.checkUser(editTextUsername.text.toString().trim{it <= ' '}  , editTextPassword.text.toString().trim{ it <= ' '})) {

            val helloIntent = Intent(applicationContext, HelloWorldActivity2::class.java)
            sessionManager.createLoginSession(editTextUsername.text.toString(), editTextPassword.text.toString())
            emptyEditTexts()
            startActivity(helloIntent)
            finish()
        } else {
            Snackbar.make(textViewUsername, getString(R.string.error_valid_username_password), Snackbar.LENGTH_LONG).show()

        }
    }

    private fun emptyEditTexts() {
        editTextUsername.text = null
        editTextPassword.text = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ifGooglePlayServicesValid() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext) == ConnectionResult.SUCCESS) {
            callSafetyNetAttentionApi()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun callSafetyNetAttentionApi() {
        SafetyNet.getClient(this).attest(generateNonce(), SAFETY_NET_CHECK_API_KEY).addOnSuccessListener { response ->
            val jwsResponse = decodeJWS(response.jwsResult)
            try {
                val attestationResponse = JSONObject(jwsResponse)
                val ctsProfileMatch = attestationResponse.getBoolean("ctsProfileMatch")
                val basicIntegrity = attestationResponse.getBoolean("basicIntegrity")
                if (!ctsProfileMatch || !basicIntegrity) {
                    isDeviceRooted = true
                    Toast.makeText(applicationContext, "DEVICE ROOTED - METHOD 1", Toast.LENGTH_LONG).show()
                }
            } catch (e : JSONException) {
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {error ->
            Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun decodeJWS(jwsResult: String?) : String {
        if (jwsResult == null) {
            return "ERROR NO RESULT"
        }
        val jwsParts  = jwsResult.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val decoder = Base64.getDecoder()
        return String(decoder.decode(jwsParts[1]))
    }

    fun generateNonce() : ByteArray {
        var nonce = ByteArray(16)
        SecureRandom().nextBytes(nonce)
        return nonce
    }

    public fun isRooted() : Boolean {
        val buildTags = android.os.Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        try {
            val file = File("/sytem/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
        } catch (exeption : Exception) {
            Toast.makeText(applicationContext, exeption.toString(), Toast.LENGTH_LONG).show()
        }

        if (!canExecuteCommand("su")) {
            if (findBinary("su")) {
                return true
            }
        }

        return false
    }

    private fun findBinary(binaryName:String) : Boolean {
        var found = false

        if (!found) {
            val places = arrayOf<String>("/sbin/", "/system/bin/", "/system/xbin/", "data/local/xbin/", "data/local/bin/",
                "/system/sd/xbin/", "/system/bin/failsafe", "/data/local")
            places.forEach {
                if (File(it+binaryName).exists()) {
                    found = true
                }
            }
        }

        return found
    }

    private fun canExecuteCommand(command:String) : Boolean {

        var executedSuccesfully : Boolean = false
        try {
            Runtime.getRuntime().exec(command)
            executedSuccesfully = true
        } catch (exeption : Exception) {
            Toast.makeText(applicationContext, exeption.toString(), Toast.LENGTH_LONG).show()
            executedSuccesfully = false
        }

        return executedSuccesfully
    }
}
