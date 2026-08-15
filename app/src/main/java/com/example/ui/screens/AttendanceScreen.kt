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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.WorkerAttendanceEntity
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun AttendanceScreen(
    attendances: List<WorkerAttendanceEntity>,
    currentUser: UserProfile,
    filterCategory: String,
    searchQuery: String,
    onFilterCategoryChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenAddAttendanceDialog: () -> Unit,
    onOpenQuickCrewDialog: () -> Unit
) {
    val categories = listOf(
        "SEMUA",
        "PROJECT_LEADER",
        "SUPERVISOR",
        "MANDOR",
        "TUKANG_BESI",
        "TUKANG_BATU",
        "TUKANG_KAYU",
        "TUKANG_LISTRIK",
        "PEKERJA_HELPER",
        "SUBCON"
    )

    val filteredAttendances = attendances.filter { record ->
        val matchCat = filterCategory == "SEMUA" || record.workerCategory.equals(filterCategory, ignoreCase = true)
        val matchSearch = searchQuery.isBlank() ||
                record.workerName.contains(searchQuery, ignoreCase = true) ||
                record.workAssignedTo.contains(searchQuery, ignoreCase = true)
        matchCat && matchSearch
    }

    val totalHadir = attendances.count { it.status == "HADIR" || it.status == "LEMBUR" }
    val totalLembur = attendances.count { it.status == "LEMBUR" || it.overtimeHours > 0 }
    val totalIzin = attendances.count { it.status == "IZIN_SAKIT" || it.status == "ALFA" }
    val totalWageSpent = attendances.sumOf { it.totalEarnedRp }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenAddAttendanceDialog,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Catat Kehadiran", fontWeight = FontWeight.Bold) },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 65.dp)
                    .testTag("add_attendance_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Summary Metrics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Absensi Tenaga Kerja & Lapangan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Tanggal: Hari Ini • Total ${attendances.size} Terdata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = onOpenQuickCrewDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = SlateSecondary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Presensi Regu", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AttendanceMetricBadge("Hadir", "$totalHadir", SuccessGreen, Modifier.weight(1f))
                        AttendanceMetricBadge("Lembur", "$totalLembur", AmberAccent, Modifier.weight(1f))
                        AttendanceMetricBadge("Izin/Sakit", "$totalIzin", DangerRed, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Estimasi Upah Harian:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(formatRupiah(totalWageSpent), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = OrangePrimary)
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Cari nama tukang, mandor, tugas pekerjaan...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("attendance_search_field")
            )

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = filterCategory.equals(cat, ignoreCase = true)
                    val label = cat.replace("_", " ")
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterCategoryChange(if (isSelected) "SEMUA" else cat) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimaryContainer,
                            selectedLabelColor = OrangeOnPrimaryContainer
                        )
                    )
                }
            }

            // Attendance List
            if (filteredAttendances.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonOff, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(52.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Belum ada data absensi untuk kategori ini", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(filteredAttendances, key = { it.id }) { record ->
                        AttendanceItemCard(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceMetricBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = color)
            Text(label, fontSize = 10.sp, color = color, maxLines = 1)
        }
    }
}

@Composable
fun AttendanceItemCard(record: WorkerAttendanceEntity) {
    val (statusBg, statusText, statusLabel) = when (record.status) {
        "HADIR" -> Triple(SuccessGreenContainer, SuccessOnGreenContainer, "HADIR PENUH")
        "SETENGAH_HARI" -> Triple(OrangePrimaryContainer, OrangeOnPrimaryContainer, "1/2 HARI")
        "LEMBUR" -> Triple(AmberAccentContainer, AmberOnAccentContainer, "LEMBUR (+${record.overtimeHours} JAM)")
        "IZIN_SAKIT" -> Triple(BlueprintBlueContainer, BlueprintBlue, "IZIN / SAKIT")
        else -> Triple(DangerRedContainer, DangerOnRedContainer, "ALFA / LIBUR")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.workerName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.workerName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Posisi: ${record.workerCategory.replace("_", " ")} | Upah: ${formatRupiah(record.totalEarnedRp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(12.dp), tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = record.workAssignedTo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (record.notes.isNotBlank()) {
                    Text("Catatan: ${record.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
