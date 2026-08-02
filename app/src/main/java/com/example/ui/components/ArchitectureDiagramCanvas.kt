package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ArchitectureNodeInfo
import com.example.data.LayerType
import com.example.ui.theme.*

@Composable
fun ArchitectureDiagramCanvas(
    onSelectNode: (ArchitectureNodeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "AetherOS Orchestration Architecture",
                    color = ImmersiveTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MOS — My Claw Orchestrator Agent | 7 Workers | 1 Daily Pipeline",
                    color = AetherCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ═══ LAYER 0: ENTRY POINTS ═══
        item {
            LayerCard(
                layerType = LayerType.ENTRY_POINTS,
                borderColor = AetherCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NodeChip("CLI Shell", "interactive missions", AetherCyan, Modifier.weight(1f)) {
                        onSelectNode(
                            ArchitectureNodeInfo(
                                "cli",
                                "CLI Shell",
                                LayerType.ENTRY_POINTS,
                                "Interactive mission commander with terminal syntax support.",
                                "ACTIVE",
                                "Commands Executed: 42 | Last: aether run mission"
                            )
                        )
                    }
                    NodeChip("Daily Pipeline", "cron (8:00 AM)", AetherIndigo, Modifier.weight(1f)) {
                        onSelectNode(
                            ArchitectureNodeInfo(
                                "daily",
                                "Daily Pipeline",
                                LayerType.ENTRY_POINTS,
                                "Scheduled cron job triggering 8:00 AM post orchestration.",
                                "ENABLED",
                                "Next Trigger: 08:00 AM | Target: We Share We Care"
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NodeChip("Single Post", "manual trigger", AetherPink, Modifier.weight(1f)) {
                        onSelectNode(
                            ArchitectureNodeInfo(
                                "single",
                                "Single Post",
                                LayerType.ENTRY_POINTS,
                                "Direct manual post trigger for custom topics.",
                                "READY",
                                "Latency: <1.2s"
                            )
                        )
                    }
                    NodeChip("Cron Jobs", "imggen / sync", AetherAmber, Modifier.weight(1f)) {
                        onSelectNode(
                            ArchitectureNodeInfo(
                                "cron",
                                "Cron Jobs",
                                LayerType.ENTRY_POINTS,
                                "Background maintenance & trend polling cron jobs.",
                                "RUNNING",
                                "Interval: 15 mins"
                            )
                        )
                    }
                }
            }
        }

        item { ConnectorArrow() }

        // ═══ LAYER 1: KERNEL ═══
        item {
            LayerCard(
                layerType = LayerType.KERNEL,
                borderColor = AetherIndigo
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Kernel Core
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AetherIndigo.copy(alpha = 0.25f))
                            .border(1.dp, AetherIndigo, RoundedCornerShape(10.dp))
                            .clickable {
                                onSelectNode(
                                    ArchitectureNodeInfo(
                                        "kernel_core",
                                        "AetherOS Kernel",
                                        LayerType.KERNEL,
                                        "Core orchestrator kernel managing mission state and worker dispatch.",
                                        "HEALTHY",
                                        "Active Missions: 1 | Worker Nodes Connected: 7/7"
                                    )
                                )
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = AetherIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AetherOS Kernel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subcomponents
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SubNodeBox("Mission Mgr", Modifier.weight(1f))
                        SubNodeBox("Task Planner", Modifier.weight(1f))
                        SubNodeBox("Scheduler", Modifier.weight(1f))
                        SubNodeBox("Worker Mgr", Modifier.weight(1f))
                    }
                }
            }
        }

        item { ConnectorArrow() }

        // ═══ LAYER 2: 7 WORKERS ═══
        item {
            LayerCard(
                layerType = LayerType.WORKERS,
                borderColor = AetherPink
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Row 1: 4 Workers
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        WorkerMiniCard("Content Worker", "quotes · facts", AetherIndigo, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_content", "Content Worker", LayerType.WORKERS, "Extracts quotes, facts, and historical lessons.", "ONLINE", "Catalogue Size: 15 entries"))
                        }
                        WorkerMiniCard("Creative Dir", "vibe · layout", AetherPink, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_creative", "Creative Director", LayerType.WORKERS, "Sets aesthetic color palette, typography tokens, visual mood.", "ONLINE", "Palettes Armed: 4"))
                        }
                        WorkerMiniCard("SEO Worker", "hashtags · RSS", AetherEmerald, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_seo", "SEO Worker", LayerType.WORKERS, "Queries Google RSS, Reddit, Wikipedia for trending keywords.", "ONLINE", "Trends Synced: 4 sources"))
                        }
                        WorkerMiniCard("Imgmaker", "1080x1080 canvas", AetherAmber, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_img", "Imgmaker Worker", LayerType.WORKERS, "Generates 1080x1080 social media graphic cards.", "ONLINE", "Canvas Engine: Active"))
                        }
                    }

                    // Row 2: 3 Workers
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        WorkerMiniCard("Duplicate Check", "hash · 12-rule", AetherCyan, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_dup", "Duplicate Checker", LayerType.WORKERS, "Calculates SHA-256 content hashes & enforces 12-type rotation.", "ONLINE", "Hash Registry: Active"))
                        }
                        WorkerMiniCard("Social Agent", "FB v22 publish", AetherCyan, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_social", "Social Media Agent", LayerType.WORKERS, "Dispatches posts to Facebook Graph API v22.", "ONLINE", "Target: We Share We Care"))
                        }
                        WorkerMiniCard("Report Worker", "ROI insights", AetherIndigo, Modifier.weight(1f)) {
                            onSelectNode(ArchitectureNodeInfo("w_report", "Report Worker", LayerType.WORKERS, "Generates engagement analytics & worker optimization metrics.", "ONLINE", "ROI Logged: 100%"))
                        }
                    }
                }
            }
        }

        item { ConnectorArrow() }

        // ═══ LAYER 3: DATA & TREND SOURCES ═══
        item {
            LayerCard(
                layerType = LayerType.DATA_SOURCES,
                borderColor = AetherEmerald
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DataNodeBox("Content Catalogue", "curated entries", AetherIndigo, Modifier.weight(1f))
                    DataNodeBox("Trend HTTP", "Google RSS / Wiki", AetherEmerald, Modifier.weight(1f))
                    DataNodeBox("On This Day DB", "history events", AetherAmber, Modifier.weight(1f))
                    DataNodeBox("Credentials", "Facebook tokens", AetherPink, Modifier.weight(1f))
                }
            }
        }

        item { ConnectorArrow() }

        // ═══ LAYER 4: PUBLISHERS ═══
        item {
            LayerCard(
                layerType = LayerType.PUBLISHERS,
                borderColor = AetherAmber
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AetherAmber.copy(alpha = 0.2f))
                            .border(1.dp, AetherAmber, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Facebook Graph API v22", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Target: We Share We Care", color = AetherAmber, fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ImmersiveSurfaceVariant)
                            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Mock Publisher", color = ImmersiveTextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Fallback mode", color = ImmersiveTextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerCard(
    layerType: LayerType,
    borderColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ImmersiveSurface)
            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "▸ ${layerType.title}",
                color = borderColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = layerType.subtitle,
                color = ImmersiveTextMuted,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun NodeChip(
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(title, color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(subtitle, color = ImmersiveTextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SubNodeBox(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ImmersiveSurfaceVariant)
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = ImmersiveTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun WorkerMiniCard(
    title: String,
    role: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center)
            Text(role, color = ImmersiveTextMuted, fontSize = 8.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DataNodeBox(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text(subtitle, color = ImmersiveTextMuted, fontSize = 8.sp)
        }
    }
}

@Composable
private fun ConnectorArrow() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
            tint = AetherCyan.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}
