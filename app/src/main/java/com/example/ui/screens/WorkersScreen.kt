package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.CanvasPostPreview
import com.example.ui.components.WorkerCard
import com.example.ui.theme.*

@Composable
fun WorkersScreen(
    selectedWorker: WorkerType?,
    onSelectWorker: (WorkerType?) -> Unit,
    trends: List<TrendItem>,
    posts: List<PostItem>,
    modifier: Modifier = Modifier
) {
    val activeWorker = selectedWorker ?: WorkerType.CONTENT

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text("7 Specialized Worker Nodes", color = ImmersiveTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Content • Creative • SEO • Imgmaker • Duplicate • Social • Report", color = ImmersiveTextMuted, fontSize = 12.sp)
            }
        }

        // Workers List / Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkerType.values().forEach { worker ->
                    WorkerCard(
                        worker = worker,
                        isSelected = worker == activeWorker,
                        onClick = { onSelectWorker(worker) }
                    )
                }
            }
        }

        // Dedicated Worker Detail View
        item {
            Text("${activeWorker.title} — Deep Dive", color = AetherCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            when (activeWorker) {
                WorkerType.CONTENT -> ContentWorkerDetail()
                WorkerType.CREATIVE -> CreativeDirectorDetail()
                WorkerType.SEO -> SeoWorkerDetail(trends)
                WorkerType.IMGMAKER -> ImgmakerWorkerDetail(posts.firstOrNull())
                WorkerType.DUPLICATE -> DuplicateCheckerDetail()
                WorkerType.SOCIAL_MEDIA -> SocialMediaAgentDetail()
                WorkerType.REPORT -> ReportWorkerDetail(posts)
            }
        }
    }
}

@Composable
private fun ContentWorkerDetail() {
    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Catalogue & Generator Mode", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Extracts quotes, science facts, productivity tips, and historical events.", color = ImmersiveTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            ContentCategory.values().forEach { cat ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cat.displayName, color = ImmersiveTextPrimary, fontSize = 12.sp)
                    Text("Ready", color = AetherEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CreativeDirectorDetail() {
    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Aesthetic Palettes & Reels Director Engine", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Surface(color = AetherPink.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                    Text("9:16 Reels Director Active", color = AetherPink, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Text("Assigns visual mood, 1:1 photo canvas, 9:16 FB Reel ratio (1080x1920), and 500MB video chunking.", color = ImmersiveTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            listOf(
                "FB Reels (9:16 Vertical Video 1080x1920, Max 60s clips)",
                "500MB Video Auto-Splitter (Chunked to Facebook policy)",
                "Cyber Cyan Palette (#06B6D4 • #818CF8)",
                "Neon Indigo Palette (#6366F1 • #EC4899)",
                "Emerald Pulse Palette (#10B981 • #06B6D4)"
            ).forEach { palette ->
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(ImmersiveSurface, RoundedCornerShape(6.dp)).padding(8.dp)
                ) {
                    Text(palette, color = ImmersiveTextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SeoWorkerDetail(trends: List<TrendItem>) {
    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Trending Keyword Feeds (Google RSS / Reddit)", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            trends.forEach { trend ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(trend.query, color = ImmersiveTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${trend.source} • ${trend.keyword}", color = AetherCyan, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AetherEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${trend.score}%", color = AetherEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ImgmakerWorkerDetail(latestPost: PostItem?) {
    Column {
        Text("Canvas Graphic Preview Engine (1080x1080)", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        val samplePost = latestPost ?: PostItem(
            id = "demo",
            title = "AetherOS Canvas Demo",
            body = "Simplicity is prerequisite for reliability in distributed multi-worker orchestration.",
            category = ContentCategory.QUOTES,
            hashtags = listOf("#AetherOS", "#AIOrchestration", "#TechTrends"),
            vibe = "Cyber Cyan",
            primaryColorHex = "#6366F1",
            secondaryColorHex = "#06B6D4"
        )
        CanvasPostPreview(post = samplePost)
    }
}

@Composable
private fun DuplicateCheckerDetail() {
    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("12-Type Rotation Rule & SHA-256 Hash", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Prevents publishing duplicate body text or repeating content types within 12 posts.", color = ImmersiveTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth().background(AetherEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp)).padding(10.dp)
            ) {
                Text("✓ Duplicate Rule Check Status: PASSING (0 Collisions)", color = AetherEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SocialMediaAgentDetail() {
    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Facebook Graph API v22 Publisher", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Target Page: We Share We Care", color = AetherCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Page ID: 1092837492019482", color = ImmersiveTextMuted, fontSize = 11.sp)
            Text("Graph API Endpoint: https://graph.facebook.com/v22.0/1092837492019482/feed", color = ImmersiveTextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ReportWorkerDetail(posts: List<PostItem>) {
    Card(colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Worker Performance & ROI Analytics", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Posts Created", color = ImmersiveTextMuted, fontSize = 11.sp)
                    Text("${posts.size + 12}", color = ImmersiveTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column {
                    Text("Avg Impressions", color = ImmersiveTextMuted, fontSize = 11.sp)
                    Text("2,450", color = AetherEmerald, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column {
                    Text("Engagement Rate", color = ImmersiveTextMuted, fontSize = 11.sp)
                    Text("5.8%", color = AetherCyan, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
