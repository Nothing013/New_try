package com.dashdazz.hyperglow.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dashdazz.hyperglow.model.ActiveLightingState
import com.dashdazz.hyperglow.ui.theme.LocalHyperGlow

@Composable
fun DashboardScreen(
    activeState: ActiveLightingState?,
    isNotifOnlyAmbient: Boolean,
    onNotifOnlyToggled: (Boolean) -> Unit,
    onNavigateToPriority: () -> Unit,
    onNavigateToCharging: () -> Unit,
    onNavigateToAutomations: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    val theme = LocalHyperGlow.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- REAL-TIME ANIMATED CAMERA RING HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(theme.cardBackground)
                .border(1.dp, theme.gradientBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "REDMI TURBO 4 PRO / POCO F7",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.primaryAccent,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Realistic Camera Module & RGB Ring Canvas
                AnimatedCameraRingCanvas(activeState = activeState)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = activeState?.description ?: if (isNotifOnlyAmbient) "Standby (Notif Only Baseline)" else "Ring Idle",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Active Source: ${activeState?.source?.name ?: "None"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // "Notif Only" Ambient Baseline Quick Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ambient Baseline: Notif Only",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Text(
                        text = "Ring stays dark during idle, but Lock, Audio, and Charging still trigger smoothly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = isNotifOnlyAmbient,
                    onCheckedChange = onNotifOnlyToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = theme.primaryAccent,
                        checkedTrackColor = theme.primaryAccent.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Grid to Subsystems
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickNavButton(
                modifier = Modifier.weight(1f),
                title = "Priority Matrix",
                subtitle = "6-Tier Non-Blocking",
                icon = Icons.Default.Layers,
                onClick = onNavigateToPriority
            )
            QuickNavButton(
                modifier = Modifier.weight(1f),
                title = "Charging Engine",
                subtitle = "Warp Pulse & Gauge",
                icon = Icons.Default.Bolt,
                onClick = onNavigateToCharging
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickNavButton(
                modifier = Modifier.weight(1f),
                title = "Automations",
                subtitle = "DND, Pocket & Night",
                icon = Icons.Default.Security,
                onClick = onNavigateToAutomations
            )
            QuickNavButton(
                modifier = Modifier.weight(1f),
                title = "Sequence Editor",
                subtitle = "Custom Animations",
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToEditor
            )
        }
    }
}

@Composable
fun AnimatedCameraRingCanvas(activeState: ActiveLightingState?) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.size(160.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.width / 2 - 10.dp.toPx()
        val innerLensRadius = outerRadius * 0.55f

        // Camera module dark glass background
        drawCircle(
            color = Color(0xFF141822),
            radius = outerRadius + 8.dp.toPx(),
            center = center
        )

        // Camera lens inner ring
        drawCircle(
            color = Color(0xFF07090E),
            radius = innerLensRadius,
            center = center
        )

        // Draw RGB Ring
        if (activeState != null) {
            val ringColor = Color(0xFF00F0FF) // Dynamic color
            drawCircle(
                color = ringColor.copy(alpha = pulseAlpha),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        } else {
            // Idle ring outline
            drawCircle(
                color = Color(0xFF232936),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}

@Composable
fun QuickNavButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val theme = LocalHyperGlow.current
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = theme.primaryAccent)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = Color.White)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
