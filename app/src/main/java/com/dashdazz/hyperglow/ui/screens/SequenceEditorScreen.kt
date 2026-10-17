package com.dashdazz.hyperglow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dashdazz.hyperglow.model.SequenceStepItem
import com.dashdazz.hyperglow.ui.theme.LocalHyperGlow

@Composable
fun SequenceEditorScreen(
    onBackPressed: () -> Unit
) {
    val theme = LocalHyperGlow.current
    val steps = remember {
        mutableStateListOf(
            SequenceStepItem("1", "#00F0FF", 400, "pulse"),
            SequenceStepItem("2", "#B026FF", 400, "fade"),
            SequenceStepItem("3", "#FF007F", 300, "strobe")
        )
    }

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
                Text(text = "Custom Sequence Builder", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(text = "Design multi-step animated RGB choreography", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(steps.size) { index ->
                val step = steps[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = theme.cardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "#${index + 1}", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(step.color)))
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${step.transition.uppercase()} · ${step.durationMs}ms", color = Color.White, style = MaterialTheme.typography.titleSmall)
                            Text(text = step.color, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { if (steps.size > 1) steps.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                steps.add(SequenceStepItem(java.util.UUID.randomUUID().toString(), "#00FF66", 500, "fade"))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = theme.primaryAccent)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Step to Timeline", color = Color.Black)
        }
    }
}
