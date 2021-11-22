package com.sunny.agoravideochat.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val whichAction: String? = intent.action
        when (whichAction) {
            "quit_action" -> {
                val i = Intent("com.app.clapingo.endmeet")
                context?.sendBroadcast(i)
                return
            }
        }
    }

}