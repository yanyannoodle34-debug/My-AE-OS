package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MissionType
import com.example.data.NavTab
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.dialogs.PostDetailDialog
import com.example.ui.dialogs.TriggerMissionDialog
import com.example.ui.screens.*
import com.example.ui.theme.AetherTheme
import com.example.ui.theme.ImmersiveBackground
import com.example.viewmodel.AetherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetherTheme {
                AetherApp()
            }
        }
    }
}

@Composable
fun AetherApp(
    viewModel: AetherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { username ->
                viewModel.login(username)
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(ImmersiveBackground),
            containerColor = ImmersiveBackground,
            topBar = {
                TopBar(
                    kernelActive = uiState.kernelActive,
                    dailyPipelineEnabled = uiState.dailyPipelineEnabled,
                    activeMission = uiState.activeMission,
                    currentUser = uiState.currentUser,
                    onLogout = { viewModel.logout() },
                    onTriggerQuickRun = {
                        viewModel.triggerMission("Quick Daily Run", MissionType.DAILY_PIPELINE)
                    },
                    onToggleDailyPipeline = { viewModel.toggleDailyPipeline() },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                BottomNavBar(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.selectedTab) {
                    NavTab.ARCHITECTURE -> ArchitectureScreen(
                        selectedNodeInfo = uiState.selectedNodeInfo,
                        onSelectNode = { viewModel.selectNodeInfo(it) }
                    )

                    NavTab.KERNEL -> KernelScreen(
                        activeMission = uiState.activeMission,
                        missions = uiState.missions,
                        dailyPipelineEnabled = uiState.dailyPipelineEnabled,
                        onToggleDailyPipeline = { viewModel.toggleDailyPipeline() },
                        onTriggerMission = { viewModel.toggleTriggerMissionDialog(true) },
                        onOpenPostDetail = { viewModel.setActivePostDetail(it) }
                    )

                    NavTab.WORKERS -> WorkersScreen(
                        selectedWorker = uiState.selectedWorker,
                        onSelectWorker = { viewModel.selectWorker(it) },
                        trends = uiState.trends,
                        posts = uiState.posts
                    )

                    NavTab.AI_AGENTS -> AiAgentsConfigScreen(
                        providers = uiState.aiProviders,
                        defaultProviderId = uiState.defaultAiProviderId,
                        workerProviderMap = uiState.workerProviderMap,
                        testOutputLog = uiState.aiTestOutput,
                        isTesting = uiState.isAiTesting,
                        directorConfig = uiState.directorConfig,
                        isSplittingVideo = uiState.isSplittingVideo,
                        videoSplitProgress = uiState.videoSplitProgress,
                        onSetDefaultProvider = { viewModel.setDefaultAiProvider(it) },
                        onUpdateProviderKey = { id, key, model, endpoint ->
                            viewModel.updateAiProviderConfig(id, key, model, endpoint)
                        },
                        onTestProvider = { id, prompt -> viewModel.testAiProviderInference(id, prompt) },
                        onAutofillFreeKeys = { viewModel.autofillFreeAiKeys() },
                        onAssignWorkerProvider = { worker, providerId -> viewModel.assignWorkerAiProvider(worker, providerId) },
                        onToggleDirectorAgent = { viewModel.toggleDirectorAgent(it) },
                        onUpdateDirectorMode = { viewModel.updateDirectorMode(it) },
                        onSplitAndUploadVideo = { title, sizeMb, durationSec -> viewModel.splitAndUploadVideoToReels(title, sizeMb, durationSec) }
                    )

                    NavTab.CLI -> CLIScreen(
                        cliInput = uiState.cliInput,
                        cliHistory = uiState.cliHistory,
                        onInputChange = { viewModel.setCliInput(it) },
                        onExecuteCommand = { viewModel.executeCliCommand(it) }
                    )

                    NavTab.LOGS_CONFIG -> LogsAndConfigScreen(
                        logs = uiState.logs,
                        fbCredentials = uiState.fbCredentials,
                        selectedLogLevel = uiState.logFilterLevel,
                        logSearchQuery = uiState.logSearchQuery,
                        onLevelSelected = { viewModel.setLogFilterLevel(it) },
                        onQueryChanged = { viewModel.setLogSearchQuery(it) },
                        onClearLogs = { viewModel.clearLogs() },
                        onUpdateCredentials = { pageName, pageId, apiVersion, accessToken ->
                            viewModel.updateFacebookCredentials(pageName, pageId, apiVersion, accessToken)
                        },
                        onTestConnection = { viewModel.testFacebookConnection() },
                        onSendTestPost = { testMessage -> viewModel.sendTestFacebookPost(testMessage) }
                    )
                }
            }
        }

        // Trigger Mission Dialog
        if (uiState.showTriggerMissionDialog) {
            TriggerMissionDialog(
                onDismiss = { viewModel.toggleTriggerMissionDialog(false) },
                onTrigger = { title, type, customTopic ->
                    viewModel.triggerMission(title, type, customTopic)
                }
            )
        }

        // Post Detail Preview Dialog
        uiState.activePostDetail?.let { post ->
            PostDetailDialog(
                post = post,
                onDismiss = { viewModel.setActivePostDetail(null) }
            )
        }
    }
}
