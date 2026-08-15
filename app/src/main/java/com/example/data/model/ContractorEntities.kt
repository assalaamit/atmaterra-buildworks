package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val title: String, val badge: String, val description: String) {
    CEO("Chief Executive Officer", "CEO", "Keputusan strategis, evaluasi finansial & portfolio seluruh proyek ABI"),
    FINANCE("Finance & Akuntansi", "FIN", "Kontrol arus kas, pencairan dana, verifikasi invoice & laporan realisasi biaya"),
    PROJECT_MANAGER("Project Manager", "PM", "Executive review, evaluasi kurva S, audit anggaran RAP & timeschedule seluruh proyek"),
    PROJECT_LEADER_GPA("Project Leader Masjid GPA", "PL-GPA", "Penanggung jawab lapangan & teknis proyek Masjid Al-ikhlas GPA Purbalingga"),
    PROJECT_LEADER_GOR("Project Leader Kost GOR", "PL-GOR", "Penanggung jawab lapangan & teknis proyek Rehab Rumah Kost GOR"),
    PROJECT_LEADER_MYKOST("Project Leader My Kost", "PL-MYK", "Penanggung jawab lapangan & teknis proyek Rehab Rumah Kost My Kost DukuhWaluh"),
    PROJECT_LEADER_LO_VILLA("Project Leader Lo Villa", "PL-LV", "Penanggung jawab lapangan & teknis proyek Rehab Rumah Lo Villa"),
    LOGISTIC("Logistik & Material", "LOG", "Pencatatan material in/out, inventaris gudang, surat jalan & peringatan stok"),
    ADMINISTRATOR("Administrator Sistem", "ADM", "Pengelolaan dokumen kontrak, perizinan, hak akses user & administrasi")
}

data class UserProfile(
    val id: String,
    val username: String,
    var password: String = "abc123",
    val name: String,
    val role: UserRole,
    val phone: String,
    val avatarInitials: String,
    val assignedProjectCode: String = "" // e.g. "ABI-01", "ALL"
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val clientName: String,
    val location: String,
    val startDate: String,
    val targetEndDate: String,
    val contractValueRp: Long,
    val currentPlannedProgress: Float,
    val currentActualProgress: Float,
    val status: String = "BERJALAN" // BERJALAN, SELESAI, TERTUNDA
)

@Entity(tableName = "materials")
data class MaterialItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val name: String,
    val category: String, // Struktur, Arsitektur, MEP, Finishing, Alat Bantu
    val unit: String,     // Sak, Batang, M3, Ton, Kg, Pcs, Lembar, Drum, Liter
    val currentStock: Double,
    val minStockThreshold: Double,
    val unitPriceRp: Long,
    val storageLocation: String,
    val supplierName: String,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "material_transactions")
data class MaterialTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val materialId: Long,
    val materialName: String,
    val type: String, // MASUK, KELUAR, RETURN
    val quantity: Double,
    val unit: String,
    val suratJalanNo: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val loggedByRole: String,
    val loggedByName: String,
    val usedForWorkItem: String, // e.g. "Pengecoran Kolom Lt. 3"
    val notes: String
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val itemCode: String,
    val name: String,
    val category: String, // Material Konstruksi, Peralatan & Hand Tools, Safety & APD, Perancah & Formwork, MEP & Elektrikal, Bahan Kimia
    val status: String,   // TERSEDIA, DIGUNAKAN, DALAM_PERBAIKAN, RUSAK, HABIS
    val quantity: Double,
    val unit: String,
    val minStockThreshold: Double,
    val unitPriceRp: Long,
    val storageLocation: String, // Gudang Utama Rak A-01, Yard Terbuka Blok B, Lantai 4 Zona Barat, Workshop Mekanik
    val assignedTo: String,     // Tersedia di Gudang, Tim Pembesian (Mandor Supri), Supervisor Hendro
    val supplierOrBrand: String,
    val lastCheckedDateMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "equipments")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val code: String,       // e.g. EXC-01, TC-01, DT-03
    val name: String,       // e.g. Excavator Komatsu PC200
    val category: String,   // Alat Berat, Alat Angkut, Mesin Kerja, Scaffolding/Bekisting
    val status: String,     // OPERASIONAL, MAINTENANCE, RUSAK, STANDBY
    val currentHourMeter: Double, // Jam operasi (HM)
    val fuelCapacityLiters: Double,
    val currentFuelLiters: Double,
    val operatorName: String,
    val lastServiceDate: String,
    val nextServiceHourMeter: Double,
    val notes: String = ""
)

@Entity(tableName = "equipment_logs")
data class EquipmentLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val equipmentId: Long,
    val equipmentCode: String,
    val equipmentName: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val startHourMeter: Double,
    val endHourMeter: Double,
    val totalHours: Double,
    val fuelAddedLiters: Double,
    val workDescription: String,
    val operatorName: String,
    val conditionNotes: String = "Normal"
)

