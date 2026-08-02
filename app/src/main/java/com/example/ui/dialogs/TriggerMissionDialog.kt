package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MissionType
import com.example.ui.theme.*

@Composable
fun TriggerMissionDialog(
    onDismiss: () -> Unit,
    onTrigger: (title: String, type: MissionType, customTopic: String) -> Unit
) {
    var title by remember { mutableStateOf("Custom Orchestration Mission") }
    var customTopic by remember { mutableStateOf("Generative AI & Cloud Scaling") }
    var selectedType by remember { mutableStateOf(MissionType.SINGLE_POST) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ImmersiveSurface,
        title = {
            Text("Launch AetherOS Mission", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Configure parameters for the 7-Worker Kernel Pipeline.", color = ImmersiveTextMuted, fontSize = 12.sp)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Mission Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = customTopic,
                    onValueChange = { customTopic = it },
                    label = { Text("Topic / Prompt Focus") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Text("MISSION TYPE", color = AetherCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                Column {
                    listOf(MissionType.SINGLE_POST, MissionType.DAILY_PIPELINE, MissionType.CLI_MISSION).forEach { type ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(selectedColor = AetherCyan)
                            )
                            Text(type.label, color = ImmersiveTextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTrigger(title, selectedType, customTopic)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AetherIndigo)
            ) {
                Text("Dispatch Mission", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ImmersiveTextMuted)
            }
        }
    )
}
