package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val categoryName: String,
    val hashtagsCsv: String,
    val vibe: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val imageUrl: String?,
    val videoUrl: String? = null,
    val postType: String = "PHOTO",
    val aspectRatio: String = "1:1",
    val durationSeconds: Int = 0,
    val chunkInfo: String? = null,
    val status: String,
    val targetPage: String,
    val fbPostId: String?,
    val hashValue: String,
    val timestamp: Long,
    val impressions: Int,
    val engagementRate: Float
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val typeName: String,
    val statusName: String,
    val startTime: Long,
    val endTime: Long?,
    val targetPage: String,
    val stepsJson: String,
    val postId: String?
)

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey val id: String,
    val level: String,
    val source: String,
    val message: String,
    val timestamp: Long
)

@Entity(tableName = "trends")
data class TrendEntity(
    @PrimaryKey val id: String,
    val source: String,
    val query: String,
    val keyword: String,
    val score: Int,
    val timestamp: Long
)

@Entity(tableName = "catalogue")
data class CatalogueEntity(
    @PrimaryKey val id: String,
    val categoryName: String,
    val text: String,
    val authorOrSource: String,
    val isUsed: Boolean
)
