package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CLIScreen(
    cliInput: String,
    cliHistory: List<Pair<String, String>>,
    onInputChange: (String) -> Unit,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Terminal, contentDescription = null, tint = AetherCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CLI Shell Commander", color = ImmersiveTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text("Interactive Mission Entry", color = ImmersiveTextMuted, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Command Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "aether run mission --topic \"AI Agents\"",
                "aether daily pipeline",
                "aether worker status",
                "aether fb status",
                "aether help"
            ).forEach { cmd ->
                AssistChip(
                    onClick = { onExecuteCommand(cmd) },
                    label = { Text(cmd, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = ImmersiveSurfaceVariant,
                        labelColor = AetherCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Terminal Output Window
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070A12))
                .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cliHistory) { pair ->
                    Column {
                        if (pair.first.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("aether@os:~$ ", color = AetherIndigo, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                Text(pair.first, color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = pair.second,
                            color = AetherEmerald,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Command Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = cliInput,
                onValueChange = onInputChange,
                placeholder = { Text("aether run mission...", fontSize = 12.sp, fontFamily = FontFamily.Monospace) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AetherCyan,
                    unfocusedBorderColor = ImmersiveCardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            IconButton(
                onClick = { onExecuteCommand(cliInput) },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AetherCyan)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}
