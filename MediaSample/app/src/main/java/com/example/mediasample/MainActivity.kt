package com.example.mediasample

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = MediaPlayer()
        val mediaFileUriStr = "android.resource://com.example.mediasample/${R.raw.bird_singing}"
        val mediaFileUri = Uri.parse(mediaFileUriStr)

        player?.let {
            it.setDataSource(this@MainActivity, mediaFileUri)
            it.setOnPreparedListener(PlayerPreparedListener())
            it.setOnCompletionListener(PlayerCompletionListener())
            it.prepareAsync()
        }

        val loopSwitch = findViewById<SwitchMaterial>(R.id.swLoop)
        loopSwitch.setOnCheckedChangeListener(LoopSwitchChangeLister())
    }

    override fun onStop() {
        player?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        super.onStop()
    }

    private inner class PlayerPreparedListener: MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            val btPlay = findViewById<Button>(R.id.btPlay)
            btPlay.isEnabled = true
            val btBack = findViewById<Button>(R.id.btBack)
            btBack.isEnabled = true
            val btForward = findViewById<Button>(R.id.btForward)
            btForward.isEnabled = true
        }
    }

    private inner class PlayerCompletionListener: MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
            player?.let {
                if (!it.isLooping) {
                    val btPlay = findViewById<Button>(R.id.btPlay)
                    btPlay.setText(R.string.bt_play_play)
                }
            }
        }
    }

    private inner class LoopSwitchChangeLister: CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            player?.isLooping = isChecked
        }
    }

    fun onPlayButtonClick(view: View){
        player?.let {
            val btPlay = findViewById<Button>(R.id.btPlay)
            if(it.isPlaying) {
                it.pause()
                btPlay.setText(R.string.bt_play_play)
            }
            else {
                it.start()
                btPlay.setText(R.string.bt_play_pause)
            }
        }
    }

    fun onBackButtonClick(view: View){
        player?.seekTo(0)
    }

    fun onForwardButtonClick(view: View) {
        player?.let {
            val duration = it.duration
            player?.seekTo(duration)
            if (!it.isPlaying) {
                val btPlay = findViewById<Button>(R.id.btPlay)
                btPlay.setText(R.string.bt_play_pause)
                it.start()
            }
        }
    }
}