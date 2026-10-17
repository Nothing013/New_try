package com.dashdazz.hyperglow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PowerStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("PowerStateReceiver", "Action received: ${intent?.action}")
    }
}
