package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab

@Composable
fun DashboardScreen(
    currentProject: ProjectEntity?,
    materials: List<MaterialItemEntity>,
    lowStockMaterials: List<MaterialItemEntity>,
    inventoryItems: List<InventoryItemEntity>,
    equipments: List<EquipmentEntity>,
    dailyReports: List<DailyReportEntity>,
    sCurveMilestones: List<SCurveMilestoneEntity>,
    activityLogs: List<ActivityLogEntity>,
    notifications: List<ProjectNotificationEntity>,
    unreadNotificationCount: Int,
    attendances: List<WorkerAttendanceEntity>,
    currentUser: UserProfile,
    onNavigateTab: (AppNavTab) -> Unit,
    onQuickAddMaterialTx: () -> Unit,
    onQuickRecordEquipment: () -> Unit,
    onQuickCreateDailyReport: () -> Unit,
    onQuickRecordAttendance: () -> Unit
) {
    val operationalEquipments = equipments.count { it.status == "OPERASIONAL" }
    val latestReport = dailyReports.firstOrNull()
    val totalWorkersToday = attendances.count { it.status == "HADIR" || it.status == "LEMBUR" }

    val completedMilestones = sCurveMilestones.filter { it.isCompleted }
    val latestMilestone = completedMilestones.lastOrNull()
    val plannedProgress = latestMilestone?.plannedCumulativePercent ?: currentProject?.currentPlannedProgress ?: 0f
    val actualProgress = latestMilestone?.actualCumulativePercent ?: currentProject?.currentActualProgress ?: 0f
    val deviation = actualProgress - plannedProgress

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 88.dp)
    ) {
        // Hero Project Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    DarkSurfaceElevated,
                                    DarkSurface
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MONITORING LAPANGAN PRO KONTRAKTOR",
                                color = OrangePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentProject?.name ?: "Proyek Konstruksi",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TextSecondaryLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentProject?.location ?: "-",
                                    color = TextSecondaryLight,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Deviation Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (deviation >= 0) SuccessGreenContainer else DangerRedContainer
                        ) {
                            Text(
                                text = if (deviation >= 0) "+${String.format("%.1f", deviation)}% (Ahead)" else "${String.format("%.1f", deviation)}% (Delayed)",
                                color = if (deviation >= 0) SuccessOnGreenContainer else DangerOnRedContainer,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bars
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress Fisik Aktual: ${String.format("%.1f", actualProgress)}%",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target Rencana: ${String.format("%.1f", plannedProgress)}%",
                                color = TextSecondaryLight,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (actualProgress / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = OrangePrimary,
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contract Info & Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Nilai Kontrak: ${formatRupiah(currentProject?.contractValueRp ?: 0L)}",
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Target Selesai: ${currentProject?.targetEndDate ?: "-"}",
                            color = TextSecondaryLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 14 NAVIGATION MODULES (ATMETERRA BUILDWORK WORKBOOK)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .background(AtmaterraAccentGold, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NAVIGASI LAPORAN LENGKAP",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                }

                val modules14 = listOf(
                    Triple("01", "Ringkasan", "KPI utama & QC sumber") to AppNavTab.M01_RINGKASAN,
                    Triple("02", "Daftar Isi", "Struktur workbook") to AppNavTab.M02_DAFTAR_ISI,
                    Triple("03", "Surat Laporan", "Surat progres mingguan") to AppNavTab.M03_SURAT_LAPORAN,
                    Triple("04", "Rekap Progres", "Rekap bobot pekerjaan") to AppNavTab.M04_REKAP_PROGRES,
                    Triple("05", "Detail Progres", "384 item pekerjaan") to AppNavTab.M05_DETAIL_PROGRES,
                    Triple("06", "Time Schedule", "Revisi schedule / kurva S") to AppNavTab.M06_TIME_SCHEDULE,
                    Triple("07", "Permasalahan & Solusi", "Hambatan, perubahan, addendum") to AppNavTab.M07_PERMASALAHAN_SOLUSI,
                    Triple("08", "Cuaca Harian", "Cuaca 03–08 Agustus") to AppNavTab.M08_CUACA_HARIAN,
                    Triple("09", "Cuaca Mingguan", "Rekap cuaca mingguan") to AppNavTab.M09_CUACA_MINGGUAN,
                    Triple("10", "Personil", "Personil & subkon/vendor") to AppNavTab.M10_PERSONIL,
                    Triple("11", "Peralatan", "Inventaris peralatan") to AppNavTab.M11_PERALATAN,
                    Triple("12", "Absensi", "Absensi man power") to AppNavTab.M12_ABSENSI,
                    Triple("13", "Aktivitas Harian", "Ringkasan aktivitas harian") to AppNavTab.M13_AKTIVITAS_HARIAN,
                    Triple("14", "Dokumentasi", "Foto pekerjaan 03–08 Agustus") to AppNavTab.M14_DOKUMENTASI
                )

                // Render in neat 2-column or list rows matching screenshot 2
                modules14.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { (info, tab) ->
                            val (num, title, sub) = info
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateTab(tab) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFF0F766E),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = num,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = TextPrimaryDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = sub,
                                            fontSize = 10.sp,
                                            color = TextSecondaryDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Button(
                        onClick = onQuickCreateDailyReport,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                        modifier = Modifier.testTag("quick_daily_report_button")
                    ) {
                        Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Laporan Harian", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    FilledTonalButton(
                        onClick = onQuickRecordAttendance,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                        modifier = Modifier.testTag("quick_attendance_button")
                    ) {
                        Icon(imageVector = Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Absensi Tukang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    FilledTonalButton(
                        onClick = onQuickAddMaterialTx,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                        modifier = Modifier.testTag("quick_material_button")
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Surat Jalan In/Out", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    FilledTonalButton(
                        onClick = onQuickRecordEquipment,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                        modifier = Modifier.testTag("quick_equipment_button")
                    ) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Jam Alat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Notification Alert Strip (if unread notifications exist)
        if (unreadNotificationCount > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberAccentContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTab(AppNavTab.NOTIFICATIONS) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberOnAccentContainer, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("$unreadNotificationCount Peringatan Proyek Baru Menunggu", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberOnAccentContainer)
                            notifications.firstOrNull { !it.isRead }?.let {
                                Text(it.title, style = MaterialTheme.typography.bodySmall, color = AmberOnAccentContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AmberOnAccentContainer)
                    }
                }
            }
        }

        // 6 Fast Module Navigation Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "MODUL KONTRAKTOR & OPERASIONAL LAPANGAN",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Inventaris & APD",
                        value = "${inventoryItems.size} Item",
                        subtitle = "${inventoryItems.count { it.status == "TERSEDIA" }} Siap Digunakan",
                        icon = Icons.Default.Build,
                        accentColor = OrangePrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppNavTab.INVENTORY) }
                    )

                    MetricCard(
                        title = "Absensi Lapangan",
                        value = "$totalWorkersToday Hadir",
                        subtitle = "${attendances.size} Total Terdata",
                        icon = Icons.Default.Groups,
                        accentColor = BlueprintBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppNavTab.ATTENDANCE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Timeschedule & RAP",
                        value = "WBS 6 Tahap",
                        subtitle = "Monitoring Realisasi",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppNavTab.SCHEDULE_RAP) }
                    )

                    MetricCard(
                        title = "Dokumen & Gambar",
                        value = "Shop Drawing",
                        subtitle = "Folder & Hak Akses",
                        icon = Icons.Default.FolderShared,
                        accentColor = SlateSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppNavTab.DOCUMENTS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Alat Berat & Mesin",
                        value = "$operationalEquipments / ${equipments.size}",
                        subtitle = "Unit Siap Kerja",
                        icon = Icons.Default.PrecisionManufacturing,
                        accentColor = AmberAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppNavTab.EQUIPMENT) }
                    )

                    MetricCard(
                        title = "Kurva S & Laporan",
                        value = "${String.format("%.1f", actualProgress)}%",
                        subtitle = "Deviasi: ${String.format("%+.1f", deviation)}%",
                        icon = Icons.Default.ShowChart,
                        accentColor = OrangePrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AppNavTab.SCURVE) }
                    )
                }
            }
        }

        // Real-Time Multi-User Activity Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SuccessGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AKTIVITAS TIM LAPANGAN REAL-TIME",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = { onNavigateTab(AppNavTab.TEAM) }) {
                    Text("Kelola Akses Tim", fontSize = 12.sp)
                }
            }
        }

        if (activityLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada aktivitas tercatat.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(activityLogs.take(5)) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    when (log.category) {
                                        "LOGISTIK" -> SlateSecondaryContainer
                                        "ALAT" -> OrangePrimaryContainer
                                        "LAPORAN" -> BlueprintBlueContainer
                                        "PROGRESS" -> AmberAccentContainer
                                        "ABSENSI" -> SuccessGreenContainer
                                        "DOKUMEN" -> SlateSecondaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (log.category) {
                                    "LOGISTIK" -> Icons.Default.Inventory2
                                    "ALAT" -> Icons.Default.PrecisionManufacturing
                                    "LAPORAN" -> Icons.Default.Assignment
                                    "PROGRESS" -> Icons.Default.TrendingUp
                                    "ABSENSI" -> Icons.Default.HowToReg
                                    "DOKUMEN" -> Icons.Default.FolderShared
                                    else -> Icons.Default.Campaign
                                },
                                contentDescription = null,
                                tint = when (log.category) {
                                    "LOGISTIK" -> SlateOnSecondaryContainer
                                    "ALAT" -> OrangeOnPrimaryContainer
                                    "LAPORAN" -> BlueprintBlue
                                    "PROGRESS" -> AmberOnAccentContainer
                                    "ABSENSI" -> SuccessOnGreenContainer
                                    "DOKUMEN" -> SlateOnSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = log.userName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                                Text(
                                    text = formatDate(log.timestampMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = log.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (log.details.isNotBlank()) {
                                Text(
                                    text = log.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
