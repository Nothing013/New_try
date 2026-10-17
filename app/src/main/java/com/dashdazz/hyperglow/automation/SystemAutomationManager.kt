package com.dashdazz.hyperglow.automation

import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.util.Log
import com.dashdazz.hyperglow.priority.PriorityManager
import java.util.Calendar

/**
 * TASK 3: Additional System Automations & Triggers.
 *
 * 1. DND / Silent Mode Sync:
 *    Dims (to 25%) or completely mutes ring lighting when Do Not Disturb or Silent mode is active.
 * 2. Pocket / Proximity Guard:
 *    Monitors proximity sensor to temporarily mute ring lighting when the device is face-down
 *    or placed inside a pocket/bag.
 * 3. Low-Power / Night Schedule Mode:
 *    Configurable start/end quiet hours (e.g., 23:00 to 07:00) to shut off lighting during sleep.
 */
class SystemAutomationManager(
    private val context: Context,
    private val priorityManager: PriorityManager
) : SensorEventListener {

    companion object {
        private const val TAG = "AutomationManager"
    }

    // Config options
    var isDndSyncEnabled: Boolean = true
    var dndActionMute: Boolean = false // false = dim to 25%, true = mute completely
    var isPocketGuardEnabled: Boolean = true
    var isNightScheduleEnabled: Boolean = false
    var nightStartHour: Int = 23
    var nightStartMinute: Int = 0
    var nightEndHour: Int = 7
    var nightEndMinute: Int = 0

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    fun start() {
        if (isPocketGuardEnabled && proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        checkDndState()
        checkNightSchedule()
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Check DND (ZenMode) or Ringer Mode
     */
    fun checkDndState() {
        if (!isDndSyncEnabled) {
            priorityManager.isDndMuted = false
            priorityManager.isDndDimmed = false
            return
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val isDnd = notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
        val isSilent = audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT ||
                       audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE

        if (isDnd || isSilent) {
            if (dndActionMute) {
                priorityManager.isDndMuted = true
                priorityManager.isDndDimmed = false
            } else {
                priorityManager.isDndMuted = false
                priorityManager.isDndDimmed = true
                priorityManager.dndDimFactor = 0.25f
            }
        } else {
            priorityManager.isDndMuted = false
            priorityManager.isDndDimmed = false
        }
    }

    /**
     * Check if current time falls into quiet hours schedule
     */
    fun checkNightSchedule(): Boolean {
        if (!isNightScheduleEnabled) return false

        val cal = Calendar.getInstance()
        val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val startMinutes = nightStartHour * 60 + nightStartMinute
        val endMinutes = nightEndHour * 60 + nightEndMinute

        val isInQuietHours = if (startMinutes > endMinutes) {
            // Overnights (e.g. 23:00 to 07:00)
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        } else {
            currentMinutes in startMinutes until endMinutes
        }

        if (isInQuietHours) {
            priorityManager.isDndMuted = true
        }
        return isInQuietHours
    }

    // Proximity Sensor Callback
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !isPocketGuardEnabled) return

        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = proximitySensor?.maximumRange ?: 5f
            val isCovered = distance < 3.0f || distance < maxRange

            // When covered in pocket or face down, mute ring lighting
            priorityManager.isPocketGuardMuted = isCovered
            Log.d(TAG, "Proximity distance: $distance cm, Pocket Mute: $isCovered")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
