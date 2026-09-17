package com.example.data.manager

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedVideo
import com.example.data.model.DownloadProgressState
import com.example.data.model.VideoDownloadOption
import com.example.data.model.VideoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class VideoDownloader(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val dao = AppDatabase.getDatabase(context).downloadedVideoDao()

    fun downloadVideo(
        metadata: VideoMetadata,
        option: VideoDownloadOption
    ): Flow<DownloadProgressState> = flow {
        emit(
            DownloadProgressState(
                isDownloading = true,
                progressPercent = 0,
                statusText = "Connecting to media stream..."
            )
        )

        try {
            val request = Request.Builder()
                .url(option.url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile)")
                .header("Accept", "*/*")
                .header("Referer", if (metadata.platform.name == "TIKTOK") "https://www.tiktok.com/" else "https://www.instagram.com/")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                emit(
                    DownloadProgressState(
                        isDownloading = false,
                        errorMessage = "Download server returned HTTP ${response.code}"
                    )
                )
                return@flow
            }

            val body = response.body ?: throw IllegalStateException("Empty response body")
            val totalBytes = body.contentLength()
            val isAudio = option.id.contains("audio") || option.label.contains("MP3", ignoreCase = true)
            val extension = if (isAudio) "mp3" else "mp4"
            val mimeType = if (isAudio) "audio/mpeg" else "video/mp4"

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val sanitizedAuthor = metadata.authorUsername.replace(Regex("[^a-zA-Z0-9_-]"), "").ifBlank { "video" }
            val fileName = "${metadata.platform.name.lowercase()}_${sanitizedAuthor}_$timeStamp.$extension"

            // Target MediaStore or external files directory
            val (outputStream, outputUri) = createDestinationStream(fileName, mimeType)

            if (outputStream == null) {
                emit(
                    DownloadProgressState(
                        isDownloading = false,
                        errorMessage = "Unable to create target media file"
                    )
                )
                return@flow
            }

            emit(
                DownloadProgressState(
                    isDownloading = true,
                    progressPercent = 5,
                    totalBytes = totalBytes,
                    statusText = "Downloading watermark-free ${if (isAudio) "audio" else "video"}..."
                )
            )

            var downloadedBytes = 0L
            val inputStream: InputStream = body.byteStream()
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            var lastEmitTime = System.currentTimeMillis()

            outputStream.use { out ->
                inputStream.use { inStream ->
                    while (inStream.read(buffer).also { bytesRead = it } != -1) {
                        out.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        val now = System.currentTimeMillis()
                        if (now - lastEmitTime > 200 || downloadedBytes == totalBytes) {
                            lastEmitTime = now
                            val percent = if (totalBytes > 0) {
                                ((downloadedBytes * 100) / totalBytes).toInt().coerceIn(0, 99)
                            } else {
                                50
                            }

                            emit(
                                DownloadProgressState(
                                    isDownloading = true,
                                    progressPercent = percent,
                                    bytesDownloaded = downloadedBytes,
                                    totalBytes = totalBytes,
                                    statusText = "Downloading: $percent% (${formatBytes(downloadedBytes)} / ${if (totalBytes > 0) formatBytes(totalBytes) else "..."})"
                                )
                            )
                        }
                    }
                }
            }

            // Mark file as complete in MediaStore if applicable
            finalizeMediaStore(outputUri)

            val record = DownloadedVideo(
                title = metadata.title.ifBlank { "${metadata.platform.displayName} Video" },
                authorName = metadata.authorName,
                authorUsername = metadata.authorUsername,
                platform = metadata.platform.name,
                thumbnailUrl = metadata.coverThumbnailUrl,
                localUri = outputUri.toString(),
                fileName = fileName,
                sourceUrl = metadata.sourceUrl,
                durationSeconds = metadata.durationSeconds,
                fileSizeBytes = downloadedBytes,
                quality = option.label
            )

            val newId = dao.insertVideo(record)
            val savedVideo = record.copy(id = newId)

            emit(
                DownloadProgressState(
                    isDownloading = false,
                    progressPercent = 100,
                    bytesDownloaded = downloadedBytes,
                    totalBytes = downloadedBytes,
                    statusText = "Download completed successfully!",
                    completedVideo = savedVideo
                )
            )

        } catch (e: Exception) {
            Log.e("VideoDownloader", "Download failed: ${e.message}", e)
            emit(
                DownloadProgressState(
                    isDownloading = false,
                    errorMessage = e.message ?: "Failed to download video"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun createDestinationStream(fileName: String, mimeType: String): Pair<OutputStream?, Uri> {
        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val isAudio = mimeType.startsWith("audio")
            val collection = if (isAudio) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, if (isAudio) "Music/VideoDownloader" else "Movies/VideoDownloader")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val uri = resolver.insert(collection, values)
            if (uri != null) {
                val stream = resolver.openOutputStream(uri)
                return Pair(stream, uri)
            }
        }

        // Fallback for older devices or if MediaStore insert failed
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
        val file = File(dir, fileName)
        val stream = FileOutputStream(file)
        return Pair(stream, Uri.fromFile(file))
    }

    private fun finalizeMediaStore(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && uri.scheme == "content") {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
                context.contentResolver.update(uri, values, null, null)
            } catch (e: Exception) {
                Log.w("VideoDownloader", "Error updating IS_PENDING: ${e.message}")
            }
        }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
            else -> "$bytes B"
        }
    }
}
