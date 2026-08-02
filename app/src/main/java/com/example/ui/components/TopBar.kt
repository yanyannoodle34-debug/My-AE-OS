package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Mission
import com.example.data.MissionStatus
import com.example.ui.theme.*

@Composable
fun TopBar(
    kernelActive: Boolean,
    dailyPipelineEnabled: Boolean,
    activeMission: Mission?,
    currentUser: String = "admin",
    onLogout: () -> Unit = {},
    onTriggerQuickRun: () -> Unit,
    onToggleDailyPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ImmersiveSurface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title & System Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AetherIndigo.copy(alpha = 0.2f))
                            .border(1.dp, AetherIndigo, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Æ", color = AetherCyan, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AetherOS",
                                color = ImmersiveTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AetherCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v2.4",
                                    color = AetherCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Text(
                            text = "Op: @$currentUser • 7 Workers",
                            color = ImmersiveTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Action controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Kernel Status Indicator
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ImmersiveSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (activeMission?.status == MissionStatus.EXECUTING) AetherAmber else AetherEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeMission?.status == MissionStatus.EXECUTING) "RUNNING" else "KERNEL READY",
                            color = if (activeMission?.status == MissionStatus.EXECUTING) AetherAmber else AetherEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Trigger Button
                    Button(
                        onClick = onTriggerQuickRun,
                        colors = ButtonDefaults.buttonColors(containerColor = AetherIndigo),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run Mission",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Run",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Logout/Lock Button
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AetherRose.copy(alpha = 0.12f))
                            .border(1.dp, AetherRose.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .testTag("lock_terminal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Terminal",
                            tint = AetherRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Running Mission Progress Strip if executing
            if (activeMission?.status == MissionStatus.EXECUTING) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AetherIndigo.copy(alpha = 0.15f))
                        .border(1.dp, AetherIndigo.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = AetherAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active: ${activeMission.title}",
                            color = ImmersiveTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    val currentStep = activeMission.steps.firstOrNull { it.status == MissionStatus.EXECUTING }
                    Text(
                        text = currentStep?.stepName ?: "Executing...",
                        color = AetherCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
