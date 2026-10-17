package com.dashdazz.hyperglow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.dashdazz.hyperglow.model.ActiveLightingState
import com.dashdazz.hyperglow.ui.screens.*
import com.dashdazz.hyperglow.ui.theme.HyperGlowTheme
import com.dashdazz.hyperglow.ui.theme.NeonCyan

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentScreen by remember { mutableStateOf("dashboard") }
            var isNotifOnlyAmbient by remember { mutableStateOf(false) }
            val activeState by remember { mutableStateOf<ActiveLightingState?>(null) }

            HyperGlowTheme(accentColor = NeonCyan, isPureAmoled = true) {
                when (currentScreen) {
                    "dashboard" -> DashboardScreen(
                        activeState = activeState,
                        isNotifOnlyAmbient = isNotifOnlyAmbient,
                        onNotifOnlyToggled = { isNotifOnlyAmbient = it },
                        onNavigateToPriority = { currentScreen = "priority" },
                        onNavigateToCharging = { currentScreen = "charging" },
                        onNavigateToAutomations = { currentScreen = "automations" },
                        onNavigateToEditor = { currentScreen = "editor" }
                    )
                    "priority" -> PriorityMatrixScreen(onBackPressed = { currentScreen = "dashboard" })
                    "charging" -> ChargingEngineScreen(onBackPressed = { currentScreen = "dashboard" })
                    "automations" -> AutomationsScreen(onBackPressed = { currentScreen = "dashboard" })
                    "editor" -> SequenceEditorScreen(onBackPressed = { currentScreen = "dashboard" })
                }
            }
        }
    }
}