@Entity(tableName = "daily_reports")
data class DailyReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val reportNumber: String,
    val reportDateMillis: Long = System.currentTimeMillis(),
    val weatherMorning: String,   // CERAH, BERAWAN, HUJAN_RINGAN, HUJAN_LEBAT
    val weatherAfternoon: String, // CERAH, BERAWAN, HUJAN_RINGAN, HUJAN_LEBAT
    val weatherEvening: String,   // CERAH, BERAWAN, HUJAN_RINGAN, HUJAN_LEBAT
    val mandorCount: Int,
    val tukangCount: Int,
    val pekerjaCount: Int,
    val subconCount: Int = 0,
    val totalWorkers: Int,
    val workDescription: String,
    val materialsUsedSummary: String,
    val heavyEquipmentUsedSummary: String,
    val obstaclesIssues: String,
    val solutionsActionTaken: String,
    val progressAddedPercent: Float = 0.0f,
    val status: String = "SUBMITTED", // DRAFT, SUBMITTED, APPROVED
    val createdByRole: String,
    val createdByName: String,
    val verifiedByName: String = "",
    val verifiedDateMillis: Long = 0L,
    val siteManagerNotes: String = ""
)

@Entity(tableName = "weekly_reports")
data class WeeklyReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val weekNumber: Int,
    val weekTitle: String,
    val startDate: String,
    val endDate: String,
    val plannedProgressCumulative: Float,
    val actualProgressCumulative: Float,
    val previousProgressCumulative: Float,
    val weeklyProgressGain: Float,
    val deviationPercent: Float,
    val workCompletedSummary: String,
    val targetNextWeek: String,
    val riskEvaluation: String,
    val approvedBySiteManager: Boolean = true,
    val status: String = "APPROVED"
)

@Entity(tableName = "s_curve_milestones")
data class SCurveMilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val weekNumber: Int,
    val weekLabel: String,
    val plannedWeeklyPercent: Float,
    val plannedCumulativePercent: Float,
    val actualWeeklyPercent: Float,
    val actualCumulativePercent: Float,
    val isCompleted: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val userRole: String,
    val userName: String,
    val category: String, // LOGISTIK, ALAT, LAPORAN, PROGRESS, SISTEM, DOKUMEN, ABSENSI
    val title: String,
    val details: String
)

@Entity(tableName = "project_notifications")
data class ProjectNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val title: String,
    val message: String,
    val type: String, // MAINTENANCE, REPORT_DEADLINE, LOW_STOCK, PROJECT_STATUS, TEAM_BROADCAST
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val targetRole: String = "ALL"
)

data class NotificationPreferences(
    val maintenanceAlerts: Boolean = true,
    val reportDeadlineAlerts: Boolean = true,
    val lowStockAlerts: Boolean = true,
    val projectStatusAlerts: Boolean = true,
    val teamBroadcastAlerts: Boolean = true
)

@Entity(tableName = "project_documents")
data class ProjectDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val folderCategory: String, // Gambar Kerja & Shop Drawing, RAB RAP & Kontrak, Surat Jalan & PO, Laporan & BA, K3 & Safety, Dokumentasi Foto
    val title: String,
    val fileName: String,
    val fileType: String,       // PDF, IMG, DWG, XLS, DOC
    val fileSizeBytes: Long,
    val uploadedByRole: String,
    val uploadedByName: String,
    val uploadDateMillis: Long = System.currentTimeMillis(),
    val version: String = "v1.0",
    val allowedRoles: String = "SEMUA", // SEMUA, MANAGEMENT_ONLY, LOGISTIK_ONLY, SPV_ONLY
    val notes: String = ""
)

@Entity(tableName = "worker_attendances")
data class WorkerAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val dateMillis: Long = System.currentTimeMillis(),
    val dateString: String,
    val workerName: String,
    val workerCategory: String, // PROJECT_LEADER, SUPERVISOR, MANDOR, TUKANG_BESI, TUKANG_BATU, TUKANG_KAYU, TUKANG_LISTRIK, PEKERJA_HELPER, SUBCON
    val status: String,         // HADIR, SETENGAH_HARI, LEMBUR, IZIN_SAKIT, ALFA
    val overtimeHours: Double = 0.0,
    val dailyRateRp: Long,
    val totalEarnedRp: Long,
    val workAssignedTo: String,
    val loggedByName: String,
    val notes: String = ""
)

@Entity(tableName = "project_schedules")
data class ProjectScheduleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val wbsCode: String,        // 1.1, 1.2, 2.1, 3.0
    val taskName: String,
    val category: String,       // Pekerjaan Persiapan, Struktur Bawah, Struktur Atas, Arsitektur & Pasangan, MEP & Utilitas, Finishing
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val weightPercent: Float,
    val plannedProgressPercent: Float,
    val actualProgressPercent: Float,
    val status: String,         // BELUM_MULAI, SEDANG_BERJALAN, SELESAI, TERTUNDA
    val assignedLeader: String
)

@Entity(tableName = "project_rap_items")
data class ProjectRapItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val category: String,       // MATERIAL, ALAT_BERAT, UPAH_TENAGA, SUBKONTRAKTOR, OVERHEAD_K3
    val itemCode: String,
    val itemName: String,
    val volume: Double,
    val unit: String,
    val unitPriceRp: Long,
    val budgetRapRp: Long,
    val actualCostRp: Long,
    val notes: String = ""
)
