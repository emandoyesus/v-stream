package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.DownloadedVideo
import com.example.ui.theme.AcidCitron
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.theme.SignalCyan
import com.example.ui.theme.SignalRose
import com.example.ui.theme.StudioBlack
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerDialog(
    video: DownloadedVideo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var showControls by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("video_player_dialog"),
            color = Color.Black
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showControls = !showControls }
            ) {
                // Video display
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            val uri = Uri.parse(video.localUri)
                            setVideoURI(uri)
                            setOnPreparedListener { mp ->
                                isBuffering = false
                                mp.isLooping = true
                                start()
                                isPlaying = true
                            }
                            setOnCompletionListener {
                                isPlaying = false
                            }
                            setOnErrorListener { _, _, _ ->
                                isBuffering = false
                                true
                            }
                            videoViewRef = this
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )

                // Buffering indicator
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(52.dp),
                        color = AcidCitron
                    )
                }

                // Controls Overlay
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xCC000000),
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color(0xEE000000)
                                    )
                                )
                            )
                    ) {
                        // Top bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 36.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x66000000))
                                    .testTag("close_player_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close player",
                                    tint = Color.White
                                )
                            }

                            // Platform pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (video.platform == "TIKTOK") Color(0x3300E5FF) else Color(0x33FF3366)
                            ) {
                                Text(
                                    text = if (video.platform == "TIKTOK") "TIKTOK // RAW STREAM" else "INSTAGRAM // RAW STREAM",
                                    color = if (video.platform == "TIKTOK") SignalCyan else SignalRose,
                                    fontSize = 11.sp,
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            // Share button
                            IconButton(
                                onClick = { shareVideo(context, video) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x66000000))
                                    .testTag("share_player_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share video",
                                    tint = Color.White
                                )
                            }
                        }

                        // Center Play/Pause toggle
                        IconButton(
                            onClick = {
                                videoViewRef?.let { vv ->
                                    if (vv.isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color(0x88000000))
                                .testTag("play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Bottom Details Bar
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 28.dp)
                        ) {
                            Text(
                                text = video.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (video.authorName.isNotBlank()) {
                                    Text(
                                        text = "@${video.authorName.removePrefix("@")}",
                                        color = AcidCitron,
                                        fontSize = 12.sp,
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = video.quality,
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 11.sp,
                                    fontFamily = OutfitFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoViewRef?.stopPlayback()
        }
    }
}

private fun shareVideo(context: Context, video: DownloadedVideo) {
    try {
        val uri = Uri.parse(video.localUri)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "${video.title}\nDownloaded via Video Downloader")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
    } catch (_: Exception) {
        // Fallback to sharing source link
        val textIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, video.sourceUrl)
        }
        context.startActivity(Intent.createChooser(textIntent, "Share Video Link"))
    }
}
