package com.example.data.model

enum class VideoPlatform(val displayName: String) {
    TIKTOK("TikTok"),
    INSTAGRAM("Instagram"),
    UNKNOWN("Unknown");

    companion object {
        fun detect(url: String): VideoPlatform {
            val lower = url.lowercase()
            return when {
                lower.contains("tiktok.com") || lower.contains("douyin.com") -> TIKTOK
                lower.contains("instagram.com") || lower.contains("instagr.am") -> INSTAGRAM
                else -> UNKNOWN
            }
        }
    }
}

data class VideoDownloadOption(
    val id: String,
    val label: String,
    val resolutionLabel: String,
    val url: String,
    val isWatermarkFree: Boolean = true,
    val estimatedSize: String = ""
)

data class VideoMetadata(
    val sourceUrl: String,
    val platform: VideoPlatform,
    val title: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String? = null,
    val coverThumbnailUrl: String? = null,
    val durationSeconds: Int = 0,
    val downloadOptions: List<VideoDownloadOption>,
    val musicTitle: String? = null,
    val likesCount: Long = 0,
    val sharesCount: Long = 0
)

data class DownloadProgressState(
    val isDownloading: Boolean = false,
    val progressPercent: Int = 0, // 0 to 100
    val bytesDownloaded: Long = 0,
    val totalBytes: Long = 0,
    val statusText: String = "",
    val errorMessage: String? = null,
    val completedVideo: com.example.data.local.DownloadedVideo? = null
)
