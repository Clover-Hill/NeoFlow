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
import com.newera.neoflow.logic.utils.Constants

class AlarmReceiver: BroadcastReceiver()
{

    // notificationId is a unique int for each notification that you must define
    private var notificationId = 0 ;

    override fun onReceive(context: Context, intent: Intent) {

        val todoTitle = intent.getStringExtra("todoTitle")
        notificationId += 1

        val allTodoIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, notificationId , allTodoIntent, 0)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                R.string.todo_channel_name.toString(),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = R.string.todo_channel_description.toString()
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
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