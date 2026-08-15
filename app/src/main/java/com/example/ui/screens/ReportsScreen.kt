package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatShortDate
import com.example.ui.theme.*

@Composable
fun ReportsScreen(
    dailyReports: List<DailyReportEntity>,
    weeklyReports: List<WeeklyReportEntity>,
    currentUser: UserProfile,
    reportTabSelection: Int,
    onTabSelected: (Int) -> Unit,
    onOpenCreateDailyReport: () -> Unit,
    onApproveReport: (Long, String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var reportToApprove by remember { mutableStateOf<DailyReportEntity?>(null) }
    var approveNotesText by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            if (reportTabSelection == 0) {
                ExtendedFloatingActionButton(
                    onClick = onOpenCreateDailyReport,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Buat Laporan Harian", fontWeight = FontWeight.Bold) },
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .testTag("create_daily_report_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Switcher
            TabRow(
                selectedTabIndex = reportTabSelection,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = OrangePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = reportTabSelection == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("Laporan Harian (${dailyReports.size})", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Today, contentDescription = null) }
                )
                Tab(
                    selected = reportTabSelection == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text("Laporan Mingguan (${weeklyReports.size})", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
            }

            // Tab 0: Laporan Harian
            if (reportTabSelection == 0) {
                if (dailyReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada laporan harian.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
                    ) {
                        items(dailyReports, key = { it.id }) { report ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("daily_report_card_${report.id}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = report.reportNumber,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = formatShortDate(report.reportDateMillis),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        StatusBadge(status = report.status)
                                    }

                                    // Weather & Labor badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Weather
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.WbSunny,
                                                    contentDescription = null,
                                                    tint = AmberAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Cuaca: ${report.weatherMorning}/${report.weatherAfternoon}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        // Total Labor
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = BlueprintBlueContainer,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Groups,
                                                    contentDescription = null,
                                                    tint = BlueprintBlue,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "${report.totalWorkers} Pekerja (${report.mandorCount}M, ${report.tukangCount}T)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    // Work description
                                    Text(
                                        text = "Uraian Pekerjaan:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = report.workDescription,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }

                                    // Materials & Equipments
                                    if (report.materialsUsedSummary.isNotBlank()) {
                                        Text(
                                            text = "📦 Material: ${report.materialsUsedSummary}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (report.heavyEquipmentUsedSummary.isNotBlank()) {
                                        Text(
                                            text = "🚜 Alat: ${report.heavyEquipmentUsedSummary}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Obstacles & Solutions
                                    if (report.obstaclesIssues.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AmberAccentContainer.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "⚠️ Kendala: ${report.obstaclesIssues}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = AmberOnAccentContainer
                                                )
                                                if (report.solutionsActionTaken.isNotBlank()) {
                                                    Text(
                                                        text = "💡 Solusi: ${report.solutionsActionTaken}",
                                                        fontSize = 11.sp,
                                                        color = AmberOnAccentContainer
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Site Manager Note if Approved
                                    if (report.verifiedByName.isNotBlank()) {
                                        Text(
                                            text = "✅ Diverifikasi oleh: ${report.verifiedByName} • '${report.siteManagerNotes}'",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SuccessGreen
                                        )
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    // Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dibuat: ${report.createdByName} (${report.createdByRole})",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Copy WhatsApp Text
                                            IconButton(
                                                onClick = {
                                                    val shareText = """
                                                        📋 *LAPORAN HARIAN PROYEK*
                                                        No: ${report.reportNumber}
                                                        Tanggal: ${formatShortDate(report.reportDateMillis)}
                                                        Cuaca: ${report.weatherMorning} / ${report.weatherAfternoon}
                                                        Tenaga Kerja: ${report.totalWorkers} Orang (Mandor: ${report.mandorCount}, Tukang: ${report.tukangCount}, Pekerja: ${report.pekerjaCount})
                                                        
                                                        *Uraian Pekerjaan:*
                                                        ${report.workDescription}
                                                        
                                                        *Material Digunakan:*
                                                        ${report.materialsUsedSummary}
                                                        
                                                        *Alat Berat:*
                                                        ${report.heavyEquipmentUsedSummary}
                                                        
                                                        *Kendala & Tindakan:*
                                                        ${report.obstaclesIssues}
                                                        Solusi: ${report.solutionsActionTaken}
                                                        
                                                        Status: ${report.status} (${report.createdByName})
                                                    """.trimIndent()

                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Laporan Harian", shareText))
                                                    onShowMessage("Format laporan berhasil disalin ke clipboard!")
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Salin WhatsApp",
                                                    tint = OrangePrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            // Approval button (CEO, PM, Project Leader)
                                            val canApprove = currentUser.role == UserRole.CEO || currentUser.role == UserRole.PROJECT_MANAGER ||
                                                    currentUser.role == UserRole.PROJECT_LEADER_GPA || currentUser.role == UserRole.PROJECT_LEADER_GOR ||
                                                    currentUser.role == UserRole.PROJECT_LEADER_MYKOST || currentUser.role == UserRole.PROJECT_LEADER_LO_VILLA
                                            if (report.status != "APPROVED" && canApprove) {
                                                Button(
                                                    onClick = {
                                                        reportToApprove = report
                                                        approveNotesText = "Disetujui. Mutu & volume pekerjaan sesuai spek teknis."
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("approve_btn_${report.id}")
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Setujui", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab 1: Laporan Mingguan
                if (weeklyReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada laporan mingguan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
                    ) {
                        items(weeklyReports, key = { it.id }) { week ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = week.weekTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (week.deviationPercent >= 0) SuccessGreenContainer else DangerRedContainer
                                        ) {
                                            Text(
                                                text = if (week.deviationPercent >= 0) "+${week.deviationPercent}% (Ahead)" else "${week.deviationPercent}% (Delayed)",
                                                color = if (week.deviationPercent >= 0) SuccessOnGreenContainer else DangerOnRedContainer,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Metrics Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text("Realisasi Kumulatif", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("${week.actualProgressCumulative}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = OrangePrimary)
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text("Rencana Kumulatif", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("${week.plannedProgressCumulative}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text("Progres Minggu Ini", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("+${week.weeklyProgressGain}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = SuccessGreen)
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Ringkasan Pekerjaan Selesai:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateSecondary
                                    )
                                    Text(
                                        text = week.workCompletedSummary,
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Text(
                                        text = "Target Minggu Depan:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary
                                    )
                                    Text(
                                        text = week.targetNextWeek,
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    if (week.riskEvaluation.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AmberAccentContainer.copy(alpha = 0.5f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "🛡️ Evaluasi Risiko: ${week.riskEvaluation}",
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(8.dp),
                                                color = AmberOnAccentContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Site Manager Approval Dialog
    reportToApprove?.let { rep ->
        AlertDialog(
            onDismissRequest = { reportToApprove = null },
            title = { Text("Persetujuan Manajer Lapangan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Menyetujui laporan harian: ${rep.reportNumber}")
                    OutlinedTextField(
                        value = approveNotesText,
                        onValueChange = { approveNotesText = it },
                        label = { Text("Catatan / Instruksi Manajer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApproveReport(rep.id, approveNotesText)
                        reportToApprove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Setujui Laporan")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToApprove = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
