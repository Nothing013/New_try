package com.dashdazz.hyperglow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dashdazz.hyperglow.ui.theme.LocalHyperGlow

@Composable
fun ChargingEngineScreen(
    onBackPressed: () -> Unit
) {
    val theme = LocalHyperGlow.current
    var selectedMode by remember { mutableStateOf(0) } // 0: Pulse, 1: Gauge, 2: Gradient
    var isEnabled by remember { mutableStateOf(true) }
    var fullCelebration by remember { mutableStateOf(true) }

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
                Text(text = "Adaptive Charging Engine", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(text = "Power & battery percentage sync", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Enable Charging Lighting", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text("Triggers automatically on charger connection", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = theme.primaryAccent)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Effect Mode Selector
        Text("Effect Choice:", style = MaterialTheme.typography.titleSmall, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        val modes = listOf(
            "Pulse / Breathing (Rate matches Wattage)",
            "Battery Fill Gauge (Segmented 0-100%)",
            "Dynamic Gradient (Red -> Amber -> Cyan/Green)"
        )

        modes.forEachIndexed { index, modeTitle ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = { selectedMode = index },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedMode == index) theme.primaryAccent.copy(alpha = 0.15f) else theme.cardBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMode == index,
                        onClick = { selectedMode = index },
                        colors = RadioButtonDefaults.colors(selectedColor = theme.primaryAccent)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(modeTitle, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery 100% Full Alert
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("100% Full Celebration Burst", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Spectrum wave flash when battery completes charge", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Checkbox(
                    checked = fullCelebration,
                    onCheckedChange = { fullCelebration = it },
                    colors = CheckboxDefaults.colors(checkedColor = theme.primaryAccent)
                )
            }
        }
    }
}
