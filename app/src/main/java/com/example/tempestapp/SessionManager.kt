package com.example.tempestapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

public class SessionManager(private var context: Context) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var PRIVATE_MODE: Int = 0

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }

    companion object {
        val SHARED_PREFS = "SESSION_PREFERENCES"
        val IS_LOGIN = "isLoggedIn"
        val USERNAME_KEY = "username_key"
        val PASSWORD_KEY = "password_key"
        val MODE_KEY = "mode_key"
    }

    fun createLoginSession(username: String, password: String) {
        editor = sharedPreferences.edit()
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USERNAME_KEY, username)
        editor.putString(PASSWORD_KEY, password)
        editor.apply()
    }
    fun checkLogin() {
        if (!this.isLoggedIn()) {
            val loginIntent = Intent(context, LoginActivity::class.java)
            context.startActivity(loginIntent)
        }
    }

    fun getUserDetails(): HashMap<String, String> {
        var user: Map<String, String> = HashMap<String, String>()
        (user as HashMap).put(USERNAME_KEY, sharedPreferences.getString(USERNAME_KEY, null).toString())
        (user as HashMap).put(PASSWORD_KEY, sharedPreferences.getString(PASSWORD_KEY, null).toString())
        (user as HashMap).put(MODE_KEY, sharedPreferences.getBoolean(MODE_KEY, false).toString())
        return user
    }

    fun setUserPreferences(isDarkMode: Boolean) {
        editor.putBoolean(MODE_KEY, isDarkMode)
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
        val loginIntent = Intent(context, LoginActivity::class.java)
        context.startActivity(loginIntent)

    }

    fun isLoggedIn() : Boolean {
        return sharedPreferences.getBoolean(IS_LOGIN, false)
    }
}