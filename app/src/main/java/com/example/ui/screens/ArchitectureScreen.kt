package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ArchitectureNodeInfo
import com.example.ui.components.ArchitectureDiagramCanvas
import com.example.ui.theme.*

@Composable
fun ArchitectureScreen(
    selectedNodeInfo: ArchitectureNodeInfo?,
    onSelectNode: (ArchitectureNodeInfo?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        ArchitectureDiagramCanvas(
            onSelectNode = { node -> onSelectNode(node) }
        )

        // Inspector Dialog
        selectedNodeInfo?.let { node ->
            AlertDialog(
                onDismissRequest = { onSelectNode(null) },
                containerColor = ImmersiveSurface,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(node.name, color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(node.layer.title, color = AetherCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { onSelectNode(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = ImmersiveTextMuted)
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(node.description, color = ImmersiveTextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ImmersiveSurfaceVariant, RoundedCornerShape(8.dp))
                                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("OPERATIONAL METRICS", color = ImmersiveTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(node.detailMetrics, color = AetherEmerald, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { onSelectNode(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = AetherIndigo)
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}
