package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContractorDao {

    // PROJECTS
    @Query("SELECT * FROM projects ORDER BY id ASC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    // MATERIALS
    @Query("SELECT * FROM materials WHERE projectId = :projectId ORDER BY name ASC")
    fun getMaterialsByProject(projectId: Long): Flow<List<MaterialItemEntity>>

    @Query("SELECT * FROM materials WHERE projectId = :projectId AND currentStock <= minStockThreshold")
    fun getLowStockMaterials(projectId: Long): Flow<List<MaterialItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<MaterialItemEntity>)

    @Update
    suspend fun updateMaterial(material: MaterialItemEntity)

    @Query("UPDATE materials SET currentStock = currentStock + :delta, lastUpdatedMillis = :time WHERE id = :materialId")
    suspend fun adjustMaterialStock(materialId: Long, delta: Double, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterialById(id: Long)

    // MATERIAL TRANSACTIONS
    @Query("SELECT * FROM material_transactions WHERE projectId = :projectId ORDER BY dateMillis DESC")
    fun getMaterialTransactions(projectId: Long): Flow<List<MaterialTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialTransaction(tx: MaterialTransactionEntity): Long

    // INVENTORY ITEMS
    @Query("SELECT * FROM inventory_items WHERE projectId = :projectId ORDER BY name ASC")
    fun getInventoryByProject(projectId: Long): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(items: List<InventoryItemEntity>)

    @Update
    suspend fun updateInventoryItem(item: InventoryItemEntity)

    @Query("UPDATE inventory_items SET status = :newStatus, storageLocation = :newLocation, assignedTo = :assignedTo, quantity = :quantity, lastCheckedDateMillis = :now WHERE id = :itemId")
    suspend fun updateInventoryItemStatusAndLocation(itemId: Long, newStatus: String, newLocation: String, assignedTo: String, quantity: Double, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteInventoryItemById(id: Long)

    // EQUIPMENTS
    @Query("SELECT * FROM equipments WHERE projectId = :projectId ORDER BY code ASC")
    fun getEquipmentsByProject(projectId: Long): Flow<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: EquipmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipments(equipments: List<EquipmentEntity>)

    @Update
    suspend fun updateEquipment(equipment: EquipmentEntity)

    @Query("UPDATE equipments SET currentHourMeter = :hourMeter, currentFuelLiters = :fuel, status = :status WHERE id = :id")
    suspend fun updateEquipmentUsage(id: Long, hourMeter: Double, fuel: Double, status: String)

    @Query("DELETE FROM equipments WHERE id = :id")
    suspend fun deleteEquipmentById(id: Long)

    // EQUIPMENT LOGS
    @Query("SELECT * FROM equipment_logs WHERE projectId = :projectId ORDER BY dateMillis DESC")
    fun getEquipmentLogs(projectId: Long): Flow<List<EquipmentLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentLog(log: EquipmentLogEntity): Long

    // DAILY REPORTS
    @Query("SELECT * FROM daily_reports WHERE projectId = :projectId ORDER BY reportDateMillis DESC")
    fun getDailyReports(projectId: Long): Flow<List<DailyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReport(report: DailyReportEntity): Long

    @Update
    suspend fun updateDailyReport(report: DailyReportEntity)

    @Query("UPDATE daily_reports SET status = :status, verifiedByName = :verifier, verifiedDateMillis = :time, siteManagerNotes = :notes WHERE id = :id")
    suspend fun verifyDailyReport(id: Long, status: String, verifier: String, time: Long, notes: String)

    @Query("DELETE FROM daily_reports WHERE id = :id")
    suspend fun deleteDailyReportById(id: Long)

    // WEEKLY REPORTS
    @Query("SELECT * FROM weekly_reports WHERE projectId = :projectId ORDER BY weekNumber ASC")
    fun getWeeklyReports(projectId: Long): Flow<List<WeeklyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReport(report: WeeklyReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyReports(reports: List<WeeklyReportEntity>)

    @Update
    suspend fun updateWeeklyReport(report: WeeklyReportEntity)

    // S-CURVE MILESTONES
    @Query("SELECT * FROM s_curve_milestones WHERE projectId = :projectId ORDER BY weekNumber ASC")
    fun getSCurveMilestones(projectId: Long): Flow<List<SCurveMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSCurveMilestone(milestone: SCurveMilestoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSCurveMilestones(milestones: List<SCurveMilestoneEntity>)

    @Update
    suspend fun updateSCurveMilestone(milestone: SCurveMilestoneEntity)

    // ACTIVITY LOGS
    @Query("SELECT * FROM activity_logs WHERE projectId = :projectId ORDER BY timestampMillis DESC LIMIT 60")
    fun getActivityLogs(projectId: Long): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity): Long

    // NOTIFICATIONS
    @Query("SELECT * FROM project_notifications WHERE projectId = :projectId ORDER BY timestampMillis DESC")
    fun getNotificationsByProject(projectId: Long): Flow<List<ProjectNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: ProjectNotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<ProjectNotificationEntity>)

    @Query("UPDATE project_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE project_notifications SET isRead = 1 WHERE projectId = :projectId")
    suspend fun markAllNotificationsAsRead(projectId: Long)

    @Query("DELETE FROM project_notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    @Query("DELETE FROM project_notifications WHERE projectId = :projectId")
    suspend fun clearAllNotifications(projectId: Long)

    // DOCUMENTS
    @Query("SELECT * FROM project_documents WHERE projectId = :projectId ORDER BY uploadDateMillis DESC")
    fun getDocumentsByProject(projectId: Long): Flow<List<ProjectDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: ProjectDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<ProjectDocumentEntity>)

    @Update
    suspend fun updateDocument(doc: ProjectDocumentEntity)

    @Query("DELETE FROM project_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)

    // ATTENDANCES
    @Query("SELECT * FROM worker_attendances WHERE projectId = :projectId ORDER BY dateMillis DESC")
    fun getAttendancesByProject(projectId: Long): Flow<List<WorkerAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: WorkerAttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<WorkerAttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: WorkerAttendanceEntity)

    @Query("DELETE FROM worker_attendances WHERE id = :id")
    suspend fun deleteAttendanceById(id: Long)

    // PROJECT SCHEDULES / TIMESCHEDULE
    @Query("SELECT * FROM project_schedules WHERE projectId = :projectId ORDER BY id ASC")
    fun getSchedulesByProject(projectId: Long): Flow<List<ProjectScheduleItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ProjectScheduleItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItems(items: List<ProjectScheduleItemEntity>)

    @Update
    suspend fun updateScheduleItem(item: ProjectScheduleItemEntity)

    @Query("UPDATE project_schedules SET actualProgressPercent = :actualProg, status = :status WHERE id = :id")
    suspend fun updateScheduleProgress(id: Long, actualProg: Float, status: String)

    // PROJECT RAP
    @Query("SELECT * FROM project_rap_items WHERE projectId = :projectId ORDER BY id ASC")
    fun getRapItemsByProject(projectId: Long): Flow<List<ProjectRapItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRapItem(item: ProjectRapItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRapItems(items: List<ProjectRapItemEntity>)

    @Update
    suspend fun updateRapItem(item: ProjectRapItemEntity)
}
