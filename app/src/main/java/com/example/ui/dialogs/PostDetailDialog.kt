package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PostItem
import com.example.ui.components.CanvasPostPreview
import com.example.ui.theme.*

@Composable
fun PostDetailDialog(
    post: PostItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ImmersiveSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(post.title, color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("FB Graph API Post ID: ${post.fbPostId ?: "Pending"}", color = AetherCyan, fontSize = 11.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = ImmersiveTextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Graphic Preview Canvas
                CanvasPostPreview(post = post)

                // Facebook Graph API Raw Payload Preview
                Text("GRAPH API v22 JSON PAYLOAD", color = AetherIndigo, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ImmersiveSurfaceVariant, RoundedCornerShape(8.dp))
                        .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = """
                            {
                              "message": "${post.body}\n\n${post.hashtags.joinToString(" ")}",
                              "target_page_id": "1092837492019482",
                              "page_name": "${post.targetPage}",
                              "published": true,
                              "hash": "${post.hashValue}"
                            }
                        """.trimIndent(),
                        color = AetherEmerald,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AetherIndigo)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share Post")
            }
        }
    )
}
