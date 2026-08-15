package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ContractorDao
import com.example.data.model.*

@Database(
    entities = [
        ProjectEntity::class,
        MaterialItemEntity::class,
        MaterialTransactionEntity::class,
        InventoryItemEntity::class,
        EquipmentEntity::class,
        EquipmentLogEntity::class,
        DailyReportEntity::class,
        WeeklyReportEntity::class,
        SCurveMilestoneEntity::class,
        ActivityLogEntity::class,
        ProjectNotificationEntity::class,
        ProjectDocumentEntity::class,
        WorkerAttendanceEntity::class,
        ProjectScheduleItemEntity::class,
        ProjectRapItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contractorDao(): ContractorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pro_kontraktor_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
