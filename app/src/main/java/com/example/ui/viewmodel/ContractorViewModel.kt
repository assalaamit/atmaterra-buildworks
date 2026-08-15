package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ContractorRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val subtitle: String, val code: String = "") {
    DASHBOARD("Dashboard", "Overview Proyek", "00"),
    M01_RINGKASAN("Ringkasan", "KPI utama & QC sumber", "01"),
    M02_DAFTAR_ISI("Daftar Isi", "Struktur workbook", "02"),
    M03_SURAT_LAPORAN("Surat Laporan", "Surat progres mingguan", "03"),
    M04_REKAP_PROGRES("Rekap Progres", "Rekap bobot pekerjaan", "04"),
    M05_DETAIL_PROGRES("Detail Progres", "384 item pekerjaan", "05"),
    M06_TIME_SCHEDULE("Time Schedule", "Revisi schedule / kurva S", "06"),
    M07_PERMASALAHAN_SOLUSI("Permasalahan & Solusi", "Hambatan, perubahan, addendum", "07"),
    M08_CUACA_HARIAN("Cuaca Harian", "Cuaca 03–08 Agustus", "08"),
    M09_CUACA_MINGGUAN("Cuaca Mingguan", "Rekap cuaca mingguan", "09"),
    M10_PERSONIL("Personil", "Personil & subkon/vendor", "10"),
    M11_PERALATAN("Peralatan", "Inventaris peralatan", "11"),
    M12_ABSENSI("Absensi", "Absensi man power", "12"),
    M13_AKTIVITAS_HARIAN("Aktivitas Harian", "Ringkasan aktivitas harian", "13"),
    M14_DOKUMENTASI("Dokumentasi", "Foto pekerjaan 03–08 Agustus", "14"),
    INVENTORY("Inventaris", "Pelacakan & Logistik", "LOG"),
    EQUIPMENT("Peralatan", "Alat Berat & Mesin", "EQP"),
    ATTENDANCE("Absensi", "Tukang & Tenaga Kerja", "ABS"),
    SCHEDULE_RAP("Jadwal & RAP", "Timeschedule & Anggaran", "RAP"),
    REPORTS("Laporan", "Harian & Mingguan", "REP"),
    DOCUMENTS("Dokumen", "Manajemen File & Akses", "DOC"),
    SCURVE("Kurva S", "Deviasi Progres Fisik", "CRV"),
    NOTIFICATIONS("Notifikasi", "Peringatan & Info", "NOT"),
    TEAM("Akses Tim", "Multi-Pengguna", "USR")
}

class ContractorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContractorRepository = ContractorRepository(AppDatabase.getInstance(application).contractorDao())

    private val _usersList = MutableStateFlow(
        listOf(
            UserProfile(
                id = "u_ceo",
                username = "aan.amri",
                password = "abc123",
                name = "Aan Amri Setiawan",
                role = UserRole.CEO,
                phone = "0812-1111-2222",
                avatarInitials = "AA",
                assignedProjectCode = "ALL"
            ),
            UserProfile(
                id = "u_fin",
                username = "isabella.riyanti",
                password = "abc123",
                name = "Isabella Riyanti",
                role = UserRole.FINANCE,
                phone = "0813-2222-3333",
                avatarInitials = "IR",
                assignedProjectCode = "ALL"
            ),
            UserProfile(
                id = "u_pm",
                username = "teguh.pambudi",
                password = "abc123",
                name = "Teguh Pambudi",
                role = UserRole.PROJECT_MANAGER,
                phone = "0811-3333-4444",
                avatarInitials = "TP",
                assignedProjectCode = "ALL"
            ),
            UserProfile(
                id = "u_pl_gpa",
                username = "sugiarto",
                password = "abc123",
                name = "Sugiarto",
                role = UserRole.PROJECT_LEADER_GPA,
                phone = "0812-4444-5555",
                avatarInitials = "SG",
                assignedProjectCode = "ABI-01"
            ),
            UserProfile(
                id = "u_pl_gor",
                username = "awal.gor",
                password = "abc123",
                name = "Awal",
                role = UserRole.PROJECT_LEADER_GOR,
                phone = "0813-5555-6666",
                avatarInitials = "AW",
                assignedProjectCode = "ABI-02"
            ),
            UserProfile(
                id = "u_pl_mykost",
                username = "awal.mykost",
                password = "abc123",
                name = "Awal",
                role = UserRole.PROJECT_LEADER_MYKOST,
                phone = "0813-5555-6666",
                avatarInitials = "AW",
                assignedProjectCode = "ABI-03"
            ),
            UserProfile(
                id = "u_pl_lv",
                username = "toni",
                password = "abc123",
                name = "Toni",
                role = UserRole.PROJECT_LEADER_LO_VILLA,
                phone = "0857-7777-8888",
                avatarInitials = "TN",
                assignedProjectCode = "ABI-04"
            ),
            UserProfile(
                id = "u_log",
                username = "rusmanto",
                password = "abc123",
                name = "Rusmanto",
                role = UserRole.LOGISTIC,
                phone = "0821-8888-9999",
                avatarInitials = "RM",
                assignedProjectCode = "ALL"
            ),
            UserProfile(
                id = "u_adm",
                username = "ibnu.abas",
                password = "abc123",
                name = "Ibnu Abas",
                role = UserRole.ADMINISTRATOR,
                phone = "0819-9999-0000",
                avatarInitials = "IA",
                assignedProjectCode = "ALL"
            )
        )
    )
    val usersList: StateFlow<List<UserProfile>> = _usersList.asStateFlow()
    val predefinedUsers: List<UserProfile> get() = _usersList.value

    private val _currentUser = MutableStateFlow(_usersList.value[0]) // Default: Aan Amri Setiawan (CEO)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _selectedProjectId = MutableStateFlow(1L)
    val selectedProjectId: StateFlow<Long> = _selectedProjectId.asStateFlow()

    // Filters & Search States
    val materialCategoryFilter = MutableStateFlow("SEMUA")
    val materialSearchQuery = MutableStateFlow("")

    val inventoryCategoryFilter = MutableStateFlow("SEMUA")
    val inventoryStatusFilter = MutableStateFlow("SEMUA")
    val inventorySearchQuery = MutableStateFlow("")

    val equipmentStatusFilter = MutableStateFlow("SEMUA")
    val equipmentSearchQuery = MutableStateFlow("")

    val reportTabSelection = MutableStateFlow(0) // 0: Harian, 1: Mingguan
    val documentFolderFilter = MutableStateFlow("SEMUA")
    val documentSearchQuery = MutableStateFlow("")

    val attendanceFilterCategory = MutableStateFlow("SEMUA")
    val attendanceSearchQuery = MutableStateFlow("")

    val scheduleCategoryFilter = MutableStateFlow("SEMUA")

    // Notification Preferences
    private val _notificationPreferences = MutableStateFlow(NotificationPreferences())
    val notificationPreferences: StateFlow<NotificationPreferences> = _notificationPreferences.asStateFlow()

    // User Feedback / Snackbars
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentProject: StateFlow<ProjectEntity?> = _selectedProjectId
        .flatMapLatest { pid -> repository.getProject(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val materials: StateFlow<List<MaterialItemEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getMaterials(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val lowStockMaterials: StateFlow<List<MaterialItemEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getLowStockMaterials(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val materialTransactions: StateFlow<List<MaterialTransactionEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getMaterialTransactions(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val inventoryItems: StateFlow<List<InventoryItemEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getInventory(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val equipments: StateFlow<List<EquipmentEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getEquipments(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val equipmentLogs: StateFlow<List<EquipmentLogEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getEquipmentLogs(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyReports: StateFlow<List<DailyReportEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getDailyReports(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklyReports: StateFlow<List<WeeklyReportEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getWeeklyReports(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val sCurveMilestones: StateFlow<List<SCurveMilestoneEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getSCurveMilestones(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activityLogs: StateFlow<List<ActivityLogEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getActivityLogs(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val rawNotifications: StateFlow<List<ProjectNotificationEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getNotifications(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter notifications based on user preferences
    val filteredNotifications: StateFlow<List<ProjectNotificationEntity>> = combine(
        rawNotifications,
        _notificationPreferences
    ) { notifs, prefs ->
        notifs.filter { n ->
            when (n.type) {
                "MAINTENANCE" -> prefs.maintenanceAlerts
                "REPORT_DEADLINE" -> prefs.reportDeadlineAlerts
                "LOW_STOCK" -> prefs.lowStockAlerts
                "PROJECT_STATUS" -> prefs.projectStatusAlerts
                "TEAM_BROADCAST" -> prefs.teamBroadcastAlerts
                else -> true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = filteredNotifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val documents: StateFlow<List<ProjectDocumentEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getDocuments(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val attendances: StateFlow<List<WorkerAttendanceEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getAttendances(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val schedules: StateFlow<List<ProjectScheduleItemEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getSchedules(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val rapItems: StateFlow<List<ProjectRapItemEntity>> = _selectedProjectId
        .flatMapLatest { pid -> repository.getRapItems(pid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureInitialSeedData()
        }
        viewModelScope.launch {
            allProjects.collect { list ->
                if (list.isNotEmpty() && list.none { it.id == _selectedProjectId.value }) {
                    _selectedProjectId.value = list.first().id
                }
            }
        }
    }

    fun selectProject(id: Long) {
        _selectedProjectId.value = id
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun switchUser(user: UserProfile) {
        _currentUser.value = user
        _userMessage.value = "Beralih akun ke: ${user.name} (${user.role.title})"
    }

    fun updateUserPassword(userId: String, oldPass: String, newPass: String): Boolean {
        val currentList = _usersList.value
        val user = currentList.find { it.id == userId }
        if (user == null) {
            _userMessage.value = "Pengguna tidak ditemukan."
            return false
        }
        if (user.password != oldPass) {
            _userMessage.value = "Password lama tidak sesuai!"
            return false
        }
        if (newPass.length < 4) {
            _userMessage.value = "Password baru minimal 4 karakter!"
            return false
        }
        val updatedList = currentList.map {
            if (it.id == userId) it.copy(password = newPass) else it
        }
        _usersList.value = updatedList
        if (_currentUser.value.id == userId) {
            _currentUser.value = _currentUser.value.copy(password = newPass)
        }
        _userMessage.value = "Password untuk ${user.name} berhasil diubah!"
        return true
    }

    fun loginWithCredentials(username: String, pass: String): Boolean {
        val user = _usersList.value.find { it.username.equals(username.trim(), ignoreCase = true) }
        if (user == null) {
            _userMessage.value = "Username '$username' tidak ditemukan."
            return false
        }
        if (user.password != pass) {
            _userMessage.value = "Password salah!"
            return false
        }
        _currentUser.value = user
        _userMessage.value = "Selamat datang, ${user.name} (${user.role.title})"
        return true
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun updateNotificationPreferences(
        maintenance: Boolean,
        deadlines: Boolean,
        lowStock: Boolean,
        statusChange: Boolean,
        broadcast: Boolean
    ) {
        _notificationPreferences.value = NotificationPreferences(
            maintenanceAlerts = maintenance,
            reportDeadlineAlerts = deadlines,
            lowStockAlerts = lowStock,
            projectStatusAlerts = statusChange,
            teamBroadcastAlerts = broadcast
        )
        _userMessage.value = "Preferensi notifikasi berhasil diperbarui!"
    }

    // ACTIONS: INVENTORY
    fun addInventoryItem(
        code: String,
        name: String,
        category: String,
        status: String,
        quantity: Double,
        unit: String,
        minStock: Double,
        priceRp: Long,
        storageLocation: String,
        assignedTo: String,
        supplierOrBrand: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val item = InventoryItemEntity(
                projectId = _selectedProjectId.value,
                itemCode = code.ifBlank { "INV-${System.currentTimeMillis() % 10000}" },
                name = name,
                category = category,
                status = status,
                quantity = quantity,
                unit = unit,
                minStockThreshold = minStock,
                unitPriceRp = priceRp,
                storageLocation = storageLocation.ifBlank { "Gudang Utama" },
                assignedTo = assignedTo.ifBlank { "Tersedia di Gudang" },
                supplierOrBrand = supplierOrBrand.ifBlank { "Distributor Resmi" },
                notes = notes
            )
            repository.addNewInventoryItem(item, user.name, user.role.title)
            _userMessage.value = "Item inventaris '$name' berhasil ditambahkan!"
        }
    }

    fun updateInventoryStatus(
        item: InventoryItemEntity,
        newStatus: String,
        newLocation: String,
        assignedTo: String,
        newQuantity: Double
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.updateInventoryItemStatus(
                item = item,
                newStatus = newStatus,
                newLocation = newLocation,
                assignedTo = assignedTo,
                quantity = newQuantity,
                userName = user.name,
                role = user.role.title
            )
            _userMessage.value = "Status inventaris ${item.itemCode} diubah menjadi '$newStatus'"
        }
    }

    // ACTIONS: MATERIALS & TRANSACTIONS
    fun recordMaterialTx(
        material: MaterialItemEntity,
        type: String,
        quantity: Double,
        suratJalan: String,
        usedFor: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.recordMaterialTransaction(
                projectId = _selectedProjectId.value,
                materialId = material.id,
                materialName = material.name,
                type = type,
                quantity = quantity,
                unit = material.unit,
                suratJalanNo = suratJalan.ifBlank { "SJ-${System.currentTimeMillis() % 100000}" },
                loggedByRole = user.role.title,
                loggedByName = user.name,
                usedForWorkItem = usedFor.ifBlank { "Stok Operasional Lapangan" },
                notes = notes
            )
            _userMessage.value = "Transaksi $type material ${material.name} berhasil dicatat!"
        }
    }

    fun addMaterial(
        name: String,
        category: String,
        unit: String,
        initialStock: Double,
        minStock: Double,
        priceRp: Long,
        storageLoc: String,
        supplier: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val item = MaterialItemEntity(
                projectId = _selectedProjectId.value,
                name = name,
                category = category,
                unit = unit,
                currentStock = initialStock,
                minStockThreshold = minStock,
                unitPriceRp = priceRp,
                storageLocation = storageLoc.ifBlank { "Gudang Proyek" },
                supplierName = supplier.ifBlank { "Supplier Utama" }
            )
            repository.addNewMaterial(item, user.name, user.role.title)
            _userMessage.value = "Material '$name' berhasil ditambahkan ke katalog!"
        }
    }

    // ACTIONS: EQUIPMENT
    fun recordEquipmentLog(
        equipment: EquipmentEntity,
        startHm: Double,
        endHm: Double,
        fuelAdded: Double,
        workDesc: String,
        operator: String,
        condition: String,
        newStatus: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.recordEquipmentLog(
                projectId = _selectedProjectId.value,
                equipment = equipment,
                startHm = startHm,
                endHm = endHm,
                fuelAdded = fuelAdded,
                workDesc = workDesc,
                operator = operator.ifBlank { equipment.operatorName },
                condition = condition,
                newStatus = newStatus,
                loggedByName = user.name,
                loggedByRole = user.role.title
            )
            _userMessage.value = "Log penggunaan ${equipment.code} berhasil disimpan!"
        }
    }

    fun addEquipment(
        code: String,
        name: String,
        category: String,
        status: String,
        hourMeter: Double,
        fuelCap: Double,
        currentFuel: Double,
        operator: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val eq = EquipmentEntity(
                projectId = _selectedProjectId.value,
                code = code,
                name = name,
                category = category,
                status = status,
                currentHourMeter = hourMeter,
                fuelCapacityLiters = fuelCap,
                currentFuelLiters = currentFuel,
                operatorName = operator.ifBlank { "Belum Ditugaskan" },
                lastServiceDate = "Hari Ini",
                nextServiceHourMeter = hourMeter + 250.0,
                notes = notes
            )
            repository.addNewEquipment(eq, user.name, user.role.title)
            _userMessage.value = "Alat berat/mesin '$code' berhasil didaftarkan!"
        }
    }

    // ACTIONS: REPORTS
    fun submitDailyReport(
        weatherMorning: String,
        weatherAfternoon: String,
        weatherEvening: String,
        mandor: Int,
        tukang: Int,
        pekerja: Int,
        subcon: Int,
        workDesc: String,
        materialsSummary: String,
        heavyEquipmentSummary: String,
        obstacles: String,
        solutions: String,
        progressAdded: Float
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val total = mandor + tukang + pekerja + subcon
            val reportNum = "LHP/PRJ/${System.currentTimeMillis() % 100000}"
            val isApprover = user.role == UserRole.CEO || user.role == UserRole.PROJECT_MANAGER ||
                    user.role == UserRole.PROJECT_LEADER_GPA || user.role == UserRole.PROJECT_LEADER_GOR ||
                    user.role == UserRole.PROJECT_LEADER_MYKOST || user.role == UserRole.PROJECT_LEADER_LO_VILLA

            val report = DailyReportEntity(
                projectId = _selectedProjectId.value,
                reportNumber = reportNum,
                reportDateMillis = System.currentTimeMillis(),
                weatherMorning = weatherMorning,
                weatherAfternoon = weatherAfternoon,
                weatherEvening = weatherEvening,
                mandorCount = mandor,
                tukangCount = tukang,
                pekerjaCount = pekerja,
                subconCount = subcon,
                totalWorkers = total,
                workDescription = workDesc,
                materialsUsedSummary = materialsSummary,
                heavyEquipmentUsedSummary = heavyEquipmentSummary,
                obstaclesIssues = obstacles.ifBlank { "Tidak ada kendala berarti di lapangan." },
                solutionsActionTaken = solutions.ifBlank { "Pekerjaan dilanjutkan sesuai target harian." },
                progressAddedPercent = progressAdded,
                status = if (isApprover) "APPROVED" else "SUBMITTED",
                createdByRole = user.role.title,
                createdByName = user.name,
                verifiedByName = if (isApprover) user.name else "",
                verifiedDateMillis = if (isApprover) System.currentTimeMillis() else 0L,
                siteManagerNotes = if (isApprover) "Disetujui langsung oleh ${user.role.title}." else ""
            )
            repository.submitDailyReport(report)
            _userMessage.value = "Laporan Harian $reportNum berhasil dibuat & terkirim ke Manajer!"
        }
    }

    fun approveDailyReport(reportId: Long, notes: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.approveDailyReport(
                reportId = reportId,
                projectId = _selectedProjectId.value,
                verifierName = user.name,
                notes = notes.ifBlank { "Disetujui oleh Site Manager. Kualitas pekerjaan sesuai spek." }
            )
            _userMessage.value = "Laporan harian berhasil disetujui!"
        }
    }

    // ACTIONS: S-CURVE & SCHEDULE
    fun updateMilestone(milestone: SCurveMilestoneEntity, newActualCumulative: Float) {
        viewModelScope.launch {
            val user = _currentUser.value
            val updated = milestone.copy(
                actualCumulativePercent = newActualCumulative,
                isCompleted = newActualCumulative > 0f
            )
            repository.updateSCurveMilestone(updated, user.name, user.role.title)
            _userMessage.value = "Data Kurva S Minggu ${milestone.weekNumber} diperbarui: $newActualCumulative%"
        }
    }

    fun updateScheduleTaskProgress(scheduleItem: ProjectScheduleItemEntity, actualProgress: Float, status: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.updateScheduleProgress(
                itemId = scheduleItem.id,
                projectId = _selectedProjectId.value,
                actualProg = actualProgress,
                status = status,
                taskName = scheduleItem.taskName,
                userName = user.name,
                role = user.role.title
            )
            _userMessage.value = "Progres '${scheduleItem.taskName}' diperbarui: $actualProgress% ($status)"
        }
    }

    // ACTIONS: NOTIFICATIONS
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(_selectedProjectId.value)
            _userMessage.value = "Semua notifikasi ditandai sudah dibaca"
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications(_selectedProjectId.value)
            _userMessage.value = "Daftar notifikasi dibersihkan"
        }
    }

    // ACTIONS: DOCUMENTS
    fun uploadDocument(
        folder: String,
        title: String,
        fileName: String,
        fileType: String,
        fileSizeBytes: Long,
        version: String,
        allowedRoles: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val doc = ProjectDocumentEntity(
                projectId = _selectedProjectId.value,
                folderCategory = folder,
                title = title,
                fileName = fileName,
                fileType = fileType,
                fileSizeBytes = fileSizeBytes,
                uploadedByRole = user.role.title,
                uploadedByName = user.name,
                uploadDateMillis = System.currentTimeMillis(),
                version = version.ifBlank { "v1.0" },
                allowedRoles = allowedRoles,
                notes = notes
            )
            repository.uploadDocument(doc, user.name, user.role.title)
            _userMessage.value = "Dokumen '$title' berhasil diunggah ke folder $folder!"
        }
    }

    // ACTIONS: EXCEL / CSV IMPORT
    fun importExcelData(
        type: String, // "MATERIAL", "SCHEDULE", "RAP", "ATTENDANCE"
        fileName: String,
        rawCsvOrText: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val pId = _selectedProjectId.value

            try {
                when (type) {
                    "MATERIAL" -> {
                        val lines = rawCsvOrText.lines().filter { it.isNotBlank() }
                        val materials = mutableListOf<MaterialItemEntity>()
                        // Header sample: Nama Material, Kategori, Satuan, Stok Awal, Min Stok, Harga Satuan, Lokasi Gudang, Supplier
                        val dataLines = if (lines.firstOrNull()?.contains("Nama", ignoreCase = true) == true) lines.drop(1) else lines
                        
                        for (line in dataLines) {
                            val parts = line.split(",", ";", "\t").map { it.trim().removeSurrounding("\"") }
                            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                val name = parts[0]
                                val category = parts.getOrNull(1)?.ifBlank { "MATERIAL_UTAMA" } ?: "MATERIAL_UTAMA"
                                val unit = parts.getOrNull(2)?.ifBlank { "Zak" } ?: "Zak"
                                val stock = parts.getOrNull(3)?.toDoubleOrNull() ?: 50.0
                                val minStock = parts.getOrNull(4)?.toDoubleOrNull() ?: 10.0
                                val price = parts.getOrNull(5)?.replace(".", "")?.replace("Rp", "")?.trim()?.toLongOrNull() ?: 65000L
                                val loc = parts.getOrNull(6)?.ifBlank { "Gudang Utama Proyek" } ?: "Gudang Utama Proyek"
                                val supplier = parts.getOrNull(7)?.ifBlank { "Supplier PT Atmaterra" } ?: "Supplier PT Atmaterra"

                                materials.add(
                                    MaterialItemEntity(
                                        projectId = pId,
                                        name = name,
                                        category = category,
                                        unit = unit,
                                        currentStock = stock,
                                        minStockThreshold = minStock,
                                        unitPriceRp = price,
                                        storageLocation = loc,
                                        supplierName = supplier
                                    )
                                )
                            }
                        }
                        if (materials.isNotEmpty()) {
                            repository.importExcelMaterialItems(pId, materials, user.name, user.role.title, fileName)
                            // Also register doc in Documents table
                            repository.uploadDocument(
                                ProjectDocumentEntity(
                                    projectId = pId,
                                    folderCategory = "Surat Jalan & PO",
                                    title = "Impor Material Excel ($fileName)",
                                    fileName = fileName,
                                    fileType = "XLSX",
                                    fileSizeBytes = 45000L,
                                    uploadedByRole = user.role.title,
                                    uploadedByName = user.name,
                                    uploadDateMillis = System.currentTimeMillis(),
                                    version = "v1.0",
                                    allowedRoles = "LOGISTIK",
                                    notes = "Hasil integrasi impor Excel ${materials.size} item material"
                                ),
                                user.name,
                                user.role.title
                            )
                            _userMessage.value = "✅ Sukses mengintegrasikan ${materials.size} material dari file $fileName ke database!"
                        } else {
                            _userMessage.value = "⚠️ Tidak ada data material valid yang terbaca dari file."
                        }
                    }

                    "SCHEDULE" -> {
                        val lines = rawCsvOrText.lines().filter { it.isNotBlank() }
                        val items = mutableListOf<ProjectScheduleItemEntity>()
                        val dataLines = if (lines.firstOrNull()?.contains("WBS", ignoreCase = true) == true || lines.firstOrNull()?.contains("Pekerjaan", ignoreCase = true) == true) lines.drop(1) else lines

                        for ((idx, line) in dataLines.withIndex()) {
                            val parts = line.split(",", ";", "\t").map { it.trim().removeSurrounding("\"") }
                            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                val wbs = if (parts.size >= 2 && parts[0].startsWith("WBS", ignoreCase = true)) parts[0] else "WBS-IMP-${idx + 1}"
                                val task = if (parts.size >= 2 && parts[0].startsWith("WBS", ignoreCase = true)) parts[1] else parts[0]
                                val weight = parts.getOrNull(2)?.replace("%", "")?.trim()?.toFloatOrNull() ?: 5.0f
                                val plan = parts.getOrNull(3)?.replace("%", "")?.trim()?.toFloatOrNull() ?: 50.0f
                                val actual = parts.getOrNull(4)?.replace("%", "")?.trim()?.toFloatOrNull() ?: 45.0f
                                val status = parts.getOrNull(5)?.ifBlank { "SEDANG_BERJALAN" } ?: "SEDANG_BERJALAN"

                                items.add(
                                    ProjectScheduleItemEntity(
                                        projectId = pId,
                                        wbsCode = wbs,
                                        taskName = task,
                                        category = "PEKERJAAN_EXCEL",
                                        startDate = "01/08/2026",
                                        endDate = "30/09/2026",
                                        durationDays = 30,
                                        weightPercent = weight,
                                        plannedProgressPercent = plan,
                                        actualProgressPercent = actual,
                                        status = status,
                                        assignedLeader = user.name
                                    )
                                )
                            }
                        }
                        if (items.isNotEmpty()) {
                            repository.importExcelScheduleItems(pId, items, user.name, user.role.title, fileName)
                            repository.uploadDocument(
                                ProjectDocumentEntity(
                                    projectId = pId,
                                    folderCategory = "RAB, RAP & Kontrak",
                                    title = "Impor Time Schedule Excel ($fileName)",
                                    fileName = fileName,
                                    fileType = "XLSX",
                                    fileSizeBytes = 62000L,
                                    uploadedByRole = user.role.title,
                                    uploadedByName = user.name,
                                    uploadDateMillis = System.currentTimeMillis(),
                                    version = "v1.0",
                                    allowedRoles = "MANAGEMENT_ONLY",
                                    notes = "Hasil integrasi impor schedule ${items.size} WBS item"
                                ),
                                user.name,
                                user.role.title
                            )
                            _userMessage.value = "✅ Sukses mengintegrasikan ${items.size} item time schedule dari $fileName!"
                        }
                    }

                    "RAP" -> {
                        val lines = rawCsvOrText.lines().filter { it.isNotBlank() }
                        val rapList = mutableListOf<ProjectRapItemEntity>()
                        val dataLines = if (lines.firstOrNull()?.contains("Item", ignoreCase = true) == true) lines.drop(1) else lines

                        for ((idx, line) in dataLines.withIndex()) {
                            val parts = line.split(",", ";", "\t").map { it.trim().removeSurrounding("\"") }
                            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                val item = parts[0]
                                val cat = parts.getOrNull(1)?.ifBlank { "MATERIAL" } ?: "MATERIAL"
                                val budget = parts.getOrNull(2)?.replace(".", "")?.replace("Rp", "")?.trim()?.toLongOrNull() ?: 10_000_000L
                                val actual = parts.getOrNull(3)?.replace(".", "")?.replace("Rp", "")?.trim()?.toLongOrNull() ?: (budget * 0.75).toLong()

                                rapList.add(
                                    ProjectRapItemEntity(
                                        projectId = pId,
                                        category = cat,
                                        itemCode = "RAP-${idx + 101}",
                                        itemName = item,
                                        volume = 1.0,
                                        unit = "Ls",
                                        unitPriceRp = budget,
                                        budgetRapRp = budget,
                                        actualCostRp = actual,
                                        notes = "Impor data Excel RAP"
                                    )
                                )
                            }
                        }
                        if (rapList.isNotEmpty()) {
                            repository.importExcelRapItems(pId, rapList, user.name, user.role.title, fileName)
                            repository.uploadDocument(
                                ProjectDocumentEntity(
                                    projectId = pId,
                                    folderCategory = "RAB, RAP & Kontrak",
                                    title = "Impor RAP Anggaran Excel ($fileName)",
                                    fileName = fileName,
                                    fileType = "XLSX",
                                    fileSizeBytes = 78000L,
                                    uploadedByRole = user.role.title,
                                    uploadedByName = user.name,
                                    uploadDateMillis = System.currentTimeMillis(),
                                    version = "v1.0",
                                    allowedRoles = "MANAGEMENT_ONLY",
                                    notes = "Integrasi RAP Excel ${rapList.size} baris anggaran"
                                ),
                                user.name,
                                user.role.title
                            )
                            _userMessage.value = "✅ Sukses mengintegrasikan ${rapList.size} pos RAP dari $fileName ke database!"
                        }
                    }

                    "ATTENDANCE" -> {
                        val lines = rawCsvOrText.lines().filter { it.isNotBlank() }
                        val attList = mutableListOf<WorkerAttendanceEntity>()
                        val dataLines = if (lines.firstOrNull()?.contains("Nama", ignoreCase = true) == true) lines.drop(1) else lines

                        for (line in dataLines) {
                            val parts = line.split(",", ";", "\t").map { it.trim().removeSurrounding("\"") }
                            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                val name = parts[0]
                                val cat = parts.getOrNull(1)?.ifBlank { "TUKANG_BATU" } ?: "TUKANG_BATU"
                                val st = parts.getOrNull(2)?.ifBlank { "HADIR" } ?: "HADIR"
                                val ot = parts.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                                val rate = parts.getOrNull(4)?.replace(".", "")?.replace("Rp", "")?.trim()?.toLongOrNull() ?: 160000L
                                val task = parts.getOrNull(5)?.ifBlank { "Pekerjaan Lapangan" } ?: "Pekerjaan Lapangan"

                                val multiplier = when (st) {
                                    "HADIR" -> 1.0
                                    "SETENGAH_HARI" -> 0.5
                                    "LEMBUR" -> 1.0 + (ot * 0.1875)
                                    else -> 0.0
                                }
                                val earned = (rate * multiplier).toLong()

                                attList.add(
                                    WorkerAttendanceEntity(
                                        projectId = pId,
                                        dateMillis = System.currentTimeMillis(),
                                        dateString = "Hari Ini",
                                        workerName = name,
                                        workerCategory = cat,
                                        status = st,
                                        overtimeHours = ot,
                                        dailyRateRp = rate,
                                        totalEarnedRp = earned,
                                        workAssignedTo = task,
                                        loggedByName = user.name,
                                        notes = "Diimpor dari file $fileName"
                                    )
                                )
                            }
                        }
                        if (attList.isNotEmpty()) {
                            repository.importExcelAttendanceItems(pId, attList, user.name, user.role.title, fileName)
                            _userMessage.value = "✅ Sukses mengintegrasikan ${attList.size} data absensi pekerja dari $fileName!"
                        }
                    }
                }
            } catch (e: Exception) {
                _userMessage.value = "❌ Gagal memproses file Excel: ${e.localizedMessage}"
            }
        }
    }

    fun deleteDocument(doc: ProjectDocumentEntity) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.deleteDocument(doc, user.name, user.role.title)
            _userMessage.value = "Dokumen '${doc.title}' berhasil dihapus"
        }
    }

    // ACTIONS: ATTENDANCE
    fun recordWorkerAttendance(
        workerName: String,
        category: String,
        status: String,
        overtimeHours: Double,
        dailyRateRp: Long,
        workAssignedTo: String,
        notes: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val multiplier = when (status) {
                "HADIR" -> 1.0
                "SETENGAH_HARI" -> 0.5
                "LEMBUR" -> 1.0 + (overtimeHours * 0.1875) // Standard 1/8 rate per overtime hour
                else -> 0.0
            }
            val totalEarned = (dailyRateRp * multiplier).toLong()

            val record = WorkerAttendanceEntity(
                projectId = _selectedProjectId.value,
                dateMillis = System.currentTimeMillis(),
                dateString = "Hari Ini",
                workerName = workerName,
                workerCategory = category,
                status = status,
                overtimeHours = overtimeHours,
                dailyRateRp = dailyRateRp,
                totalEarnedRp = totalEarned,
                workAssignedTo = workAssignedTo.ifBlank { "Pekerjaan Lapangan" },
                loggedByName = user.name,
                notes = notes
            )
            repository.recordAttendance(record, user.name, user.role.title)
            _userMessage.value = "Absensi untuk $workerName ($status) berhasil dicatat!"
        }
    }

    // ACTIONS: RAP
    fun updateRapRealization(item: ProjectRapItemEntity, newActualCost: Long) {
        viewModelScope.launch {
            val user = _currentUser.value
            val updated = item.copy(actualCostRp = newActualCost)
            repository.updateRapItem(updated, user.name, user.role.title)
            _userMessage.value = "Realisasi RAP '${item.itemName}' diperbarui: Rp $newActualCost"
        }
    }

    fun broadcastMessage(title: String, message: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.postTeamBroadcast(
                projectId = _selectedProjectId.value,
                role = user.role.title,
                name = user.name,
                title = title,
                message = message
            )
            _userMessage.value = "Instruksi/Pesan lapangan disiarkan ke seluruh tim proyek!"
        }
    }
}
