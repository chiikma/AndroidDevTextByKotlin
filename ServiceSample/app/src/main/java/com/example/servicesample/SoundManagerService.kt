package com.example.servicesample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SoundManagerService : Service() {

    companion object {
        private const val CHANNEL_ID = "notification_channel"
    }

    private var player: MediaPlayer? = null

    override fun onCreate() {
        player = MediaPlayer()

        // 通知チャネル名をstrings.xmlから取得。
        val name = getString(R.string.notification_channel_name)
        // 通知チャネルの重要度を標準に設定。
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        // 通知チャネルを生成。
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        // NotificationManagerオブジェクトを取得。
        val manager = getSystemService(NotificationManager::class.java)
        // 通知チャネルを設定。
        manager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mediaFileUriStr = "android.resource://com.example.servicesample/${R.raw.bird_singing}"
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

    private inner class PlayerPreparedListener: MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            mp.start()
        }
    }

    private inner class PlayerCompletionListener: MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
            // Notificationを作成するBuilderクラス生成。
            val builder = NotificationCompat.Builder(this@SoundManagerService, CHANNEL_ID)
            // 通知エリアに表示されるアイコンを設定。
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            // 通知ドロワーでの表示タイトルを設定。
            builder.setContentTitle(getString(R.string.msg_notification_title_stop))
            // 通知ドロワーでの表示メッセージを設定。
            builder.setContentText(getString(R.string.msg_notification_text_stop))
            // BuilderからNotificationオブジェクトを生成。
            val notification = builder.build()
            // NotificationManagerCompatオブジェクトを取得。
            val manager = NotificationManagerCompat.from(this@SoundManagerService)
            // 通知。
            manager.notify(100, notification)
            // 自分自身を終了。
            stopSelf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}

