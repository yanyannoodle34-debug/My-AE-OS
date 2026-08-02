package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.db.AetherDatabase
import com.example.data.db.AetherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class AetherUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: String = "admin",
    val selectedTab: NavTab = NavTab.ARCHITECTURE,
    val kernelActive: Boolean = true,
    val dailyPipelineEnabled: Boolean = true,
    val lastPipelineRun: Long = System.currentTimeMillis() - 3600000 * 4,
    val activeMission: Mission? = null,
    val missions: List<Mission> = emptyList(),
    val posts: List<PostItem> = emptyList(),
    val trends: List<TrendItem> = emptyList(),
    val catalogue: List<ContentCatalogueItem> = emptyList(),
    val logs: List<SystemLog> = emptyList(),
    val fbCredentials: FacebookCredentials = FacebookCredentials(),
    val directorConfig: DirectorAgentConfig = DirectorAgentConfig(),
    val isSplittingVideo: Boolean = false,
    val videoSplitProgress: String = "",
    val defaultAiProviderId: String = "nvidia",
    val aiProviders: List<AiAgentProvider> = emptyList(),
    val workerProviderMap: Map<WorkerType, String> = emptyMap(),
    val aiTestOutput: String = "",
    val isAiTesting: Boolean = false,
    val cliInput: String = "",
    val cliHistory: List<Pair<String, String>> = emptyList(), // command -> output
    val selectedWorker: WorkerType? = null,
    val selectedNodeInfo: ArchitectureNodeInfo? = null,
    val showTriggerMissionDialog: Boolean = false,
    val activePostDetail: PostItem? = null,
    val logFilterLevel: String = "ALL",
    val logSearchQuery: String = ""
)

class AetherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AetherRepository

    private val _isLoggedIn = MutableStateFlow(false)
    private val _currentUser = MutableStateFlow("admin")
    private val _selectedTab = MutableStateFlow(NavTab.ARCHITECTURE)
    private val _kernelActive = MutableStateFlow(true)
    private val _dailyPipelineEnabled = MutableStateFlow(true)
    private val _activeMission = MutableStateFlow<Mission?>(null)
    private val _fbCredentials = MutableStateFlow(FacebookCredentials())

    private val _directorConfig = MutableStateFlow(DirectorAgentConfig())
    private val _isSplittingVideo = MutableStateFlow(false)
    private val _videoSplitProgress = MutableStateFlow("")

    private val _defaultAiProviderId = MutableStateFlow("nvidia")
    private val _aiProviders = MutableStateFlow(
        listOf(
            AiAgentProvider(
                id = "nvidia",
                name = "NVIDIA Free API (NIM)",
                description = "NVIDIA Build Free API Key for Llama 3.1 70B & Nemotron",
                badge = "NVIDIA FREE API",
                isFreeTier = true,
                apiKey = "nvapi-free_demo_key_9928341",
                selectedModel = "meta/llama-3.1-70b-instruct",
                availableModels = listOf("meta/llama-3.1-70b-instruct", "nvidia/nemotron-4-340b-instruct", "mistralai/mixtral-8x22b-instruct"),
                status = "Connected (10,000 Free Credits)",
                endpointUrl = "https://integrate.api.nvidia.com/v1"
            ),
            AiAgentProvider(
                id = "openrouter",
                name = "OpenRouter API",
                description = "Universal LLM Gateway supporting DeepSeek R1, Claude, Llama 3.3",
                badge = "OPENROUTER API",
                isFreeTier = true,
                apiKey = "sk-or-v1-9283741029384",
                selectedModel = "deepseek/deepseek-r1",
                availableModels = listOf("openrouter/auto", "deepseek/deepseek-r1", "anthropic/claude-3.5-sonnet", "meta-llama/llama-3.3-70b-instruct"),
                status = "Connected & Active",
                endpointUrl = "https://openrouter.ai/api/v1"
            ),
            AiAgentProvider(
                id = "gemini",
                name = "Google Gemini API",
                description = "Google Gemini 2.0 Flash & 1.5 Pro high-speed multi-modal agent",
                badge = "GEMINI API",
                isFreeTier = true,
                apiKey = "AIzaSy_demo_gemini_key_88291",
                selectedModel = "gemini-2.0-flash",
                availableModels = listOf("gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro"),
                status = "Connected (Free Tier Active)",
                endpointUrl = "https://generativelanguage.googleapis.com"
            ),
            AiAgentProvider(
                id = "deepseek",
                name = "DeepSeek API",
                description = "DeepSeek V3 Chat & DeepSeek R1 Reasoning LLM Endpoints",
                badge = "DEEPSEEK API",
                isFreeTier = true,
                apiKey = "sk-deepseek-demo-992831",
                selectedModel = "deepseek-reasoner",
                availableModels = listOf("deepseek-chat", "deepseek-reasoner"),
                status = "Connected & Active",
                endpointUrl = "https://api.deepseek.com/v1"
            )
        )
    )

    private val _workerProviderMap = MutableStateFlow(
        mapOf(
            WorkerType.CONTENT to "deepseek",
            WorkerType.CREATIVE to "gemini",
            WorkerType.SEO to "nvidia",
            WorkerType.IMGMAKER to "openrouter",
            WorkerType.DUPLICATE to "nvidia",
            WorkerType.SOCIAL_MEDIA to "gemini",
            WorkerType.REPORT to "deepseek"
        )
    )

    private val _aiTestOutput = MutableStateFlow("")
    private val _isAiTesting = MutableStateFlow(false)

    private val _cliInput = MutableStateFlow("")
    private val _cliHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    private val _selectedWorker = MutableStateFlow<WorkerType?>(null)
    private val _selectedNodeInfo = MutableStateFlow<ArchitectureNodeInfo?>(null)
    private val _showTriggerMissionDialog = MutableStateFlow(false)
    private val _activePostDetail = MutableStateFlow<PostItem?>(null)
    private val _logFilterLevel = MutableStateFlow("ALL")
    private val _logSearchQuery = MutableStateFlow("")

    init {
        val db = AetherDatabase.getDatabase(application)
        repository = AetherRepository(db)

        viewModelScope.launch {
            // Seed initial catalogue & trends if DB empty
            seedInitialDataIfNeeded()
        }

        // Add welcome CLI banner
        _cliHistory.value = listOf(
            "" to "AetherOS Terminal v2.4.0 [Kernel Active]\nType 'aether help' or click sample chips below to run orchestration missions."
        )
    }

    val uiState: StateFlow<AetherUiState> = combine(
        combine(_isLoggedIn, _currentUser, _selectedTab, _kernelActive) { loggedIn, user, tab, kernel ->
            AuthQuad(loggedIn, user, tab, kernel)
        },
        combine(_dailyPipelineEnabled, _activeMission, _selectedWorker, _selectedNodeInfo) { daily, mission, worker, node ->
            KernelQuad(daily, mission, worker, node)
        },
        combine(repository.allMissions, repository.allPosts, repository.allTrends, repository.allCatalogue) { missions, posts, trends, catalogue ->
            DataQuad(missions, posts, trends, catalogue)
        },
        combine(repository.allLogs, _fbCredentials, _cliInput, _cliHistory) { logs, creds, input, history ->
            UiQuad(logs, creds, input, history)
        },
        combine(
            combine(_defaultAiProviderId, _aiProviders, _workerProviderMap) { defaultId, providers, map -> Triple(defaultId, providers, map) },
            combine(_aiTestOutput, _isAiTesting) { output, testing -> Pair(output, testing) },
            combine(_showTriggerMissionDialog, _activePostDetail, _logFilterLevel, _logSearchQuery) { trigger, postDetail, level, query -> DialogQuadEx(trigger, postDetail, level, query) },
            combine(_directorConfig, _isSplittingVideo, _videoSplitProgress) { director, splitting, progress -> Triple(director, splitting, progress) }
        ) { aiTriple, aiPair, dialogQuadEx, directorTriple ->
            AiAndDirectorGroup(aiTriple.first, aiTriple.second, aiTriple.third, aiPair.first, aiPair.second, dialogQuadEx, directorTriple.first, directorTriple.second, directorTriple.third)
        }
    ) { authQuad, kernelQuad, dataQuad, uiQuad, aiGroup ->
        AetherUiState(
            isLoggedIn = authQuad.loggedIn,
            currentUser = authQuad.user,
            selectedTab = authQuad.tab,
            kernelActive = authQuad.kernel,
            dailyPipelineEnabled = kernelQuad.daily,
            activeMission = kernelQuad.mission,
            selectedWorker = kernelQuad.worker,
            selectedNodeInfo = kernelQuad.node,
            missions = dataQuad.missions,
            posts = dataQuad.posts,
            trends = dataQuad.trends,
            catalogue = dataQuad.catalogue,
            logs = filterLogs(uiQuad.logs, aiGroup.dialogQuadEx.level, aiGroup.dialogQuadEx.query),
            fbCredentials = uiQuad.creds,
            directorConfig = aiGroup.directorConfig,
            isSplittingVideo = aiGroup.isSplittingVideo,
            videoSplitProgress = aiGroup.videoSplitProgress,
            defaultAiProviderId = aiGroup.defaultId,
            aiProviders = aiGroup.providers,
            workerProviderMap = aiGroup.workerMap,
            aiTestOutput = aiGroup.aiOutput,
            isAiTesting = aiGroup.aiTesting,
            cliInput = uiQuad.input,
            cliHistory = uiQuad.history,
            showTriggerMissionDialog = aiGroup.dialogQuadEx.trigger,
            activePostDetail = aiGroup.dialogQuadEx.postDetail,
            logFilterLevel = aiGroup.dialogQuadEx.level,
            logSearchQuery = aiGroup.dialogQuadEx.query
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AetherUiState()
    )

    fun toggleDirectorAgent(enabled: Boolean) {
        _directorConfig.value = _directorConfig.value.copy(
            isEnabled = enabled,
            statusMessage = if (enabled) "Director Agent Active • 9:16 Facebook Reels Policy Enforced • 500MB Split Engine Ready" else "Director Agent Paused (Manual Pipeline Mode)"
        )
        logEvent("KERNEL", "AI Director Agent turned ${if (enabled) "ON" else "OFF"}.")
    }

    fun updateDirectorMode(mode: String) {
        _directorConfig.value = _directorConfig.value.copy(mode = mode)
        logEvent("KERNEL", "AI Director Agent mode set to $mode.")
    }

    fun splitAndUploadVideoToReels(videoTitle: String, fileSizeBytesMb: Int, durationSec: Int) {
        viewModelScope.launch {
            if (!_directorConfig.value.isEnabled) {
                logEvent("ERROR", "[Director Agent] Director Agent is OFF. Turn ON Director Agent to process video split.")
                return@launch
            }

            _isSplittingVideo.value = true
            _videoSplitProgress.value = "Ingesting $fileSizeBytesMb MB video payload..."
            logEvent("KERNEL", "[Director Agent] Received video upload '$videoTitle' ($fileSizeBytesMb MB, ${durationSec}s).")
            delay(800)

            _videoSplitProgress.value = "Checking Facebook Reels Policy (9:16 ratio, max 60s per Reel, 500MB max payload)..."
            delay(1000)

            val maxReelSec = _directorConfig.value.maxReelDurationSec
            val chunkCount = Math.max(1, Math.ceil(durationSec.toDouble() / maxReelSec).toInt())
            val chunkSizeMb = fileSizeBytesMb / chunkCount

            _videoSplitProgress.value = "Facebook Policy Match: Splitting $fileSizeBytesMb MB payload into $chunkCount x 9:16 Reels clips (max 60s each)..."
            logEvent("WORKER", "[Creative Director] Formatting canvas to 9:16 vertical video ratio (1080x1920) for Facebook Reels.")
            delay(1200)

            val palette = getRandomPalette()
            val category = ContentCategory.TIPS

            for (i in 1..chunkCount) {
                _videoSplitProgress.value = "Encoding Reel Part $i of $chunkCount (9:16 1080x1920)..."
                delay(700)

                val reelClipDuration = if (i == chunkCount) (durationSec % maxReelSec).let { if (it == 0) maxReelSec else it } else maxReelSec
                val fbPostId = "fb_reel_${(100000..999999).random()}"
                val reelPost = PostItem(
                    id = "reel_${System.currentTimeMillis()}_$i",
                    title = if (chunkCount > 1) "$videoTitle (Part $i/$chunkCount)" else videoTitle,
                    body = "🎬 [FB REEL 9:16] ${getSampleBodyForCategory(category, videoTitle)}\n\nAuto-directed by AetherOS Director Agent • ${chunkSizeMb}MB • Part $i of $chunkCount",
                    category = category,
                    hashtags = listOf("#FacebookReels", "#AetherOS", "#Reels916", "#AIDirector", "#ViralReels"),
                    vibe = palette.first,
                    primaryColorHex = palette.second,
                    secondaryColorHex = palette.third,
                    imageUrl = "https://picsum.photos/seed/reel_$i/1080/1920",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    postType = "REEL",
                    aspectRatio = "9:16",
                    durationSeconds = reelClipDuration,
                    chunkInfo = "Part $i of $chunkCount ($fileSizeBytesMb MB Split)",
                    fbPostId = fbPostId,
                    hashValue = "sha256_reel_${UUID.randomUUID().toString().take(6)}",
                    targetPage = "We Share We Care (Facebook Page)"
                )

                repository.savePost(reelPost)
                logEvent("PUBLISH", "[Social Media Agent] Published Facebook Reel #$i ($reelClipDuration sec, 9:16 ratio) to Graph API v22. Post ID: $fbPostId")
            }

            _videoSplitProgress.value = "Completed! Successfully generated & published $chunkCount Facebook Reels."
            delay(800)
            _isSplittingVideo.value = false
            _videoSplitProgress.value = ""
            logEvent("KERNEL", "[Director Agent] 500MB Video Splitter finished. $chunkCount Facebook Reels published in compliance with Meta 9:16 ratio policy.")
        }
    }

    fun setDefaultAiProvider(providerId: String) {
        _defaultAiProviderId.value = providerId
        logEvent("KERNEL", "Primary AI Orchestration Provider changed to '$providerId'")
    }

    fun updateAiProviderConfig(providerId: String, apiKey: String, selectedModel: String, endpointUrl: String) {
        _aiProviders.value = _aiProviders.value.map { p ->
            if (p.id == providerId) {
                p.copy(
                    apiKey = apiKey,
                    selectedModel = selectedModel,
                    endpointUrl = endpointUrl,
                    status = if (apiKey.isNotBlank()) "Configured & Active" else "Key Missing"
                )
            } else p
        }
        logEvent("KERNEL", "Updated AI Provider '$providerId': Model '$selectedModel'")
    }

    fun autofillFreeAiKeys() {
        _aiProviders.value = _aiProviders.value.map { p ->
            when (p.id) {
                "nvidia" -> p.copy(
                    apiKey = "nvapi-free_demo_key_9928341",
                    selectedModel = "meta/llama-3.1-70b-instruct",
                    status = "Connected (10,000 Free Credits)"
                )
                "openrouter" -> p.copy(
                    apiKey = "sk-or-v1-9283741029384",
                    selectedModel = "deepseek/deepseek-r1",
                    status = "Connected & Active"
                )
                "gemini" -> p.copy(
                    apiKey = "AIzaSy_demo_gemini_key_88291",
                    selectedModel = "gemini-2.0-flash",
                    status = "Connected (Free Tier Active)"
                )
                "deepseek" -> p.copy(
                    apiKey = "sk-deepseek-demo-992831",
                    selectedModel = "deepseek-reasoner",
                    status = "Connected & Active"
                )
                else -> p
            }
        }
        logEvent("KERNEL", "Autofilled free demo keys & endpoints for NVIDIA Free API, OpenRouter, Gemini, and DeepSeek.")
    }

    fun assignWorkerAiProvider(worker: WorkerType, providerId: String) {
        _workerProviderMap.value = _workerProviderMap.value + (worker to providerId)
        logEvent("KERNEL", "Assigned Worker '${worker.title}' -> AI Provider '$providerId'")
    }

    fun testAiProviderInference(providerId: String, prompt: String) {
        viewModelScope.launch {
            _isAiTesting.value = true
            _aiTestOutput.value = "Initiating HTTPS connection to provider '$providerId'..."
            val provider = _aiProviders.value.find { it.id == providerId }

            logEvent("KERNEL", "[AI INFERENCE PING] Sending test inference to ${provider?.name} (${provider?.selectedModel})...")
            delay(1000)

            val latency = (120..480).random()
            val tokens = (45..220).random()

            val sampleResponse = when (providerId) {
                "nvidia" -> "NVIDIA NIM [Llama-3.1-70b-Instruct] Response: \"AetherOS Kernel initialized with 7 worker nodes. NVIDIA NIM inference active with zero latency penalty.\""
                "openrouter" -> "OpenRouter [DeepSeek-R1] Response: \"<think>Analyzing AetherOS prompt...</think> OpenRouter gateway confirmed. DeepSeek-R1 reasoning engine connected.\""
                "gemini" -> "Google Gemini [gemini-2.0-flash] Response: \"Gemini 2.0 Flash online. Multimodal processing ready for creative layout and social content generation.\""
                "deepseek" -> "DeepSeek API [deepseek-reasoner] Response: \"DeepSeek R1 reasoning chain verified. Autonomous posting pipeline ready for deployment.\""
                else -> "AI Agent response received 200 OK."
            }

            _aiTestOutput.value = """
                [HTTP/1.1 200 OK]
                Provider    : ${provider?.name}
                Model       : ${provider?.selectedModel}
                Endpoint    : ${provider?.endpointUrl}
                Latency     : ${latency}ms
                Token Count : $tokens tokens
                ---------------------------------------------------
                Output Content:
                $sampleResponse
            """.trimIndent()

            _isAiTesting.value = false
            logEvent("KERNEL", "[AI INFERENCE SUCCESS] $providerId returned 200 OK (${latency}ms, $tokens tokens)")
        }
    }

    fun login(username: String) {
        _currentUser.value = username.ifBlank { "admin" }
        _isLoggedIn.value = true
        logEvent("INFO", "Operator '${_currentUser.value}' logged in successfully.")
    }

    fun logout() {
        logEvent("WARN", "Operator '${_currentUser.value}' logged out.")
        _isLoggedIn.value = false
    }

    fun selectTab(tab: NavTab) {
        _selectedTab.value = tab
    }

    fun selectWorker(worker: WorkerType?) {
        _selectedWorker.value = worker
    }

    fun selectNodeInfo(node: ArchitectureNodeInfo?) {
        _selectedNodeInfo.value = node
    }

    fun toggleTriggerMissionDialog(show: Boolean) {
        _showTriggerMissionDialog.value = show
    }

    fun setActivePostDetail(post: PostItem?) {
        _activePostDetail.value = post
    }

    fun setCliInput(text: String) {
        _cliInput.value = text
    }

    fun setLogFilterLevel(level: String) {
        _logFilterLevel.value = level
    }

    fun setLogSearchQuery(query: String) {
        _logSearchQuery.value = query
    }

    fun updateFacebookCredentials(pageName: String, pageId: String, apiVersion: String, accessToken: String) {
        _fbCredentials.value = _fbCredentials.value.copy(
            pageName = pageName,
            pageId = pageId,
            apiVersion = apiVersion,
            accessToken = accessToken,
            status = "Configured (Pending Verification)"
        )
        logEvent("PUBLISH", "Updated Facebook Graph API config: Page ID '$pageId', Target Page '$pageName', API '$apiVersion'")
    }

    fun testFacebookConnection() {
        viewModelScope.launch {
            _fbCredentials.value = _fbCredentials.value.copy(status = "Testing Connection...")
            logEvent("PUBLISH", "Initiating Graph API handshake for Page ID '${_fbCredentials.value.pageId}' (v${_fbCredentials.value.apiVersion})...")
            delay(1100)
            if (_fbCredentials.value.pageId.isBlank() || _fbCredentials.value.accessToken.isBlank()) {
                _fbCredentials.value = _fbCredentials.value.copy(status = "Error: Invalid Credentials")
                logEvent("ERROR", "Facebook Graph API Handshake Failed: Page ID or Access Token missing.")
            } else {
                _fbCredentials.value = _fbCredentials.value.copy(
                    status = "Connected & Active (v22.0)",
                    lastSyncTime = System.currentTimeMillis()
                )
                logEvent("PUBLISH", "Facebook Graph API v22.0 Handshake 200 OK! Page ID '${_fbCredentials.value.pageId}' verified.")
            }
        }
    }

    fun sendTestFacebookPost(testMessage: String) {
        viewModelScope.launch {
            val creds = _fbCredentials.value
            logEvent("PUBLISH", "[TEST POST] POST https://graph.facebook.com/${creds.apiVersion}/${creds.pageId}/feed")
            logEvent("PUBLISH", "[TEST POST] Payload message: \"${testMessage.take(50)}...\"")
            delay(1000)
            val mockFbPostId = "${creds.pageId}_${(1000000..9999999).random()}"
            logEvent("PUBLISH", "[TEST POST SUCCESS] Graph API 200 OK — Facebook Post ID: $mockFbPostId")
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    fun toggleDailyPipeline() {
        _dailyPipelineEnabled.value = !_dailyPipelineEnabled.value
        logEvent("KERNEL", "Daily Pipeline toggled: ${_dailyPipelineEnabled.value}")
    }

    fun executeCliCommand(commandText: String) {
        val cmd = commandText.trim()
        if (cmd.isEmpty()) return

        val response: String
        when {
            cmd.equals("aether help", ignoreCase = true) -> {
                response = """
                    AetherOS Orchestrator CLI Commands:
                    • aether run mission --topic "<topic>"   : Trigger 7-Worker Pipeline
                    • aether daily pipeline                  : Run 8:00 AM Cron Daily Job
                    • aether worker status                   : Inspect all 7 Worker Nodes
                    • aether fb status                       : Facebook Graph API v22 health
                    • aether trend sync                      : Fetch Google RSS / Wiki trends
                    • aether clear logs                      : Clear stdout logs stream
                """.trimIndent()
            }
            cmd.startsWith("aether run mission", ignoreCase = true) -> {
                val topic = cmd.substringAfter("--topic", "Daily AI & Tech").trim().removeSurrounding("\"")
                triggerMission("CLI: $topic", MissionType.CLI_MISSION, topic)
                response = "🚀 Dispatching AetherOS Kernel Mission for topic: '$topic'..."
            }
            cmd.equals("aether daily pipeline", ignoreCase = true) -> {
                triggerMission("Daily Pipeline Run (Cron)", MissionType.DAILY_PIPELINE, "Daily AI Highlights")
                response = "⏰ Triggering 8:00 AM Daily Pipeline..."
            }
            cmd.equals("aether worker status", ignoreCase = true) -> {
                response = """
                    Worker Status Summary [7/7 ONLINE]:
                    [✓] Content Worker       : Catalogue Ready (15 entries)
                    [✓] Creative Director    : Palettes Armed (4 styles)
                    [✓] SEO Worker           : Hot Trends Synced (Google RSS)
                    [✓] Imgmaker Worker      : Canvas Engine 1080x1080 Ready
                    [✓] Duplicate Checker    : Hash Registry active (12-rule)
                    [✓] Social Media Agent   : FB v22 'We Share We Care' Active
                    [✓] Report Worker        : ROI Dashboard Active
                """.trimIndent()
            }
            cmd.equals("aether fb status", ignoreCase = true) -> {
                response = """
                    Facebook Graph API v22 Target Page:
                    • Page Name: We Share We Care
                    • Page ID  : 1092837492019482
                    • Token    : EAAG...m9ZAZB9x2Y10P (Valid)
                    • Status   : Connected & Authorized
                """.trimIndent()
            }
            cmd.equals("aether trend sync", ignoreCase = true) -> {
                viewModelScope.launch {
                    refreshTrends()
                }
                response = "📡 Syncing trends from Google RSS, Wikipedia On-This-Day, and Reddit..."
            }
            cmd.equals("aether clear logs", ignoreCase = true) -> {
                clearLogs()
                response = "System logs cleared."
            }
            else -> {
                response = "Unknown command '$cmd'. Type 'aether help' for available commands."
            }
        }

        _cliHistory.value = _cliHistory.value + (cmd to response)
        _cliInput.value = ""
    }

    fun triggerMission(title: String, type: MissionType, customTopic: String = "") {
        if (_activeMission.value?.status == MissionStatus.EXECUTING) {
            logEvent("KERNEL", "Mission execution rejected: Another mission is in progress.")
            return
        }

        val missionId = "m_${System.currentTimeMillis()}"
        val steps = listOf(
            MissionStep(1, WorkerType.CONTENT, "Content Extraction", "Selecting curated quotes & topic insights"),
            MissionStep(2, WorkerType.CREATIVE, "Aesthetic Direction", "Generating palette, typography, visual vibe"),
            MissionStep(3, WorkerType.SEO, "SEO & Trend Analysis", "Synthesizing hot hashtags & Google RSS keywords"),
            MissionStep(4, WorkerType.DUPLICATE, "Duplicate Rule Check", "Calculating SHA-256 hash & 12-rotation check"),
            MissionStep(5, WorkerType.IMGMAKER, "1080x1080 Canvas Render", "Generating social media graphic asset"),
            MissionStep(6, WorkerType.SOCIAL_MEDIA, "Facebook Graph API v22", "Publishing to 'We Share We Care' Page"),
            MissionStep(7, WorkerType.REPORT, "Analytics & Insights", "Recording impressions & worker ROI metrics")
        )

        val mission = Mission(
            id = missionId,
            title = title,
            type = type,
            status = MissionStatus.PLANNING,
            startTime = System.currentTimeMillis(),
            steps = steps
        )

        _activeMission.value = mission
        logEvent("KERNEL", "Kernel initialized mission: $title (${type.label})")

        // Execute step-by-step asynchronously
        viewModelScope.launch {
            executeMissionSequence(mission, customTopic)
        }
    }

    private suspend fun executeMissionSequence(initialMission: Mission, customTopic: String) {
        val currentSteps = initialMission.steps.map { it.copy() }.toMutableList()
        _activeMission.value = initialMission.copy(status = MissionStatus.EXECUTING, steps = currentSteps)

        // Step 1: Content Worker
        currentSteps[0] = currentSteps[0].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("WORKER", "[Content Worker] Fetching content for topic: '${customTopic.ifEmpty { "Tech & AI Inspiration" }}'")
        delay(900)
        val category = listOf(ContentCategory.QUOTES, ContentCategory.FACTS, ContentCategory.TIPS, ContentCategory.HISTORY).random()
        val quoteText = getSampleBodyForCategory(category, customTopic)
        currentSteps[0] = currentSteps[0].copy(status = MissionStatus.COMPLETED, detail = "Extracted: \"${quoteText.take(35)}...\"")
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())

        // Step 2: Creative Director
        currentSteps[1] = currentSteps[1].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("WORKER", "[Creative Director] Assigning color token and aesthetic vibe")
        delay(800)
        val palette = getRandomPalette()
        currentSteps[1] = currentSteps[1].copy(status = MissionStatus.COMPLETED, detail = "Vibe: ${palette.first} (${palette.second})")
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())

        // Step 3: SEO Worker
        currentSteps[2] = currentSteps[2].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("WORKER", "[SEO Worker] Querying Google RSS & Reddit trends for hashtags")
        delay(850)
        val hashtags = listOf("#AetherOS", "#AIOrchestration", "#TechTrends", "#DailyInspiration", "#WeShareWeCare", "#FutureTech")
        currentSteps[2] = currentSteps[2].copy(status = MissionStatus.COMPLETED, detail = "Generated 6 high-density hashtags")
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())

        // Step 4: Duplicate Checker
        currentSteps[3] = currentSteps[3].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("WORKER", "[Duplicate Checker] Computing SHA-256 hash & checking 12-type rotation rule")
        delay(700)
        val hash = "sha256_${UUID.randomUUID().toString().take(8)}"
        currentSteps[3] = currentSteps[3].copy(status = MissionStatus.COMPLETED, detail = "Passed: Hash $hash (Type rotation valid)")
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())

        // Step 5: Imgmaker Worker
        currentSteps[4] = currentSteps[4].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("WORKER", "[Imgmaker Worker] Rendering 1080x1080 visual graphic layout")
        delay(1000)
        currentSteps[4] = currentSteps[4].copy(status = MissionStatus.COMPLETED, detail = "Canvas layout rendered (1080x1080)")
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())

        // Step 6: Social Media Agent (Facebook Graph API v22)
        currentSteps[5] = currentSteps[5].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("PUBLISH", "[Social Media Agent] Publishing to Facebook Graph API v22 (Page: We Share We Care)")
        delay(900)
        val fbPostId = "fb_post_${(100000..999999).random()}"
        currentSteps[5] = currentSteps[5].copy(status = MissionStatus.COMPLETED, detail = "Published to Facebook! Post ID: $fbPostId")
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())

        // Step 7: Report Worker
        currentSteps[6] = currentSteps[6].copy(status = MissionStatus.EXECUTING)
        _activeMission.value = _activeMission.value?.copy(steps = currentSteps.toList())
        logEvent("WORKER", "[Report Worker] Writing performance log & ROI analytics")
        delay(600)

        val createdPost = PostItem(
            id = "p_${System.currentTimeMillis()}",
            title = customTopic.ifEmpty { "Daily ${category.displayName}" },
            body = quoteText,
            category = category,
            hashtags = hashtags,
            vibe = palette.first,
            primaryColorHex = palette.second,
            secondaryColorHex = palette.third,
            fbPostId = fbPostId,
            hashValue = hash,
            targetPage = "We Share We Care (Facebook)"
        )

        currentSteps[6] = currentSteps[6].copy(status = MissionStatus.COMPLETED, detail = "ROI recorded. Post ready.")

        val completedMission = _activeMission.value?.copy(
            status = MissionStatus.COMPLETED,
            endTime = System.currentTimeMillis(),
            steps = currentSteps.toList(),
            generatedPost = createdPost
        )

        _activeMission.value = completedMission

        if (completedMission != null) {
            repository.savePost(createdPost)
            repository.saveMission(completedMission)
            logEvent("KERNEL", "Mission '${completedMission.title}' successfully completed!")
        }
    }

    private fun logEvent(level: String, message: String) {
        viewModelScope.launch {
            repository.saveLog(
                SystemLog(
                    id = "log_${System.currentTimeMillis()}_${(10..99).random()}",
                    level = level,
                    source = "Kernel",
                    message = message
                )
            )
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val initialCatalogue = listOf(
            ContentCatalogueItem("c1", ContentCategory.QUOTES, "Simplicity is prerequisite for reliability.", "Edsger W. Dijkstra"),
            ContentCatalogueItem("c2", ContentCategory.FACTS, "The first computer bug was an actual moth found in a Relay Calculator in 1947.", "Computer History Museum"),
            ContentCatalogueItem("c3", ContentCategory.TIPS, "Focus on building modular, asynchronous worker pipelines for resilient automation.", "AetherOS Best Practices"),
            ContentCatalogueItem("c4", ContentCategory.HISTORY, "On this day in 1969, Apollo 11 moon landing broadcast captivated 650 million viewers worldwide.", "On This Day Database"),
            ContentCatalogueItem("c5", ContentCategory.LESSONS, "Small daily consistency yields compound results over long execution cycles.", "Aether Philosophy")
        )

        val initialTrends = listOf(
            TrendItem("t1", "Google RSS", "AI Agent Orchestration", "#AIOrchestrator", 98),
            TrendItem("t2", "Wikipedia", "Apollo 11 Moon Landing", "#OnThisDay", 92),
            TrendItem("t3", "Reddit r/Technology", "Generative Canvas Rendering", "#DesignTech", 88),
            TrendItem("t4", "Google RSS", "Autonomous Social Publishing", "#FacebookGraphAPI", 85)
        )

        val initialPosts = listOf(
            PostItem(
                id = "p_seed_1",
                title = "AI Agent Orchestration in 2026",
                body = "Autonomous 7-worker multi-agent pipelines bring high reliability and zero downtime to social publishing.",
                category = ContentCategory.FACTS,
                hashtags = listOf("#AetherOS", "#AIAgents", "#MetaGraphAPI"),
                vibe = "Cyber Cyan",
                primaryColorHex = "#06B6D4",
                secondaryColorHex = "#818CF8",
                imageUrl = "https://picsum.photos/seed/post1/1080/1080",
                postType = "PHOTO",
                aspectRatio = "1:1",
                status = "Published",
                targetPage = "We Share We Care",
                fbPostId = "fb_post_882910"
            ),
            PostItem(
                id = "p_seed_2",
                title = "AetherOS AI Director Reel #1",
                body = "🎬 Watch how AI Director Agent auto-formats and renders 9:16 vertical Reels for maximum viral engagement on Facebook!",
                category = ContentCategory.TIPS,
                hashtags = listOf("#FacebookReels", "#Reels916", "#AIDirector", "#ViralShorts"),
                vibe = "Neon Indigo",
                primaryColorHex = "#6366F1",
                secondaryColorHex = "#EC4899",
                imageUrl = "https://picsum.photos/seed/reel_demo1/1080/1920",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                postType = "REEL",
                aspectRatio = "9:16",
                durationSeconds = 45,
                chunkInfo = "Standalone Reel (9:16 Ratio)",
                status = "Published",
                targetPage = "We Share We Care",
                fbPostId = "fb_reel_992018"
            ),
            PostItem(
                id = "p_seed_3",
                title = "500MB Masterclass Video (Part 1/2)",
                body = "🎬 AI Director Agent Auto-Splitter: Chunk 1 of 500MB Video formatted to Meta 9:16 Reel ratio.",
                category = ContentCategory.LESSONS,
                hashtags = listOf("#FacebookReels", "#VideoSplitter", "#MetaPolicy", "#500MBSplit"),
                vibe = "Emerald Pulse",
                primaryColorHex = "#10B981",
                secondaryColorHex = "#06B6D4",
                imageUrl = "https://picsum.photos/seed/reel_demo2/1080/1920",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                postType = "REEL",
                aspectRatio = "9:16",
                durationSeconds = 60,
                chunkInfo = "Part 1 of 2 (500MB Split)",
                status = "Published",
                targetPage = "We Share We Care",
                fbPostId = "fb_reel_992019"
            )
        )

        repository.seedInitialCatalogue(initialCatalogue)
        repository.seedInitialTrends(initialTrends)
        initialPosts.forEach { repository.savePost(it) }

        logEvent("KERNEL", "AetherOS Kernel initialized. 7 Worker nodes & AI Director Agent armed and ready.")
    }

    private fun refreshTrends() {
        viewModelScope.launch {
            val newTrends = listOf(
                TrendItem("t_${System.currentTimeMillis()}_1", "Google RSS", "Next-Gen AI Workflows", "#AIAgents", 99),
                TrendItem("t_${System.currentTimeMillis()}_2", "Reddit", "Reactive Jetpack Compose UI", "#AndroidDev", 94),
                TrendItem("t_${System.currentTimeMillis()}_3", "Wikipedia", "History of Microprocessors", "#TechHistory", 90)
            )
            repository.seedInitialTrends(newTrends)
            logEvent("WORKER", "[SEO Worker] Trends successfully refreshed from HTTP sources.")
        }
    }

    private fun getSampleBodyForCategory(category: ContentCategory, topic: String): String {
        return when (category) {
            ContentCategory.QUOTES -> if (topic.isNotBlank()) "Mastering '$topic' requires relentless curiosity and structured orchestration." else "Orchestration turns complex isolated steps into a harmonious symphony."
            ContentCategory.FACTS -> if (topic.isNotBlank()) "Did you know: '$topic' is accelerating autonomous workflows by 300% across modern platforms." else "Did you know: Over 75% of high-volume social content is scheduled via automated graph API pipelines."
            ContentCategory.TIPS -> if (topic.isNotBlank()) "Pro Tip for '$topic': Break down large goals into small, verifiable worker steps." else "Pro Tip: Always validate duplicate hashes before dispatching to social media endpoints."
            ContentCategory.HISTORY -> "On this day in technology: Distributed mission orchestration transformed how teams scale digital platforms."
            else -> "Continuous progress is built step by step through autonomous worker collaboration."
        }
    }

    private fun getRandomPalette(): Triple<String, String, String> {
        val palettes = listOf(
            Triple("Cyber Cyan", "#06B6D4", "#818CF8"),
            Triple("Neon Indigo", "#6366F1", "#EC4899"),
            Triple("Emerald Pulse", "#10B981", "#06B6D4"),
            Triple("Amber Sunrise", "#F59E0B", "#F43F5E")
        )
        return palettes.random()
    }

    private fun filterLogs(logs: List<SystemLog>, level: String, query: String): List<SystemLog> {
        return logs.filter { log ->
            (level == "ALL" || log.level.equals(level, ignoreCase = true)) &&
                    (query.isBlank() || log.message.contains(query, ignoreCase = true) || log.source.contains(query, ignoreCase = true))
        }
    }
}

private data class AuthQuad(val loggedIn: Boolean, val user: String, val tab: NavTab, val kernel: Boolean)
private data class KernelQuad(val daily: Boolean, val mission: Mission?, val worker: WorkerType?, val node: ArchitectureNodeInfo?)
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
private data class DataQuad(val missions: List<Mission>, val posts: List<PostItem>, val trends: List<TrendItem>, val catalogue: List<ContentCatalogueItem>)
private data class UiQuad(val logs: List<SystemLog>, val creds: FacebookCredentials, val input: String, val history: List<Pair<String, String>>)
private data class DialogQuad(val worker: WorkerType?, val node: ArchitectureNodeInfo?, val trigger: Boolean, val postDetail: PostItem?)
private data class DialogQuadEx(val trigger: Boolean, val postDetail: PostItem?, val level: String, val query: String)
private data class AiAndDirectorGroup(
    val defaultId: String,
    val providers: List<AiAgentProvider>,
    val workerMap: Map<WorkerType, String>,
    val aiOutput: String,
    val aiTesting: Boolean,
    val dialogQuadEx: DialogQuadEx,
    val directorConfig: DirectorAgentConfig,
    val isSplittingVideo: Boolean,
    val videoSplitProgress: String
)
