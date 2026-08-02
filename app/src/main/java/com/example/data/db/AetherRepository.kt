package com.example.data.db

import com.example.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AetherRepository(private val db: AetherDatabase) {

    val allPosts: Flow<List<PostItem>> = db.postDao().getAllPosts().map { entities ->
        entities.map { it.toDomain() }
    }

    val allMissions: Flow<List<Mission>> = db.missionDao().getAllMissions().map { entities ->
        entities.map { it.toDomain() }
    }

    val allLogs: Flow<List<SystemLog>> = db.logDao().getAllLogs().map { entities ->
        entities.map { SystemLog(it.id, it.level, it.source, it.message, it.timestamp) }
    }

    val allTrends: Flow<List<TrendItem>> = db.trendDao().getAllTrends().map { entities ->
        entities.map { TrendItem(it.id, it.source, it.query, it.keyword, it.score, it.timestamp) }
    }

    val allCatalogue: Flow<List<ContentCatalogueItem>> = db.catalogueDao().getAllCatalogueItems().map { entities ->
        entities.map { ContentCatalogueItem(it.id, parseCategory(it.categoryName), it.text, it.authorOrSource, it.isUsed) }
    }

    suspend fun savePost(post: PostItem) {
        db.postDao().insertPost(post.toEntity())
    }

    suspend fun saveMission(mission: Mission) {
        db.missionDao().insertMission(mission.toEntity())
    }

    suspend fun saveLog(log: SystemLog) {
        db.logDao().insertLog(LogEntity(log.id, log.level, log.source, log.message, log.timestamp))
    }

    suspend fun clearLogs() {
        db.logDao().clearLogs()
    }

    suspend fun seedInitialCatalogue(items: List<ContentCatalogueItem>) {
        db.catalogueDao().insertCatalogueItems(items.map {
            CatalogueEntity(it.id, it.category.name, it.text, it.authorOrSource, it.isUsed)
        })
    }

    suspend fun seedInitialTrends(trends: List<TrendItem>) {
        db.trendDao().insertTrends(trends.map {
            TrendEntity(it.id, it.source, it.query, it.keyword, it.score, it.timestamp)
        })
    }
}

private fun parseCategory(name: String): ContentCategory {
    return try {
        ContentCategory.valueOf(name)
    } catch (e: Exception) {
        ContentCategory.QUOTES
    }
}

private fun PostItem.toEntity() = PostEntity(
    id = id,
    title = title,
    body = body,
    categoryName = category.name,
    hashtagsCsv = hashtags.joinToString(","),
    vibe = vibe,
    primaryColorHex = primaryColorHex,
    secondaryColorHex = secondaryColorHex,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    postType = postType,
    aspectRatio = aspectRatio,
    durationSeconds = durationSeconds,
    chunkInfo = chunkInfo,
    status = status,
    targetPage = targetPage,
    fbPostId = fbPostId,
    hashValue = hashValue,
    timestamp = timestamp,
    impressions = impressions,
    engagementRate = engagementRate
)

private fun PostEntity.toDomain() = PostItem(
    id = id,
    title = title,
    body = body,
    category = parseCategory(categoryName),
    hashtags = hashtagsCsv.split(",").filter { it.isNotBlank() },
    vibe = vibe,
    primaryColorHex = primaryColorHex,
    secondaryColorHex = secondaryColorHex,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    postType = postType,
    aspectRatio = aspectRatio,
    durationSeconds = durationSeconds,
    chunkInfo = chunkInfo,
    status = status,
    targetPage = targetPage,
    fbPostId = fbPostId,
    hashValue = hashValue,
    timestamp = timestamp,
    impressions = impressions,
    engagementRate = engagementRate
)

private fun Mission.toEntity() = MissionEntity(
    id = id,
    title = title,
    typeName = type.name,
    statusName = status.name,
    startTime = startTime,
    endTime = endTime,
    targetPage = targetPage,
    stepsJson = steps.joinToString(";") { "${it.stepIndex}|${it.workerType.name}|${it.stepName}|${it.detail}|${it.status.name}" },
    postId = generatedPost?.id
)

private fun MissionEntity.toDomain(): Mission {
    val stepsList = if (stepsJson.isNotBlank()) {
        stepsJson.split(";").mapNotNull { stepStr ->
            val parts = stepStr.split("|")
            if (parts.size >= 5) {
                MissionStep(
                    stepIndex = parts[0].toIntOrNull() ?: 0,
                    workerType = try { WorkerType.valueOf(parts[1]) } catch (e: Exception) { WorkerType.CONTENT },
                    stepName = parts[2],
                    detail = parts[3],
                    status = try { MissionStatus.valueOf(parts[4]) } catch (e: Exception) { MissionStatus.IDLE }
                )
            } else null
        }
    } else emptyList()

    return Mission(
        id = id,
        title = title,
        type = try { MissionType.valueOf(typeName) } catch (e: Exception) { MissionType.DAILY_PIPELINE },
        status = try { MissionStatus.valueOf(statusName) } catch (e: Exception) { MissionStatus.IDLE },
        startTime = startTime,
        endTime = endTime,
        targetPage = targetPage,
        steps = stepsList,
        generatedPost = null
    )
}
