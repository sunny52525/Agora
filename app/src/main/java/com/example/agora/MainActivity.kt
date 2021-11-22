package com.example.agora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.sunny.agoravideochat.lib.AgoraVideoChat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        findViewById<Button>(R.id.join_meeting_button).setOnClickListener {
            val name = findViewById<EditText>(R.id.name).text.toString()
            val channel = findViewById<EditText>(R.id.channel_name).text.toString()


            AgoraVideoChat().apply {
                init(
                    appId = "0c825a804e914943a32b5bac902ae2a6",
                    channelName = channel.trim(),
                    uid = "uid1",
                    token = null,
                    context = this@MainActivity,
                    isLearner = true,
                    name = name,
                    channelNameOfRTC =channel.trim(),
                    sessionEndTimeInUTC = addMinutesToTimeInMillis(5)
                )

                startVideoChat()
            }
        }



    }

    fun addMinutesToTimeInMillis(minute: Int): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentTime = Calendar.getInstance()
        val diff = currentTime.timeInMillis - utc.timeInMillis
        println(System.currentTimeMillis() - diff + (minute * 60 * 1000))
        return System.currentTimeMillis() - diff + (minute * 60 * 1000)

    }

}