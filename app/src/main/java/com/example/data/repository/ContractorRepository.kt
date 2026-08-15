package com.example.data.repository

import com.example.data.dao.ContractorDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ContractorRepository(private val dao: ContractorDao) {

    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()

    fun getProject(projectId: Long): Flow<ProjectEntity?> = dao.getProjectById(projectId)

    fun getMaterials(projectId: Long): Flow<List<MaterialItemEntity>> = dao.getMaterialsByProject(projectId)

    fun getLowStockMaterials(projectId: Long): Flow<List<MaterialItemEntity>> = dao.getLowStockMaterials(projectId)

    fun getMaterialTransactions(projectId: Long): Flow<List<MaterialTransactionEntity>> = dao.getMaterialTransactions(projectId)

    fun getInventory(projectId: Long): Flow<List<InventoryItemEntity>> = dao.getInventoryByProject(projectId)

    fun getEquipments(projectId: Long): Flow<List<EquipmentEntity>> = dao.getEquipmentsByProject(projectId)

    fun getEquipmentLogs(projectId: Long): Flow<List<EquipmentLogEntity>> = dao.getEquipmentLogs(projectId)

    fun getDailyReports(projectId: Long): Flow<List<DailyReportEntity>> = dao.getDailyReports(projectId)

    fun getWeeklyReports(projectId: Long): Flow<List<WeeklyReportEntity>> = dao.getWeeklyReports(projectId)

    fun getSCurveMilestones(projectId: Long): Flow<List<SCurveMilestoneEntity>> = dao.getSCurveMilestones(projectId)

    fun getActivityLogs(projectId: Long): Flow<List<ActivityLogEntity>> = dao.getActivityLogs(projectId)

    fun getNotifications(projectId: Long): Flow<List<ProjectNotificationEntity>> = dao.getNotificationsByProject(projectId)

    fun getDocuments(projectId: Long): Flow<List<ProjectDocumentEntity>> = dao.getDocumentsByProject(projectId)

    fun getAttendances(projectId: Long): Flow<List<WorkerAttendanceEntity>> = dao.getAttendancesByProject(projectId)

    fun getSchedules(projectId: Long): Flow<List<ProjectScheduleItemEntity>> = dao.getSchedulesByProject(projectId)

    fun getRapItems(projectId: Long): Flow<List<ProjectRapItemEntity>> = dao.getRapItemsByProject(projectId)

    // ACTIONS: IMPORT EXCEL / CSV
    suspend fun importExcelMaterialItems(
        projectId: Long,
        materials: List<MaterialItemEntity>,
        userName: String,
        role: String,
        fileName: String
    ): Unit = withContext(Dispatchers.IO) {
        dao.insertMaterials(materials)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = role,
                userName = userName,
                category = "LOGISTIK",
                title = "Impor Material Excel: $fileName",
                details = "Berhasil mengimpor ${materials.size} item material baru dari spreadsheet $fileName"
            )
        )
    }

    suspend fun importExcelScheduleItems(
        projectId: Long,
        scheduleItems: List<ProjectScheduleItemEntity>,
        userName: String,
        role: String,
        fileName: String
    ): Unit = withContext(Dispatchers.IO) {
        scheduleItems.forEach { dao.insertScheduleItem(it) }
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = role,
                userName = userName,
                category = "PROGRESS",
                title = "Impor Time Schedule Excel: $fileName",
                details = "Berhasil mengimpor ${scheduleItems.size} uraian pekerjaan WBS dari spreadsheet $fileName"
            )
        )
    }

    suspend fun importExcelRapItems(
        projectId: Long,
        rapItems: List<ProjectRapItemEntity>,
        userName: String,
        role: String,
        fileName: String
    ): Unit = withContext(Dispatchers.IO) {
        rapItems.forEach { dao.insertRapItem(it) }
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = role,
                userName = userName,
                category = "PROGRESS",
                title = "Impor RAP & Anggaran Excel: $fileName",
                details = "Berhasil mengimpor ${rapItems.size} pos anggaran RAP dari spreadsheet $fileName"
            )
        )
    }

    suspend fun importExcelAttendanceItems(
        projectId: Long,
        attendances: List<WorkerAttendanceEntity>,
        userName: String,
        role: String,
        fileName: String
    ): Unit = withContext(Dispatchers.IO) {
        attendances.forEach { dao.insertAttendance(it) }
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = role,
                userName = userName,
                category = "ABSENSI",
                title = "Impor Absensi Excel: $fileName",
                details = "Berhasil mengimpor ${attendances.size} data kehadiran pekerja dari spreadsheet $fileName"
            )
        )
    }

    // ACTIONS: MATERIALS & TRANSACTIONS
    suspend fun recordMaterialTransaction(
        projectId: Long,
        materialId: Long,
        materialName: String,
        type: String,
        quantity: Double,
        unit: String,
        suratJalanNo: String,
        loggedByRole: String,
        loggedByName: String,
        usedForWorkItem: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val tx = MaterialTransactionEntity(
            projectId = projectId,
            materialId = materialId,
            materialName = materialName,
            type = type,
            quantity = quantity,
            unit = unit,
            suratJalanNo = suratJalanNo,
            dateMillis = System.currentTimeMillis(),
            loggedByRole = loggedByRole,
            loggedByName = loggedByName,
            usedForWorkItem = usedForWorkItem,
            notes = notes
        )
        dao.insertMaterialTransaction(tx)

        // Adjust stock
        val delta = if (type == "MASUK" || type == "RETURN") quantity else -quantity
        dao.adjustMaterialStock(materialId, delta)

        // Activity Log
        val actionText = if (type == "MASUK") "Penerimaan Material" else if (type == "KELUAR") "Pengeluaran Material" else "Pengembalian Material"
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = loggedByRole,
                userName = loggedByName,
                category = "LOGISTIK",
                title = "$actionText: $materialName ($quantity $unit)",
                details = "SJ: $suratJalanNo | Lokasi/Peruntukan: $usedForWorkItem"
            )
        )
    }

    suspend fun addNewMaterial(material: MaterialItemEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        val id = dao.insertMaterial(material)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = material.projectId,
                userRole = role,
                userName = userName,
                category = "LOGISTIK",
                title = "Material Baru Ditambahkan",
                details = "${material.name} (${material.currentStock} ${material.unit}) di ${material.storageLocation}"
            )
        )
        id
    }

    // ACTIONS: INVENTORY
    suspend fun addNewInventoryItem(item: InventoryItemEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        val id = dao.insertInventoryItem(item)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = item.projectId,
                userRole = role,
                userName = userName,
                category = "LOGISTIK",
                title = "Item Inventaris Didaftarkan",
                details = "[${item.itemCode}] ${item.name} (${item.quantity} ${item.unit}) - Status: ${item.status}"
            )
        )
        id
    }

    suspend fun updateInventoryItemStatus(
        item: InventoryItemEntity,
        newStatus: String,
        newLocation: String,
        assignedTo: String,
        quantity: Double,
        userName: String,
        role: String
    ) = withContext(Dispatchers.IO) {
        dao.updateInventoryItemStatusAndLocation(
            itemId = item.id,
            newStatus = newStatus,
            newLocation = newLocation,
            assignedTo = assignedTo,
            quantity = quantity,
            now = System.currentTimeMillis()
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = item.projectId,
                userRole = role,
                userName = userName,
                category = "LOGISTIK",
                title = "Update Status Inventaris: ${item.itemCode}",
                details = "Status: $newStatus | Lokasi: $newLocation | Ptg: $assignedTo"
            )
        )
    }

    // ACTIONS: EQUIPMENT
    suspend fun recordEquipmentLog(
        projectId: Long,
        equipment: EquipmentEntity,
        startHm: Double,
        endHm: Double,
        fuelAdded: Double,
        workDesc: String,
        operator: String,
        condition: String,
        newStatus: String,
        loggedByName: String,
        loggedByRole: String
    ) = withContext(Dispatchers.IO) {
        val totalHours = if (endHm >= startHm) endHm - startHm else 0.0
        val currentFuel = (equipment.currentFuelLiters + fuelAdded - (totalHours * 12.0)).coerceAtLeast(0.0)

        val log = EquipmentLogEntity(
            projectId = projectId,
            equipmentId = equipment.id,
            equipmentCode = equipment.code,
            equipmentName = equipment.name,
            dateMillis = System.currentTimeMillis(),
            startHourMeter = startHm,
            endHourMeter = endHm,
            totalHours = totalHours,
            fuelAddedLiters = fuelAdded,
            workDescription = workDesc,
            operatorName = operator,
            conditionNotes = condition
        )
        dao.insertEquipmentLog(log)
        dao.updateEquipmentUsage(equipment.id, endHm, currentFuel, newStatus)

        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = loggedByRole,
                userName = loggedByName,
                category = "ALAT",
                title = "Log Alat: ${equipment.code} - ${equipment.name}",
                details = "HM: $endHm (+$totalHours jam) | Status: $newStatus | Op: $operator"
            )
        )
    }

    suspend fun addNewEquipment(equipment: EquipmentEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        val id = dao.insertEquipment(equipment)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = equipment.projectId,
                userRole = role,
                userName = userName,
                category = "ALAT",
                title = "Peralatan Baru Didaftarkan",
                details = "${equipment.code} - ${equipment.name} (${equipment.status})"
            )
        )
        id
    }

    // ACTIONS: REPORTS
    suspend fun submitDailyReport(report: DailyReportEntity) = withContext(Dispatchers.IO) {
        val id = dao.insertDailyReport(report)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = report.projectId,
                userRole = report.createdByRole,
                userName = report.createdByName,
                category = "LAPORAN",
                title = "Laporan Harian Disubmit (${report.reportNumber})",
                details = "Pekerja: ${report.totalWorkers} org | Uraian: ${report.workDescription.take(50)}..."
            )
        )
        // Auto trigger notification for manager
        dao.insertNotification(
            ProjectNotificationEntity(
                projectId = report.projectId,
                title = "Laporan Harian Baru",
                message = "${report.reportNumber} dibuat oleh ${report.createdByName} (${report.totalWorkers} tenaga kerja). Menunggu persetujuan.",
                type = "REPORT_DEADLINE",
                priority = "HIGH",
                targetRole = "Site Manager"
            )
        )
        id
    }

    suspend fun approveDailyReport(
        reportId: Long,
        projectId: Long,
        verifierName: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        dao.verifyDailyReport(
            id = reportId,
            status = "APPROVED",
            verifier = verifierName,
            time = System.currentTimeMillis(),
            notes = notes
        )
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = "Manajer Lapangan",
                userName = verifierName,
                category = "LAPORAN",
                title = "Persetujuan Laporan Harian",
                details = "Laporan #$reportId disetujui oleh $verifierName. Catatan: $notes"
            )
        )
    }

    // ACTIONS: S-CURVE & SCHEDULE
    suspend fun updateSCurveMilestone(
        milestone: SCurveMilestoneEntity,
        userName: String,
        role: String
    ) = withContext(Dispatchers.IO) {
        dao.updateSCurveMilestone(milestone)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = milestone.projectId,
                userRole = role,
                userName = userName,
                category = "PROGRESS",
                title = "Update Kurva S Minggu ${milestone.weekNumber}",
                details = "Realisasi: ${milestone.actualCumulativePercent}% (Rencana: ${milestone.plannedCumulativePercent}%)"
            )
        )
    }

    suspend fun updateScheduleProgress(
        itemId: Long,
        projectId: Long,
        actualProg: Float,
        status: String,
        taskName: String,
        userName: String,
        role: String
    ) = withContext(Dispatchers.IO) {
        dao.updateScheduleProgress(itemId, actualProg, status)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = role,
                userName = userName,
                category = "PROGRESS",
                title = "Update Timeschedule: $taskName",
                details = "Progres Aktual: $actualProg% | Status: $status"
            )
        )
    }

    // ACTIONS: NOTIFICATIONS
    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead(projectId: Long) = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead(projectId)
    }

    suspend fun clearAllNotifications(projectId: Long) = withContext(Dispatchers.IO) {
        dao.clearAllNotifications(projectId)
    }

    suspend fun sendNotification(notification: ProjectNotificationEntity) = withContext(Dispatchers.IO) {
        dao.insertNotification(notification)
    }

    // ACTIONS: DOCUMENTS
    suspend fun uploadDocument(doc: ProjectDocumentEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        val id = dao.insertDocument(doc)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = doc.projectId,
                userRole = role,
                userName = userName,
                category = "DOKUMEN",
                title = "Dokumen Baru Diunggah: ${doc.title}",
                details = "Folder: ${doc.folderCategory} | File: ${doc.fileName} (${doc.fileType}) | Hak Akses: ${doc.allowedRoles}"
            )
        )
        id
    }

    suspend fun deleteDocument(doc: ProjectDocumentEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        dao.deleteDocumentById(doc.id)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = doc.projectId,
                userRole = role,
                userName = userName,
                category = "DOKUMEN",
                title = "Dokumen Dihapus: ${doc.title}",
                details = "File: ${doc.fileName} dari folder ${doc.folderCategory}"
            )
        )
    }

    // ACTIONS: ATTENDANCE
    suspend fun recordAttendance(attendance: WorkerAttendanceEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        val id = dao.insertAttendance(attendance)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = attendance.projectId,
                userRole = role,
                userName = userName,
                category = "ABSENSI",
                title = "Absensi: ${attendance.workerName} (${attendance.status})",
                details = "Kategori: ${attendance.workerCategory} | Lembur: ${attendance.overtimeHours} jam | Tugas: ${attendance.workAssignedTo}"
            )
        )
        id
    }

    suspend fun recordBulkAttendance(attendances: List<WorkerAttendanceEntity>, userName: String, role: String) = withContext(Dispatchers.IO) {
        dao.insertAttendances(attendances)
        if (attendances.isNotEmpty()) {
            dao.insertActivityLog(
                ActivityLogEntity(
                    projectId = attendances.first().projectId,
                    userRole = role,
                    userName = userName,
                    category = "ABSENSI",
                    title = "Absensi Massal Dicatat (${attendances.size} Tenaga Kerja)",
                    details = "Tanggal: ${attendances.first().dateString} | Pencatat: $userName"
                )
            )
        }
    }

    // ACTIONS: RAP
    suspend fun updateRapItem(item: ProjectRapItemEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        dao.updateRapItem(item)
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = item.projectId,
                userRole = role,
                userName = userName,
                category = "SISTEM",
                title = "Update Realisasi RAP: ${item.itemName}",
                details = "Anggaran RAP: Rp ${item.budgetRapRp} | Realisasi: Rp ${item.actualCostRp}"
            )
        )
    }

    suspend fun addRapItem(item: ProjectRapItemEntity, userName: String, role: String) = withContext(Dispatchers.IO) {
        dao.insertRapItem(item)
    }

    suspend fun postTeamBroadcast(
        projectId: Long,
        role: String,
        name: String,
        title: String,
        message: String
    ) = withContext(Dispatchers.IO) {
        dao.insertActivityLog(
            ActivityLogEntity(
                projectId = projectId,
                userRole = role,
                userName = name,
                category = "SISTEM",
                title = title,
                details = message
            )
        )
        dao.insertNotification(
            ProjectNotificationEntity(
                projectId = projectId,
                title = "Siaran Tim: $title",
                message = "$message (Oleh: $name - $role)",
                type = "TEAM_BROADCAST",
                priority = "HIGH",
                targetRole = "ALL"
            )
        )
    }

    // INITIAL SEED DATA
    suspend fun ensureInitialSeedData() = withContext(Dispatchers.IO) {
        val existing = dao.getAllProjects().first()
        val hasAbiCode = existing.isNotEmpty() && existing.any { it.code.startsWith("ABI-") && it.name.contains("Purbalingga") }
        if (hasAbiCode) {
            // Already initialized with ABI projects
            return@withContext
        }

        // If legacy projects exist, remove them first to keep data clean
        dao.deleteAllProjects()

        val now = System.currentTimeMillis()
        val day = 86400000L

        // 1. Projects (PT Atmaterra Buildwork Indonesia)
        val p1 = ProjectEntity(
            code = "ABI-01",
            name = "Pembangunan Masjid Al-ikhlas GPA Purbalingga",
            clientName = "Pembangunan Masjid Al-ikhlas GPA Purbalingga",
            location = "Perumahan Griya Perwira Asri, Purbalingga",
            startDate = "01 Sep 2025",
            targetEndDate = "30 Sep 2026",
            contractValueRp = 3_850_000_000L,
            currentPlannedProgress = 76.8f,
            currentActualProgress = 78.4f,
            status = "BERJALAN"
        )
        val p2 = ProjectEntity(
            code = "ABI-02",
            name = "Rehab Rumah Kost GOR",
            clientName = "Rehab Rumah Kost GOR",
            location = "Kompleks GOR Satria No. 88, Purwokerto",
            startDate = "10 Jun 2026",
            targetEndDate = "20 Des 2026",
            contractValueRp = 1_150_000_000L,
            currentPlannedProgress = 30.0f,
            currentActualProgress = 32.0f,
            status = "BERJALAN"
        )
        val p3 = ProjectEntity(
            code = "ABI-03",
            name = "Rehab Rumah Kost My Kost DukuhWaluh",
            clientName = "Rehab Rumah Kost My Kost DukuhWaluh",
            location = "Jl. Raya Dukuhwaluh No. 12, Kembaran",
            startDate = "01 Jun 2026",
            targetEndDate = "30 Nov 2026",
            contractValueRp = 920_000_000L,
            currentPlannedProgress = 48.0f,
            currentActualProgress = 45.0f,
            status = "BERJALAN"
        )
        val p4 = ProjectEntity(
            code = "ABI-04",
            name = "Rehab Rumah Lo Villa",
            clientName = "Rehab Rumah Lo Villa",
            location = "Cluster Lo Villa Blok B-07, Kav. 14",
            startDate = "15 Mei 2026",
            targetEndDate = "15 Okt 2026",
            contractValueRp = 680_000_000L,
            currentPlannedProgress = 60.0f,
            currentActualProgress = 62.5f,
            status = "BERJALAN"
        )

        val p1Id = dao.insertProject(p1)
        val p2Id = dao.insertProject(p2)
        val p3Id = dao.insertProject(p3)
        val p4Id = dao.insertProject(p4)

        // 2. Materials
        val materials = listOf(
            MaterialItemEntity(projectId = p1Id, name = "Semen Gresik PPC 50kg", category = "Struktur", unit = "Sak", currentStock = 420.0, minStockThreshold = 200.0, unitPriceRp = 74_000L, storageLocation = "Gudang Utama A-01", supplierName = "PT Semen Indonesia Distributor"),
            MaterialItemEntity(projectId = p1Id, name = "Besi Beton Ulir D16 BJTS 420B", category = "Struktur", unit = "Batang", currentStock = 310.0, minStockThreshold = 150.0, unitPriceRp = 198_000L, storageLocation = "Yard Terbuka Blok B", supplierName = "PT Krakatau Steel Tbk"),
            MaterialItemEntity(projectId = p1Id, name = "Besi Beton Polos D10 BJTP 280", category = "Struktur", unit = "Batang", currentStock = 185.0, minStockThreshold = 100.0, unitPriceRp = 89_000L, storageLocation = "Yard Terbuka Blok B", supplierName = "PT Krakatau Steel Tbk"),
            MaterialItemEntity(projectId = p1Id, name = "Pasir Pasang Extra Cuci Bangka", category = "Struktur", unit = "M3", currentStock = 85.0, minStockThreshold = 40.0, unitPriceRp = 295_000L, storageLocation = "Bak Pasir Area Timur", supplierName = "CV Sumber Galian Jaya"),
            MaterialItemEntity(projectId = p1Id, name = "Batu Split Cor 2/3", category = "Struktur", unit = "M3", currentStock = 65.0, minStockThreshold = 35.0, unitPriceRp = 315_000L, storageLocation = "Bak Agregat Area Timur", supplierName = "CV Sumber Galian Jaya"),
            MaterialItemEntity(projectId = p1Id, name = "Ready Mix Beton K-350 NFA", category = "Struktur", unit = "M3", currentStock = 18.0, minStockThreshold = 50.0, unitPriceRp = 985_000L, storageLocation = "Batching Plant Vendor", supplierName = "PT Pionirbeton Industri"),
            MaterialItemEntity(projectId = p1Id, name = "Bata Ringan Hebel 10x20x60cm", category = "Arsitektur", unit = "M3", currentStock = 140.0, minStockThreshold = 60.0, unitPriceRp = 690_000L, storageLocation = "Lantai 2 Gudang Transisi", supplierName = "PT Broco Aerated Concrete"),
            MaterialItemEntity(projectId = p1Id, name = "Mortar Perekat Bata Thinbed 40kg", category = "Arsitektur", unit = "Sak", currentStock = 90.0, minStockThreshold = 50.0, unitPriceRp = 96_000L, storageLocation = "Gudang Utama A-02", supplierName = "PT Cipta Mortar Utama"),
            MaterialItemEntity(projectId = p1Id, name = "Pipa Conduit PVC 20mm Clipsal", category = "MEP", unit = "Batang", currentStock = 450.0, minStockThreshold = 150.0, unitPriceRp = 19_500L, storageLocation = "Gudang MEP Rak 3", supplierName = "PT Schneider Electric"),
            MaterialItemEntity(projectId = p1Id, name = "Kabel NYM 3x2.5mm Supreme", category = "MEP", unit = "Roll", currentStock = 14.0, minStockThreshold = 25.0, unitPriceRp = 1_180_000L, storageLocation = "Gudang MEP Rak 1", supplierName = "PT Supreme Cable"),
            MaterialItemEntity(projectId = p1Id, name = "Cat Dulux WeatherShield 20L", category = "Finishing", unit = "Pail", currentStock = 35.0, minStockThreshold = 15.0, unitPriceRp = 1_460_000L, storageLocation = "Gudang Kimia & Cat", supplierName = "PT ICI Paints Indonesia"),
            MaterialItemEntity(projectId = p1Id, name = "Bahan Bakar Solar Dexlite", category = "Alat Bantu", unit = "Liter", currentStock = 320.0, minStockThreshold = 500.0, unitPriceRp = 14_800L, storageLocation = "Tangki Solar Lapangan T-1", supplierName = "PT Pertamina Patra Niaga")
        )
        dao.insertMaterials(materials)

        // 3. Detailed Inventory Items (Tools, Equipment, Safety APD, Scaffolding)
        val inventoryItems = listOf(
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-TL-001",
                name = "Vibrator Beton Electric Mikasa FX-30B",
                category = "Peralatan & Hand Tools",
                status = "TERSEDIA",
                quantity = 4.0,
                unit = "Unit",
                minStockThreshold = 2.0,
                unitPriceRp = 4_850_000L,
                storageLocation = "Gudang Alat Lantai 1 (Rak B-01)",
                assignedTo = "Tersedia di Gudang",
                supplierOrBrand = "Mikasa Japan",
                notes = "Termasuk selang vibrator 4 meter, kondisi terkalibrasi"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-TL-002",
                name = "Mesin Bar Bender Rebar Bender B-32",
                category = "Peralatan & Hand Tools",
                status = "DIGUNAKAN",
                quantity = 2.0,
                unit = "Unit",
                minStockThreshold = 1.0,
                unitPriceRp = 28_000_000L,
                storageLocation = "Area Pabrikasi Besi Yard Timur",
                assignedTo = "Tim Pembesian (Mandor Supri)",
                supplierOrBrand = "Strong Heavy Ind.",
                notes = "Digunakan untuk tekuk sengkang & tulangan utama D16"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-TL-003",
                name = "Theodolite Digital Laser Sokkia DT-540",
                category = "Peralatan & Hand Tools",
                status = "TERSEDIA",
                quantity = 2.0,
                unit = "Set",
                minStockThreshold = 1.0,
                unitPriceRp = 35_000_000L,
                storageLocation = "Ruang Kantor Tim Surveyor",
                assignedTo = "Surveyor Utama (Irwan)",
                supplierOrBrand = "Sokkia Topcon",
                notes = "Sertifikat kalibrasi berlaku hingga Des 2026"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-SAF-001",
                name = "Full Body Harness Double Lanyard Absorber",
                category = "Safety & APD",
                status = "DIGUNAKAN",
                quantity = 45.0,
                unit = "Pcs",
                minStockThreshold = 15.0,
                unitPriceRp = 480_000L,
                storageLocation = "Locker K3 & Safety Basecamp",
                assignedTo = "Pekerja Struktur Lt. 4 & Fasad",
                supplierOrBrand = "Petzl Pro",
                notes = "Wajib dipakai saat pekerjaan ketinggian > 2 meter"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-SAF-002",
                name = "Helm Proyek Safety MSA V-Gard SNI",
                category = "Safety & APD",
                status = "TERSEDIA",
                quantity = 120.0,
                unit = "Pcs",
                minStockThreshold = 30.0,
                unitPriceRp = 125_000L,
                storageLocation = "Gudang APD Pintu Masuk",
                assignedTo = "Tersedia untuk Tamu & Pekerja",
                supplierOrBrand = "MSA Safety",
                notes = "Warna Putih (Staff), Kuning (Tukang), Merah (K3)"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-SCF-001",
                name = "Scaffolding Main Frame 190x120cm HDG",
                category = "Perancah & Formwork",
                status = "DIGUNAKAN",
                quantity = 850.0,
                unit = "Set",
                minStockThreshold = 200.0,
                unitPriceRp = 380_000L,
                storageLocation = "Terpasang di Lantai 3, 4, dan Fasad",
                assignedTo = "Regu Bekisting (Mandor Joko)",
                supplierOrBrand = "PT Unggul Jaya Scaffolding",
                notes = "Lengkap dengan cross brace, jack base, u-head"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-PMP-001",
                name = "Submersible Water Pump Alcon 3 Inch",
                category = "Peralatan & Hand Tools",
                status = "DALAM_PERBAIKAN",
                quantity = 3.0,
                unit = "Unit",
                minStockThreshold = 2.0,
                unitPriceRp = 5_200_000L,
                storageLocation = "Workshop Mekanik Proyek",
                assignedTo = "Mekanik Lapangan (Supriyadi)",
                supplierOrBrand = "Honda Robin Power",
                notes = "Penggantian impeller & seal karena air berlumpur dewatering"
            ),
            InventoryItemEntity(
                projectId = p1Id,
                itemCode = "INV-CHM-001",
                name = "Concrete Curing Compound Sika Antisol 20L",
                category = "Bahan Kimia",
                status = "TERSEDIA",
                quantity = 15.0,
                unit = "Pail",
                minStockThreshold = 10.0,
                unitPriceRp = 840_000L,
                storageLocation = "Gudang Bahan Kimia & Cat",
                assignedTo = "Tersedia di Gudang",
                supplierOrBrand = "PT Sika Indonesia",
                notes = "Untuk semprotan perawatan beton pasca cor"
            )
        )
        dao.insertInventoryItems(inventoryItems)

        // 4. Notifications
        val notifications = listOf(
            ProjectNotificationEntity(
                projectId = p1Id,
                title = "Peringatan Pemeliharaan Alat",
                message = "Genset Silent Denyo 100 kVA (GS-01) telah mencapai 4.120 Jam. Servis berkala & ganti filter oli wajib dilakukan sebelum 4.200 HM.",
                type = "MAINTENANCE",
                priority = "HIGH",
                timestampMillis = now - 2 * 3600000L
            ),
            ProjectNotificationEntity(
                projectId = p1Id,
                title = "Stok Material Menipis",
                message = "Kabel NYM 3x2.5mm tersisa 14 Roll (Batas minimum: 25 Roll) dan Solar Dexlite tersisa 320L. Segera terbitkan Purchase Order (PO).",
                type = "LOW_STOCK",
                priority = "HIGH",
                timestampMillis = now - 4 * 3600000L
            ),
            ProjectNotificationEntity(
                projectId = p1Id,
                title = "Tenggat Laporan Harian",
                message = "Laporan harian pekerjaan pengecoran Lantai 4 belum diverifikasi oleh Site Manager. Batas waktu penutupan shift: 19:00 WIB.",
                type = "REPORT_DEADLINE",
                priority = "MEDIUM",
                timestampMillis = now - 6 * 3600000L
            ),
            ProjectNotificationEntity(
                projectId = p1Id,
                title = "Status Proyek Kurva S",
                message = "Pencapaian progres minggu ke-7 mencapai 56.2% (Target: 54.5%). Deviasi positif +1.7% (Ahead of schedule).",
                type = "PROJECT_STATUS",
                priority = "MEDIUM",
                timestampMillis = now - 1 * day
            ),
            ProjectNotificationEntity(
                projectId = p1Id,
                title = "Instruksi K3 & Safety Lapangan",
                message = "Wajib pasang safety net perimeter di seluruh lantai 4 sebelum pekerjaan pengecoran kolom dan balok dimulai.",
                type = "TEAM_BROADCAST",
                priority = "MEDIUM",
                timestampMillis = now - 2 * day
            )
        )
        dao.insertNotifications(notifications)

        // 5. Project Documents
        val documents = listOf(
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "Gambar Kerja & Shop Drawing",
                title = "Shop Drawing Struktur Plat & Balok Lantai 4",
                fileName = "SD-STR-FL04-REV02.pdf",
                fileType = "PDF",
                fileSizeBytes = 14_850_000L,
                uploadedByRole = "Site Supervisor",
                uploadedByName = "Hendro Wibowo, S.T.",
                uploadDateMillis = now - 3 * day,
                version = "Rev 02 Approved",
                allowedRoles = "SEMUA",
                notes = "Sudah disetujui Konsultan MK dan Owner"
            ),
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "Gambar Kerja & Shop Drawing",
                title = "Shop Drawing Detail Pembesian Kolom K1 & K2",
                fileName = "DWG-KOLOM-DET-01.dwg",
                fileType = "DWG",
                fileSizeBytes = 28_400_000L,
                uploadedByRole = "Site Supervisor",
                uploadedByName = "Hendro Wibowo, S.T.",
                uploadDateMillis = now - 5 * day,
                version = "Rev 01",
                allowedRoles = "SEMUA",
                notes = "Spesifikasi besi ulir D16 BJTS 420B"
            ),
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "RAB, RAP & Kontrak",
                title = "Rencana Anggaran Pelaksanaan (RAP) Final RS Graha Medika",
                fileName = "RAP-FINAL-RSGM-2026.xlsx",
                fileType = "XLS",
                fileSizeBytes = 5_200_000L,
                uploadedByRole = "Project Manager",
                uploadedByName = "Drs. Hermawan Setiadi",
                uploadDateMillis = now - 14 * day,
                version = "v1.4 Final",
                allowedRoles = "MANAGEMENT_ONLY",
                notes = "Termasuk breakdown material, alat berat, dan upah subkon"
            ),
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "Surat Jalan & PO",
                title = "Surat Jalan & Invoice Semen Gresik 200 Sak",
                fileName = "SJ-SG-2026-08112.pdf",
                fileType = "PDF",
                fileSizeBytes = 1_850_000L,
                uploadedByRole = "Petugas Logistik",
                uploadedByName = "Rian Hidayat",
                uploadDateMillis = now - 2 * day,
                version = "v1.0",
                allowedRoles = "LOGISTIK",
                notes = "Telah diterima gudang utama dalam kondisi utuh"
            ),
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "Laporan & BA",
                title = "Berita Acara Uji Tekan Beton Silinder Umur 7 & 14 Hari",
                fileName = "BA-LAB-BETON-K350.pdf",
                fileType = "PDF",
                fileSizeBytes = 3_400_000L,
                uploadedByRole = "Manajer Lapangan",
                uploadedByName = "Ir. Wahyu Pratama",
                uploadDateMillis = now - 1 * day,
                version = "Resmi Lab",
                allowedRoles = "SEMUA",
                notes = "Hasil rata-rata 28.5 MPa (Melampaui target f'c 25 MPa)"
            ),
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "K3 & Izin Kerja (PTW)",
                title = "Permit To Work (PTW) Pekerjaan Pengecoran Malam Hari",
                fileName = "PTW-COR-MALAM-0814.pdf",
                fileType = "PDF",
                fileSizeBytes = 890_000L,
                uploadedByRole = "Supervisor Lapangan",
                uploadedByName = "Hendro Wibowo, S.T.",
                uploadDateMillis = now - 12 * 3600000L,
                version = "v1.0",
                allowedRoles = "SEMUA",
                notes = "Lighting menara & APD full body harness lengkap"
            ),
            ProjectDocumentEntity(
                projectId = p1Id,
                folderCategory = "Foto Dokumentasi Proyek",
                title = "Dokumentasi Progres Pengecoran Plat Lantai 4 Zona Barat",
                fileName = "DOK-COR-FL4-ZONE-B.jpg",
                fileType = "IMG",
                fileSizeBytes = 4_700_000L,
                uploadedByRole = "Site Supervisor",
                uploadedByName = "Hendro Wibowo, S.T.",
                uploadDateMillis = now - 6 * 3600000L,
                version = "Foto Lapangan",
                allowedRoles = "SEMUA",
                notes = "Pengecoran menggunakan concrete pump 36 m3"
            )
        )
        dao.insertDocuments(documents)

        // 6. Worker Attendances (Project Leader, Supervisors, Mandor, Tukang, Helpers)
        val todayStr = "14 Agu 2026"
        val attendances = listOf(
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Ir. Wahyu Pratama",
                workerCategory = "PROJECT_LEADER",
                status = "HADIR",
                overtimeHours = 2.0,
                dailyRateRp = 450_000L,
                totalEarnedRp = 562_500L,
                workAssignedTo = "Inspeksi Kualitas, Koordinasi Owner & MK",
                loggedByName = "Hendro Wibowo (SPV)",
                notes = "Hadir full day + inspeksi slump test cor malam"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Hendro Wibowo, S.T.",
                workerCategory = "SUPERVISOR",
                status = "HADIR",
                overtimeHours = 2.5,
                dailyRateRp = 280_000L,
                totalEarnedRp = 367_500L,
                workAssignedTo = "Pengawasan Pengecoran Plat Lantai 4",
                loggedByName = "Ir. Wahyu Pratama",
                notes = "Monitoring flow ready mix dan kubus uji beton"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Supriyanto (Mandor Besi)",
                workerCategory = "MANDOR",
                status = "HADIR",
                overtimeHours = 3.0,
                dailyRateRp = 220_000L,
                totalEarnedRp = 302_500L,
                workAssignedTo = "Fabrikasi & Pemasangan Tulangan Kolom Lt. 4-5",
                loggedByName = "Hendro Wibowo",
                notes = "Memimpin regu 12 orang tukang pembesian"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Joko Susilo (Mandor Bekisting)",
                workerCategory = "MANDOR",
                status = "HADIR",
                overtimeHours = 2.0,
                dailyRateRp = 220_000L,
                totalEarnedRp = 275_000L,
                workAssignedTo = "Pemasangan Scaffolding & Formwork Balok Lt. 4",
                loggedByName = "Hendro Wibowo",
                notes = "Regu 10 tukang kayu bekisting"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Ahmad Rifai",
                workerCategory = "TUKANG_BESI",
                status = "HADIR",
                overtimeHours = 3.0,
                dailyRateRp = 160_000L,
                totalEarnedRp = 220_000L,
                workAssignedTo = "Perakitan Sengkang & Begel Kolom K1",
                loggedByName = "Hendro Wibowo",
                notes = "Besi D16 dan begel D10"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Budi Hartono",
                workerCategory = "TUKANG_BATU",
                status = "HADIR",
                overtimeHours = 1.0,
                dailyRateRp = 150_000L,
                totalEarnedRp = 168_750L,
                workAssignedTo = "Pasangan Bata Ringan Hebel Koridor Lt. 2",
                loggedByName = "Hendro Wibowo",
                notes = "Target 14 m2 tercapai rapi"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Suryadi",
                workerCategory = "TUKANG_KAYU",
                status = "HADIR",
                overtimeHours = 2.0,
                dailyRateRp = 150_000L,
                totalEarnedRp = 187_500L,
                workAssignedTo = "Penyetelan Multiplex 12mm Plat Lantai 4",
                loggedByName = "Hendro Wibowo",
                notes = "Presisi elevasi level laser checked"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Deden Ramli",
                workerCategory = "TUKANG_LISTRIK",
                status = "HADIR",
                overtimeHours = 0.0,
                dailyRateRp = 165_000L,
                totalEarnedRp = 165_000L,
                workAssignedTo = "Penanaman Pipa Conduit In-Slab Lantai 4",
                loggedByName = "Hendro Wibowo",
                notes = "Jalur kabel stop kontak & saklar aman sebelum cor"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Yanto (Helper Cor)",
                workerCategory = "PEKERJA_HELPER",
                status = "HADIR",
                overtimeHours = 3.0,
                dailyRateRp = 120_000L,
                totalEarnedRp = 165_000L,
                workAssignedTo = "Pegang Selang Concrete Pump & Vibrator",
                loggedByName = "Hendro Wibowo",
                notes = "Pengecoran plat & balok"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Iwan Setiawan",
                workerCategory = "PEKERJA_HELPER",
                status = "HADIR",
                overtimeHours = 0.0,
                dailyRateRp = 120_000L,
                totalEarnedRp = 120_000L,
                workAssignedTo = "Langsir Material Semen & Bata Ringan",
                loggedByName = "Hendro Wibowo",
                notes = "Pengangkutan via Hoist"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Mansur Subcon MEP",
                workerCategory = "SUBCON",
                status = "HADIR",
                overtimeHours = 1.0,
                dailyRateRp = 175_000L,
                totalEarnedRp = 196_875L,
                workAssignedTo = "Instalasi Shaft Pipa Air Bersih & Kotor",
                loggedByName = "Hendro Wibowo",
                notes = "Subkontraktor PT Sinar MEP Sentosa"
            ),
            WorkerAttendanceEntity(
                projectId = p1Id,
                dateMillis = now,
                dateString = todayStr,
                workerName = "Kurniawan (Tukang Besi)",
                workerCategory = "TUKANG_BESI",
                status = "IZIN_SAKIT",
                overtimeHours = 0.0,
                dailyRateRp = 160_000L,
                totalEarnedRp = 0L,
                workAssignedTo = "Izin Sakit (Demam)",
                loggedByName = "Hendro Wibowo",
                notes = "Surat dokter diserahkan ke SPV"
            )
        )
        dao.insertAttendances(attendances)

        // 7. Project Schedules / Timeschedule Gantt Breakdown
        val schedules = listOf(
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "1.0",
                taskName = "Pekerjaan Persiapan, Direksi Keet & Pembersihan Lahan",
                category = "Pekerjaan Persiapan",
                startDate = "05 Jan 2026",
                endDate = "20 Jan 2026",
                durationDays = 15,
                weightPercent = 3.5f,
                plannedProgressPercent = 100.0f,
                actualProgressPercent = 100.0f,
                status = "SELESAI",
                assignedLeader = "Ir. Wahyu Pratama"
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "2.0",
                taskName = "Struktur Bawah: Pondasi Bore Pile & Pile Cap D80",
                category = "Struktur Bawah",
                startDate = "15 Jan 2026",
                endDate = "15 Feb 2026",
                durationDays = 31,
                weightPercent = 18.0f,
                plannedProgressPercent = 100.0f,
                actualProgressPercent = 100.0f,
                status = "SELESAI",
                assignedLeader = "Hendro Wibowo, S.T."
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "3.1",
                taskName = "Struktur Atas: Kolom, Balok & Plat Lantai 1 s/d 3",
                category = "Struktur Atas",
                startDate = "01 Feb 2026",
                endDate = "10 Mar 2026",
                durationDays = 38,
                weightPercent = 22.0f,
                plannedProgressPercent = 100.0f,
                actualProgressPercent = 100.0f,
                status = "SELESAI",
                assignedLeader = "Hendro Wibowo, S.T."
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "3.2",
                taskName = "Struktur Atas: Kolom, Balok & Plat Lantai 4 s/d 6",
                category = "Struktur Atas",
                startDate = "05 Mar 2026",
                endDate = "25 Apr 2026",
                durationDays = 51,
                weightPercent = 20.0f,
                plannedProgressPercent = 65.0f,
                actualProgressPercent = 70.0f,
                status = "SEDANG_BERJALAN",
                assignedLeader = "Hendro Wibowo, S.T."
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "3.3",
                taskName = "Struktur Atas: Kolom, Balok & Plat Lantai 7 s/d Atap Roof",
                category = "Struktur Atas",
                startDate = "20 Apr 2026",
                endDate = "30 Mei 2026",
                durationDays = 40,
                weightPercent = 12.0f,
                plannedProgressPercent = 0.0f,
                actualProgressPercent = 0.0f,
                status = "BELUM_MULAI",
                assignedLeader = "Hendro Wibowo, S.T."
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "4.0",
                taskName = "Arsitektur: Pasangan Dinding Bata Ringan & Plester Aci",
                category = "Arsitektur & Pasangan",
                startDate = "20 Feb 2026",
                endDate = "15 Jun 2026",
                durationDays = 115,
                weightPercent = 10.5f,
                plannedProgressPercent = 35.0f,
                actualProgressPercent = 38.0f,
                status = "SEDANG_BERJALAN",
                assignedLeader = "Mandor Joko Susilo"
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "5.0",
                taskName = "MEP: Instalasi Conduit Elektrikal, Plumbing & Tata Udara",
                category = "MEP & Utilitas",
                startDate = "01 Mar 2026",
                endDate = "15 Jul 2026",
                durationDays = 136,
                weightPercent = 9.0f,
                plannedProgressPercent = 25.0f,
                actualProgressPercent = 28.0f,
                status = "SEDANG_BERJALAN",
                assignedLeader = "Tim Subcon MEP"
            ),
            ProjectScheduleItemEntity(
                projectId = p1Id,
                wbsCode = "6.0",
                taskName = "Finishing: Keramik Homogeneous Tile, Plafon Gypsum & Pengecatan",
                category = "Finishing",
                startDate = "01 Mei 2026",
                endDate = "15 Agu 2026",
                durationDays = 106,
                weightPercent = 5.0f,
                plannedProgressPercent = 0.0f,
                actualProgressPercent = 0.0f,
                status = "BELUM_MULAI",
                assignedLeader = "Mandor Finishing"
            )
        )
        dao.insertScheduleItems(schedules)

        // 8. Project RAP (Rencana Anggaran Pelaksanaan)
        val rapItems = listOf(
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "MATERIAL",
                itemCode = "RAP-MAT-01",
                itemName = "Besi Beton Ulir & Polos (Semua Ukuran)",
                volume = 280.0,
                unit = "Ton",
                unitPriceRp = 14_500_000L,
                budgetRapRp = 4_060_000_000L,
                actualCostRp = 3_890_000_000L,
                notes = "Pengadaan dari Pabrik Krakatau Steel (Hemat 4.1%)"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "MATERIAL",
                itemCode = "RAP-MAT-02",
                itemName = "Beton Ready Mix K-350 NFA Cor Plat & Balok",
                volume = 3200.0,
                unit = "M3",
                unitPriceRp = 950_000L,
                budgetRapRp = 3_040_000_000L,
                actualCostRp = 2_850_000_000L,
                notes = "Volume realisasi hingga Lt. 4 sesuai jadwal"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "MATERIAL",
                itemCode = "RAP-MAT-03",
                itemName = "Bata Ringan Hebel + Perekat Thinbed",
                volume = 1400.0,
                unit = "M3",
                unitPriceRp = 750_000L,
                budgetRapRp = 1_050_000_000L,
                actualCostRp = 620_000_000L,
                notes = "Realisasi lantai 1-3 berjalan lancar"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "ALAT_BERAT",
                itemCode = "RAP-EQP-01",
                itemName = "Sewa Tower Crane 50m (6 Bulan Operasi)",
                volume = 6.0,
                unit = "Bulan",
                unitPriceRp = 85_000_000L,
                budgetRapRp = 510_000_000L,
                actualCostRp = 425_000_000L,
                notes = "Termasuk operator & maintenance rutin"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "ALAT_BERAT",
                itemCode = "RAP-EQP-02",
                itemName = "Sewa Scaffolding Heavy Duty (800 Set)",
                volume = 7.0,
                unit = "Bulan",
                unitPriceRp = 35_000_000L,
                budgetRapRp = 245_000_000L,
                actualCostRp = 175_000_000L,
                notes = "Tersewa dari PT Unggul Jaya"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "UPAH_TENAGA",
                itemCode = "RAP-LAB-01",
                itemName = "Upah Borongan Tenaga Struktur (Besi, Bekisting, Cor)",
                volume = 1.0,
                unit = "Ls",
                unitPriceRp = 3_800_000_000L,
                budgetRapRp = 3_800_000_000L,
                actualCostRp = 2_150_000_000L,
                notes = "Termin progres fisik (Dibayar setiap 2 minggu)"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "SUBKONTRAKTOR",
                itemCode = "RAP-SUB-01",
                itemName = "Pekerjaan Instalasi MEP & Fire Fighting",
                volume = 1.0,
                unit = "Ls",
                unitPriceRp = 4_200_000_000L,
                budgetRapRp = 4_200_000_000L,
                actualCostRp = 1_260_000_000L,
                notes = "Subkon PT Sinar MEP (Uang muka & termin 1 selesai)"
            ),
            ProjectRapItemEntity(
                projectId = p1Id,
                category = "OVERHEAD_K3",
                itemCode = "RAP-K3-01",
                itemName = "Biaya K3, APD, Asuransi Tenaga Kerja & Direksi Keet",
                volume = 8.0,
                unit = "Bulan",
                unitPriceRp = 45_000_000L,
                budgetRapRp = 360_000_000L,
                actualCostRp = 240_000_000L,
                notes = "Zero accident campaign berjalan optimal"
            )
        )
        dao.insertRapItems(rapItems)

        // 9. Equipments
        val equipments = listOf(
            EquipmentEntity(projectId = p1Id, code = "EXC-01", name = "Excavator Komatsu PC200-8", category = "Alat Berat", status = "OPERASIONAL", currentHourMeter = 1420.5, fuelCapacityLiters = 400.0, currentFuelLiters = 260.0, operatorName = "Agus Santoso", lastServiceDate = "01 Agu 2026", nextServiceHourMeter = 1500.0, notes = "Kondisi hidrolik prima"),
            EquipmentEntity(projectId = p1Id, code = "TC-01", name = "Tower Crane Zoomlion TC5013 (50m)", category = "Alat Berat", status = "OPERASIONAL", currentHourMeter = 2110.0, fuelCapacityLiters = 0.0, currentFuelLiters = 0.0, operatorName = "Bambang Haryanto", lastServiceDate = "10 Agu 2026", nextServiceHourMeter = 2500.0, notes = "Inspeksi sling mingguan aman"),
            EquipmentEntity(projectId = p1Id, code = "DT-02", name = "Dump Truck Hino FM260JD (10 Roda)", category = "Alat Angkut", status = "OPERASIONAL", currentHourMeter = 3450.0, fuelCapacityLiters = 200.0, currentFuelLiters = 140.0, operatorName = "Slamet Riyadi", lastServiceDate = "25 Jul 2026", nextServiceHourMeter = 3600.0, notes = "Transportasi buangan puing & tanah"),
            EquipmentEntity(projectId = p1Id, code = "DT-03", name = "Dump Truck Mitsubishi Fuso Fighter", category = "Alat Angkut", status = "STANDBY", currentHourMeter = 2890.0, fuelCapacityLiters = 160.0, currentFuelLiters = 110.0, operatorName = "Dedi Kurnia", lastServiceDate = "28 Jul 2026", nextServiceHourMeter = 3000.0, notes = "Siap untuk mobilisasi material"),
            EquipmentEntity(projectId = p1Id, code = "BB-01", name = "Bar Bender & Bar Cutter Strong 42", category = "Mesin Kerja", status = "OPERASIONAL", currentHourMeter = 640.0, fuelCapacityLiters = 0.0, currentFuelLiters = 0.0, operatorName = "Tim Pembesian (Supri)", lastServiceDate = "05 Agu 2026", nextServiceHourMeter = 800.0, notes = "Pabrikasi tulangan balok & kolom"),
            EquipmentEntity(projectId = p1Id, code = "SC-01", name = "Scaffolding Main Frame HDG (800 Set)", category = "Scaffolding/Bekisting", status = "OPERASIONAL", currentHourMeter = 0.0, fuelCapacityLiters = 0.0, currentFuelLiters = 0.0, operatorName = "Mandor Bekisting (Joko)", lastServiceDate = "02 Agu 2026", nextServiceHourMeter = 0.0, notes = "Terpasang di lantai 3 & 4"),
            EquipmentEntity(projectId = p1Id, code = "GS-01", name = "Genset Silent Denyo 100 kVA", category = "Mesin Kerja", status = "MAINTENANCE", currentHourMeter = 4120.0, fuelCapacityLiters = 250.0, currentFuelLiters = 85.0, operatorName = "Mekanik Supriyadi", lastServiceDate = "12 Agu 2026", nextServiceHourMeter = 4200.0, notes = "Penggantian filter oli & solar berkala"),
            EquipmentEntity(projectId = p1Id, code = "TM-04", name = "Concrete Mixer Molen Portable 500L", category = "Mesin Kerja", status = "RUSAK", currentHourMeter = 890.0, fuelCapacityLiters = 15.0, currentFuelLiters = 4.0, operatorName = "Standby Workshop", lastServiceDate = "15 Jun 2026", nextServiceHourMeter = 900.0, notes = "V-belt putus, suku cadang sedang dipesan")
        )
        dao.insertEquipments(equipments)

        // 10. S-Curve Milestones
        val sCurve = listOf(
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 1, weekLabel = "M-01 (05-11 Jan)", plannedWeeklyPercent = 4.2f, plannedCumulativePercent = 4.2f, actualWeeklyPercent = 4.5f, actualCumulativePercent = 4.5f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 2, weekLabel = "M-02 (12-18 Jan)", plannedWeeklyPercent = 5.3f, plannedCumulativePercent = 9.5f, actualWeeklyPercent = 5.7f, actualCumulativePercent = 10.2f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 3, weekLabel = "M-03 (19-25 Jan)", plannedWeeklyPercent = 7.3f, plannedCumulativePercent = 16.8f, actualWeeklyPercent = 7.8f, actualCumulativePercent = 18.0f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 4, weekLabel = "M-04 (26 Jan-01 Feb)", plannedWeeklyPercent = 8.6f, plannedCumulativePercent = 25.4f, actualWeeklyPercent = 8.5f, actualCumulativePercent = 26.5f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 5, weekLabel = "M-05 (02-08 Feb)", plannedWeeklyPercent = 9.6f, plannedCumulativePercent = 35.0f, actualWeeklyPercent = 10.3f, actualCumulativePercent = 36.8f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 6, weekLabel = "M-06 (09-15 Feb)", plannedWeeklyPercent = 10.2f, plannedCumulativePercent = 45.2f, actualWeeklyPercent = 10.2f, actualCumulativePercent = 47.0f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 7, weekLabel = "M-07 (16-22 Feb)", plannedWeeklyPercent = 9.3f, plannedCumulativePercent = 54.5f, actualWeeklyPercent = 9.2f, actualCumulativePercent = 56.2f, isCompleted = true),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 8, weekLabel = "M-08 (23 Feb-01 Mar)", plannedWeeklyPercent = 9.5f, plannedCumulativePercent = 64.0f, actualWeeklyPercent = 0.0f, actualCumulativePercent = 0.0f, isCompleted = false),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 9, weekLabel = "M-09 (02-08 Mar)", plannedWeeklyPercent = 9.5f, plannedCumulativePercent = 73.5f, actualWeeklyPercent = 0.0f, actualCumulativePercent = 0.0f, isCompleted = false),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 10, weekLabel = "M-10 (09-15 Mar)", plannedWeeklyPercent = 9.5f, plannedCumulativePercent = 83.0f, actualWeeklyPercent = 0.0f, actualCumulativePercent = 0.0f, isCompleted = false),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 11, weekLabel = "M-11 (16-22 Mar)", plannedWeeklyPercent = 9.5f, plannedCumulativePercent = 92.5f, actualWeeklyPercent = 0.0f, actualCumulativePercent = 0.0f, isCompleted = false),
            SCurveMilestoneEntity(projectId = p1Id, weekNumber = 12, weekLabel = "M-12 (23-29 Mar)", plannedWeeklyPercent = 7.5f, plannedCumulativePercent = 100.0f, actualWeeklyPercent = 0.0f, actualCumulativePercent = 0.0f, isCompleted = false)
        )
        dao.insertSCurveMilestones(sCurve)

        // 11. Daily Reports
        val dailyReports = listOf(
            DailyReportEntity(
                projectId = p1Id,
                reportNumber = "LHP/GM/2026/08-14",
                reportDateMillis = now,
                weatherMorning = "CERAH",
                weatherAfternoon = "BERAWAN",
                weatherEvening = "HUJAN_RINGAN",
                mandorCount = 4,
                tukangCount = 28,
                pekerjaCount = 35,
                subconCount = 12,
                totalWorkers = 79,
                workDescription = "1. Pengecoran Plat & Balok Lantai 4 Zona Barat (Volume 36 m3).\n2. Pemasangan pembesian kolom Lantai 4 ke 5 (Besi D16).\n3. Fabrikasi bekisting balok sisi timur.\n4. Pasangan dinding bata ringan hebel koridor lantai 2.",
                materialsUsedSummary = "Ready Mix K-350 (36 m3), Besi D16 (45 btg), Kawat Bendrat (12 kg), Hebel 10cm (18 m3), Thinbed (14 sak)",
                heavyEquipmentUsedSummary = "Tower Crane TC-01 (6 jam), Concrete Pump (4 jam), Bar Bender BB-01 (7 jam)",
                obstaclesIssues = "Hujan ringan mulai pukul 16:20 WIB, pekerjaan finishing luar dihentikan 45 menit untuk keselamatan kerja.",
                solutionsActionTaken = "Pekerja dialihkan ke pekerjaan plesteran dan instalasi pipa conduit di area dalam (indoor).",
                progressAddedPercent = 0.85f,
                status = "APPROVED",
                createdByRole = "Supervisor Lapangan",
                createdByName = "Hendro Wibowo, S.T.",
                verifiedByName = "Ir. Wahyu Pratama (Site Manager)",
                verifiedDateMillis = now,
                siteManagerNotes = "Pengecoran berjalan baik, mutu slump terjaga. Perhatikan curing beton besok pagi."
            )
        )
        dailyReports.forEach { dao.insertDailyReport(it) }

        // 12. Weekly Reports
        val weeklyReports = listOf(
            WeeklyReportEntity(
                projectId = p1Id,
                weekNumber = 7,
                weekTitle = "Laporan Mingguan Ke-7 (16 - 22 Feb 2026)",
                startDate = "16 Feb 2026",
                endDate = "22 Feb 2026",
                plannedProgressCumulative = 54.5f,
                actualProgressCumulative = 56.2f,
                previousProgressCumulative = 47.0f,
                weeklyProgressGain = 9.2f,
                deviationPercent = 1.7f,
                workCompletedSummary = "Penyelesaian struktur plat & balok Lantai 4 (Zona Barat & Timur), pembesian kolom Lt. 5, instalasi conduit MEP Lantai 2 & 3, pasangan hebel dinding koridor Lt. 2.",
                targetNextWeek = "Pengecoran kolom Lt. 4 ke 5, perancah & bekisting plat Lantai 5, plester aci dinding koridor Lt. 2.",
                riskEvaluation = "Perkiraan cuaca minggu depan berpotensi hujan petir sore hari. Penjadwalan pengecoran dimajukan mulai pagi pukul 07:30 WIB.",
                approvedBySiteManager = true,
                status = "APPROVED"
            )
        )
        dao.insertWeeklyReports(weeklyReports)
    }
}
