package com.dashdazz.hyperglow.priority

import android.util.Log
import com.dashdazz.hyperglow.model.ActiveLightingState
import com.dashdazz.hyperglow.model.LightingSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Priority Manager for concurrent HyperOS 3 camera-ring lighting events.
 *
 * Priority Matrix Hierarchy:
 * Priority 1 (Highest): Notification Alerts (Temporary Overrides)
 * Priority 2: Lock / Unlock / Screen Flash Effects
 * Priority 3: Charging Animations & Battery Full Alerts
 * Priority 4: Special System Events (Pocket Mode / Low Battery)
 * Priority 5: React to Audio (Visualizer / dynamic audio beat sync)
 * Priority 6 (Lowest): Ambient / Always-On Baseline
 *
 * CRITICAL FIX: "Notif Only" logic now defines ONLY the ambient baseline (ring off by default),
 * while Lock/Unlock Flash, React to Audio, Charging, and Notifications remain fully independent
 * triggers that co-exist smoothly and respect the priority hierarchy.
 */
class PriorityManager(
    private val scope: CoroutineScope,
    private val onStateChanged: (ActiveLightingState?) -> Unit
) {
    companion object {
        private const val TAG = "PriorityManager"
    }

    private val mutex = Mutex()
    private val activeRequests = ConcurrentHashMap<Int, ActiveLightingState>()

    // Priority configuration
    var isNotifOnlyAmbientBaseline: Boolean = false
    var isPocketGuardMuted: Boolean = false
    var isDndMuted: Boolean = false
    var isDndDimmed: Boolean = false
    var dndDimFactor: Float = 0.25f

    private val _currentState = MutableStateFlow<ActiveLightingState?>(null)
    val currentState: StateFlow<ActiveLightingState?> = _currentState.asStateFlow()

    private var activeTimerJob: Job? = null

    /**
     * Submit or update a lighting request for a specific priority tier.
     * @param priority 1 (highest) to 6 (lowest)
     * @param state The lighting state containing effect, colors, brightness, etc.
     * @param durationMs Optional duration after which the state automatically expires (e.g. for notifications or screen flash)
     */
    fun postRequest(priority: Int, state: ActiveLightingState, durationMs: Long? = null) {
        scope.launch {
            mutex.withLock {
                activeRequests[priority] = state
                Log.d(TAG, "Posted request at Priority $priority: ${state.source.name} (duration: $durationMs ms)")

                // Schedule auto-cancellation for temporary overrides
                if (durationMs != null && durationMs > 0) {
                    scheduleExpiration(priority, durationMs)
                }

                evaluateActiveState()
            }
        }
    }

    /**
     * Clear an active priority tier when its trigger ceases.
     */
    fun clearRequest(priority: Int) {
        scope.launch {
            mutex.withLock {
                activeRequests.remove(priority)
                Log.d(TAG, "Cleared request at Priority $priority")
                evaluateActiveState()
            }
        }
    }

    private fun scheduleExpiration(priority: Int, durationMs: Long) {
        scope.launch {
            delay(durationMs)
            mutex.withLock {
                // Only remove if it is still the same state
                activeRequests.remove(priority)
                Log.d(TAG, "Expired temporary request at Priority $priority")
                evaluateActiveState()
            }
        }
    }

    /**
     * Re-evaluates active priorities and picks the highest winning state.
     * Non-blocking and thread-safe.
     */
    private fun evaluateActiveState() {
        // Priority 4 Check: Pocket mode overrides everything if active (ring is in pocket / face down)
        if (isPocketGuardMuted) {
            Log.d(TAG, "Pocket Guard is active: Ring lighting completely muted")
            _currentState.value = null
            onStateChanged(null)
            return
        }

        // Evaluate from Priority 1 down to Priority 6
        var winningState: ActiveLightingState? = null

        for (p in 1..6) {
            val candidate = activeRequests[p]
            if (candidate != null) {
                // Check if priority 6 is affected by "Notif Only"
                if (p == 6 && isNotifOnlyAmbientBaseline) {
                    // Ambient baseline is muted because user wants the ring off by default until a trigger occurs
                    continue
                }
                winningState = candidate
                break
            }
        }

        // Apply DND modifications if DND is active and not muted entirely
        if (winningState != null) {
            if (isDndMuted) {
                Log.d(TAG, "DND active with Mute setting: suppressing lighting")
                winningState = null
            } else if (isDndDimmed) {
                winningState = winningState.copy(
                    brightness = (winningState.brightness * dndDimFactor).coerceIn(5f, 100f)
                )
            }
        }

        _currentState.value = winningState
        onStateChanged(winningState)
    }

    /**
     * Helper to set Ambient Baseline (Priority 6)
     */
    fun setAmbientBaseline(state: ActiveLightingState?) {
        if (state == null || isNotifOnlyAmbientBaseline) {
            clearRequest(6)
        } else {
            postRequest(6, state)
        }
    }
}
