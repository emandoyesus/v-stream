package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.DownloadedVideo
import com.example.data.model.VideoDownloadOption
import com.example.data.model.VideoPlatform
import com.example.ui.MainUiState
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.CodeOrange
import com.example.ui.theme.CodePurple
import com.example.ui.theme.CodeRed
import com.example.ui.theme.CodeYellow
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixGreenMuted
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.theme.SignalCyan
import com.example.ui.theme.SignalRose
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderActive
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TermDotClose
import com.example.ui.theme.TermDotMin
import com.example.ui.theme.TermDotZoom
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DownloaderScreen(
    uiState: MainUiState,
    onUrlChanged: (String) -> Unit,
    onPasteClicked: (String) -> Unit,
    onSampleClicked: (String) -> Unit,
    onExtractClicked: () -> Unit,
    onOptionSelected: (VideoDownloadOption) -> Unit,
    onDownloadClicked: () -> Unit,
    onCancelDownload: () -> Unit,
    onPlayCompletedVideo: (DownloadedVideo) -> Unit,
    onViewLibraryClicked: () -> Unit,
    onOpenCodeSnippet: () -> Unit,
    onOpenLanguageSelector: () -> Unit,
    onToggleTerminalLogs: () -> Unit,
    onClearError: () -> Unit
) {
    val scrollState = rememberScrollState()
    val strings = uiState.strings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("downloader_screen")
    ) {
        // Terminal Window Header Chrome
        TerminalHeaderChrome(
            currentLanguage = uiState.currentLanguage.displayName,
            detectedPlatform = uiState.detectedPlatform,
            onOpenLanguageSelector = onOpenLanguageSelector
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Programmer CLI Input Console
        ProgrammerInputConsole(
            inputUrl = uiState.inputUrl,
            isAnalyzing = uiState.isAnalyzing,
            strings = strings,
            onUrlChanged = onUrlChanged,
            onPasteClicked = onPasteClicked,
            onExtractClicked = onExtractClicked,
            onSampleClicked = onSampleClicked,
            onOpenCodeSnippet = onOpenCodeSnippet
        )

        // Error message if any
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            uiState.errorMessage?.let { errorMsg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    color = Color(0xFF1E1418),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CodeRed.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = CodeRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STDERR // EXIT_FAILURE (1)",
                                color = CodeRed,
                                fontSize = 10.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = errorMsg,
                                color = Color(0xFFFFD2D9),
                                fontSize = 12.sp,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        }
                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Dismiss",
                                tint = Color(0xFFFFD2D9),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Realtime Terminal Output Logs Buffer
        AnimatedVisibility(
            visible = uiState.showTerminalLogs,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            TerminalLogsBuffer(
                logs = uiState.terminalLogs,
                onToggle = onToggleTerminalLogs
            )
        }

        // Extracted Video Dev Inspector Card
        AnimatedVisibility(
            visible = uiState.metadata != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            uiState.metadata?.let { meta ->
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.statusReady,
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = MatrixGreen
                        )

                        Text(
                            text = "WATERMARK: NULL // BITRATE: MAX",
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            color = CodeComment
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = StudioCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorderActive)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Video summary block
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Frame preview
                                Box(
                                    modifier = Modifier
                                        .size(width = 96.dp, height = 124.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioCardElevated)
                                        .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                                ) {
                                    if (!meta.coverThumbnailUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = meta.coverThumbnailUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = MatrixGreen,
                                            modifier = Modifier.size(36.dp).align(Alignment.Center)
                                        )
                                    }

                                    // Clean tag
                                    Surface(
                                        modifier = Modifier.align(Alignment.TopStart).padding(5.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        color = StudioBlack.copy(alpha = 0.9f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MatrixGreen)
                                    ) {
                                        Text(
                                            text = strings.statusCleanBadge,
                                            color = MatrixGreen,
                                            fontSize = 8.sp,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }

                                    if (meta.durationSeconds > 0) {
                                        Surface(
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(5.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            color = StudioBlack.copy(alpha = 0.9f)
                                        ) {
                                            Text(
                                                text = "${meta.durationSeconds}s",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Technical metadata specs
                                Column(modifier = Modifier.weight(1f)) {
                                    val isTiktok = meta.platform == VideoPlatform.TIKTOK
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isTiktok) Color(0xFF0D2530) else Color(0xFF331422),
                                            border = androidx.compose.foundation.BorderStroke(
                                                0.5.dp,
                                                if (isTiktok) SignalCyan else SignalRose
                                            )
                                        ) {
                                            Text(
                                                text = meta.platform.displayName.uppercase(),
                                                color = if (isTiktok) SignalCyan else SignalRose,
                                                fontSize = 9.sp,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "HTTP/2 (200 OK)",
                                            fontSize = 9.sp,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            color = CodeComment
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = meta.title,
                                        fontSize = 13.sp,
                                        fontFamily = PlusJakartaFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "author: @${meta.authorUsername.removePrefix("@").ifBlank { meta.authorName }}",
                                        fontSize = 11.sp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = CodeOrange,
                                        maxLines = 1
                                    )

                                    if (!meta.musicTitle.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = CodeYellow,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = meta.musicTitle,
                                                fontSize = 10.sp,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                color = CodeComment,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Dev Code Generator Button
                            Surface(
                                onClick = onOpenCodeSnippet,
                                shape = RoundedCornerShape(6.dp),
                                color = StudioCardElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CodeCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().testTag("open_code_snippet_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Code,
                                            contentDescription = null,
                                            tint = CodeCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = strings.btnCodeSnippet,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = CodeCyan
                                        )
                                    }

                                    Text(
                                        text = "cURL • Python • Node • Go",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp,
                                        color = CodeComment
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Available Streams Header
                            Text(
                                text = "// ${strings.resolutionLabel} (DECODER TARGETS)",
                                fontSize = 10.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = CodeComment
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            meta.downloadOptions.forEach { opt ->
                                val isSelected = uiState.selectedOption?.id == opt.id
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable { onOptionSelected(opt) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) StudioCardElevated else StudioBlack,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MatrixGreen else StudioBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Shell bullet indicator
                                        Text(
                                            text = if (isSelected) ">" else "$",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) MatrixGreen else CodeComment
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = opt.label,
                                                    fontSize = 12.sp,
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) TextPrimary else TextSecondary
                                                )
                                                if (opt.isWatermarkFree) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = MatrixGreenMuted
                                                    ) {
                                                        Text(
                                                            text = "NO-WM",
                                                            color = MatrixGreen,
                                                            fontSize = 8.sp,
                                                            fontFamily = JetBrainsMonoFontFamily,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Text(
                                                text = "${opt.resolutionLabel} | size: ${opt.estimatedSize.ifBlank { "variable" }}",
                                                fontSize = 10.sp,
                                                fontFamily = JetBrainsMonoFontFamily,
                                                color = CodeComment
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Download Action Button
                            val isDownloading = uiState.downloadProgress.isDownloading
                            if (!isDownloading) {
                                Button(
                                    onClick = onDownloadClicked,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("start_download_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MatrixGreen,
                                        contentColor = StudioBlack
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = StudioBlack
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.btnDownloadClean,
                                        fontSize = 12.sp,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioBlack
                                    )
                                }
                            } else {
                                // Realtime Stream Progress
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(StudioBlack, RoundedCornerShape(8.dp))
                                        .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$ ${uiState.downloadProgress.statusText}",
                                            fontSize = 11.sp,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            color = MatrixGreen
                                        )
                                        Text(
                                            text = "[${uiState.downloadProgress.progressPercent}%]",
                                            fontSize = 12.sp,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    LinearProgressIndicator(
                                        progress = { uiState.downloadProgress.progressPercent / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = MatrixGreen,
                                        trackColor = StudioCardElevated
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = onCancelDownload,
                                        modifier = Modifier.align(Alignment.End).height(30.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = StudioCardElevated,
                                            contentColor = CodeRed
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = strings.btnCancel,
                                            fontSize = 10.sp,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Completion Banner
        AnimatedVisibility(
            visible = uiState.downloadProgress.completedVideo != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            uiState.downloadProgress.completedVideo?.let { completed ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    color = Color(0xFF0F1E14),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MatrixGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = StudioBlack,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "FILE SAVED -> /sdcard/Movies/RawFeed/",
                                color = MatrixGreen,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Clean raw MP4 stream synced. Watermark-free and ready in filesystem.",
                            color = Color(0xFFB4FAD0),
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onPlayCompletedVideo(completed) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("play_completed_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatrixGreen,
                                    contentColor = StudioBlack
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PLAY STREAM",
                                    fontSize = 11.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onViewLibraryClicked,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StudioCardElevated,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                            ) {
                                Text(
                                    text = strings.btnViewVault,
                                    fontSize = 11.sp,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TerminalHeaderChrome(
    currentLanguage: String,
    detectedPlatform: VideoPlatform,
    onOpenLanguageSelector: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StudioCard,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // macOS/Linux window dots + terminal title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotClose))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotMin))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotZoom))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "bash - root@v-stream:~ (v2.5)",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = CodeComment
                    )
                }

                // Interactive Language Switcher Pill
                Surface(
                    onClick = onOpenLanguageSelector,
                    shape = RoundedCornerShape(6.dp),
                    color = StudioCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("lang_switcher_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MatrixGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentLanguage.uppercase(),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MatrixGreen
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "▾",
                            fontSize = 9.sp,
                            color = MatrixGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bash Prompt Display
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "root@v-stream:~$ ",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MatrixGreen
                )
                Text(
                    text = "./extract-stream --target-nowm",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 13.sp,
                    color = CodeCyan
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "// PROTOCOL: TLS 1.3 • CODEC: H.264/AAC • WATERMARK: STRIPPED",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 10.sp,
                color = CodeComment
            )
        }
    }
}

@Composable
private fun ProgrammerInputConsole(
    inputUrl: String,
    isAnalyzing: Boolean,
    strings: com.example.data.model.UiStrings,
    onUrlChanged: (String) -> Unit,
    onPasteClicked: (String) -> Unit,
    onExtractClicked: () -> Unit,
    onSampleClicked: (String) -> Unit,
    onOpenCodeSnippet: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StudioCard,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INPUT_BUFFER (STDIN)",
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = CodeComment
                )

                Text(
                    text = "TARGET: TIKTOK | REELS",
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = CodeYellow
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Monospace CLI Text input field
            OutlinedTextField(
                value = inputUrl,
                onValueChange = onUrlChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("url_input_field"),
                placeholder = {
                    Text(
                        text = strings.inputHint,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = CodeComment
                    )
                },
                leadingIcon = {
                    Text(
                        text = "$ ",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MatrixGreen,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                trailingIcon = {
                    if (inputUrl.isNotEmpty()) {
                        IconButton(
                            onClick = { onUrlChanged("") },
                            modifier = Modifier.testTag("clear_url_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MatrixGreen,
                    unfocusedBorderColor = StudioBorder,
                    focusedContainerColor = StudioBlack,
                    unfocusedContainerColor = StudioBlack,
                    focusedTextColor = MatrixGreen,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Paste button
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = clipboard.primaryClip
                        if (clipData != null && clipData.itemCount > 0) {
                            val text = clipData.getItemAt(0).text?.toString() ?: ""
                            onPasteClicked(text)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("paste_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioCardElevated,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MatrixGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.btnPaste,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Extract button
                Button(
                    onClick = onExtractClicked,
                    enabled = inputUrl.isNotBlank() && !isAnalyzing,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("extract_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreen,
                        contentColor = StudioBlack,
                        disabledContainerColor = StudioCardElevated,
                        disabledContentColor = CodeComment
                    )
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = StudioBlack,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PARSING...",
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = strings.btnExtract,
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test sample shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TerminalSamplePill(
                    label = strings.sampleTiktok,
                    tagColor = SignalCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onSampleClicked("https://www.tiktok.com/@tiktok/video/7123456789012345678") }
                )
                TerminalSamplePill(
                    label = strings.sampleInstagram,
                    tagColor = SignalRose,
                    modifier = Modifier.weight(1f),
                    onClick = { onSampleClicked("https://www.instagram.com/reel/C8abc123xyz/") }
                )
            }
        }
    }
}

@Composable
private fun TerminalSamplePill(
    label: String,
    tagColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = StudioBlack,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(tagColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TerminalLogsBuffer(
    logs: List<String>,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        color = StudioBlack,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CONSOLE STDOUT / LOGS",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatrixGreen
                    )
                }

                Text(
                    text = "${logs.size} lines",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.sp,
                    color = CodeComment
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioCardElevated.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                logs.takeLast(6).forEach { logLine ->
                    val color = when {
                        logLine.contains("SUCCESS") || logLine.contains("200 OK") -> MatrixGreen
                        logLine.contains("WARN") -> CodeYellow
                        logLine.contains("ERROR") || logLine.contains("ERR") -> CodeRed
                        logLine.contains("NET") -> CodeCyan
                        logLine.contains("EXEC") -> CodePurple
                        else -> TextSecondary
                    }

                    Text(
                        text = logLine,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        color = color,
                        lineHeight = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
