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
import com.example.data.model.ProjectRapItemEntity
import com.example.data.model.ProjectScheduleItemEntity
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRapScreen(
    schedules: List<ProjectScheduleItemEntity>,
    rapItems: List<ProjectRapItemEntity>,
    currentUser: UserProfile,
    onUpdateScheduleProgress: (ProjectScheduleItemEntity) -> Unit,
    onUpdateRapItem: (ProjectRapItemEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Timeschedule & Timeline, 1: RAP (Rencana Anggaran Pelaksanaan)
    var selectedCategoryFilter by remember { mutableStateOf("SEMUA") }

    val scheduleCategories = listOf("SEMUA", "Pekerjaan Persiapan", "Struktur Bawah", "Struktur Atas", "Arsitektur & Pasangan", "MEP & Utilitas", "Finishing")
    val rapCategories = listOf("SEMUA", "MATERIAL", "ALAT_BERAT", "UPAH_TENAGA", "SUBKONTRAKTOR", "OVERHEAD_K3")

    // Filtered lists
    val filteredSchedules = schedules.filter { item ->
        selectedCategoryFilter == "SEMUA" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
    }

    val filteredRapItems = rapItems.filter { item ->
        selectedCategoryFilter == "SEMUA" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
    }

    // RAP totals
    val totalBudgetRap = rapItems.sumOf { it.budgetRapRp }
    val totalActualCost = rapItems.sumOf { it.actualCostRp }
    val costVariance = totalBudgetRap - totalActualCost
    val isUnderBudget = costVariance >= 0

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = OrangePrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        selectedCategoryFilter = "SEMUA"
                    },
                    text = { Text("Timeline & Timeschedule", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedCategoryFilter = "SEMUA"
                    },
                    text = { Text("RAP (Rencana Anggaran)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filterList = if (selectedTab == 0) scheduleCategories else rapCategories
            items(filterList) { cat ->
                val isSelected = selectedCategoryFilter.equals(cat, ignoreCase = true)
                val label = cat.replace("_", " ")
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryFilter = if (isSelected) "SEMUA" else cat },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimaryContainer,
                        selectedLabelColor = OrangeOnPrimaryContainer
                    )
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> {
                // TIMESCHEDULE LIST
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Ringkasan Progress Fisik Proyek (WBS Breakdown)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(6.dp))

                                val avgProgress = if (schedules.isNotEmpty()) schedules.sumOf { (it.actualProgressPercent * it.weightPercent).toDouble() } / 100.0 else 0.0
                                Text("Progres Kumulatif Terbobot: ${"%.1f".format(avgProgress)}%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = OrangePrimary)

                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = (avgProgress / 100f).toFloat().coerceIn(0f, 1f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = OrangePrimary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }

                    items(filteredSchedules, key = { it.id }) { item ->
                        ScheduleItemCard(
                            item = item,
                            onUpdateClick = { onUpdateScheduleProgress(item) }
                        )
                    }
                }
            }

            1 -> {
                // RAP LIST
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    // RAP Summary Metrics Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Monitoring Rencana Anggaran Pelaksanaan (RAP)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RapSummaryMetric("Total RAP", formatRupiah(totalBudgetRap), BlueprintBlue, Modifier.weight(1f))
                                    RapSummaryMetric("Realisasi Biaya", formatRupiah(totalActualCost), OrangePrimary, Modifier.weight(1f))
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = if (isUnderBudget) SuccessGreenContainer else DangerRedContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isUnderBudget) "Efisiensi / Sisa Anggaran RAP:" else "Over Budget (Pembengkakan Biaya):",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUnderBudget) SuccessOnGreenContainer else DangerOnRedContainer
                                        )
                                        Text(
                                            text = formatRupiah(Math.abs(costVariance)),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Black,
                                            color = if (isUnderBudget) SuccessOnGreenContainer else DangerOnRedContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(filteredRapItems, key = { it.id }) { item ->
                        RapItemCard(
                            item = item,
                            onUpdateClick = { onUpdateRapItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RapSummaryMetric(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.10f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(label, fontSize = 11.sp, color = color)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
        }
    }
}

@Composable
fun ScheduleItemCard(
    item: ProjectScheduleItemEntity,
    onUpdateClick: () -> Unit
) {
    val (statusColor, statusLabel) = when (item.status) {
        "SELESAI" -> Pair(SuccessGreen, "SELESAI (100%)")
        "SEDANG_BERJALAN" -> Pair(OrangePrimary, "SEDANG BERJALAN")
        "TERLAMBAT" -> Pair(DangerRed, "TERLAMBAT")
        else -> Pair(SlateSecondary, "BELUM MULAI")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = OrangePrimaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "WBS ${item.wbsCode}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeOnPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.taskName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progres: ${item.actualProgressPercent}% (Rencana: ${item.plannedProgressPercent}%)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Bobot: ${item.weightPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = (item.actualProgressPercent / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (item.actualProgressPercent >= item.plannedProgressPercent) SuccessGreen else DangerRed,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${item.startDate} s/d ${item.endDate} (${item.durationDays} hari)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                OutlinedButton(
                    onClick = onUpdateClick,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update Progres", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun RapItemCard(
    item: ProjectRapItemEntity,
    onUpdateClick: () -> Unit
) {
    val variance = item.budgetRapRp - item.actualCostRp
    val isEfficient = variance >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SlateSecondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.itemCode,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateOnSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.category.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = OrangePrimary
                    )
                }

                Surface(
                    color = if (isEfficient) SuccessGreenContainer else DangerRedContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isEfficient) "Sisa: ${formatRupiah(variance)}" else "Over: ${formatRupiah(Math.abs(variance))}",
                        color = if (isEfficient) SuccessOnGreenContainer else DangerOnRedContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.itemName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Volume: ${item.volume} ${item.unit} @ ${formatRupiah(item.unitPriceRp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cost Comparison
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Anggaran RAP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatRupiah(item.budgetRapRp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Realisasi Biaya", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatRupiah(item.actualCostRp),
                        fontWeight = FontWeight.Bold,
                        color = if (isEfficient) OrangePrimary else DangerRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column {
                    Text("Persentase Serapan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val percent = if (item.budgetRapRp > 0) (item.actualCostRp.toDouble() / item.budgetRapRp.toDouble() * 100.0) else 0.0
                    Text("${"%.1f".format(percent)}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Keterangan: ${item.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onUpdateClick,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ubah Realisasi Biaya", fontSize = 11.sp)
                }
            }
        }
    }
}
