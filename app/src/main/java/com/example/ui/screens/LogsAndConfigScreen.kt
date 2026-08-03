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
import com.example.data.FacebookCredentials
import com.example.data.SystemLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogsAndConfigScreen(
    logs: List<SystemLog>,
    fbCredentials: FacebookCredentials,
    selectedLogLevel: String,
    logSearchQuery: String,
    onLevelSelected: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
    onClearLogs: () -> Unit,
    onUpdateCredentials: (pageName: String, pageId: String, apiVersion: String, accessToken: String) -> Unit,
    onTestConnection: () -> Unit,
    onSendTestPost: (testMessage: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editPageName by remember(fbCredentials) { mutableStateOf(fbCredentials.pageName) }
    var editPageId by remember(fbCredentials) { mutableStateOf(fbCredentials.pageId) }
    var editApiVersion by remember(fbCredentials) { mutableStateOf(fbCredentials.apiVersion) }
    var editAccessToken by remember(fbCredentials) { mutableStateOf(fbCredentials.accessToken) }
    var tokenVisible by remember { mutableStateOf(false) }

    var testPostMessage by remember { mutableStateOf("Testing Facebook Graph API v22.0 connection from AetherOS Kernel!") }
    var showGuide by remember { mutableStateOf(false) }

    var isFbSetupExpanded by remember { mutableStateOf(true) }
    var isFbSandboxExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Logs & System Configuration", color = ImmersiveTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AetherIndigo.copy(alpha = 0.2f))
                            .border(1.dp, AetherIndigo.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("v22.0 GRAPH API", color = AetherIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Facebook Graph API v22.0 Settings & Live Test Suite", color = ImmersiveTextMuted, fontSize = 12.sp)

                val isSecureConfigActive = fbCredentials.status.contains("Securely") || (!fbCredentials.accessToken.contains("EAAG...m9ZAZB9x2Y10P") && fbCredentials.accessToken.isNotBlank())
                Surface(
                    color = if (isSecureConfigActive) AetherEmerald.copy(alpha = 0.08f) else AetherAmber.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSecureConfigActive) AetherEmerald.copy(alpha = 0.25f) else AetherAmber.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isSecureConfigActive) Icons.Default.LockOpen else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isSecureConfigActive) AetherEmerald else AetherAmber,
                            modifier = Modifier.size(18.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSecureConfigActive) "Secure Facebook Credentials Handshake Active" else "Demo Sandbox Mode",
                                color = ImmersiveTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isSecureConfigActive)
                                    "Facebook Page ID and Access Token are synchronized securely via AI Studio Secrets."
                                    else "Running with local simulated credentials. Add real Facebook Page credentials securely in the Secrets panel in AI Studio.",
                                color = ImmersiveTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Facebook Graph API & Page ID Configuration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (fbCredentials.status.contains("Active") || fbCredentials.status.contains("Connected")) AetherEmerald else AetherCyan
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Header row inside card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isFbSetupExpanded = !isFbSetupExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SettingsInputComponent,
                                contentDescription = null,
                                tint = AetherCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Facebook Page & Graph API Setup", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val badgeColor = when {
                                fbCredentials.status.contains("Active") || fbCredentials.status.contains("Connected") -> AetherEmerald
                                fbCredentials.status.contains("Error") -> AetherRose
                                else -> AetherAmber
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeColor.copy(alpha = 0.18f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(fbCredentials.status, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Icon(
                                imageVector = if (isFbSetupExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand or collapse FB setup",
                                tint = AetherCyan
                            )
                        }
                    }

                    AnimatedVisibility(visible = isFbSetupExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 6.dp)) {
                            HorizontalDivider(color = ImmersiveCardBorder)

                            // Target Page Name
                            OutlinedTextField(
                                value = editPageName,
                                onValueChange = { editPageName = it },
                                label = { Text("Target Page Name") },
                                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = AetherCyan) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherCyan,
                                    unfocusedBorderColor = ImmersiveCardBorder,
                                    focusedTextColor = ImmersiveTextPrimary,
                                    unfocusedTextColor = ImmersiveTextPrimary,
                                    focusedContainerColor = ImmersiveBackground,
                                    unfocusedContainerColor = ImmersiveBackground
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("fb_page_name_input")
                            )

                            // Target Page ID
                            OutlinedTextField(
                                value = editPageId,
                                onValueChange = { editPageId = it },
                                label = { Text("Facebook Page ID") },
                                leadingIcon = { Icon(Icons.Default.PermIdentity, contentDescription = null, tint = AetherCyan) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherCyan,
                                    unfocusedBorderColor = ImmersiveCardBorder,
                                    focusedTextColor = ImmersiveTextPrimary,
                                    unfocusedTextColor = ImmersiveTextPrimary,
                                    focusedContainerColor = ImmersiveBackground,
                                    unfocusedContainerColor = ImmersiveBackground
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier.fillMaxWidth().testTag("fb_page_id_input")
                            )

                            // Graph API Version Input
                            OutlinedTextField(
                                value = editApiVersion,
                                onValueChange = { editApiVersion = it },
                                label = { Text("Graph API Version") },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = AetherCyan) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherCyan,
                                    unfocusedBorderColor = ImmersiveCardBorder,
                                    focusedTextColor = ImmersiveTextPrimary,
                                    unfocusedTextColor = ImmersiveTextPrimary,
                                    focusedContainerColor = ImmersiveBackground,
                                    unfocusedContainerColor = ImmersiveBackground
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("fb_api_version_input")
                            )

                            // Page Access Token Input
                            OutlinedTextField(
                                value = editAccessToken,
                                onValueChange = { editAccessToken = it },
                                label = { Text("Page Access Token") },
                                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = AetherCyan) },
                                trailingIcon = {
                                    IconButton(onClick = { tokenVisible = !tokenVisible }) {
                                        Icon(
                                            imageVector = if (tokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle token visibility",
                                            tint = ImmersiveTextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (tokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherCyan,
                                    unfocusedBorderColor = ImmersiveCardBorder,
                                    focusedTextColor = ImmersiveTextPrimary,
                                    unfocusedTextColor = ImmersiveTextPrimary,
                                    focusedContainerColor = ImmersiveBackground,
                                    unfocusedContainerColor = ImmersiveBackground
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("fb_access_token_input")
                            )

                            // Setup Action Buttons
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onUpdateCredentials(editPageName, editPageId, editApiVersion, editAccessToken)
                                        onTestConnection()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AetherCyan, contentColor = Color.Black),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("save_and_test_fb_button")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save & Test Connection", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        editPageName = "We Share We Care"
                                        editPageId = "1092837492019482"
                                        editApiVersion = "v22.0"
                                        editAccessToken = "EAAG...m9ZAZB9x2Y10P"
                                        onUpdateCredentials(editPageName, editPageId, editApiVersion, editAccessToken)
                                        onTestConnection()
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AetherIndigo),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("autofill_fb_credentials")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = AetherIndigo)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Autofill Sandbox Demo Credentials", color = AetherIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Developer Help Toggle
                            TextButton(
                                onClick = { showGuide = !showGuide },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = if (showGuide) Icons.Default.ExpandLess else Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = AetherCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showGuide) "Hide Facebook Setup Guide" else "How to get Page ID & Access Token?",
                                    color = AetherCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Animated Step-By-Step Facebook Guide
                            AnimatedVisibility(visible = showGuide) {
                                Surface(
                                    color = ImmersiveBackground,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("📖 Facebook Developers Setup Guide", color = AetherCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        GuideStep(1, "Go to Meta for Developers (developers.facebook.com) -> My Apps.")
                                        GuideStep(2, "Create an App with 'Business' or 'Consumer' type, then select Graph API Explorer.")
                                        GuideStep(3, "In User or Page token dropdown, select your Facebook Page.")
                                        GuideStep(4, "Grant permissions: pages_manage_posts, pages_read_engagement, pages_show_list.")
                                        GuideStep(5, "Copy the Page ID (found under Page Settings -> About) & generated Page Access Token into the fields above.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Test Graph API Live Sandbox Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AetherIndigo)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isFbSandboxExpanded = !isFbSandboxExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = AetherIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Post Publisher Sandbox", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Icon(
                            imageVector = if (isFbSandboxExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand or collapse FB sandbox",
                            tint = AetherIndigo
                        )
                    }

                    AnimatedVisibility(visible = isFbSandboxExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 6.dp)) {
                            HorizontalDivider(color = ImmersiveCardBorder)

                            Text(
                                text = "Endpoint: https://graph.facebook.com/${fbCredentials.apiVersion}/${fbCredentials.pageId}/feed",
                                color = ImmersiveTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            OutlinedTextField(
                                value = testPostMessage,
                                onValueChange = { testPostMessage = it },
                                label = { Text("Test Message Payload") },
                                modifier = Modifier.fillMaxWidth().testTag("test_post_message_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AetherIndigo,
                                    unfocusedBorderColor = ImmersiveCardBorder,
                                    focusedTextColor = ImmersiveTextPrimary,
                                    unfocusedTextColor = ImmersiveTextPrimary,
                                    focusedContainerColor = ImmersiveBackground,
                                    unfocusedContainerColor = ImmersiveBackground
                                )
                            )

                            Button(
                                onClick = { onSendTestPost(testPostMessage) },
                                colors = ButtonDefaults.buttonColors(containerColor = AetherIndigo, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("send_test_post_button")
                            ) {
                                Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Test Post to Page ID", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // System Event Stream Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = AetherCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SYSTEM EVENT STREAM", color = AetherCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onClearLogs,
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", tint = ImmersiveTextMuted)
                }
            }
        }

        // Search & Filter Level Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = logSearchQuery,
                    onValueChange = onQueryChanged,
                    placeholder = { Text("Search logs...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ImmersiveTextMuted) },
                    trailingIcon = {
                        if (logSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = ImmersiveTextMuted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("log_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AetherCyan,
                        unfocusedBorderColor = ImmersiveCardBorder,
                        focusedTextColor = ImmersiveTextPrimary,
                        unfocusedTextColor = ImmersiveTextPrimary,
                        focusedContainerColor = ImmersiveSurface,
                        unfocusedContainerColor = ImmersiveSurface
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL", "KERNEL", "WORKER", "PUBLISH", "ERROR").forEach { level ->
                        FilterChip(
                            selected = selectedLogLevel == level,
                            onClick = { onLevelSelected(level) },
                            label = { Text(level, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AetherCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Logs Stream List
        if (logs.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No logs matching current filter.", color = ImmersiveTextMuted, fontSize = 12.sp)
                }
            }
        } else {
            items(logs) { log ->
                val color = when (log.level) {
                    "KERNEL" -> AetherIndigo
                    "WORKER" -> AetherCyan
                    "PUBLISH" -> AetherEmerald
                    "ERROR" -> AetherRose
                    else -> ImmersiveTextMuted
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ImmersiveSurface)
                        .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("[${log.level}] ${log.source}", color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(formatLogTime(log.timestamp), color = ImmersiveTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.message, color = ImmersiveTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideStep(stepNum: Int, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "$stepNum. ",
            color = AetherCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
        Text(
            text = text,
            color = ImmersiveTextMuted,
            fontSize = 11.sp
        )
    }
}

private fun formatLogTime(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return sdf.format(Date(millis))
}
