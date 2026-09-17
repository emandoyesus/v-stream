package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UiStrings
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.CodeOrange
import com.example.ui.theme.CodeYellow
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.SignalCyan
import com.example.ui.theme.SignalRose
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TermDotClose
import com.example.ui.theme.TermDotMin
import com.example.ui.theme.TermDotZoom
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GuideScreen(
    strings: UiStrings,
    onOpenCodeSnippet: () -> Unit,
    onGoToDownloader: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("guide_screen")
    ) {
        // Developer Man Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = StudioCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotClose))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotMin))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotZoom))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "man 1 extract-stream",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MatrixGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.guideTitle,
                    fontSize = 20.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = strings.guideSubtitle,
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = CodeComment
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Code Generation Quick Access Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = StudioCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CodeCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = CodeCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MULTI-LANGUAGE DEVELOPER SDK",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = CodeCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Generate ready-to-run code snippets in Bash/cURL, Python, Node.js, Go, Rust, Kotlin, or PHP to demux and stream unwatermarked media programmatically.",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOpenCodeSnippet,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CodeCyan,
                        contentColor = StudioBlack
                    ),
                    modifier = Modifier.testTag("open_code_from_guide")
                ) {
                    Text(
                        text = "$ ./codegen --all-langs",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 1: Architecture Pipeline
        TerminalSectionCard(
            sectionNumber = "01",
            title = "PIPELINE ARCHITECTURE",
            bodyLines = listOf(
                "1. CLIENT -> POST /api/v2/stream/demux with target URL",
                "2. RESOLVER -> Evaluates canonical CDN manifests (TikTok API / IG Graph)",
                "3. DEMUXER -> Strips watermark overlay bits & reconstructs container",
                "4. OUTPUT -> Clean H.264 video stream written to /sdcard/Movies/RawFeed/"
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 2: TikTok Instructions
        TerminalSectionCard(
            sectionNumber = "02",
            title = "TIKTOK INGESTION (NOWM)",
            tagColor = SignalCyan,
            bodyLines = listOf(
                "• In TikTok app, press Share -> Copy Link",
                "• Valid URI scheme: https://www.tiktok.com/@user/video/[id] or vm.tiktok.com/[token]",
                "• Demuxer strips floating watermark logo and author tail bumper",
                "• Audio stream preserved at original 320 kbps bitrate"
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 3: Instagram Instructions
        TerminalSectionCard(
            sectionNumber = "03",
            title = "INSTAGRAM INGESTION (REELS/POSTS)",
            tagColor = SignalRose,
            bodyLines = listOf(
                "• In Instagram app, tap Share -> Copy Link",
                "• Valid URI scheme: https://www.instagram.com/reel/[code] or /p/[code]",
                "• Resolves top progressive MP4 stream directly from Meta CDN node",
                "• Free of platform branding, captions overlay, and audio compression"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Launch Terminal Button
        Button(
            onClick = onGoToDownloader,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MatrixGreen,
                contentColor = StudioBlack
            )
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = StudioBlack
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$ RETURN TO CLI TERMINAL",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TerminalSectionCard(
    sectionNumber: String,
    title: String,
    tagColor: Color = MatrixGreen,
    bodyLines: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StudioCard,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "[$sectionNumber]",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = tagColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            bodyLines.forEach { line ->
                Text(
                    text = line,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}
