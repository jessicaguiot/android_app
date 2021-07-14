package com.example.tempestapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList
import java.util.concurrent.Executors

class HelloWorldActivity2 : AppCompatActivity(), View.OnClickListener {
    private val activity = this@HelloWorldActivity2

    private lateinit var linearLayout: LinearLayoutCompat

    private lateinit var textView: AppCompatTextView
    private lateinit var sessionManager: SessionManager

    private lateinit var usernameTextView: AppCompatTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var listPosts: MutableList<Posts>
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var buttonLogout: AppCompatButton

    private lateinit var toggleButton: AppCompatToggleButton

    private lateinit var webViewButton: AppCompatButton

    private val black = -16777216
    private val white = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_world2)
        supportActionBar!!.hide()
        initView()
        initListeners()
        initObject()
        sessionManager.checkLogin()
        var user = sessionManager.getUserDetails()
        var username = user.get(SessionManager.USERNAME_KEY)
        usernameTextView.setText(username)
        var isDarkMode = user.get(SessionManager.MODE_KEY).toBoolean()
        toggleButton.isChecked = isDarkMode

        val notificationReceiver = NotificationReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        this.registerReceiver(notificationReceiver,filter)
    }

    private fun initView() {
        linearLayout = findViewById(R.id.linearLayoutCompat) as LinearLayoutCompat

        textView = findViewById(R.id.textViewHello) as AppCompatTextView
        usernameTextView = findViewById(R.id.textViewUsernameHello) as AppCompatTextView

        webViewButton = findViewById(R.id.webViewButton) as AppCompatButton
        buttonLogout = findViewById(R.id.buttonLogout) as AppCompatButton

        recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        toggleButton = findViewById(R.id.toggleButton) as AppCompatToggleButton
    }

    private fun initListeners() {
        webViewButton.setOnClickListener(this)
        buttonLogout.setOnClickListener(this)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setUserPreferences(isChecked)
            changeViewColors(isChecked)
        }

    }

    private fun initObject() {
        sessionManager = SessionManager(activity)

        listPosts = ArrayList()
        recyclerAdapter = RecyclerAdapter(listPosts, toggleButton.isChecked)
        var mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerAdapter


        getData()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.webViewButton -> {
                val webViewIntent = Intent(activity, WebViewActivity::class.java)
                startActivity(webViewIntent)
            }
            R.id.buttonLogout -> {
                sessionManager.logoutUser()
            }
        }
    }

    private fun changeViewColors(isDarkMode: Boolean) {

        recyclerAdapter.setBackground(isDarkMode)

        if (isDarkMode) {
            linearLayout.setBackgroundColor(black)
            textView.setTextColor(white)
            usernameTextView.setTextColor(white)
            recyclerView.setBackgroundColor(black)

        } else {
            linearLayout.setBackgroundColor(white)
            textView.setTextColor(black)
            usernameTextView.setTextColor(black)
            recyclerView.setBackgroundColor(white)
        }
    }

    fun getData()  {
        val retrofitClient = NetworkUtils.getRetroFitInstance("https://jsonplaceholder.typicode.com")
        val endpoint = retrofitClient.create(Endpoint::class.java)
        val callback = endpoint.getPosts()
        val result :  MutableList<Posts> = ArrayList()


        callback.enqueue(object: Callback<List<Posts>> {
            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                response.body()?.forEach {
                    listPosts.add(it)
                }

                recyclerAdapter.swapItems(listPosts)
            }
        })
    }
}