package com.example.servicesample

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // POST_NOTIFICATIONSの許可が下りていないなら…
		if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
			val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
			ActivityCompat.requestPermissions(this@MainActivity, permissions, 1000)
			return
		}

        val fromNotification = intent.getBooleanExtra("fromNotification", false)
        if(fromNotification) {
            val btPlay = findViewById<Button>(R.id.btPlay)
            val btStop = findViewById<Button>(R.id.btStop)
            btPlay.isEnabled = false
            btStop.isEnabled = true
        }
    }

    fun onPlayButtonClick(view: View){
        val serviceIntent = Intent(this@MainActivity, SoundManagerService::class.java)
        ContextCompat.startForegroundService(this@MainActivity, serviceIntent)

        val btPlay = findViewById<Button>(R.id.btPlay)
        val btStop = findViewById<Button>(R.id.btStop)
        btPlay.isEnabled = false
        btStop.isEnabled = true
    }

    fun onStopButtonClick(view: View){
        val intent = Intent(this@MainActivity, SoundManagerService::class.java)
        stopService(intent)

        val btPlay = findViewById<Button>(R.id.btPlay)
        val btStop = findViewById<Button>(R.id.btStop)
        btPlay.isEnabled = true
        btStop.isEnabled = false
    }
}

