package com.dashdazz.hyperglow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dashdazz.hyperglow.ui.theme.LocalHyperGlow

data class PriorityTierItem(
    val priority: Int,
    val title: String,
    val subtitle: String,
    val isTemporary: Boolean,
    val color: Color
)

@Composable
fun PriorityMatrixScreen(
    onBackPressed: () -> Unit
) {
    val theme = LocalHyperGlow.current

    val tiers = listOf(
        PriorityTierItem(1, "Priority 1: Notification Alerts", "High-priority pulse/strobe. Overrides all while unread.", true, Color(0xFFFF0055)),
        PriorityTierItem(2, "Priority 2: Lock / Unlock Screen Flash", "Interactive transient burst on display power/unlock.", true, Color(0xFFFFB800)),
        PriorityTierItem(3, "Priority 3: Charging Animations & Battery Full", "Continuous warp pulse or gauge fill while plugged in.", false, Color(0xFF00F0FF)),
        PriorityTierItem(4, "Priority 4: Special System Alerts", "Pocket Mode proximity mute & low battery emergency flash.", false, Color(0xFFB026FF)),
        PriorityTierItem(5, "Priority 5: React to Audio", "Real-time equalizer amplitude modulation on music playback.", false, Color(0xFF00FF66)),
        PriorityTierItem(6, "Priority 6: Ambient / Baseline Glow", "Fallback idle animation or OFF when Notif Only is active.", false, Color(0xFF3B82F6))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Priority Matrix Engine", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(text = "Non-blocking concurrent trigger resolution", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tiers.size) { index ->
                val tier = tiers[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, tier.color.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = tier.color.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "P${tier.priority}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tier.color
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = tier.title, style = MaterialTheme.typography.titleSmall, color = Color.White)
                            Text(text = tier.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        if (tier.isTemporary) {
                            Badge(containerColor = Color(0xFF263238), contentColor = Color(0xFF80CBC4)) {
                                Text("Auto-Revert")
                            }
                        }
                    }
                }
            }
        }
    }
}
