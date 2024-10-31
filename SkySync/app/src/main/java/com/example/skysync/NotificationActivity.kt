package com.example.skysync

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

class NotificationActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val date = intent.getStringExtra("Date")
        createNotificationChannel()

        if (checkNotificationPermissions(this)) {
            if (date != null) {
                scheduleNotification(
                    date,
                    intent.getBooleanExtra("toRemove", false),
                    intent.getStringExtra("Title")!!,
                    intent.getStringExtra("Message")!!
                )
            }
        }

        finish()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(
        date: String,
        toRemove: Boolean,
        notificationTitle: String,
        notificationMessage: String
    ) {

        val intent = Intent(applicationContext, Notification::class.java)
        intent.putExtra(titleExtra, notificationTitle)
        intent.putExtra(messageExtra, notificationMessage)

        if (toRemove) {
            val pendingIntentAlready = PendingIntent.getBroadcast(
                applicationContext,
                notificationID,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntentAlready != null) {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntentAlready)
            }

            return
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            changeForTimeStamp(date),
            pendingIntent
        )
    }

    private fun changeForTimeStamp(notificationDateTime: String): Long {

        val calendar = Calendar.getInstance()
        val dateTime = notificationDateTime.split(";")

        val date = dateTime[0].split("-")
        val time = dateTime[1].split(":")

        calendar.set(
            date[0].toInt(),
            date[1].toInt() - 1,
            date[2].toInt(),
            time[0].toInt(),
            time[1].toInt(),
            0
        )

        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val notificationManager =
            getSystemService(ComponentActivity.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelID,
            "Upcoming Meeting Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description =
            "This Channel is used to show Upcoming Meeting Notifications for SkySync"
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkNotificationPermissions(context: Context): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (!notificationManager.areNotificationsEnabled()) {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        context.packageName
                    )
                )

                return false
            }
        } else {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
                        Settings.EXTRA_APP_PACKAGE,
                        context.packageName
                    )
                )

                return false
            }
        }

        return true
    }
}
