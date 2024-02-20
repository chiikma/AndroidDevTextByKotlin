package com.example.servicesample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SoundManagerService : Service() {

    companion object {
        private const val CHANNEL_ID = "notification_channel"
    }

    private var player: MediaPlayer? = null

    override fun onCreate() {
        player = MediaPlayer()

        val manager = getSystemService(NotificationManager::class.java)
        val name = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notifyDescription = "お知らせを通知します"

        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.apply {
                description = notifyDescription
            }
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val mediaFileUriStr = "android.resource://${packageName}/${R.raw.bird_singing}"
        val mediaFileUri = Uri.parse(mediaFileUriStr)

        player?.let {
            it.setDataSource(this@SoundManagerService, mediaFileUri)
            it.setOnPreparedListener(PlayerPreparedListener())
            it.setOnCompletionListener(PlayerCompletionListener())
            it.prepareAsync()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
    }

    private inner class PlayerPreparedListener : MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            // メディアを再生。
            mp.start()

            val intent = Intent(this@SoundManagerService, MainActivity::class.java)
            intent.putExtra("fromNotification", true)
            val stopServiceIntent = PendingIntent.getActivity(this@SoundManagerService, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            // Notificationを作成するBuilderクラス生成。
            val notification = NotificationCompat.Builder(this@SoundManagerService, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.msg_notification_title_start))
                .setContentText(getString(R.string.msg_notification_text_start))
                .setContentIntent(stopServiceIntent)
                .setAutoCancel(true)
                .build()

            startForeground(1,  notification)
        }
    }

    private inner class PlayerCompletionListener: MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
            val manager = NotificationManagerCompat.from(this@SoundManagerService)
            val notification = NotificationCompat.Builder(this@SoundManagerService, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.msg_notification_title_stop))
                .setContentText(getString(R.string.msg_notification_text_stop))
                .build()
            manager.notify(100, notification)
            stopSelf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}

