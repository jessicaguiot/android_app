package com.example.tempestapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.TextViewCompat
import android.net.Uri

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView : WebView

    private val yellow = -256

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        webView = findViewById(R.id.webView) as WebView
        webView.loadUrl("https://www.google.com.br/")

        val uri: Uri? = intent.data
        if (uri != null) {
            val name = uri.getQueryParameter("username")
            val registerIntent = Intent(applicationContext, RegisterActivity2::class.java)
            registerIntent.putExtra("username", name)
            startActivity(registerIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}