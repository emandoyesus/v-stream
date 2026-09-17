package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.local.DownloadedVideo
import com.example.data.model.UiStrings
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.CodeOrange
import com.example.ui.theme.CodeRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.theme.SignalCyan
import com.example.ui.theme.SignalRose
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderActive
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LibraryScreen(
    videos: List<DownloadedVideo>,
    currentFilter: String,
    strings: UiStrings,
    onFilterChanged: (String) -> Unit,
    onVideoClicked: (DownloadedVideo) -> Unit,
    onDeleteVideo: (DownloadedVideo) -> Unit,
    onGoToDownloader: () -> Unit
) {
    val context = LocalContext.current
    var videoToDelete by remember { mutableStateOf<DownloadedVideo?>(null) }

    val filteredVideos = remember(videos, currentFilter) {
        when (currentFilter) {
            "TIKTOK" -> videos.filter { it.platform == "TIKTOK" }
            "INSTAGRAM" -> videos.filter { it.platform == "INSTAGRAM" }
            else -> videos
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("library_screen")
    ) {
        // Developer Terminal Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "root@v-stream:~/storage$ ls -la",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MatrixGreen
                )
                Text(
                    text = strings.vaultTitle,
                    fontSize = 22.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
            ) {
                Text(
                    text = "[${videos.size} BLOBS]",
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MatrixGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CLI Segmented Filter Tabs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = StudioCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
            ) {
                listOf("ALL" to "ALL [ * ]", "TIKTOK" to "TIKTOK", "INSTAGRAM" to "REELS").forEach { (filterKey, label) ->
                    val isSelected = currentFilter == filterKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) MatrixGreen else Color.Transparent)
                            .clickable { onFilterChanged(filterKey) }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) StudioBlack else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content
        if (filteredVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioCard)
                            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = MatrixGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (videos.isEmpty()) strings.vaultEmptyTitle else "// NO $currentFilter STREAMS",
                        fontSize = 13.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = strings.vaultEmptySubtitle,
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = CodeComment,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onGoToDownloader,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreen,
                            contentColor = StudioBlack
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$ ./extract-stream",
                            fontSize = 11.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredVideos, key = { it.id }) { video ->
                    ProgrammerVideoItem(
                        video = video,
                        onPlayClicked = { onVideoClicked(video) },
                        onShareClicked = { shareVideoFile(context, video) },
                        onDeleteClicked = { videoToDelete = video }
                    )
                }
            }
        }
    }

    // CLI Delete Dialog
    videoToDelete?.let { video ->
        AlertDialog(
            onDismissRequest = { videoToDelete = null },
            title = {
                Text(
                    text = "rm -rf ${video.fileName.take(24)}...",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CodeRed
                )
            },
            text = {
                Text(
                    text = "Remove stream entry and unlink file from disk? This cannot be undone.",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteVideo(video)
                        videoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CodeRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "rm --force",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { videoToDelete = null }) {
                    Text(
                        text = "abort (SIGINT)",
                        fontFamily = JetBrainsMonoFontFamily,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            },
            containerColor = StudioBlack,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun ProgrammerVideoItem(
    video: DownloadedVideo,
    onPlayClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClicked() }
            .testTag("video_item_${video.id}"),
        shape = RoundedCornerShape(8.dp),
        color = StudioCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail preview
            Box(
                modifier = Modifier
                    .size(width = 72.dp, height = 90.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(StudioCardElevated)
                    .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
            ) {
                if (!video.thumbnailUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(24.dp).align(Alignment.Center)
                    )
                }

                // Play icon overlay
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(StudioBlack.copy(alpha = 0.8f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = MatrixGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Metadata column
            Column(modifier = Modifier.weight(1f)) {
                val isTiktok = video.platform == "TIKTOK"
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
                            text = video.platform,
                            color = if (isTiktok) SignalCyan else SignalRose,
                            fontSize = 8.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "NO-WM // ${video.quality}",
                        fontSize = 9.sp,
                        fontFamily = JetBrainsMonoFontFamily,
                        color = MatrixGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.title,
                    fontSize = 12.sp,
                    fontFamily = PlusJakartaFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "author: @${video.authorUsername.removePrefix("@").ifBlank { video.authorName }}",
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = CodeOrange,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                val formattedDate = remember(video.downloadedAt) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    sdf.format(Date(video.downloadedAt))
                }

                Text(
                    text = "$formattedDate • ${formatBytes(video.fileSizeBytes)}",
                    fontSize = 9.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = CodeComment
                )
            }

            // Actions
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onShareClicked,
                    modifier = Modifier.size(32.dp).testTag("share_video_${video.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MatrixGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClicked,
                    modifier = Modifier.size(32.dp).testTag("delete_video_${video.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = CodeRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun shareVideoFile(context: Context, video: DownloadedVideo) {
    try {
        val uri = android.net.Uri.parse(video.localUri)
        val shareUri = if (uri.scheme == "file") {
            val file = File(uri.path ?: "")
            if (file.exists()) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else uri
        } else {
            uri
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, shareUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Clean Video"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "18.4 MB"
    val mb = bytes / (1024.0 * 1024.0)
    return String.format(Locale.US, "%.1f MB", mb)
}
