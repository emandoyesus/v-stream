package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedVideo
import com.example.data.manager.VideoDownloader
import com.example.data.model.DownloadProgressState
import com.example.data.model.VideoDownloadOption
import com.example.data.model.VideoMetadata
import com.example.data.remote.VideoResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class VideoRepository(private val context: Context) {

    private val resolver = VideoResolver()
    private val downloader = VideoDownloader(context)
    private val dao = AppDatabase.getDatabase(context).downloadedVideoDao()

    suspend fun resolveVideo(url: String): Result<VideoMetadata> {
        return resolver.resolve(url)
    }

    fun startDownload(
        metadata: VideoMetadata,
        option: VideoDownloadOption
    ): Flow<DownloadProgressState> {
        return downloader.downloadVideo(metadata, option)
    }

    fun getDownloadedVideos(): Flow<List<DownloadedVideo>> {
        return dao.getAllVideos()
    }

    suspend fun deleteVideo(video: DownloadedVideo) = withContext(Dispatchers.IO) {
        dao.deleteVideoById(video.id)
        // Clean up physical file if accessible
        try {
            val uri = Uri.parse(video.localUri)
            if (uri.scheme == "file") {
                val file = File(uri.path ?: "")
                if (file.exists()) file.delete()
            } else if (uri.scheme == "content") {
                context.contentResolver.delete(uri, null, null)
            }
        } catch (_: Exception) {
            // Ignore if file was already removed by user
        }
    }
}
