package com.sunny.agoravideochat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.sunny.agoravideochat.R
import com.sunny.agoravideochat.broadcast.Receiver
import com.sunny.agoravideochat.models.MeetingInfo
import com.sunny.agoravideochat.ui.activities.AgoraVideoChatActivity
import com.sunny.agoravideochat.utils.Constants.MEETING_INFO


class AgoraVideoChatNotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {


        stopSelf()
    }

    override fun onDestroy() {
        stopForeground(true)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val notification = NotificationCompat
            .Builder(this, "meeting")
            .setOngoing(true)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Clapingo")
            .setContentText("Clapingo Meet")
            .addAction(R.drawable.close_round_icon, "End meeting", makePendingIntent("quit_action"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(false)
            .build()


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
            notificationManager.notify(0, notification)
        }
        startActivity(Intent(this, AgoraVideoChatActivity::class.java).apply {
            setFlags(FLAG_ACTIVITY_NEW_TASK)
            putExtra(MEETING_INFO, intent?.getParcelableExtra(MEETING_INFO) as MeetingInfo?)
        })


        return START_NOT_STICKY

    }

    private fun makePendingIntent(name: String?): PendingIntent? {
        val intent = Intent(this, Receiver::class.java)
        intent.action = name
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "ClapingoMeet"
        val adminChannel =
            NotificationChannel("meeting",
                adminChannelName,
                NotificationManager.IMPORTANCE_HIGH)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}