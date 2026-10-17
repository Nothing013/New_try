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
fun AutomationsScreen(
    onBackPressed: () -> Unit
) {
    val theme = LocalHyperGlow.current
    var dndSync by remember { mutableStateOf(true) }
    var dndMute by remember { mutableStateOf(false) } // false = dim, true = mute
    var pocketGuard by remember { mutableStateOf(true) }
    var nightSchedule by remember { mutableStateOf(true) }

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
                Text(text = "System Automations", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(text = "Sensors, DND sync & scheduled quiet hours", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DND Sync Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DND / Silent Mode Sync", style = MaterialTheme.typography.titleSmall, color = Color.White)
                        Text("Dim or disable LED when Do Not Disturb or Silent is active", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Switch(checked = dndSync, onCheckedChange = { dndSync = it })
                }
                if (dndSync) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !dndMute, onClick = { dndMute = false })
                        Text("Dim Brightness to 25%", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = dndMute, onClick = { dndMute = true })
                        Text("Mute Ring Completely", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pocket & Proximity Guard Card
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
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pocket & Proximity Guard", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Auto-mutes ring when face-down on desk or inside pocket to save battery.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Switch(checked = pocketGuard, onCheckedChange = { pocketGuard = it })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Low Power / Night Schedule Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Night Quiet Schedule", style = MaterialTheme.typography.titleSmall, color = Color.White)
                        Text("Automatically shut off ring lighting during sleep hours", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Switch(checked = nightSchedule, onCheckedChange = { nightSchedule = it })
                }
                if (nightSchedule) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Quiet Hours: 23:00 (11:00 PM) → 07:00 (7:00 AM)", color = theme.primaryAccent, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
