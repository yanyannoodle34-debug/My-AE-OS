package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AiAgentProvider
import com.example.data.DirectorAgentConfig
import com.example.data.WorkerType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAgentsConfigScreen(
    providers: List<AiAgentProvider>,
    defaultProviderId: String,
    workerProviderMap: Map<WorkerType, String>,
    testOutputLog: String,
    isTesting: Boolean,
    directorConfig: DirectorAgentConfig = DirectorAgentConfig(),
    isSplittingVideo: Boolean = false,
    videoSplitProgress: String = "",
    onSetDefaultProvider: (providerId: String) -> Unit,
    onUpdateProviderKey: (providerId: String, apiKey: String, selectedModel: String, endpointUrl: String) -> Unit,
    onTestProvider: (providerId: String, prompt: String) -> Unit,
    onAutofillFreeKeys: () -> Unit,
    onAssignWorkerProvider: (worker: WorkerType, providerId: String) -> Unit,
    onToggleDirectorAgent: ((Boolean) -> Unit)? = null,
    onUpdateDirectorMode: ((String) -> Unit)? = null,
    onSplitAndUploadVideo: ((title: String, sizeMb: Int, durationSec: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var testPrompt by remember { mutableStateOf("Write a 1-sentence tech post introducing AetherOS AI Kernel.") }
    var selectedTestProviderId by remember { mutableStateOf("nvidia") }
    var isSandboxExpanded by remember { mutableStateOf(false) }
    var isWorkerMatrixExpanded by remember { mutableStateOf(false) }

    // Video Upload Splitter Form State
    var videoTitleInput by remember { mutableStateOf("Facebook Reels Masterclass & Tech Roadmap") }
    var selectedVideoSizeMb by remember { mutableStateOf(500) } // 500 MB max payload
    var selectedVideoDurationSec by remember { mutableStateOf(180) } // 3 minutes total video

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Agents & Director Engine",
                            color = ImmersiveTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Manage AI Director Agent • 9:16 Facebook Reels Policy • 500MB Splitter • LLM APIs",
                            color = ImmersiveTextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onAutofillFreeKeys,
                        colors = ButtonDefaults.buttonColors(containerColor = AetherCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("autofill_all_ai_keys_button")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Autofill Free Keys", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==========================================
        // AI DIRECTOR AGENT CONTROL CONSOLE CARD
        // ==========================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (directorConfig.isEnabled) AetherPink else ImmersiveCardBorder
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with ON/OFF Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = if (directorConfig.isEnabled) AetherPink else ImmersiveTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Director Agent",
                                    color = ImmersiveTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (directorConfig.isEnabled) "ACTIVE • Auto-Directing Photos & 9:16 FB Reels" else "OFF • Manual Pipeline Mode",
                                    color = if (directorConfig.isEnabled) AetherPink else ImmersiveTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (directorConfig.isEnabled) "ON" else "OFF",
                                color = if (directorConfig.isEnabled) AetherPink else ImmersiveTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = directorConfig.isEnabled,
                                onCheckedChange = { onToggleDirectorAgent?.invoke(it) },
                                modifier = Modifier.testTag("director_agent_toggle_switch")
                            )
                        }
                    }

                    HorizontalDivider(color = ImmersiveCardBorder)

                    // Capabilities & Policy Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = AetherIndigo.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Photo, contentDescription = null, tint = AetherIndigo, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Photos (1:1 Ratio)", color = AetherIndigo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            color = AetherPink.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = AetherPink, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("FB Reels (9:16 1080x1920)", color = AetherPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            color = AetherAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = AetherAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("500MB Splitter", color = AetherAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Mode Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Director Execution Strategy:", color = ImmersiveTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "AUTO_DIRECT" to "Auto Direct (Photos + Reels)",
                                "REELS_ONLY" to "9:16 Reels Only",
                                "PHOTO_ONLY" to "1:1 Photos Only"
                            ).forEach { (modeKey, label) ->
                                val isSelected = directorConfig.mode == modeKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateDirectorMode?.invoke(modeKey) },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AetherPink,
                                        selectedLabelColor = Color.White,
                                        containerColor = ImmersiveSurfaceVariant,
                                        labelColor = ImmersiveTextMuted
                                    ),
                                    modifier = Modifier.testTag("director_mode_$modeKey")
                                )
                            }
                        }
                    }

                    // 500MB Video Upload & Auto-Splitter to Facebook Reels Simulator Form
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AetherCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("500MB Video Uploader & FB Reels Splitter", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AetherEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Meta Policy Compliant", color = AetherEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "Facebook Policy: Videos up to 500 MB are auto-formatted into 9:16 vertical ratio (1080x1920) and chunked into max 60s Reel segments.",
                                color = ImmersiveTextMuted,
                                fontSize = 11.sp
                            )

                            // Title Field
                            OutlinedTextField(
                                value = videoTitleInput,
                                onValueChange = { videoTitleInput = it },
                                label = { Text("Video Payload Title", fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("video_title_input_field"),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = ImmersiveTextPrimary),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherCyan,
                                    unfocusedBorderColor = ImmersiveCardBorder
                                )
                            )

                            // File Size Selector Chips
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Select Upload Payload File Size:", color = ImmersiveTextMuted, fontSize = 10.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(50, 200, 500).forEach { sizeMb ->
                                        val isSel = selectedVideoSizeMb == sizeMb
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedVideoSizeMb = sizeMb },
                                            label = { Text("${sizeMb} MB ${if (sizeMb == 500) "(MAX)" else ""}", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AetherAmber,
                                                selectedLabelColor = Color.Black,
                                                containerColor = ImmersiveSurface
                                            )
                                        )
                                    }
                                }
                            }

                            // Duration Selector Chips
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Select Video Duration:", color = ImmersiveTextMuted, fontSize = 10.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(45 to "45s (1 Reel)", 120 to "2 min (2 Reels)", 180 to "3 min (3 Reels)").forEach { (durSec, label) ->
                                        val isSel = selectedVideoDurationSec == durSec
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedVideoDurationSec = durSec },
                                            label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AetherCyan,
                                                selectedLabelColor = Color.Black,
                                                containerColor = ImmersiveSurface
                                            )
                                        )
                                    }
                                }
                            }

                            // Realtime Facebook Policy Compliance Calculation Preview
                            val calculatedChunks = Math.max(1, Math.ceil(selectedVideoDurationSec.toDouble() / directorConfig.maxReelDurationSec).toInt())
                            val calculatedChunkSize = selectedVideoSizeMb / calculatedChunks

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .border(1.dp, AetherCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = AetherCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "FB Policy Match: ${selectedVideoSizeMb}MB video will auto-split into $calculatedChunks x 9:16 Reels clips ($calculatedChunkSize MB each, max 60s).",
                                        color = AetherCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Process Button
                            Button(
                                onClick = {
                                    onSplitAndUploadVideo?.invoke(videoTitleInput, selectedVideoSizeMb, selectedVideoDurationSec)
                                },
                                enabled = directorConfig.isEnabled && !isSplittingVideo,
                                colors = ButtonDefaults.buttonColors(containerColor = AetherPink, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("split_and_upload_video_button")
                            ) {
                                if (isSplittingVideo) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Splitting & Formatting 9:16 FB Reels...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Direct & Split ${selectedVideoSizeMb}MB Video to 9:16 FB Reels", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (videoSplitProgress.isNotBlank()) {
                                Text(
                                    text = "Progress: $videoSplitProgress",
                                    color = AetherEmerald,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Global Default Provider Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AetherIndigo)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = AetherIndigo, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Primary AI Orchestrator Model", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AetherIndigo.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Active Default: ${defaultProviderId.uppercase()}", color = AetherIndigo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "Select which AI provider powers default kernel reasoning and fallback mission execution:",
                        color = ImmersiveTextMuted,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        providers.forEach { provider ->
                            val isSelected = provider.id == defaultProviderId
                            OutlinedButton(
                                onClick = { onSetDefaultProvider(provider.id) },
                                modifier = Modifier.weight(1f).testTag("select_default_provider_${provider.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) AetherIndigo.copy(alpha = 0.25f) else Color.Transparent,
                                    contentColor = if (isSelected) AetherCyan else ImmersiveTextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) AetherCyan else ImmersiveCardBorder
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(provider.badge, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (isSelected) "PRIMARY" else "Select",
                                        fontSize = 9.sp,
                                        color = if (isSelected) AetherEmerald else ImmersiveTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUPPORTED AI API PROVIDERS (4)",
                    color = AetherCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Free Tiers & Direct Keys Supported",
                    color = ImmersiveTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // List of Provider Config Cards (NVIDIA, OpenRouter, Gemini, DeepSeek)
        items(providers) { provider ->
            ProviderConfigCard(
                provider = provider,
                isDefault = provider.id == defaultProviderId,
                onUpdateKey = { key, model, endpoint ->
                    onUpdateProviderKey(provider.id, key, model, endpoint)
                },
                onQuickTest = {
                    selectedTestProviderId = provider.id
                    onTestProvider(provider.id, testPrompt)
                }
            )
        }

        // Live AI Test Sandbox Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(AetherEmerald)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isSandboxExpanded = !isSandboxExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = AetherEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live AI Agent Test Sandbox", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isTesting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = AetherEmerald,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Icon(
                                imageVector = if (isSandboxExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand or collapse sandbox",
                                tint = AetherEmerald
                            )
                        }
                    }

                    AnimatedVisibility(visible = isSandboxExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            HorizontalDivider(color = ImmersiveCardBorder)

                            Text(
                                text = "Select a provider and execute a test inference ping to verify API key validity, response speed, and token streaming:",
                                color = ImmersiveTextMuted,
                                fontSize = 12.sp
                            )

                            // Provider Selector for Test
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                providers.forEach { provider ->
                                    val isSelected = provider.id == selectedTestProviderId
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedTestProviderId = provider.id },
                                        label = { Text(provider.badge, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AetherEmerald,
                                            selectedLabelColor = Color.Black
                                        ),
                                        modifier = Modifier.testTag("test_chip_${provider.id}")
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = testPrompt,
                                onValueChange = { testPrompt = it },
                                label = { Text("Test Prompt") },
                                modifier = Modifier.fillMaxWidth().testTag("ai_test_prompt_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherEmerald,
                                    unfocusedBorderColor = ImmersiveCardBorder,
                                    focusedTextColor = ImmersiveTextPrimary,
                                    unfocusedTextColor = ImmersiveTextPrimary,
                                    focusedContainerColor = ImmersiveBackground,
                                    unfocusedContainerColor = ImmersiveBackground
                                )
                            )

                            Button(
                                onClick = { onTestProvider(selectedTestProviderId, testPrompt) },
                                enabled = !isTesting,
                                colors = ButtonDefaults.buttonColors(containerColor = AetherEmerald, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("execute_ai_test_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTesting) "Executing AI Call..." else "Send Test Prompt to ${selectedTestProviderId.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            // Console Output Box
                            if (testOutputLog.isNotEmpty()) {
                                Surface(
                                    color = ImmersiveBackground,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("RESPONSE LOG", color = AetherEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                            Text("STATUS 200 OK", color = AetherEmerald, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = testOutputLog,
                                            color = ImmersiveTextPrimary,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Worker AI Model Mapping Matrix
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AetherIndigo)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isWorkerMatrixExpanded = !isWorkerMatrixExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, tint = AetherIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("7 Workers AI Provider Assignment", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Icon(
                            imageVector = if (isWorkerMatrixExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand or collapse worker matrix",
                            tint = AetherIndigo
                        )
                    }

                    AnimatedVisibility(visible = isWorkerMatrixExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            HorizontalDivider(color = ImmersiveCardBorder)

                            Text(
                                text = "Assign specific AI models to each of the 7 kernel workers for optimal task performance:",
                                color = ImmersiveTextMuted,
                                fontSize = 12.sp
                            )

                            WorkerType.values().forEach { worker ->
                                val assignedProviderId = workerProviderMap[worker] ?: defaultProviderId
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ImmersiveBackground)
                                        .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(0.55f)) {
                                        Text(worker.title, color = ImmersiveTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(worker.role, color = ImmersiveTextMuted, fontSize = 10.sp)
                                    }

                                    Row(
                                        modifier = Modifier.weight(0.45f),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        providers.forEach { p ->
                                            val isSelected = p.id == assignedProviderId
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 2.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isSelected) AetherIndigo else Color.Transparent)
                                                    .border(1.dp, if (isSelected) AetherIndigo else ImmersiveCardBorder, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                TextButton(
                                                    onClick = { onAssignWorkerProvider(worker, p.id) },
                                                    contentPadding = PaddingValues(0.dp),
                                                    modifier = Modifier.height(18.dp)
                                                ) {
                                                    Text(
                                                        text = p.badge,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else ImmersiveTextMuted
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderConfigCard(
    provider: AiAgentProvider,
    isDefault: Boolean,
    onUpdateKey: (key: String, model: String, endpoint: String) -> Unit,
    onQuickTest: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(isDefault) }
    var apiKeyInput by remember(provider) { mutableStateOf(provider.apiKey) }
    var selectedModel by remember(provider) { mutableStateOf(provider.selectedModel) }
    var endpointUrl by remember(provider) { mutableStateOf(provider.endpointUrl) }
    var keyVisible by remember { mutableStateOf(false) }

    val providerColor = when (provider.id) {
        "nvidia" -> AetherEmerald
        "openrouter" -> AetherPink
        "gemini" -> AetherCyan
        "deepseek" -> AetherIndigo
        else -> AetherCyan
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (provider.status.contains("Active") || provider.status.contains("Verified") || provider.status.contains("Connected")) providerColor else ImmersiveCardBorder
            )
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Main Header Row (Always Visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(providerColor.copy(alpha = 0.2f))
                            .border(1.dp, providerColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(provider.badge, color = providerColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(provider.name, color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = if (isExpanded) provider.description else "Active Model: ${provider.selectedModel}",
                            color = ImmersiveTextMuted,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statusColor = when {
                        provider.status.contains("Active") || provider.status.contains("Verified") || provider.status.contains("Connected") -> AetherEmerald
                        provider.status.contains("Error") -> AetherRose
                        else -> AetherAmber
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(provider.status, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand or collapse provider settings",
                        tint = ImmersiveTextMuted
                    )
                }
            }

            // Expandable Content Details
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    HorizontalDivider(color = ImmersiveCardBorder)

                    // API Key Input
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = {
                            apiKeyInput = it
                            onUpdateKey(apiKeyInput, selectedModel, endpointUrl)
                        },
                        label = { Text("${provider.name} API Key") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = providerColor) },
                        trailingIcon = {
                            IconButton(onClick = { keyVisible = !keyVisible }) {
                                Icon(
                                    imageVector = if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle key visibility",
                                    tint = ImmersiveTextMuted
                                )
                            }
                        },
                        visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = providerColor,
                            unfocusedBorderColor = ImmersiveCardBorder,
                            focusedTextColor = ImmersiveTextPrimary,
                            unfocusedTextColor = ImmersiveTextPrimary,
                            focusedContainerColor = ImmersiveBackground,
                            unfocusedContainerColor = ImmersiveBackground
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("api_key_input_${provider.id}")
                    )

                    // Custom Endpoint for DeepSeek or OpenAI-compatible proxies
                    if (provider.id == "deepseek" || provider.id == "openrouter") {
                        OutlinedTextField(
                            value = endpointUrl,
                            onValueChange = {
                                endpointUrl = it
                                onUpdateKey(apiKeyInput, selectedModel, endpointUrl)
                            },
                            label = { Text("Base Endpoint URL") },
                            leadingIcon = { Icon(Icons.Default.Dns, contentDescription = null, tint = providerColor) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = providerColor,
                                unfocusedBorderColor = ImmersiveCardBorder,
                                focusedTextColor = ImmersiveTextPrimary,
                                unfocusedTextColor = ImmersiveTextPrimary,
                                focusedContainerColor = ImmersiveBackground,
                                unfocusedContainerColor = ImmersiveBackground
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("endpoint_input_${provider.id}")
                        )
                    }

                    // Model Selection Dropdown Chips
                    Text("Select Model:", color = ImmersiveTextMuted, fontSize = 11.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        provider.availableModels.forEach { modelName ->
                            val isModelSelected = modelName == selectedModel
                            FilterChip(
                                selected = isModelSelected,
                                onClick = {
                                    selectedModel = modelName
                                    onUpdateKey(apiKeyInput, selectedModel, endpointUrl)
                                },
                                label = { Text(modelName, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = providerColor,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.testTag("model_chip_${provider.id}_$modelName")
                            )
                        }
                    }

                    // Save and Test Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (provider.isFreeTier) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = AetherEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("FREE TIER AVAILABLE", color = AetherEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = onQuickTest,
                            colors = ButtonDefaults.buttonColors(containerColor = providerColor, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("verify_key_button_${provider.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify & Save Key", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
