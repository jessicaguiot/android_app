package com.example.tempestapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver: BroadcastReceiver() {

    private var context: Context? = null
    private lateinit var channel: NotificationChannel
    private var isChannelCreated = false
    private val EVENT_CHANNEL_ID = "EVENT_CHANNEL_ID"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        this.context = context
        when (action) {
            Intent.ACTION_POWER_CONNECTED -> {
                notifyUser()
            }

            Intent.ACTION_POWER_DISCONNECTED -> {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifyUser() {
        if (!isChannelCreated) {
            createChannel()
        }
        val mBuider = NotificationCompat.Builder(context!!, EVENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hello, how u doing?")
            .setContentText("Your battery is charging")
        val notification = mBuider.build()
        val notificationManagerCompat = NotificationManagerCompat.from(context!!)
        notificationManagerCompat.notify(1, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        channel = NotificationChannel(EVENT_CHANNEL_ID, "Greeting Events", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "A channel for notifications"
        channel.lightColor = Color.GREEN
        val notificationManager = context!!.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        isChannelCreated = true
    }
}