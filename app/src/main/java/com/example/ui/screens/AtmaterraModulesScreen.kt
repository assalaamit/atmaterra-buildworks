package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.formatDate
import com.example.ui.components.formatRupiah
import com.example.ui.components.formatShortDate
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab

// Module Data Models for 14 Sections
data class NavigationModuleItem(
    val code: String,
    val title: String,
    val subtitle: String,
    val tab: AppNavTab,
    val icon: ImageVector
)

val ATMETERRA_14_MODULES = listOf(
    NavigationModuleItem("01", "Ringkasan", "KPI utama & QC sumber", AppNavTab.M01_RINGKASAN, Icons.Default.Assessment),
    NavigationModuleItem("02", "Daftar Isi", "Struktur workbook", AppNavTab.M02_DAFTAR_ISI, Icons.Default.ListAlt),
    NavigationModuleItem("03", "Surat Laporan", "Surat progres mingguan", AppNavTab.M03_SURAT_LAPORAN, Icons.Default.MarkEmailRead),
    NavigationModuleItem("04", "Rekap Progres", "Rekap bobot pekerjaan", AppNavTab.M04_REKAP_PROGRES, Icons.Default.PieChart),
    NavigationModuleItem("05", "Detail Progres", "384 item pekerjaan", AppNavTab.M05_DETAIL_PROGRES, Icons.Default.ChecklistRtl),
    NavigationModuleItem("06", "Time Schedule", "Revisi schedule / kurva S", AppNavTab.M06_TIME_SCHEDULE, Icons.Default.Timeline),
    NavigationModuleItem("07", "Permasalahan & Solusi", "Hambatan, perubahan, addendum", AppNavTab.M07_PERMASALAHAN_SOLUSI, Icons.Default.WarningAmber),
    NavigationModuleItem("08", "Cuaca Harian", "Cuaca 03–08 Agustus", AppNavTab.M08_CUACA_HARIAN, Icons.Default.WbSunny),
    NavigationModuleItem("09", "Cuaca Mingguan", "Rekap cuaca mingguan", AppNavTab.M09_CUACA_MINGGUAN, Icons.Default.CloudQueue),
    NavigationModuleItem("10", "Personil", "Personil & subkon/vendor", AppNavTab.M10_PERSONIL, Icons.Default.PeopleAlt),
    NavigationModuleItem("11", "Peralatan", "Inventaris peralatan", AppNavTab.M11_PERALATAN, Icons.Default.Construction),
    NavigationModuleItem("12", "Absensi", "Absensi man power", AppNavTab.M12_ABSENSI, Icons.Default.Badge),
    NavigationModuleItem("13", "Aktivitas Harian", "Ringkasan aktivitas harian", AppNavTab.M13_AKTIVITAS_HARIAN, Icons.Default.EventNote),
    NavigationModuleItem("14", "Dokumentasi", "Foto pekerjaan 03–08 Agustus", AppNavTab.M14_DOKUMENTASI, Icons.Default.PhotoCamera)
)

