package com.example.data

enum class NavTab(val title: String, val iconName: String) {
    ARCHITECTURE("Architecture", "hub"),
    KERNEL("Kernel & Pipeline", "memory"),
    WORKERS("7 Workers", "engineering"),
    AI_AGENTS("AI Agents", "smart_toy"),
    CLI("CLI Shell", "terminal"),
    LOGS_CONFIG("Logs & Config", "settings")
}

enum class LayerType(val title: String, val subtitle: String) {
    ENTRY_POINTS("ENTRY POINTS", "CLI Shell • Daily Pipeline • Single Post • Cron Jobs"),
    KERNEL("KERNEL", "Mission Orchestration • Task Planner • Scheduler • Worker Manager"),
    WORKERS("7 WORKERS", "Content • Creative Director • SEO • Imgmaker • Duplicate Checker • Social Media • Report"),
    DATA_SOURCES("DATA & TREND SOURCES", "Catalogue • HTTP Trends (RSS/Wiki/Reddit) • On This Day • Credentials"),
    PUBLISHERS("PUBLISHERS", "Facebook Graph API v22 • Mock Publisher (fallback)")
}

enum class WorkerType(
    val id: String,
    val title: String,
    val role: String,
    val iconName: String,
    val colorHex: String
) {
    CONTENT("content", "Content Worker", "Quotes, facts, tips & historical events", "article", "#818CF8"),
    CREATIVE("creative", "Creative Director", "Vibe, color palette, font tokens & layout", "palette", "#EC4899"),
    SEO("seo", "SEO Worker", "Trending keywords, hashtags & RSS monitoring", "search", "#10B981"),
    IMGMAKER("imgmaker", "Imgmaker Worker", "Renders 1080x1080 visual social graphics", "image", "#F59E0B"),
    DUPLICATE("duplicate", "Duplicate Checker", "Hash validator & 12-type rotation rule", "verified", "#3B82F6"),
    SOCIAL_MEDIA("social", "Social Media Agent", "Graph API v22 publisher & page engagement", "share", "#06B6D4"),
    REPORT("report", "Report Worker", "Analytics, ROI metrics & worker suggestions", "analytics", "#8B5CF6")
}

enum class MissionStatus(val label: String, val colorHex: String) {
    IDLE("Idle", "#64748B"),
    PLANNING("Planning", "#F59E0B"),
    EXECUTING("Executing", "#3B82F6"),
    COMPLETED("Completed", "#10B981"),
    FAILED("Failed", "#EF4444")
}

enum class MissionType(val label: String) {
    DAILY_PIPELINE("Daily Pipeline (8:00 AM Cron)"),
    CLI_MISSION("CLI Mission Command"),
    SINGLE_POST("Single Post Trigger"),
    CRON_JOB("Scheduled ImgMaker Cron")
}

enum class ContentCategory(val displayName: String) {
    QUOTES("Curated Quote"),
    FACTS("Science & Tech Fact"),
    TIPS("Productivity Tip"),
    HISTORY("On This Day History"),
    LESSONS("Life Lesson"),
    NEWS("Trending News Insight")
}

data class MissionStep(
    val stepIndex: Int,
    val workerType: WorkerType,
    val stepName: String,
    val detail: String,
    var status: MissionStatus = MissionStatus.IDLE,
    val timestamp: Long = System.currentTimeMillis()
)

data class PostItem(
    val id: String,
    val title: String,
    val body: String,
    val category: ContentCategory,
    val hashtags: List<String>,
    val vibe: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val postType: String = "PHOTO", // "PHOTO", "REEL", "SPLIT_REEL_CHUNK"
    val aspectRatio: String = "1:1", // "1:1" for Photo, "9:16" for Reel
    val durationSeconds: Int = 0,
    val chunkInfo: String? = null,
    val status: String = "Published",
    val targetPage: String = "We Share We Care",
    val fbPostId: String? = null,
    val hashValue: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val impressions: Int = (120..3500).random(),
    val engagementRate: Float = (2.4f + Math.random().toFloat() * 5f)
)

data class DirectorAgentConfig(
    val isEnabled: Boolean = true,
    val mode: String = "AUTO_DIRECT", // "AUTO_DIRECT", "REELS_ONLY", "PHOTO_ONLY"
    val allowPhoto: Boolean = true,
    val allowReels: Boolean = true,
    val maxVideoSizeMb: Int = 500,
    val targetAspectRatio: String = "9:16 (Vertical Reel)",
    val maxReelDurationSec: Int = 60,
    val autoSplitLargeVideo: Boolean = true,
    val statusMessage: String = "Director Agent Active • 9:16 Facebook Reels Policy Enforced • 500MB Split Engine Ready"
)

data class Mission(
    val id: String,
    val title: String,
    val type: MissionType,
    var status: MissionStatus,
    val startTime: Long,
    var endTime: Long? = null,
    val targetPage: String = "We Share We Care (Facebook)",
    val steps: List<MissionStep>,
    var generatedPost: PostItem? = null
)

data class TrendItem(
    val id: String,
    val source: String, // Google RSS, Wikipedia, Reddit
    val query: String,
    val keyword: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class ContentCatalogueItem(
    val id: String,
    val category: ContentCategory,
    val text: String,
    val authorOrSource: String,
    val isUsed: Boolean = false
)

data class SystemLog(
    val id: String,
    val level: String, // INFO, KERNEL, WORKER, PUBLISH, ERROR
    val source: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class FacebookCredentials(
    val pageName: String = "We Share We Care",
    val pageId: String = "1092837492019482",
    val apiVersion: String = "v22.0",
    val accessToken: String = "EAAG...m9ZAZB9x2Y10P",
    val status: String = "Connected & Active",
    val lastSyncTime: Long = System.currentTimeMillis()
)

data class ArchitectureNodeInfo(
    val id: String,
    val name: String,
    val layer: LayerType,
    val description: String,
    val status: String,
    val detailMetrics: String
)

data class AiAgentProvider(
    val id: String, // "nvidia", "openrouter", "gemini", "deepseek"
    val name: String,
    val description: String,
    val badge: String,
    val isFreeTier: Boolean = true,
    var apiKey: String = "",
    var selectedModel: String = "",
    val availableModels: List<String>,
    var status: String = "Not Configured",
    val endpointUrl: String = ""
)

data class AiAgentsConfig(
    val defaultProviderId: String = "nvidia",
    val providers: List<AiAgentProvider> = listOf()
)

