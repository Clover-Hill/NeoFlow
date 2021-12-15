package com.newera.neoflow.logic.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.newera.neoflow.R
import com.newera.neoflow.ui.MainActivity
import java.util.*

/**
 * Alarm receiver
 *
 * Send a notification to phone when called
 * 3 steps:
 * 1. Establish notification channel
 * 2. Build a notification using NotificationCompat
 * 3. Call notify to actually send the notification
 *
 * TODO: Still can't send multiple message at once every time, may need a better way to generate a unique number
 *
 * @constructor Create empty Alarm receiver
 */
class AlarmReceiver: BroadcastReceiver()
{

    override fun onReceive(context: Context, intent: Intent) {

        val todoTitle = intent.getStringExtra("todoTitle")

        /**
         * Notification id
         * This id is unique for every notification
         * Here uses random number to simulate a unique number
         */
        val notificationId : Int = (0..10000).random() + 1

        val allTodoIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, notificationId , allTodoIntent, 0)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                R.string.todo_channel_id.toString(),
                R.string.todo_channel_name.toString(),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = R.string.todo_channel_description.toString()
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, R.string.todo_channel_id.toString())
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle("Neo Flow")
            .setContentText("Reminder for $todoTitle")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationCompat = NotificationManagerCompat.from(context)
        notificationCompat.notify(notificationId, builder.build())

    }

}