package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun KernelScreen(
    activeMission: Mission?,
    missions: List<Mission>,
    dailyPipelineEnabled: Boolean,
    onToggleDailyPipeline: () -> Unit,
    onTriggerMission: () -> Unit,
    onOpenPostDetail: (PostItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Kernel Mission Control", color = ImmersiveTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Task Planner • Scheduler • Pipeline Runner", color = ImmersiveTextMuted, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onTriggerMission,
                        colors = ButtonDefaults.buttonColors(containerColor = AetherIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Mission", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Daily Pipeline Toggle Control
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AetherIndigo.copy(alpha = 0.4f))),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AetherIndigo.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = AetherIndigo)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("8:00 AM Daily Pipeline", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Target: We Share We Care (FB v22)", color = ImmersiveTextMuted, fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = dailyPipelineEnabled,
                            onCheckedChange = { onToggleDailyPipeline() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AetherEmerald
                            )
                        )
                    }
                }
            }

            // Active Mission Execution Card
            activeMission?.let { mission ->
                item {
                    Text("ACTIVE MISSION EXECUTION", color = AetherAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AetherAmber)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mission.title, color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                StatusBadge(mission.status)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Steps breakdown
                            mission.steps.forEach { step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StepIcon(step.status)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${step.stepIndex}. ${step.stepName}", color = ImmersiveTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(step.detail, color = ImmersiveTextMuted, fontSize = 11.sp)
                                    }
                                    Text(step.workerType.title.take(8), color = AetherCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // View Post Button if completed
                            mission.generatedPost?.let { post ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onOpenPostDetail(post) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AetherEmerald)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View Generated Post", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Mission History
            item {
                Text("MISSION HISTORY", color = ImmersiveTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            if (missions.isEmpty() && activeMission == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No past missions yet. Click 'New Mission' to start!", color = ImmersiveTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(missions) { mission ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                mission.generatedPost?.let { onOpenPostDetail(it) }
                            }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(mission.title, color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(formatTime(mission.startTime), color = ImmersiveTextMuted, fontSize = 11.sp)
                                }
                                StatusBadge(mission.status)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: MissionStatus) {
    val color = when (status) {
        MissionStatus.COMPLETED -> AetherEmerald
        MissionStatus.EXECUTING -> AetherAmber
        MissionStatus.PLANNING -> AetherIndigo
        MissionStatus.FAILED -> AetherRose
        else -> ImmersiveTextMuted
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status.label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StepIcon(status: MissionStatus) {
    val (icon, color) = when (status) {
        MissionStatus.COMPLETED -> Icons.Default.CheckCircle to AetherEmerald
        MissionStatus.EXECUTING -> Icons.Default.Refresh to AetherAmber
        MissionStatus.FAILED -> Icons.Default.Error to AetherRose
        else -> Icons.Default.RadioButtonUnchecked to ImmersiveTextMuted
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(18.dp)
    )
}

private fun formatTime(timeMillis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return sdf.format(Date(timeMillis))
}
