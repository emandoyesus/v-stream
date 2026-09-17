package com.example.data.remote

import android.util.Log
import com.example.data.model.VideoDownloadOption
import com.example.data.model.VideoMetadata
import com.example.data.model.VideoPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class VideoResolver {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun resolve(rawInputUrl: String): Result<VideoMetadata> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = extractUrl(rawInputUrl)
                ?: return@withContext Result.failure(IllegalArgumentException("Please enter a valid URL"))

            val platform = VideoPlatform.detect(cleanUrl)
            if (platform == VideoPlatform.UNKNOWN) {
                return@withContext Result.failure(IllegalArgumentException("Please provide a valid TikTok or Instagram link"))
            }

            // Follow potential short links like vm.tiktok.com or vt.tiktok.com
            val resolvedUrl = expandShortUrlIfNeeded(cleanUrl)

            when (platform) {
                VideoPlatform.TIKTOK -> resolveTikTok(resolvedUrl)
                VideoPlatform.INSTAGRAM -> resolveInstagram(resolvedUrl)
                else -> Result.failure(IllegalArgumentException("Unsupported platform"))
            }
        } catch (e: Exception) {
            Log.e("VideoResolver", "Error resolving video: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun resolveTikTok(url: String): Result<VideoMetadata> {
        // Strategy 1: TikWM API (Fast, completely free, returns watermark-free HD stream)
        try {
            val encoded = URLEncoder.encode(url, "UTF-8")
            val request = Request.Builder()
                .url("https://www.tikwm.com/api/?url=$encoded")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile)")
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()

            if (response.isSuccessful && !bodyString.isNullOrBlank()) {
                val json = JSONObject(bodyString)
                val code = json.optInt("code", -1)
                if (code == 0 && json.has("data")) {
                    val data = json.getJSONObject("data")
                    val title = data.optString("title").ifBlank { "TikTok Video" }
                    val cover = data.optString("cover")
                    val playNoWatermark = data.optString("play")
                    val hdPlay = data.optString("hdplay")
                    val wmPlay = data.optString("wmplay")
                    val music = data.optString("music")
                    val musicInfo = data.optJSONObject("music_info")
                    val musicTitle = musicInfo?.optString("title") ?: "Original Sound"
                    val duration = data.optInt("duration", 0)
                    val likes = data.optLong("digg_count", 0L)
                    val shares = data.optLong("share_count", 0L)

                    val authorObj = data.optJSONObject("author")
                    val authorName = authorObj?.optString("nickname") ?: "TikTok Creator"
                    val authorUsername = authorObj?.optString("unique_id") ?: ""
                    val authorAvatar = authorObj?.optString("avatar")

                    val options = mutableListOf<VideoDownloadOption>()

                    // HD option
                    if (hdPlay.isNotBlank()) {
                        val fullHd = if (hdPlay.startsWith("http")) hdPlay else "https://www.tikwm.com$hdPlay"
                        options.add(
                            VideoDownloadOption(
                                id = "hd_no_wm",
                                label = "HD (No Watermark)",
                                resolutionLabel = "1080p Original",
                                url = fullHd,
                                isWatermarkFree = true,
                                estimatedSize = "High Quality"
                            )
                        )
                    }

                    // Standard No Watermark option
                    if (playNoWatermark.isNotBlank()) {
                        val fullPlay = if (playNoWatermark.startsWith("http")) playNoWatermark else "https://www.tikwm.com$playNoWatermark"
                        options.add(
                            VideoDownloadOption(
                                id = "sd_no_wm",
                                label = "Fast Download (No Watermark)",
                                resolutionLabel = "720p Clean",
                                url = fullPlay,
                                isWatermarkFree = true,
                                estimatedSize = "Standard"
                            )
                        )
                    }

                    // Audio MP3 option
                    if (music.isNotBlank()) {
                        val fullMusic = if (music.startsWith("http")) music else "https://www.tikwm.com$music"
                        options.add(
                            VideoDownloadOption(
                                id = "audio_mp3",
                                label = "Audio Sound (MP3)",
                                resolutionLabel = "320 kbps",
                                url = fullMusic,
                                isWatermarkFree = true,
                                estimatedSize = "Audio only"
                            )
                        )
                    }

                    if (options.isNotEmpty()) {
                        return Result.success(
                            VideoMetadata(
                                sourceUrl = url,
                                platform = VideoPlatform.TIKTOK,
                                title = title,
                                authorName = authorName,
                                authorUsername = authorUsername,
                                authorAvatarUrl = authorAvatar,
                                coverThumbnailUrl = cover,
                                durationSeconds = duration,
                                downloadOptions = options,
                                musicTitle = musicTitle,
                                likesCount = likes,
                                sharesCount = shares
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("VideoResolver", "TikWM primary failed, trying fallback: ${e.message}")
        }

        // Strategy 2: Cobalt public API fallback
        val cobaltResult = tryCobaltResolver(url, VideoPlatform.TIKTOK)
        if (cobaltResult.isSuccess) {
            return cobaltResult
        }

        return Result.failure(Exception("Unable to extract TikTok video. Please ensure the link is public and active."))
    }

    private suspend fun resolveInstagram(url: String): Result<VideoMetadata> {
        // Strategy 1: Cobalt public API
        val cobaltResult = tryCobaltResolver(url, VideoPlatform.INSTAGRAM)
        if (cobaltResult.isSuccess) {
            return cobaltResult
        }

        // Strategy 2: Try public GraphQL / Embed API
        try {
            val shortcode = extractInstagramShortcode(url)
            if (shortcode != null) {
                // Try embed json endpoint
                val embedUrl = "https://www.instagram.com/p/$shortcode/?__a=1&__d=dis"
                val request = Request.Builder()
                    .url(embedUrl)
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
                    .header("Accept", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank() && body.startsWith("{")) {
                    val root = JSONObject(body)
                    val items = root.optJSONArray("items")
                    val item = items?.optJSONObject(0) ?: root.optJSONObject("graphql")?.optJSONObject("shortcode_media")
                    if (item != null) {
                        val videoVersions = item.optJSONArray("video_versions")
                        var bestVideoUrl: String? = null
                        if (videoVersions != null && videoVersions.length() > 0) {
                            bestVideoUrl = videoVersions.getJSONObject(0).optString("url")
                        } else if (item.has("video_url")) {
                            bestVideoUrl = item.optString("video_url")
                        }

                        if (!bestVideoUrl.isNullOrBlank()) {
                            val userObj = item.optJSONObject("user") ?: item.optJSONObject("owner")
                            val authorName = userObj?.optString("full_name") ?: "Instagram User"
                            val authorUsername = userObj?.optString("username") ?: ""
                            val authorAvatar = userObj?.optString("profile_pic_url")
                            val captionObj = item.optJSONObject("caption")
                            val caption = captionObj?.optString("text") ?: "Instagram Reel"
                            val imageVersions = item.optJSONObject("image_versions2")?.optJSONArray("candidates")
                            val thumb = imageVersions?.optJSONObject(0)?.optString("url")
                                ?: item.optString("display_url")

                            val options = listOf(
                                VideoDownloadOption(
                                    id = "ig_hd",
                                    label = "HD Video (No Watermark)",
                                    resolutionLabel = "Original Quality",
                                    url = bestVideoUrl,
                                    isWatermarkFree = true,
                                    estimatedSize = "Direct Stream"
                                )
                            )

                            return Result.success(
                                VideoMetadata(
                                    sourceUrl = url,
                                    platform = VideoPlatform.INSTAGRAM,
                                    title = caption.take(150),
                                    authorName = authorName,
                                    authorUsername = authorUsername,
                                    authorAvatarUrl = authorAvatar,
                                    coverThumbnailUrl = thumb,
                                    durationSeconds = item.optInt("video_duration", 0),
                                    downloadOptions = options,
                                    musicTitle = "Instagram Audio"
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("VideoResolver", "Instagram direct embed lookup failed: ${e.message}")
        }

        // Strategy 3: FastDL public API
        try {
            val fastDlUrl = "https://api.v02.fastdl.app/graphql"
            val jsonPayload = JSONObject().apply {
                put("url", url)
            }
            val request = Request.Builder()
                .url(fastDlUrl)
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (response.isSuccessful && !body.isNullOrBlank() && body.contains("http")) {
                val json = JSONObject(body)
                val mediaArr = json.optJSONArray("media")
                if (mediaArr != null && mediaArr.length() > 0) {
                    val first = mediaArr.getJSONObject(0)
                    val videoUrl = first.optString("video_url").ifBlank { first.optString("url") }
                    val thumb = first.optString("thumbnail_url").ifBlank { first.optString("cover") }
                    if (videoUrl.isNotBlank()) {
                        return Result.success(
                            VideoMetadata(
                                sourceUrl = url,
                                platform = VideoPlatform.INSTAGRAM,
                                title = "Instagram Video",
                                authorName = "Instagram Reel",
                                authorUsername = "instagram",
                                coverThumbnailUrl = thumb.ifBlank { null },
                                durationSeconds = 0,
                                downloadOptions = listOf(
                                    VideoDownloadOption(
                                        id = "ig_dl",
                                        label = "HD Video (No Watermark)",
                                        resolutionLabel = "Full HD",
                                        url = videoUrl,
                                        isWatermarkFree = true,
                                        estimatedSize = "Original MP4"
                                    )
                                )
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("VideoResolver", "FastDL lookup failed: ${e.message}")
        }

        return Result.failure(Exception("Could not extract Instagram video. Make sure the Instagram post or reel is public."))
    }

    private suspend fun tryCobaltResolver(url: String, platform: VideoPlatform): Result<VideoMetadata> {
        val cobaltInstances = listOf(
            "https://api.cobalt.tools",
            "https://cobalt-api.kwiatekm.tokyo",
            "https://cobalt.canine.tools"
        )

        for (endpoint in cobaltInstances) {
            try {
                val payload = JSONObject().apply {
                    put("url", url)
                    put("videoQuality", "max")
                    put("audioFormat", "mp3")
                    put("filenameStyle", "basic")
                    put("downloadMode", "auto")
                }

                val request = Request.Builder()
                    .url(endpoint)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "VideoDownloader/1.0")
                    .post(payload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val status = json.optString("status")
                    val videoUrl = json.optString("url")

                    if ((status == "tunnel" || status == "redirect" || status == "success") && videoUrl.isNotBlank()) {
                        val title = json.optString("filename").ifBlank {
                            if (platform == VideoPlatform.TIKTOK) "TikTok Video (No Watermark)" else "Instagram Video (No Watermark)"
                        }

                        val options = listOf(
                            VideoDownloadOption(
                                id = "cobalt_hd",
                                label = "HD Quality (No Watermark)",
                                resolutionLabel = "1080p No Watermark",
                                url = videoUrl,
                                isWatermarkFree = true,
                                estimatedSize = "High Quality"
                            )
                        )

                        return Result.success(
                            VideoMetadata(
                                sourceUrl = url,
                                platform = platform,
                                title = title,
                                authorName = if (platform == VideoPlatform.TIKTOK) "TikTok Creator" else "Instagram Creator",
                                authorUsername = if (platform == VideoPlatform.TIKTOK) "@tiktok" else "@instagram",
                                coverThumbnailUrl = null,
                                durationSeconds = 0,
                                downloadOptions = options
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("VideoResolver", "Cobalt instance $endpoint error: ${e.message}")
            }
        }

        return Result.failure(Exception("Cobalt instances unavailable"))
    }

    private fun extractUrl(text: String): String? {
        val matcher = Pattern.compile("https?://[a-zA-Z0-9.-]+(?:/[a-zA-Z0-9._~:/?#\\[\\]@!$&'()*+,;=-]*)?").matcher(text)
        return if (matcher.find()) matcher.group(0) else null
    }

    private fun expandShortUrlIfNeeded(url: String): String {
        return try {
            val headRequest = Request.Builder()
                .url(url)
                .head()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()
            client.newCall(headRequest).execute().use { response ->
                response.request.url.toString()
            }
        } catch (_: Exception) {
            url
        }
    }

    private fun extractInstagramShortcode(url: String): String? {
        val pattern = Pattern.compile("/(?:p|reel|tv)/([a-zA-Z0-9_-]+)")
        val matcher = pattern.matcher(url)
        return if (matcher.find()) matcher.group(1) else null
    }
}
