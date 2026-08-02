package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PostEntity::class,
        MissionEntity::class,
        LogEntity::class,
        TrendEntity::class,
        CatalogueEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun missionDao(): MissionDao
    abstract fun logDao(): LogDao
    abstract fun trendDao(): TrendDao
    abstract fun catalogueDao(): CatalogueDao

    companion object {
        @Volatile
        private var INSTANCE: AetherDatabase? = null

        fun getDatabase(context: Context): AetherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AetherDatabase::class.java,
                    "aether_os_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
