package com.ice.ktuice.AL.GradeTable

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.ice.ktuice.R
import com.ice.ktuice.UI.main.MainActivity


/**
 * Created by Andrius on 2/20/2018.
 */
class NotificationFactory(val context: Context) {
    companion object {

        private var _notificationTrack = 416
        val newNotificationID: Int
            get(){
                return ++_notificationTrack
            }
    }
    init{
        createNotificationChannel()
    }

    fun pushNotification(message: String){
        println("pushing notification!")
        // The id of the channel.
        val CHANNEL_ID = "test_notification_channel"
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setLights(Color.argb(0xff, 0x00, 0xff, 0x00), 300, 100)
                .setSmallIcon(R.drawable.logo_v2)
                .setContentTitle("KTU ice")
                .setContentText(message)
                .setAutoCancel(true)
                .setOngoing(false)

        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(context, MainActivity::class.java)

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        val stackBuilder = TaskStackBuilder.create(context)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)

        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        //mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        val notif = mBuilder.build()
        notif.defaults = 0
        notif.ledARGB = Color.CYAN
        notif.ledOnMS = 300
        notif.ledOffMS = 100
        mNotificationManager.notify(newNotificationID, notif)
        println("Notification pushed!")
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // The id of the channel.
            val id = "test_notification_channel"
            // The user-visible name of the channel.
            val name = "Test"
            // The user-visible description of the channel.
            val description = "Heyy"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)

            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.CYAN
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(10)
            mNotificationManager.createNotificationChannel(mChannel)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}