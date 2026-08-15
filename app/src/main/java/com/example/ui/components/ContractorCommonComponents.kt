package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return formatter.format(amount).replace("Rp", "Rp ")
}

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
    return sdf.format(Date(millis))
}

fun formatShortDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
    return sdf.format(Date(millis))
}

@Composable
fun ContractorTopBar(
    currentProject: ProjectEntity?,
    allProjects: List<ProjectEntity>,
    currentUser: UserProfile,
    currentTab: com.example.ui.viewmodel.AppNavTab,
    unreadNotificationCount: Int = 0,
    onSelectTab: (com.example.ui.viewmodel.AppNavTab) -> Unit,
    onSelectProject: (Long) -> Unit,
    onOpenRoleSelector: () -> Unit,
    onOpenBroadcast: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    var expandedProjectDropdown by remember { mutableStateOf(false) }

    Surface(
        color = AtmaterraDarkGreen,
        tonalElevation = 4.dp,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 10.dp, bottom = 8.dp)
        ) {
            // Row 1: Brand & Project info & Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Header with Project Dropdown
                Box {
                    Column(
                        modifier = Modifier
                            .clickable { expandedProjectDropdown = true }
                            .padding(vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AtmaterraGoldDot, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ATMATERRA BUILDWORK",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Pilih Proyek",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${currentProject?.name ?: "Pembangunan Masjid Al-ikhlas GPA Purbalingga"} · Minggu 46",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    DropdownMenu(
                        expanded = expandedProjectDropdown,
                        onDismissRequest = { expandedProjectDropdown = false }
                    ) {
                        allProjects.forEach { proj ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = proj.code,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = AtmaterraDarkGreen
                                        )
                                        Text(
                                            text = proj.name,
                                            fontSize = 11.sp,
                                            color = TextSecondaryDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectProject(proj.id)
                                    expandedProjectDropdown = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = if (proj.id == currentProject?.id) Color(0xFF0F766E) else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }

                // Actions: Notification Bell, Broadcast & Role Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Notification Bell with Badge
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                            .testTag("notification_bell_button")
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = if (unreadNotificationCount > 0) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                contentDescription = "Notifikasi",
                                tint = if (unreadNotificationCount > 0) AtmaterraGoldDot else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            if (unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(DangerRed, CircleShape)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (unreadNotificationCount > 9) "9+" else "$unreadNotificationCount",
                                        color = Color.White,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    // Role pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenRoleSelector() }
                            .testTag("role_switcher_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(AtmaterraAccentGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.role.badge,
                                    color = Color(0xFF78350F),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentUser.role.title.split("/")[0].trim(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Horizontally Scrollable Pills matching screenshot 2
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val navTabs = listOf(
                    com.example.ui.viewmodel.AppNavTab.DASHBOARD,
                    com.example.ui.viewmodel.AppNavTab.M01_RINGKASAN,
                    com.example.ui.viewmodel.AppNavTab.M02_DAFTAR_ISI,
                    com.example.ui.viewmodel.AppNavTab.M03_SURAT_LAPORAN,
                    com.example.ui.viewmodel.AppNavTab.M04_REKAP_PROGRES,
                    com.example.ui.viewmodel.AppNavTab.M05_DETAIL_PROGRES,
                    com.example.ui.viewmodel.AppNavTab.M06_TIME_SCHEDULE,
                    com.example.ui.viewmodel.AppNavTab.M07_PERMASALAHAN_SOLUSI,
                    com.example.ui.viewmodel.AppNavTab.M08_CUACA_HARIAN,
                    com.example.ui.viewmodel.AppNavTab.M09_CUACA_MINGGUAN,
                    com.example.ui.viewmodel.AppNavTab.M10_PERSONIL,
                    com.example.ui.viewmodel.AppNavTab.M11_PERALATAN,
                    com.example.ui.viewmodel.AppNavTab.M12_ABSENSI,
                    com.example.ui.viewmodel.AppNavTab.M13_AKTIVITAS_HARIAN,
                    com.example.ui.viewmodel.AppNavTab.M14_DOKUMENTASI,
                    com.example.ui.viewmodel.AppNavTab.INVENTORY,
                    com.example.ui.viewmodel.AppNavTab.DOCUMENTS
                )

                items(navTabs.size) { index ->
                    val tab = navTabs[index]
                    val isSelected = currentTab == tab
                    Surface(
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectTab(tab) }
                    ) {
                        Text(
                            text = tab.title,
                            color = if (isSelected) AtmaterraDarkGreen else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "OPERASIONAL", "APPROVED", "BERJALAN", "SELESAI", "MASUK", "TERSEDIA" ->
            Triple(SuccessGreenContainer, SuccessOnGreenContainer, if (status == "APPROVED") "DISETUJUI" else status)
        "STANDBY", "SUBMITTED", "RETURN", "BERAWAN" ->
            Triple(OrangePrimaryContainer, OrangeOnPrimaryContainer, if (status == "SUBMITTED") "MENUNGGU ACC" else status)
        "MAINTENANCE", "DRAFT", "HUJAN_RINGAN", "DIGUNAKAN" ->
            Triple(AmberAccentContainer, AmberOnAccentContainer, status)
        "RUSAK", "KRITIS", "KELUAR", "HUJAN_LEBAT", "TERTUNDA", "DALAM_PERBAIKAN", "HABIS" ->
            Triple(DangerRedContainer, DangerOnRedContainer, status.replace("_", " "))
        "CERAH" ->
            Triple(Color(0xFFFEF9C3), Color(0xFF854D0E), "CERAH")
        else ->
            Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accentColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
