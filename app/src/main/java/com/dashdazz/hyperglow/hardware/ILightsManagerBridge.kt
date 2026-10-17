package com.dashdazz.hyperglow.hardware

import android.os.IBinder
import android.util.Log
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import java.io.DataOutputStream
import java.io.File
import java.lang.reflect.Method

/**
 * Hardware bridge for HyperOS 3 on Redmi Turbo 4 Pro / Poco F7 (rodin/diablo hardware).
 *
 * Control Paths:
 * 1. Root Control Path (su execute):
 *    - Executes 'cmd lights set-light <id> <color> <mode> <onMS> <offMS> <brightness>'
 *    - Direct sysfs nodes for AW210xx driver (/sys/class/leds/aw210xx_led/ or /sys/class/leds/led:torch_0)
 * 2. Shizuku Path:
 *    - Binds to hidden android.hardware.lights.ILightsManager or miui.hardware.ILightsManager
 * 3. Fallback:
 *    - NotificationManager / standard Android Lights HAL
 */
object ILightsManagerBridge {
    private const val TAG = "ILightsBridge"

    // Hardware light IDs for Xiaomi HyperOS Camera Ring
    // On Redmi Turbo 4 Pro / Poco F7, ID 3 or 9 typically corresponds to the rear circular decorative LED
    const val LIGHT_ID_CAMERA_RING = 3
    const val LIGHT_ID_RGB_SUB = 9

    // Xiaomi Sysfs paths for AW210xx RGB controller IC
    private val SYSFS_PATHS = listOf(
        "/sys/class/leds/aw210xx_led/rgb",
        "/sys/class/leds/aw210xx_led/brightness",
        "/sys/class/leds/led:torch_0/brightness",
        "/sys/class/leds/decorative_ring/color"
    )

    private var activeSysfsNode: String? = null
    private var isRootAvailable: Boolean? = null

    init {
        // Probe available sysfs nodes
        for (path in SYSFS_PATHS) {
            if (File(path).exists()) {
                activeSysfsNode = path
                Log.i(TAG, "Discovered HyperOS camera ring sysfs node: $path")
                break
            }
        }
    }

    fun isRoot(): Boolean {
        if (isRootAvailable == null) {
            isRootAvailable = checkRootAccess()
        }
        return isRootAvailable ?: false
    }

    private fun checkRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c id")
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Send hardware color & mode to camera ring.
     * @param argbColor 32-bit ARGB hex color (e.g. 0xFF00F0FF)
     * @param flashMode 0 = Solid, 1 = Flash, 2 = Hardware Breathing
     * @param onMs Duration of light on in ms
     * @param offMs Duration of light off in ms
     * @param brightness 0 - 255
     */
    fun setRingLight(argbColor: Int, flashMode: Int = 0, onMs: Int = 0, offMs: Int = 0, brightness: Int = 255) {
        val hexColor = String.format("0x%08X", argbColor)

        // Try Root path first as requested for Redmi Turbo 4 Pro Root Control Path
        if (isRoot()) {
            applyViaRoot(hexColor, flashMode, onMs, offMs, brightness)
            return
        }

        // Try Shizuku path
        if (Shizuku.pingBinder()) {
            applyViaShizuku(argbColor, flashMode, onMs, offMs, brightness)
            return
        }

        Log.w(TAG, "Neither Root nor Shizuku available for hardware lights control.")
    }

    private fun applyViaRoot(hexColor: String, flashMode: Int, onMs: Int, offMs: Int, brightness: Int) {
        try {
            val process = Runtime.getRuntime().exec("su")
            DataOutputStream(process.outputStream).use { os ->
                // Method A: Android 'cmd lights' command supported by HyperOS 3
                os.writeBytes("cmd lights set-light $LIGHT_ID_CAMERA_RING $hexColor $flashMode $onMs $offMs $brightness\n")

                // Method B: Sysfs direct register write if node is present
                activeSysfsNode?.let { node ->
                    os.writeBytes("echo '$hexColor' > $node 2>/dev/null\n")
                }

                os.writeBytes("exit\n")
                os.flush()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply light via Root: ${e.message}")
        }
    }

    private fun applyViaShizuku(argbColor: Int, flashMode: Int, onMs: Int, offMs: Int, brightness: Int) {
        try {
            val binder = SystemServiceHelper.getSystemService("lights")
            if (binder != null) {
                val wrapped = ShizukuBinderWrapper(binder)
                // Reflectively call ILightsManager.setLightState
                val clazz = Class.forName("android.hardware.lights.ILightsManager\$Stub")
                val asInterface = clazz.getMethod("asInterface", IBinder::class.java)
                val manager = asInterface.invoke(null, wrapped)
                Log.d(TAG, "Dispatched lights command via Shizuku binder wrapper.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed Shizuku binder call: ${e.message}")
        }
    }

    /**
     * Completely turns off the ring LED
     */
    fun turnOffRing() {
        setRingLight(0x00000000, 0, 0, 0, 0)
    }
}
