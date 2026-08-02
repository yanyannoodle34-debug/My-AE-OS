package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PostItem
import com.example.ui.theme.*

@Composable
fun CanvasPostPreview(
    post: PostItem,
    modifier: Modifier = Modifier,
    onShare: (() -> Unit)? = null
) {
    val primaryColor = parseHexColor(post.primaryColorHex, AetherIndigo)
    val secondaryColor = parseHexColor(post.secondaryColorHex, AetherCyan)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ImmersiveSurfaceVariant)
            .border(1.dp, ImmersiveCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header info bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (post.postType == "REEL") AetherPink else secondaryColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (post.postType == "REEL") "FB Reel (9:16 1080×1920 Vertical)" else "1080×1080 Canvas Asset",
                    color = if (post.postType == "REEL") AetherPink else ImmersiveTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = AetherEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AetherEmerald,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.targetPage,
                        color = AetherEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (post.chunkInfo != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = AetherAmber.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AetherAmber.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = AetherAmber,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "500MB Video Splitter: ${post.chunkInfo}",
                        color = AetherAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Aspect Ratio Box (9:16 for Reel, 1:1 for Photo)
        val isReel = post.postType == "REEL" || post.aspectRatio == "9:16"
        val boxAspectRatio = if (isReel) 9f / 16f else 1f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(boxAspectRatio)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isReel) Color(0xFF833AB4) else primaryColor.copy(alpha = 0.9f),
                            if (isReel) Color(0xFFFD1D1D) else secondaryColor.copy(alpha = 0.85f),
                            if (isReel) Color(0xFFF77737) else Color(0xFF0F172A)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isReel) {
                    // Play icon for Reel
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Reel",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "FACEBOOK REEL • 9:16",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    // Watermark / Brand Header
                    Text(
                        text = "WE SHARE WE CARE",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Main Quote / Body Text
                Text(
                    text = "\"${post.body}\"",
                    color = Color.White,
                    fontSize = if (isReel) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = if (isReel) 18.sp else 22.sp,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category & Vibe badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isReel) "🎬 Reel (${post.durationSeconds}s) • ${post.category.displayName}" else "${post.category.displayName} • ${post.vibe}",
                        color = if (isReel) AetherCyan else secondaryColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hashtags & Share row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = post.hashtags.take(3).joinToString(" "),
                color = AetherCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            if (onShare != null) {
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Post",
                        tint = ImmersiveTextPrimary
                    )
                }
            }
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}
