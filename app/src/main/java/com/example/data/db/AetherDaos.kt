package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePost(id: String)
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY startTime DESC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Query("DELETE FROM missions WHERE id = :id")
    suspend fun deleteMission(id: String)
}

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT 300")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntity)

    @Query("DELETE FROM logs")
    suspend fun clearLogs()
}

@Dao
interface TrendDao {
    @Query("SELECT * FROM trends ORDER BY timestamp DESC")
    fun getAllTrends(): Flow<List<TrendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrends(trends: List<TrendEntity>)
}

@Dao
interface CatalogueDao {
    @Query("SELECT * FROM catalogue")
    fun getAllCatalogueItems(): Flow<List<CatalogueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogueItems(items: List<CatalogueEntity>)
}