// Header Component for Sub-Screens
@Composable
fun ModuleScreenHeader(
    moduleNumber: String,
    title: String,
    subtitle: String,
    projectName: String,
    weekInfo: String,
    onBackToDashboard: () -> Unit
) {
    Surface(
        color = AtmaterraDarkGreen,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBackToDashboard,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        color = Color(0xFF0F766E),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = moduleNumber,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitle,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(AtmaterraGoldDot, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = weekInfo,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// 01_RINGKASAN SCREEN
@Composable
fun M01RingkasanScreen(
    currentProject: ProjectEntity?,
    weeklyReports: List<WeeklyReportEntity>,
    sCurveMilestones: List<SCurveMilestoneEntity>,
    attendances: List<WorkerAttendanceEntity>,
    onBack: () -> Unit
) {
    val completedMilestones = sCurveMilestones.filter { it.isCompleted }
    val latestMilestone = completedMilestones.lastOrNull()
    val planned = latestMilestone?.plannedCumulativePercent ?: currentProject?.currentPlannedProgress ?: 76.8f
    val actual = latestMilestone?.actualCumulativePercent ?: currentProject?.currentActualProgress ?: 78.4f
    val deviation = actual - planned

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "01",
                title = "Ringkasan Eksekutif",
                subtitle = "KPI utama & QC sumber",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "Minggu 46",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Executive KPI Cards
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
                                    .background(AtmaterraAccentGold, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RINGKASAN PROGRES & DEVILASI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimaryDark
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Planned
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Rencana", fontSize = 11.sp, color = TextSecondaryDark)
                                    Text(
                                        "${String.format("%.1f", planned)}%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Text("Target Minggu 46", fontSize = 10.sp, color = TextSecondaryDark)
                                }
                            }
                            // Actual
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Realisasi Fisik", fontSize = 11.sp, color = Color(0xFF047857))
                                    Text(
                                        "${String.format("%.1f", actual)}%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                    Text("Kumulatif Lapangan", fontSize = 10.sp, color = Color(0xFF047857))
                                }
                            }
                            // Deviation
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (deviation >= 0) Color(0xFFFEF3C7) else DangerRedContainer),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Deviasi", fontSize = 11.sp, color = if (deviation >= 0) Color(0xFF92400E) else DangerOnRedContainer)
                                    Text(
                                        "${if (deviation >= 0) "+" else ""}${String.format("%.1f", deviation)}%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (deviation >= 0) Color(0xFF92400E) else DangerOnRedContainer
                                    )
                                    Text(if (deviation >= 0) "Ahead / Maju" else "Behind", fontSize = 10.sp, color = if (deviation >= 0) Color(0xFF92400E) else DangerOnRedContainer)
                                }
                            }
                        }
                    }
                }

                // QC & Mutu Sumber
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
                                    .background(Color(0xFF0F766E), RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KONTROL KUALITAS & QC SUMBER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimaryDark
                            )
                        }

                        val qcItems = listOf(
                            Triple("Uji Kuat Tekan Beton (Silinder 7, 14, 28 Hari)", "fc' 25 MPa (K-300) tercapai 27.8 MPa", "LULUS / SESUAI"),
                            Triple("Inspeksi Pemasangan Bekisting & Pembesian D16", "Toleransi selimut beton 2.5cm dipenuhi", "APPROVED MK"),
                            Triple("Uji Tarik Baja Tulangan Krakatau Steel", "Tensile Strength fy 420 MPa", "SERTIFIKAT VALID"),
                            Triple("Slump Test Ready Mix Pengecoran Kubah", "Slump 12 ± 2 cm, flowability optimal", "LULUS UJI")
                        )

                        qcItems.forEach { (title, desc, status) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimaryDark)
                                    Text(desc, fontSize = 11.sp, color = TextSecondaryDark)
                                }
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        status,
                                        color = Color(0xFF166534),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Nilai Kontrak & Informasi Proyek
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("INFORMASI KONTRAK & LEGALITAS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                        Divider(color = Color(0xFFE2E8F0))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kontraktor Pelaksana", fontSize = 12.sp, color = TextSecondaryDark)
                            Text("PT Atmaterra Buildwork Indonesia", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F766E))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pemilik Proyek (Client)", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(currentProject?.clientName ?: "DKM Masjid Al-Ikhlas GPA", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nilai Kontrak", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(formatRupiah(currentProject?.contractValueRp ?: 3_850_000_000L), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Masa Pelaksanaan", fontSize = 12.sp, color = TextSecondaryDark)
                            Text("${currentProject?.startDate} s/d ${currentProject?.targetEndDate}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// 02_DAFTAR_ISI SCREEN
@Composable
fun M02DaftarIsiScreen(
    onSelectModule: (AppNavTab) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "02",
                title = "Daftar Isi Laporan",
                subtitle = "Struktur workbook proyek",
                projectName = "Atmaterra Buildwork",
                weekInfo = "14 Sheet Aktif",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "STRUKTUR WORKBOOK DOKUMENTASI LENGKAP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF0F766E)
                )

                ATMETERRA_14_MODULES.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectModule(item.tab) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.code,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                    Text(item.subtitle, fontSize = 11.sp, color = TextSecondaryDark)
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 03_SURAT_LAPORAN SCREEN
@Composable
fun M03SuratLaporanScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "03",
                title = "Surat Laporan Progres",
                subtitle = "Surat progres mingguan resmi",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "Minggu 46",
                onBackToDashboard = onBack
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Formal Letterhead
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "PT ATMATERRA BUILDWORK INDONESIA",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = AtmaterraDarkGreen
                        )
                        Text(
                            "General Contractor, Civil Engineering & Interior Architecture",
                            fontSize = 10.sp,
                            color = TextSecondaryDark
                        )
                        Text(
                            "Kompleks Perkantoran GPA No. 18, Jawa Tengah | Telp: (0281) 684210",
                            fontSize = 9.sp,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(thickness = 2.dp, color = AtmaterraDarkGreen)
                    }

                    // Metadata Surat
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Nomor   : 046/ATMA-LAP/VIII/2026", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("Lampiran: 1 (Satu) Berkas Laporan Lengkap", fontSize = 11.sp)
                            Text("Perihal : Laporan Progres Mingguan Ke-46", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("14 Agustus 2026", fontSize = 11.sp, color = TextSecondaryDark)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Kepada Yth:\nTim Pengawas & Panitia Pembangunan\n${currentProject?.name ?: "Pembangunan Masjid Al-ikhlas GPA Purbalingga"}\nDi Tempat",
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Text(
                        "Dengan hormat,\n\nBersama surat ini kami sampaikan Laporan Kemajuan Pekerjaan (Weekly Progress Report) Periode Minggu Ke-46 (03 Agustus s/d 08 Agustus 2026) untuk proyek yang kami laksanakan:",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 16.sp
                    )

                    // Summary Box
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Progres Rencana Kumulatif   : 76.80 %", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("• Progres Realisasi Kumulatif : 78.40 %", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                            Text("• Deviasi Progres              : + 1.60 % (Ahead / Lebih Cepat)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            Text("• Status Tenaga Kerja          : 28 Orang (Hadir Penuh)", fontSize = 11.sp)
                            Text("• Kondisi Cuaca Lapangan       : 5 Hari Cerah, 1 Hari Hujan Ringan", fontSize = 11.sp)
                        }
                    }

                    Text(
                        "Demikian surat pengantar laporan mingguan ini kami sampaikan. Atas perhatian dan kerja samanya kami ucapkan terima kasih.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Signatures
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Diajukan Oleh:", fontSize = 10.sp, color = TextSecondaryDark)
                            Text("Project Manager", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(36.dp))
                            Text("Teguh Pambudi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AtmaterraDarkGreen)
                            Text("PT Atmaterra Buildwork Indonesia", fontSize = 9.sp, color = TextSecondaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Mengetahui & Menyetujui:", fontSize = 10.sp, color = TextSecondaryDark)
                            Text("Chief Executive Officer (CEO)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(36.dp))
                            Text("Aan Amri Setiawan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("PT Atmaterra Buildwork Indonesia", fontSize = 9.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }
    }
}

// 04_REKAP_PROGRES SCREEN
@Composable
fun M04RekapProgresScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    val divisionList = listOf(
        DivisionProgress("I", "Pekerjaan Persiapan & K3 Lapangan", 3.50f, 3.50f, 3.50f, 0.0f, "100% Selesai"),
        DivisionProgress("II", "Pekerjaan Pondasi Bore Pile & Struktur Bawah", 18.20f, 18.20f, 18.20f, 0.0f, "100% Selesai"),
        DivisionProgress("III", "Pekerjaan Struktur Utama (Kolom, Balok, Plat Lantai 1-2)", 28.50f, 28.50f, 28.50f, 0.0f, "100% Selesai"),
        DivisionProgress("IV", "Pekerjaan Rangka Kubah Utama & Konstruksi Galvalum", 16.80f, 14.20f, 15.60f, 1.40f, "Dalam Pengerjaan"),
        DivisionProgress("V", "Pekerjaan Arsitektur, Pasangan Krawangan GRC & Marmer", 21.00f, 8.40f, 9.80f, 1.40f, "Dalam Pengerjaan"),
        DivisionProgress("VI", "Pekerjaan MEP, Instalasi Sound System & Tempat Wudhu", 12.00f, 2.80f, 2.80f, 0.0f, "Mulai Pengerjaan")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "04",
                title = "Rekap Bobot Progres",
                subtitle = "Rekap bobot per divisi pekerjaan",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "6 Divisi Utama",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                divisionList.forEach { div ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(color = Color(0xFF0F766E), shape = RoundedCornerShape(4.dp)) {
                                        Text(div.code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(div.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark, maxLines = 2)
                                }
                                Surface(
                                    color = if (div.actualGain >= div.weight) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        div.status,
                                        color = if (div.actualGain >= div.weight) Color(0xFF166534) else Color(0xFF92400E),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            LinearProgressIndicator(
                                progress = { (div.actualGain / div.weight).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (div.actualGain >= div.weight) Color(0xFF16A34A) else Color(0xFF0F766E),
                                trackColor = Color(0xFFE2E8F0)
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Bobot: ${String.format("%.2f", div.weight)}%", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("Minggu Lalu: ${String.format("%.2f", div.prevGain)}%", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("Kumulatif: ${String.format("%.2f", div.actualGain)}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DivisionProgress(
    val code: String,
    val name: String,
    val weight: Float,
    val prevGain: Float,
    val actualGain: Float,
    val dev: Float,
    val status: String
)

// 05_DETAIL_PROGRES SCREEN
@Composable
fun M05DetailProgresScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDivisionFilter by remember { mutableStateOf("SEMUA") }

    val sample384Items = remember {
        listOf(
            DetailProgressItem("3.1.1", "Pembesian Kolom K1 60x60 Ulir D16 BJTS 420B", "Struktur", 24.0, "Titik", 4.20f, 100f, 100f, "SELESAI"),
            DetailProgressItem("3.1.2", "Pemasangan Bekisting Kolom Polyfilm 12mm", "Struktur", 185.0, "M2", 2.80f, 100f, 100f, "SELESAI"),
            DetailProgressItem("3.1.3", "Pengecoran Kolom K1 Readymix K-300 NFA", "Struktur", 42.0, "M3", 5.60f, 100f, 100f, "SELESAI"),
            DetailProgressItem("4.1.1", "Fabrikasi Rangka Baja Pipa Kubah Utama D12m", "Kubah", 1.0, "Unit", 7.50f, 100f, 100f, "SELESAI"),
            DetailProgressItem("4.1.2", "Ereksi & Setting Rangka Kubah Utama Galvalum", "Kubah", 1.0, "Unit", 5.20f, 85f, 92f, "BERJALAN"),
            DetailProgressItem("4.1.3", "Pemasangan Panel Enamel Kubah Motif Hijau Gold", "Kubah", 280.0, "M2", 4.10f, 40f, 55f, "BERJALAN"),
            DetailProgressItem("5.1.1", "Pemasangan Panel Krawangan GRC Fasad Islami", "Arsitektur", 120.0, "M2", 4.80f, 30f, 45f, "BERJALAN"),
            DetailProgressItem("5.1.2", "Pemasangan Marmer Ujung Pandang Lantai Sholat", "Arsitektur", 450.0, "M2", 8.20f, 15f, 25f, "BERJALAN"),
            DetailProgressItem("5.2.1", "Pekerjaan Ornamen Kaligrafi Mihrab Timbul GRC", "Arsitektur", 35.0, "M2", 3.10f, 0f, 10f, "BERJALAN"),
            DetailProgressItem("6.1.1", "Instalasi Sound System Acoustic Line Array Masjid", "MEP", 1.0, "Paket", 2.40f, 0f, 15f, "BERJALAN"),
            DetailProgressItem("6.1.2", "Instalasi Kran Wudhu Stainless & Pipa PPR Air Bersih", "MEP", 32.0, "Titik", 1.90f, 20f, 35f, "BERJALAN")
        )
    }

    val filtered = sample384Items.filter {
        (selectedDivisionFilter == "SEMUA" || it.division == selectedDivisionFilter) &&
        (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.wbs.contains(searchQuery))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "05",
                title = "Detail Item Progres",
                subtitle = "384 item pekerjaan WBS",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "384 Items",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari item pekerjaan atau WBS...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Category Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val cats = listOf("SEMUA", "Struktur", "Kubah", "Arsitektur", "MEP")
                    items(cats) { cat ->
                        FilterChip(
                            selected = selectedDivisionFilter == cat,
                            onClick = { selectedDivisionFilter = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Menampilkan ${filtered.size} dari 384 item WBS", fontSize = 11.sp, color = TextSecondaryDark)

                filtered.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                        Text(item.wbs, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.division, fontSize = 10.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.SemiBold)
                                }
                                Surface(
                                    color = if (item.actualPercent >= 100f) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "${String.format("%.0f", item.actualPercent)}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.actualPercent >= 100f) Color(0xFF166534) else Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)

                            LinearProgressIndicator(
                                progress = { (item.actualPercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (item.actualPercent >= 100f) Color(0xFF16A34A) else Color(0xFF0F766E),
                                trackColor = Color(0xFFF1F5F9)
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Volume: ${item.volume} ${item.unit}", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("Bobot: ${item.weight}%", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("Lalu: ${item.prevPercent}% → Ini: ${item.actualPercent}%", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DetailProgressItem(
    val wbs: String,
    val name: String,
    val division: String,
    val volume: Double,
    val unit: String,
    val weight: Float,
    val prevPercent: Float,
    val actualPercent: Float,
    val status: String
)

// 06_TIME_SCHEDULE SCREEN (KURVA S)
@Composable
fun M06TimeScheduleScreen(
    currentProject: ProjectEntity?,
    sCurveMilestones: List<SCurveMilestoneEntity>,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "06",
                title = "Time Schedule & Kurva S",
                subtitle = "Revisi schedule / kurva S progres",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "Minggu 46",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // S Curve Visual Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GRAFIK KURVA S REVISI MINGGU 46", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF2563EB), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rencana", fontSize = 10.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF16A34A), CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Aktual", fontSize = 10.sp)
                                }
                            }
                        }

                        // Curve Table / Bar representation
                        val sampleWeeks = listOf(
                            Triple("M-42", 68.5f, 69.2f),
                            Triple("M-43", 71.0f, 72.4f),
                            Triple("M-44", 73.2f, 74.8f),
                            Triple("M-45", 75.0f, 76.5f),
                            Triple("M-46", 76.8f, 78.4f),
                            Triple("M-47 (Target)", 79.5f, 0f),
                            Triple("M-48 (Target)", 82.5f, 0f)
                        )

                        sampleWeeks.forEach { (week, plan, act) ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(week, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Rencana: $plan% | Realisasi: ${if (act > 0) "$act%" else "-"}", fontSize = 10.sp, color = TextSecondaryDark)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LinearProgressIndicator(
                                        progress = { plan / 100f },
                                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFF2563EB),
                                        trackColor = Color(0xFFE2E8F0)
                                    )
                                    if (act > 0) {
                                        LinearProgressIndicator(
                                            progress = { act / 100f },
                                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                            color = Color(0xFF16A34A),
                                            trackColor = Color(0xFFE2E8F0)
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

// 07_PERMASALAHAN_SOLUSI SCREEN
@Composable
fun M07PermasalahanSolusiScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    val issueList = listOf(
        ProjectIssueItem(
            id = "ISS-01",
            title = "Keterlambatan Pengiriman Marmer Ujung Pandang 450 M2",
            category = "Material",
            impact = "Pekerjaan finishing lantai sholat tertunda 3 hari",
            solution = "Eskalasi ke supplier utama Makassar via udara express & penyesuaian urutan kerja ke area selasar luar",
            status = "SELESAI / TERATASI",
            pic = "Rian Hidayat (Logistik)"
        ),
        ProjectIssueItem(
            id = "ISS-02",
            title = "Hujan Deras Saat Pengecoran Balok Ring Kubah",
            category = "Cuaca Lapangan",
            impact = "Pengecoran terhenti 2.5 jam di sore hari",
            solution = "Penutupan terpal kanopi kedap air, penambahan accelerator curing compound & lembur 3 jam malam hari",
            status = "SELESAI / TERATASI",
            pic = "Hendro Wibowo (SPV)"
        ),
        ProjectIssueItem(
            id = "ISS-03",
            title = "Addendum Perubahan Ornamen Kaligrafi Mihrab GRC",
            category = "Perubahan Desain (Owner)",
            impact = "Penyelarasan shop drawing ornamen timbul ayat Al-Qur'an",
            solution = "Penerbitan CCO/Addendum No. 02, disetujui DKM & pencetakan moulding baru di workshop GRC",
            status = "DALAM PROSES",
            pic = "Ir. Wahyu Pratama (SM)"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "07",
                title = "Permasalahan & Solusi",
                subtitle = "Hambatan, perubahan, addendum",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "3 Log Terpantau",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                issueList.forEach { issue ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                        Text(issue.id, color = Color(0xFF92400E), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(issue.category, fontSize = 10.sp, color = TextSecondaryDark)
                                }
                                Surface(
                                    color = if (issue.status.contains("SELESAI")) Color(0xFFDCFCE7) else Color(0xFFDBEAFE),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        issue.status,
                                        color = if (issue.status.contains("SELESAI")) Color(0xFF166534) else Color(0xFF1E40AF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(issue.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)

                            Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("⚠️ Dampak: ${issue.impact}", fontSize = 11.sp, color = Color(0xFFB45309))
                                    Text("✅ Solusi & Mitigasi: ${issue.solution}", fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Text("PIC: ${issue.pic}", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ProjectIssueItem(
    val id: String,
    val title: String,
    val category: String,
    val impact: String,
    val solution: String,
    val status: String,
    val pic: String
)

// 08_CUACA_HARIAN SCREEN
@Composable
fun M08CuacaHarianScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    val dailyWeatherList = listOf(
        DailyWeatherRow("Senin, 03 Agu", "Cerah", "Cerah", "Berawan", 0, "Pekerjaan pembesian & pasang krawangan normal"),
        DailyWeatherRow("Selasa, 04 Agu", "Cerah", "Cerah", "Cerah", 0, "Pengecoran balok ring lantai 2 lancar"),
        DailyWeatherRow("Rabu, 05 Agu", "Cerah", "Berawan", "Hujan Ringan (45m)", 1, "Pekerjaan dipindahkan ke interior masjid"),
        DailyWeatherRow("Kamis, 06 Agu", "Cerah", "Cerah", "Cerah", 0, "Pemasangan rangka kubah utama"),
        DailyWeatherRow("Jumat, 07 Agu", "Cerah", "Cerah", "Berawan", 0, "Plesteran dinding & pemasangan pipa MEP"),
        DailyWeatherRow("Sabtu, 08 Agu", "Cerah", "Cerah", "Cerah", 0, "Lembur pengecoran & pembersihan area")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "08",
                title = "Log Cuaca Harian",
                subtitle = "Cuaca 03–08 Agustus 2026",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "6 Hari Kerja",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                dailyWeatherList.forEach { row ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(row.date, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                                Surface(
                                    color = if (row.rainHours == 0) Color(0xFFECFDF5) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        if (row.rainHours == 0) "100% Efektif" else "Hujan 45 mnt",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (row.rainHours == 0) Color(0xFF047857) else Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Pagi", fontSize = 9.sp, color = TextSecondaryDark)
                                        Text(row.morning, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Siang", fontSize = 9.sp, color = TextSecondaryDark)
                                        Text(row.noon, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text("Sore", fontSize = 9.sp, color = TextSecondaryDark)
                                        Text(row.evening, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Text("Catatan Lapangan: ${row.note}", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }
    }
}

data class DailyWeatherRow(
    val date: String,
    val morning: String,
    val noon: String,
    val evening: String,
    val rainHours: Int,
    val note: String
)

// 09_CUACA_MINGGUAN SCREEN
@Composable
fun M09CuacaMingguanScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "09",
                title = "Rekap Cuaca Mingguan",
                subtitle = "Rekapitulasi jam efektif vs hujan",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "Minggu 46",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("RINGKASAN EFISIENSI KERJA MINGGU 46", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Hari Efektif", fontSize = 11.sp, color = Color(0xFF047857))
                                    Text("5.5 Hari", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                    Text("Dari 6 hari kalender", fontSize = 10.sp, color = Color(0xFF047857))
                                }
                            }
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Jam Hujan", fontSize = 11.sp, color = Color(0xFF92400E))
                                    Text("0.75 Jam", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                    Text("Terganti via lembur", fontSize = 10.sp, color = Color(0xFF92400E))
                                }
                            }
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Efisiensi", fontSize = 11.sp, color = TextPrimaryDark)
                                    Text("98.4%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                                    Text("Kondisi Sangat Baik", fontSize = 10.sp, color = TextSecondaryDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 10_PERSONIL SCREEN
@Composable
fun M10PersonilScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    val personilList = listOf(
        PersonilItem("Aan Amri Setiawan", "CEO", "Direksi & Manajemen", "0812-1111-2222", "Penanggung jawab utama PT Atmaterra Buildwork Indonesia"),
        PersonilItem("Isabella Riyanti", "Finance", "Keuangan & Akuntansi", "0813-2222-3333", "Manajemen arus kas, termin pencairan & verifikasi invoice"),
        PersonilItem("Teguh Pambudi", "Project Manager", "Manajemen Proyek", "0811-3333-4444", "Pengawasan menyeluruh 4 proyek aktif & evaluasi deviasi"),
        PersonilItem("Sugiarto", "Project Leader Masjid GPA", "Pimpinan Proyek Lapangan", "0812-4444-5555", "Penanggung jawab Pembangunan Masjid Al-ikhlas GPA"),
        PersonilItem("Awal", "Project Leader Kost GOR & My Kost", "Pimpinan Proyek Lapangan", "0813-5555-6666", "Penanggung jawab Rehab Kost GOR & My Kost DukuhWaluh"),
        PersonilItem("Toni", "Project Leader Lo Villa", "Pimpinan Proyek Lapangan", "0857-7777-8888", "Penanggung jawab Rehab Rumah Lo Villa"),
        PersonilItem("Rusmanto", "Logistic", "Logistik & Gudang", "0821-8888-9999", "Pengendalian stok material masuk/keluar & pengadaan"),
        PersonilItem("Ibnu Abas", "Administrator", "Administrasi & Legal", "0819-9999-0000", "Pengarsipan surat jalan, kontrak, perizinan & data sistem")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "10",
                title = "Personil & Subkontraktor",
                subtitle = "Struktur tim proyek PT Atmaterra Buildwork",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "8 Entitas",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                personilList.forEach { p ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = if (p.category == "Subkontraktor") Color(0xFFFEF3C7) else Color(0xFFECFDF5),
                                    shape = CircleShape,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            p.name.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (p.category == "Subkontraktor") Color(0xFF92400E) else Color(0xFF047857)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(p.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                                    Text("${p.position} • ${p.category}", fontSize = 10.sp, color = Color(0xFF0F766E))
                                    Text(p.task, fontSize = 10.sp, color = TextSecondaryDark)
                                }
                            }
                            Text(p.phone, fontSize = 10.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }
    }
}

data class PersonilItem(
    val name: String,
    val position: String,
    val category: String,
    val phone: String,
    val task: String
)

// 11_PERALATAN SCREEN
@Composable
fun M11PeralatanScreen(
    currentProject: ProjectEntity?,
    equipments: List<EquipmentEntity>,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "11",
                title = "Inventaris Peralatan",
                subtitle = "Status kesiapan alat & mesin",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "${equipments.size} Unit",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                equipments.forEach { eq ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = Color(0xFF0F766E), shape = RoundedCornerShape(4.dp)) {
                                        Text(eq.code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(eq.category, fontSize = 10.sp, color = TextSecondaryDark)
                                }
                                Surface(
                                    color = if (eq.status == "OPERASIONAL") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        eq.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (eq.status == "OPERASIONAL") Color(0xFF166534) else Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(eq.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("HM: ${eq.currentHourMeter} Jam", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("Operator: ${eq.operatorName}", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("Servis: ${eq.lastServiceDate}", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 12_ABSENSI SCREEN
@Composable
fun M12AbsensiScreen(
    currentProject: ProjectEntity?,
    attendances: List<WorkerAttendanceEntity>,
    onBack: () -> Unit
) {
    val totalWorkers = attendances.size
    val hadirCount = attendances.count { it.status == "HADIR" || it.status == "LEMBUR" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "12",
                title = "Absensi Tenaga Kerja",
                subtitle = "Man power harian & mingguan",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "$hadirCount / $totalWorkers Hadir",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                attendances.forEach { att ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(att.workerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                                Text("${att.workerCategory} • ${att.workAssignedTo}", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                            Surface(
                                color = if (att.status == "HADIR" || att.status == "LEMBUR") Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    att.status,
                                    color = if (att.status == "HADIR" || att.status == "LEMBUR") Color(0xFF166534) else Color(0xFF991B1B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 13_AKTIVITAS_HARIAN SCREEN
@Composable
fun M13AktivitasHarianScreen(
    currentProject: ProjectEntity?,
    dailyReports: List<DailyReportEntity>,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "13",
                title = "Aktivitas Harian Proyek",
                subtitle = "Ringkasan aktivitas harian lapangan",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "${dailyReports.size} Laporan",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                dailyReports.forEach { rep ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(rep.reportNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F766E))
                                Text(formatShortDate(rep.reportDateMillis), fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Text(rep.workDescription, fontSize = 12.sp, color = TextPrimaryDark)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tenaga Kerja: ${rep.totalWorkers} Orang", fontSize = 10.sp, color = TextSecondaryDark)
                                Text("Penyusun: ${rep.createdByName}", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 14_DOKUMENTASI SCREEN
@Composable
fun M14DokumentasiScreen(
    currentProject: ProjectEntity?,
    onBack: () -> Unit
) {
    val photoDocs = listOf(
        PhotoDocItem("DOK-01", "08 Agu 2026", "Ereksi Rangka Kubah Utama Galvalum D12m", "Kubah", "Penyetelan pipa lengkung & pengelasan titik simpul oleh subkon"),
        PhotoDocItem("DOK-02", "07 Agu 2026", "Pemasangan Panel Krawangan GRC Fasad Timur", "Arsitektur", "Pengangkatan panel GRC menggunakan Tower Hoist ke elevasi +8.50m"),
        PhotoDocItem("DOK-03", "06 Agu 2026", "Pengecoran Balok Ring Kolom Lantai 2", "Struktur", "Uji slump test 12cm & pemadatan beton dengan vibrator Mikasa"),
        PhotoDocItem("DOK-04", "05 Agu 2026", "Instalasi Keramik & Kran Stainless Tempat Wudhu Pria", "Sanitair & MEP", "Pemasangan jalur pipa air bersih PPR & keramik anti selip 30x30"),
        PhotoDocItem("DOK-05", "04 Agu 2026", "Pabrikasi Besi Tulangan D16 di Yard Terbuka", "Pembesian", "Pemotongan & tekuk begel sengkang sesuai BBS bar bending schedule"),
        PhotoDocItem("DOK-06", "03 Agu 2026", "Toolbox Meeting K3 & Safety Briefing Pagi", "K3", "Pemeriksaan full body harness & helm proyek seluruh tenaga kerja")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModuleScreenHeader(
                moduleNumber = "14",
                title = "Dokumentasi Foto Proyek",
                subtitle = "Foto pekerjaan 03–08 Agustus 2026",
                projectName = currentProject?.name ?: "Proyek",
                weekInfo = "${photoDocs.size} Foto Terarsip",
                onBackToDashboard = onBack
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                photoDocs.forEach { doc ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Image placeholder box with blueprint styling
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(AtmaterraForestGreen, AtmaterraDarkGreen)
                                        ),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        doc.id,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        "PT Atmaterra Buildwork • Foto Lapangan",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(4.dp)) {
                                    Text(doc.category, color = Color(0xFF047857), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Text(doc.date, fontSize = 10.sp, color = TextSecondaryDark)
                            }

                            Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                            Text(doc.desc, fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }
        }
    }
}

data class PhotoDocItem(
    val id: String,
    val date: String,
    val title: String,
    val category: String,
    val desc: String
)
