package com.dashdazz.hyperglow.charging

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.dashdazz.hyperglow.model.ActiveLightingState
import com.dashdazz.hyperglow.model.LightingSource
import com.dashdazz.hyperglow.priority.PriorityManager
import kotlinx.coroutines.*

/**
 * TASK 2: Advanced Charging & Battery Indicator Engine.
 *
 * Features:
 * - Listens for ACTION_POWER_CONNECTED/DISCONNECTED and BatteryManager updates.
 * - Effect choices:
 *   1. "Pulse / Breathing": Speed matches charging rate (Warp/120W = fast pulse, Standard = slow harmonic).
 *   2. "Battery Fill Gauge": Segmented ring fill representing 0% to 100% level.
 *   3. "Dynamic Color Gradient": Smooth transition from Red (0-20% Low) -> Amber (21-65%) -> Cyan/Green (66-100% Full).
 * - Energy Efficient: Zero CPU tight loops! Updates light states strictly when battery percentage or status changes.
 */
class ChargingEngine(
    private val context: Context,
    private val priorityManager: PriorityManager,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "ChargingEngine"
        const val PRIORITY_CHARGING = 3
    }

    enum class ChargingEffectMode {
        PULSE_RATE,
        FILL_GAUGE,
        DYNAMIC_GRADIENT
    }

    var isEnabled: Boolean = true
    var effectMode: ChargingEffectMode = ChargingEffectMode.PULSE_RATE
    var isFullCelebrationEnabled: Boolean = true

    private var isCharging: Boolean = false
    private var batteryLevel: Int = 50
    private var isFastCharge: Boolean = false

    fun onPowerConnected() {
        isCharging = true
        refreshBatteryMetrics()
        dispatchChargingLighting()
    }

    fun onPowerDisconnected() {
        isCharging = false
        priorityManager.clearRequest(PRIORITY_CHARGING)
    }

    fun onBatteryChanged(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val newLevel = if (level >= 0 && scale > 0) (level * 100) / scale else batteryLevel
        val currentlyCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                status == BatteryManager.BATTERY_STATUS_FULL

        if (newLevel != batteryLevel || currentlyCharging != isCharging) {
            batteryLevel = newLevel
            isCharging = currentlyCharging
            if (isCharging) {
                dispatchChargingLighting()
            } else {
                priorityManager.clearRequest(PRIORITY_CHARGING)
            }
        }
    }

    private fun refreshBatteryMetrics() {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter) ?: return

        val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val isAcCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

        // HyperOS fast charge detection (> 3000mA or AC plug)
        isFastCharge = isAcCharge && (currentNow > 3000000 || currentNow < -3000000)
    }

    private fun dispatchChargingLighting() {
        if (!isEnabled || !isCharging) {
            priorityManager.clearRequest(PRIORITY_CHARGING)
            return
        }

        // Check Battery Full Alert
        if (batteryLevel >= 100 && isFullCelebrationEnabled) {
            val fullState = ActiveLightingState(
                source = LightingSource.BATTERY_FULL,
                effectId = "spectrum_wave",
                colors = listOf("#00FF66", "#00F0FF", "#B026FF"),
                speedMs = 1200,
                brightness = 100f,
                description = "Battery 100% Full Charged"
            )
            priorityManager.postRequest(PRIORITY_CHARGING, fullState)
            return
        }

        val lightingState = when (effectMode) {
            ChargingEffectMode.PULSE_RATE -> {
                // Warp/Fast Charge = faster pulse (e.g. 1000ms vs 2600ms)
                val pulseSpeed = if (isFastCharge) 900 else 2200
                val color = calculateGradientColor(batteryLevel)
                ActiveLightingState(
                    source = LightingSource.CHARGING,
                    effectId = "breathing_pulse",
                    colors = listOf(color),
                    speedMs = pulseSpeed,
                    brightness = 90f,
                    description = if (isFastCharge) "HyperCharge ${batteryLevel}%" else "Charging ${batteryLevel}%"
                )
            }
            ChargingEffectMode.FILL_GAUGE -> {
                // Segmented ring gauge fill representing 0% to 100%
                val color = calculateGradientColor(batteryLevel)
                ActiveLightingState(
                    source = LightingSource.CHARGING,
                    effectId = "battery_gauge",
                    colors = listOf(color),
                    speedMs = 0, // static gauge position, zero CPU consumption
                    brightness = 85f,
                    gaugeFillPercent = batteryLevel.toFloat(),
                    description = "Fill Gauge: ${batteryLevel}%"
                )
            }
            ChargingEffectMode.DYNAMIC_GRADIENT -> {
                // Smooth transition Red -> Amber -> Cyan/Green
                val primaryColor = calculateGradientColor(batteryLevel)
                ActiveLightingState(
                    source = LightingSource.CHARGING,
                    effectId = "smooth_wave",
                    colors = listOf(primaryColor, "#00F0FF"),
                    speedMs = 2800,
                    brightness = 95f,
                    description = "Gradient ${batteryLevel}%"
                )
            }
        }

        priorityManager.postRequest(PRIORITY_CHARGING, lightingState)
    }

    private fun calculateGradientColor(level: Int): String {
        return when {
            level <= 20 -> "#FF3B30" // Low battery: Red
            level <= 60 -> "#FF9500" // Mid level: Electric Amber
            level <= 90 -> "#00F0FF" // Good level: Neon Cyan
            else -> "#00FF66"        // High/Full: Hyper Green
        }
    }
}
