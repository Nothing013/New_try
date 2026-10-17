package com.dashdazz.hyperglow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ScreenStateReceiver", "Screen state changed: ${intent?.action}")
    }
}
