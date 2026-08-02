package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkerType
import com.example.ui.theme.*

@Composable
fun WorkerCard(
    worker: WorkerType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val workerColor = parseHexColor(worker.colorHex, AetherIndigo)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) workerColor.copy(alpha = 0.15f) else ImmersiveSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) workerColor else ImmersiveCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(workerColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getWorkerIcon(worker),
                        contentDescription = worker.title,
                        tint = workerColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = worker.title,
                        color = ImmersiveTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = worker.role,
                        color = ImmersiveTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Online Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AetherEmerald)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ONLINE",
                    color = AetherEmerald,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getWorkerIcon(worker: WorkerType): ImageVector {
    return when (worker) {
        WorkerType.CONTENT -> Icons.Default.Article
        WorkerType.CREATIVE -> Icons.Default.Palette
        WorkerType.SEO -> Icons.Default.Search
        WorkerType.IMGMAKER -> Icons.Default.Image
        WorkerType.DUPLICATE -> Icons.Default.Verified
        WorkerType.SOCIAL_MEDIA -> Icons.Default.Share
        WorkerType.REPORT -> Icons.Default.Analytics
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}
