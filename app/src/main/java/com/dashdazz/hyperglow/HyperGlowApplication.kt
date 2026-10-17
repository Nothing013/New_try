package com.dashdazz.hyperglow

import android.app.Application
import android.util.Log

class HyperGlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("HyperGlowApp", "HyperGlow Application initialized for Xiaomi HyperOS 3")
    }
}
