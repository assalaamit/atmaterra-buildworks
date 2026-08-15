package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.dialogs.*
import com.example.ui.theme.OrangePrimary
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ContractorViewModel

@Composable
fun MainScreen(viewModel: ContractorViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()

    val materials by viewModel.materials.collectAsStateWithLifecycle()
    val lowStockMaterials by viewModel.lowStockMaterials.collectAsStateWithLifecycle()
    val materialTransactions by viewModel.materialTransactions.collectAsStateWithLifecycle()

    val inventoryItems by viewModel.inventoryItems.collectAsStateWithLifecycle()
    val equipments by viewModel.equipments.collectAsStateWithLifecycle()
    val equipmentLogs by viewModel.equipmentLogs.collectAsStateWithLifecycle()

    val dailyReports by viewModel.dailyReports.collectAsStateWithLifecycle()
    val weeklyReports by viewModel.weeklyReports.collectAsStateWithLifecycle()

    val sCurveMilestones by viewModel.sCurveMilestones.collectAsStateWithLifecycle()
    val activityLogs by viewModel.activityLogs.collectAsStateWithLifecycle()

    val notifications by viewModel.filteredNotifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val notificationPreferences by viewModel.notificationPreferences.collectAsStateWithLifecycle()

    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val attendances by viewModel.attendances.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val rapItems by viewModel.rapItems.collectAsStateWithLifecycle()

    // Filter states
    val materialCatFilter by viewModel.materialCategoryFilter.collectAsStateWithLifecycle()
    val materialSearch by viewModel.materialSearchQuery.collectAsStateWithLifecycle()

    val inventoryCatFilter by viewModel.inventoryCategoryFilter.collectAsStateWithLifecycle()
    val inventoryStatusFilter by viewModel.inventoryStatusFilter.collectAsStateWithLifecycle()
    val inventorySearch by viewModel.inventorySearchQuery.collectAsStateWithLifecycle()

    val equipmentStatusFilter by viewModel.equipmentStatusFilter.collectAsStateWithLifecycle()
    val equipmentSearch by viewModel.equipmentSearchQuery.collectAsStateWithLifecycle()

    val reportTabSelection by viewModel.reportTabSelection.collectAsStateWithLifecycle()
    val documentFolderFilter by viewModel.documentFolderFilter.collectAsStateWithLifecycle()
    val documentSearch by viewModel.documentSearchQuery.collectAsStateWithLifecycle()

    val attendanceFilterCategory by viewModel.attendanceFilterCategory.collectAsStateWithLifecycle()
    val attendanceSearch by viewModel.attendanceSearchQuery.collectAsStateWithLifecycle()

    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Dialog States
    var showAddInventoryDialog by remember { mutableStateOf(false) }
    var selectedInventoryForUpdate by remember { mutableStateOf<InventoryItemEntity?>(null) }
    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var selectedMaterialForTx by remember { mutableStateOf<MaterialItemEntity?>(null) }
    var showAddEquipmentDialog by remember { mutableStateOf(false) }
    var selectedEquipmentForLog by remember { mutableStateOf<EquipmentEntity?>(null) }
    var showCreateDailyReportDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showRoleSelectorDialog by remember { mutableStateOf(false) }
    var showNotificationPrefDialog by remember { mutableStateOf(false) }
    var showUploadDocDialog by remember { mutableStateOf(false) }
    var showImportExcelDialog by remember { mutableStateOf(false) }
    var showAddAttendanceDialog by remember { mutableStateOf(false) }
    var selectedScheduleForUpdate by remember { mutableStateOf<ProjectScheduleItemEntity?>(null) }
    var selectedRapForUpdate by remember { mutableStateOf<ProjectRapItemEntity?>(null) }

    var inventorySubTab by remember { mutableStateOf(0) }

    // Navigation Bar items to show on the bottom bar (Top 5 primary + overflow access)
    val bottomNavTabs = listOf(
        AppNavTab.DASHBOARD,
        AppNavTab.INVENTORY,
        AppNavTab.ATTENDANCE,
        AppNavTab.SCHEDULE_RAP,
        AppNavTab.REPORTS,
        AppNavTab.DOCUMENTS
    )

    Scaffold(
        topBar = {
            ContractorTopBar(
                currentProject = currentProject,
                allProjects = allProjects,
                currentUser = currentUser,
                currentTab = currentTab,
                unreadNotificationCount = unreadNotificationCount,
                onSelectTab = { viewModel.selectTab(it) },
                onSelectProject = { viewModel.selectProject(it) },
                onOpenRoleSelector = { showRoleSelectorDialog = true },
                onOpenBroadcast = { showBroadcastDialog = true },
                onOpenNotifications = { viewModel.selectTab(AppNavTab.NOTIFICATIONS) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                bottomNavTabs.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    AppNavTab.DASHBOARD -> Icons.Default.Dashboard
                                    AppNavTab.INVENTORY -> Icons.Default.Build
                                    AppNavTab.ATTENDANCE -> Icons.Default.Groups
                                    AppNavTab.SCHEDULE_RAP -> Icons.Default.AccountBalanceWallet
                                    AppNavTab.REPORTS -> Icons.Default.Assignment
                                    AppNavTab.DOCUMENTS -> Icons.Default.FolderShared
                                    AppNavTab.EQUIPMENT -> Icons.Default.PrecisionManufacturing
                                    AppNavTab.SCURVE -> Icons.Default.ShowChart
                                    AppNavTab.NOTIFICATIONS -> Icons.Default.Notifications
                                    AppNavTab.TEAM -> Icons.Default.SupervisedUserCircle
                                    else -> Icons.Default.Description
                                },
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title, fontSize = 9.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OrangePrimary,
                            selectedTextColor = OrangePrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentTab,
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    AppNavTab.DASHBOARD -> DashboardScreen(
                        currentProject = currentProject,
                        materials = materials,
                        lowStockMaterials = lowStockMaterials,
                        inventoryItems = inventoryItems,
                        equipments = equipments,
                        dailyReports = dailyReports,
                        sCurveMilestones = sCurveMilestones,
                        activityLogs = activityLogs,
                        notifications = notifications,
                        unreadNotificationCount = unreadNotificationCount,
                        attendances = attendances,
                        currentUser = currentUser,
                        onNavigateTab = { viewModel.selectTab(it) },
                        onQuickAddMaterialTx = {
                            if (materials.isNotEmpty()) selectedMaterialForTx = materials.first()
                        },
                        onQuickRecordEquipment = {
                            if (equipments.isNotEmpty()) selectedEquipmentForLog = equipments.first()
                        },
                        onQuickCreateDailyReport = { showCreateDailyReportDialog = true },
                        onQuickRecordAttendance = { showAddAttendanceDialog = true }
                    )

                    AppNavTab.INVENTORY -> InventoryScreen(
                        inventoryItems = inventoryItems,
                        materials = materials,
                        lowStockMaterials = lowStockMaterials,
                        transactions = materialTransactions,
                        selectedTab = inventorySubTab,
                        onTabSelected = { inventorySubTab = it },
                        categoryFilter = inventoryCatFilter,
                        statusFilter = inventoryStatusFilter,
                        searchQuery = inventorySearch,
                        onCategoryFilterChange = { viewModel.inventoryCategoryFilter.value = it },
                        onStatusFilterChange = { viewModel.inventoryStatusFilter.value = it },
                        onSearchQueryChange = { viewModel.inventorySearchQuery.value = it },
                        onOpenAddInventoryDialog = { showAddInventoryDialog = true },
                        onOpenAddMaterialDialog = { showAddMaterialDialog = true },
                        onOpenUpdateInventoryDialog = { selectedInventoryForUpdate = it },
                        onOpenMaterialTransactionDialog = { selectedMaterialForTx = it }
                    )

                    AppNavTab.EQUIPMENT -> EquipmentScreen(
                        equipments = equipments,
                        equipmentLogs = equipmentLogs,
                        statusFilter = equipmentStatusFilter,
                        searchQuery = equipmentSearch,
                        onStatusFilterChange = { viewModel.equipmentStatusFilter.value = it },
                        onSearchQueryChange = { viewModel.equipmentSearchQuery.value = it },
                        onOpenRecordLogDialog = { selectedEquipmentForLog = it },
                        onOpenAddEquipmentDialog = { showAddEquipmentDialog = true }
                    )

                    AppNavTab.ATTENDANCE -> AttendanceScreen(
                        attendances = attendances,
                        currentUser = currentUser,
                        filterCategory = attendanceFilterCategory,
                        searchQuery = attendanceSearch,
                        onFilterCategoryChange = { viewModel.attendanceFilterCategory.value = it },
                        onSearchQueryChange = { viewModel.attendanceSearchQuery.value = it },
                        onOpenAddAttendanceDialog = { showAddAttendanceDialog = true },
                        onOpenQuickCrewDialog = {
                            // Quick attendance batch
                            viewModel.recordWorkerAttendance("Ahmad Rifai", "TUKANG_BESI", "HADIR", 2.0, 160000L, "Penulangan Kolom K1", "Presensi Regu")
                            viewModel.recordWorkerAttendance("Budi Hartono", "TUKANG_BATU", "HADIR", 0.0, 150000L, "Pasang Hebel Lt. 2", "Presensi Regu")
                            viewModel.recordWorkerAttendance("Suryadi", "TUKANG_KAYU", "HADIR", 2.0, 150000L, "Bekisting Balok Lt. 4", "Presensi Regu")
                            viewModel.recordWorkerAttendance("Yanto (Helper)", "PEKERJA_HELPER", "HADIR", 2.0, 120000L, "Pegang Selang Vibrator", "Presensi Regu")
                        }
                    )

                    AppNavTab.SCHEDULE_RAP -> ScheduleRapScreen(
                        schedules = schedules,
                        rapItems = rapItems,
                        currentUser = currentUser,
                        onUpdateScheduleProgress = { selectedScheduleForUpdate = it },
                        onUpdateRapItem = { selectedRapForUpdate = it }
                    )

                    AppNavTab.REPORTS -> ReportsScreen(
                        dailyReports = dailyReports,
                        weeklyReports = weeklyReports,
                        currentUser = currentUser,
                        reportTabSelection = reportTabSelection,
                        onTabSelected = { viewModel.reportTabSelection.value = it },
                        onOpenCreateDailyReport = { showCreateDailyReportDialog = true },
                        onApproveReport = { repId, notes -> viewModel.approveDailyReport(repId, notes) },
                        onShowMessage = { viewModel.broadcastMessage("Salin Laporan", it) }
                    )

                    AppNavTab.DOCUMENTS -> DocumentsScreen(
                        documents = documents,
                        currentUser = currentUser,
                        folderFilter = documentFolderFilter,
                        searchQuery = documentSearch,
                        onFolderFilterChange = { viewModel.documentFolderFilter.value = it },
                        onSearchQueryChange = { viewModel.documentSearchQuery.value = it },
                        onOpenUploadDialog = { showUploadDocDialog = true },
                        onOpenImportExcelDialog = { showImportExcelDialog = true },
                        onDeleteDocument = { viewModel.deleteDocument(it) },
                        onShowMessage = { viewModel.broadcastMessage("Akses Dokumen", it) }
                    )

                    AppNavTab.SCURVE -> SCurveScreen(
                        milestones = sCurveMilestones,
                        onUpdateMilestone = { ms, actual -> viewModel.updateMilestone(ms, actual) }
                    )

                    AppNavTab.NOTIFICATIONS -> NotificationsScreen(
                        notifications = notifications,
                        preferences = notificationPreferences,
                        unreadCount = unreadNotificationCount,
                        onMarkAsRead = { viewModel.markNotificationRead(it) },
                        onMarkAllAsRead = { viewModel.markAllNotificationsRead() },
                        onClearAll = { viewModel.clearAllNotifications() },
                        onOpenPreferencesDialog = { showNotificationPrefDialog = true },
                        onNavigateToTab = { tabName ->
                            when (tabName) {
                                "EQUIPMENT" -> viewModel.selectTab(AppNavTab.EQUIPMENT)
                                "INVENTORY" -> viewModel.selectTab(AppNavTab.INVENTORY)
                                "REPORTS" -> viewModel.selectTab(AppNavTab.REPORTS)
                                else -> viewModel.selectTab(AppNavTab.DASHBOARD)
                            }
                        }
                    )

                    AppNavTab.M01_RINGKASAN -> M01RingkasanScreen(
                        currentProject = currentProject,
                        weeklyReports = weeklyReports,
                        sCurveMilestones = sCurveMilestones,
                        attendances = attendances,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M02_DAFTAR_ISI -> M02DaftarIsiScreen(
                        onSelectModule = { viewModel.selectTab(it) },
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M03_SURAT_LAPORAN -> M03SuratLaporanScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M04_REKAP_PROGRES -> M04RekapProgresScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M05_DETAIL_PROGRES -> M05DetailProgresScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M06_TIME_SCHEDULE -> M06TimeScheduleScreen(
                        currentProject = currentProject,
                        sCurveMilestones = sCurveMilestones,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M07_PERMASALAHAN_SOLUSI -> M07PermasalahanSolusiScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M08_CUACA_HARIAN -> M08CuacaHarianScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M09_CUACA_MINGGUAN -> M09CuacaMingguanScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M10_PERSONIL -> M10PersonilScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M11_PERALATAN -> M11PeralatanScreen(
                        currentProject = currentProject,
                        equipments = equipments,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M12_ABSENSI -> M12AbsensiScreen(
                        currentProject = currentProject,
                        attendances = attendances,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M13_AKTIVITAS_HARIAN -> M13AktivitasHarianScreen(
                        currentProject = currentProject,
                        dailyReports = dailyReports,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.M14_DOKUMENTASI -> M14DokumentasiScreen(
                        currentProject = currentProject,
                        onBack = { viewModel.selectTab(AppNavTab.DASHBOARD) }
                    )

                    AppNavTab.TEAM -> TeamCollabScreen(
                        currentUser = currentUser,
                        predefinedUsers = viewModel.predefinedUsers,
                        onSwitchUser = { viewModel.switchUser(it) },
                        onOpenBroadcast = { showBroadcastDialog = true },
                        onChangePassword = { userId, oldPass, newPass ->
                            viewModel.updateUserPassword(userId, oldPass, newPass)
                        }
                    )
                }
            }
        }
    }

    // DIALOGS

    // 1. Add Inventory
    if (showAddInventoryDialog) {
        AddInventoryItemDialog(
            onDismiss = { showAddInventoryDialog = false },
            onConfirm = { code, name, cat, st, qty, u, min, pr, loc, ass, sup, not ->
                viewModel.addInventoryItem(code, name, cat, st, qty, u, min, pr, loc, ass, sup, not)
                showAddInventoryDialog = false
            }
        )
    }

    // 2. Update Inventory Status
    selectedInventoryForUpdate?.let { item ->
        UpdateInventoryStatusDialog(
            item = item,
            onDismiss = { selectedInventoryForUpdate = null },
            onConfirm = { newStatus, newLoc, ass, newQty ->
                viewModel.updateInventoryStatus(item, newStatus, newLoc, ass, newQty)
                selectedInventoryForUpdate = null
            }
        )
    }

    // 3. Notification Preferences
    if (showNotificationPrefDialog) {
        NotificationPreferencesDialog(
            currentPreferences = notificationPreferences,
            onDismiss = { showNotificationPrefDialog = false },
            onSave = { main, dl, ls, sc, bc ->
                viewModel.updateNotificationPreferences(main, dl, ls, sc, bc)
                showNotificationPrefDialog = false
            }
        )
    }

    // 4. Upload Document
    if (showUploadDocDialog) {
        UploadDocumentDialog(
            onDismiss = { showUploadDocDialog = false },
            onConfirm = { folder, title, fileName, fileType, size, ver, roles, notes ->
                viewModel.uploadDocument(folder, title, fileName, fileType, size, ver, roles, notes)
                showUploadDocDialog = false
            }
        )
    }

    // 4.1 Import Excel / Spreadsheet
    if (showImportExcelDialog) {
        ImportExcelDataDialog(
            onDismiss = { showImportExcelDialog = false },
            onConfirmImport = { type, fileName, rawText ->
                viewModel.importExcelData(type, fileName, rawText)
                showImportExcelDialog = false
            }
        )
    }

    // 5. Add Attendance
    if (showAddAttendanceDialog) {
        AddAttendanceDialog(
            onDismiss = { showAddAttendanceDialog = false },
            onConfirm = { worker, cat, st, ot, rate, work, not ->
                viewModel.recordWorkerAttendance(worker, cat, st, ot, rate, work, not)
                showAddAttendanceDialog = false
            }
        )
    }

    // 6. Update Schedule Progress
    selectedScheduleForUpdate?.let { schedule ->
        UpdateScheduleProgressDialog(
            item = schedule,
            onDismiss = { selectedScheduleForUpdate = null },
            onConfirm = { actualProgress, status ->
                viewModel.updateScheduleTaskProgress(schedule, actualProgress, status)
                selectedScheduleForUpdate = null
            }
        )
    }

    // 7. Update RAP Cost
    selectedRapForUpdate?.let { rap ->
        UpdateRapCostDialog(
            item = rap,
            onDismiss = { selectedRapForUpdate = null },
            onConfirm = { newCost ->
                viewModel.updateRapRealization(rap, newCost)
                selectedRapForUpdate = null
            }
        )
    }

    // 8. Add Material
    if (showAddMaterialDialog) {
        AddMaterialDialog(
            onDismiss = { showAddMaterialDialog = false },
            onSave = { name, cat, unit, stock, min, price, loc, supplier ->
                viewModel.addMaterial(name, cat, unit, stock, min, price, loc, supplier)
                showAddMaterialDialog = false
            }
        )
    }

    // 9. Material Transaction
    selectedMaterialForTx?.let { mat ->
        MaterialTransactionDialog(
            material = mat,
            onDismiss = { selectedMaterialForTx = null },
            onConfirm = { type, qty, sj, usedFor, notes ->
                viewModel.recordMaterialTx(mat, type, qty, sj, usedFor, notes)
                selectedMaterialForTx = null
            }
        )
    }

    // 10. Add Equipment
    if (showAddEquipmentDialog) {
        AddEquipmentDialog(
            onDismiss = { showAddEquipmentDialog = false },
            onSave = { code, name, cat, st, hm, fCap, curF, op, notes ->
                viewModel.addEquipment(code, name, cat, st, hm, fCap, curF, op, notes)
                showAddEquipmentDialog = false
            }
        )
    }

    // 11. Equipment Log
    selectedEquipmentForLog?.let { eq ->
        RecordEquipmentLogDialog(
            equipment = eq,
            onDismiss = { selectedEquipmentForLog = null },
            onSave = { startHm, endHm, fuel, desc, op, cond, status ->
                viewModel.recordEquipmentLog(eq, startHm, endHm, fuel, desc, op, cond, status)
                selectedEquipmentForLog = null
            }
        )
    }

    // 12. Create Daily Report
    if (showCreateDailyReportDialog) {
        CreateDailyReportDialog(
            onDismiss = { showCreateDailyReportDialog = false },
            onSubmit = { wM, wA, wE, mandor, tukang, pekerja, subcon, desc, mat, eq, obs, sol, prog ->
                viewModel.submitDailyReport(wM, wA, wE, mandor, tukang, pekerja, subcon, desc, mat, eq, obs, sol, prog)
                showCreateDailyReportDialog = false
            }
        )
    }

    // 13. Broadcast
    if (showBroadcastDialog) {
        BroadcastDialog(
            onDismiss = { showBroadcastDialog = false },
            onSend = { title, msg ->
                viewModel.broadcastMessage(title, msg)
                showBroadcastDialog = false
            }
        )
    }

    // 14. Role Switcher Modal Dialog
    if (showRoleSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSelectorDialog = false },
            title = {
                Column {
                    Text("Ganti Akun Pengguna", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("PT Atmaterra Buildwork Indonesia", style = MaterialTheme.typography.bodySmall, color = Color(0xFF0F766E))
                }
            },
            text = {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.predefinedUsers) { user ->
                        val isCurrent = user.id == currentUser.id
                        Card(
                            onClick = {
                                viewModel.switchUser(user)
                                showRoleSelectorDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F766E)) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = user.name, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = user.role.badge,
                                                fontSize = 9.sp,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${user.role.title} · @${user.username}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0F766E))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleSelectorDialog = false }) { Text("Tutup") }
            }
        )
    }
}
