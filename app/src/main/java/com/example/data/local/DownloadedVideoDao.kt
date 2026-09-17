package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedVideoDao {
    @Query("SELECT * FROM downloaded_videos ORDER BY downloadedAt DESC")
    fun getAllVideos(): Flow<List<DownloadedVideo>>

    @Query("SELECT * FROM downloaded_videos WHERE platform = :platform ORDER BY downloadedAt DESC")
    fun getVideosByPlatform(platform: String): Flow<List<DownloadedVideo>>

    @Query("SELECT * FROM downloaded_videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: Long): DownloadedVideo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: DownloadedVideo): Long

    @Query("DELETE FROM downloaded_videos WHERE id = :id")
    suspend fun deleteVideoById(id: Long)

    @Query("DELETE FROM downloaded_videos")
    suspend fun clearAll()
}
