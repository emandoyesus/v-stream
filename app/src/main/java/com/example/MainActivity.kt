package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.CodeSnippetModal
import com.example.ui.components.LanguageSelectorModal
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.screens.DownloaderScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TermDotClose
import com.example.ui.theme.TermDotMin
import com.example.ui.theme.TermDotZoom
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                viewModel.handleSharedText(sharedText)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val downloadedVideos by viewModel.downloadedVideos.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = uiState.strings

    LaunchedEffect(uiState.successNotice) {
        uiState.successNotice?.let { notice ->
            snackbarHostState.showSnackbar(
                message = notice,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotice()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = StudioBlack,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Terminal icon badge
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(StudioCard)
                                .border(1.dp, StudioBorder, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ">_",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MatrixGreen,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "v-stream-demux",
                                    fontSize = 14.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "v2.5.0",
                                    fontSize = 10.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = MatrixGreen
                                )
                            }
                            Text(
                                text = "NOWM // TIKTOK & IG GRAPH API",
                                fontSize = 9.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                color = CodeComment
                            )
                        }
                    }
                },
                actions = {
                    // Code Generator Action
                    IconButton(
                        onClick = { viewModel.openCodeSnippet() },
                        modifier = Modifier.testTag("topbar_code_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Code Generator",
                            tint = CodeCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Locale Selector Action
                    IconButton(
                        onClick = { viewModel.openLanguageSelector() },
                        modifier = Modifier.testTag("topbar_lang_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MatrixGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioBlack,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = StudioCard,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .testTag("bottom_navigation_bar")
                    .border(androidx.compose.foundation.BorderStroke(1.dp, StudioBorder))
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Tab 0: Downloader / CLI
                NavigationBarItem(
                    selected = uiState.currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == 0) Icons.Filled.Terminal else Icons.Outlined.Terminal,
                            contentDescription = "Terminal",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = strings.tabDownloader,
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBlack,
                        selectedTextColor = MatrixGreen,
                        indicatorColor = MatrixGreen,
                        unselectedIconColor = TextTertiary,
                        unselectedTextColor = TextTertiary
                    ),
                    modifier = Modifier.testTag("tab_downloader")
                )

                // Tab 1: Library / Vault
                NavigationBarItem(
                    selected = uiState.currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (downloadedVideos.isNotEmpty()) {
                                    Badge(
                                        containerColor = MatrixGreen,
                                        contentColor = StudioBlack
                                    ) {
                                        Text(
                                            text = "${downloadedVideos.size}",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.currentTab == 1) Icons.Filled.Folder else Icons.Outlined.Folder,
                                contentDescription = "Vault",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = strings.tabVault,
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBlack,
                        selectedTextColor = MatrixGreen,
                        indicatorColor = MatrixGreen,
                        unselectedIconColor = TextTertiary,
                        unselectedTextColor = TextTertiary
                    ),
                    modifier = Modifier.testTag("tab_library")
                )

                // Tab 2: Guide / Protocol
                NavigationBarItem(
                    selected = uiState.currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == 2) Icons.Filled.HelpOutline else Icons.Outlined.HelpOutline,
                            contentDescription = "Protocol",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = strings.tabProtocol,
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBlack,
                        selectedTextColor = MatrixGreen,
                        indicatorColor = MatrixGreen,
                        unselectedIconColor = TextTertiary,
                        unselectedTextColor = TextTertiary
                    ),
                    modifier = Modifier.testTag("tab_guide")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentTab) {
                0 -> DownloaderScreen(
                    uiState = uiState,
                    onUrlChanged = { viewModel.onUrlChanged(it) },
                    onPasteClicked = { viewModel.pasteFromClipboard(it) },
                    onSampleClicked = { viewModel.loadSampleUrl(it) },
                    onExtractClicked = { viewModel.analyzeCurrentUrl() },
                    onOptionSelected = { viewModel.selectOption(it) },
                    onDownloadClicked = { viewModel.startDownload() },
                    onCancelDownload = { viewModel.cancelDownload() },
                    onPlayCompletedVideo = { viewModel.openPlayer(it) },
                    onViewLibraryClicked = { viewModel.setTab(1) },
                    onOpenCodeSnippet = { viewModel.openCodeSnippet() },
                    onOpenLanguageSelector = { viewModel.openLanguageSelector() },
                    onToggleTerminalLogs = { viewModel.toggleTerminalLogs() },
                    onClearError = { viewModel.clearError() }
                )
                1 -> LibraryScreen(
                    videos = downloadedVideos,
                    currentFilter = uiState.libraryFilter,
                    strings = strings,
                    onFilterChanged = { viewModel.setLibraryFilter(it) },
                    onVideoClicked = { viewModel.openPlayer(it) },
                    onDeleteVideo = { viewModel.deleteVideo(it) },
                    onGoToDownloader = { viewModel.setTab(0) }
                )
                2 -> GuideScreen(
                    strings = strings,
                    onOpenCodeSnippet = { viewModel.openCodeSnippet() },
                    onGoToDownloader = { viewModel.setTab(0) }
                )
            }
        }
    }

    // Modal In-App Video Player
    uiState.playingVideo?.let { playing ->
        VideoPlayerDialog(
            video = playing,
            onDismiss = { viewModel.closePlayer() }
        )
    }

    // Modal Code Snippet Generator
    if (uiState.showCodeSnippetModal) {
        val targetUrl = uiState.metadata?.sourceUrl
            ?: uiState.inputUrl.ifBlank { "https://www.tiktok.com/@creator/video/7123456789012345678" }
        val quality = uiState.selectedOption?.resolutionLabel ?: "1080p Ultra HD (No Watermark)"

        CodeSnippetModal(
            targetUrl = targetUrl,
            quality = quality,
            onDismiss = { viewModel.closeCodeSnippet() }
        )
    }

    // Modal Language Selector
    if (uiState.showLanguageModal) {
        LanguageSelectorModal(
            currentLanguage = uiState.currentLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.closeLanguageSelector() }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

