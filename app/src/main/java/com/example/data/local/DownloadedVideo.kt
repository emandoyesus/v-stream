package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_videos")
data class DownloadedVideo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val authorName: String = "",
    val authorUsername: String = "",
    val platform: String, // "TIKTOK" or "INSTAGRAM"
    val thumbnailUrl: String? = null,
    val localUri: String, // content:// or file:// or cache uri
    val fileName: String,
    val sourceUrl: String,
    val durationSeconds: Int = 0,
    val fileSizeBytes: Long = 0,
    val quality: String = "HD No Watermark",
    val downloadedAt: Long = System.currentTimeMillis()
)
