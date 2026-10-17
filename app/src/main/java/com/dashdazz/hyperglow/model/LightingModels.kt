package com.dashdazz.hyperglow.model

enum class LightingSource {
    NOTIFICATION,
    SCREEN_EVENT,
    CHARGING,
    BATTERY_FULL,
    SYSTEM_ALERT,
    AUDIO_REACT,
    AMBIENT_BASELINE
}

data class ActiveLightingState(
    val source: LightingSource,
    val effectId: String,
    val colors: List<String>,
    val speedMs: Int,
    val brightness: Float,
    val gaugeFillPercent: Float? = null,
    val description: String = ""
)

data class SequenceStepItem(
    val id: String,
    val color: String,
    val durationMs: Int,
    val transition: String, // "instant", "fade", "pulse", "strobe"
    val brightness: Float = 100f
)

data class CustomAnimationSequence(
    val id: String,
    val name: String,
    val steps: List<SequenceStepItem>,
    val repeatCount: Int = 0 // 0 = infinite
)
