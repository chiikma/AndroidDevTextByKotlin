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


class MainActivity : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // POST_NOTIFICATIONSの許可が下りていないなら…
		if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
			// 許可をPOST_NOTIFICATIONSに設定。
			val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
			// 許可を求めるダイアログを表示。その際、リクエストコードを1000に設定。
			ActivityCompat.requestPermissions(this@MainActivity, permissions, 1000)
			// onCreate()メソッドを終了。
			return
		}
    }

    fun onPlayButtonClick(view: View){
        val intent = Intent(this@MainActivity, SoundManagerService::class.java)
        startService(intent)

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

