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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationPreferences
import com.example.data.model.ProjectNotificationEntity
import com.example.ui.components.formatDate
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<ProjectNotificationEntity>,
    preferences: NotificationPreferences,
    unreadCount: Int,
    onMarkAsRead: (Long) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onClearAll: () -> Unit,
    onOpenPreferencesDialog: () -> Unit,
    onNavigateToTab: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("SEMUA") }
    val filterTypes = listOf("SEMUA", "BELUM_DIBACA", "MAINTENANCE", "REPORT_DEADLINE", "LOW_STOCK", "PROJECT_STATUS", "TEAM_BROADCAST")

    val displayedNotifications = notifications.filter { notif ->
        when (selectedFilter) {
            "SEMUA" -> true
            "BELUM_DIBACA" -> !notif.isRead
            else -> notif.type.equals(selectedFilter, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Notification Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(OrangePrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = OrangeOnPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pusat Notifikasi & Alarm",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$unreadCount belum dibaca dari ${notifications.size} notifikasi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onOpenPreferencesDialog,
                    modifier = Modifier.testTag("notification_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Pengaturan Notifikasi",
                        tint = OrangePrimary
                    )
                }
            }
        }

        // Quick Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onMarkAllAsRead,
                enabled = unreadCount > 0,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tandai Semua Dibaca", fontSize = 12.sp)
            }

            TextButton(
                onClick = onClearAll,
                enabled = notifications.isNotEmpty(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = DangerRed)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bersihkan", fontSize = 12.sp, color = DangerRed)
            }
        }

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterTypes) { type ->
                val label = when (type) {
                    "SEMUA" -> "Semua (${notifications.size})"
                    "BELUM_DIBACA" -> "Belum Dibaca ($unreadCount)"
                    "MAINTENANCE" -> "Alat & Servis"
                    "REPORT_DEADLINE" -> "Tenggat Laporan"
                    "LOW_STOCK" -> "Stok Menipis"
                    "PROJECT_STATUS" -> "Status Proyek"
                    "TEAM_BROADCAST" -> "Siaran Tim"
                    else -> type
                }
                val isSelected = selectedFilter == type

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = type },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimaryContainer,
                        selectedLabelColor = OrangeOnPrimaryContainer
                    )
                )
            }
        }

        // Notification Items
        if (displayedNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tidak ada notifikasi pada kategori ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(displayedNotifications, key = { it.id }) { notif ->
                    NotificationCard(
                        notification = notif,
                        onMarkRead = { onMarkAsRead(notif.id) },
                        onNavigateToTab = onNavigateToTab
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: ProjectNotificationEntity,
    onMarkRead: () -> Unit,
    onNavigateToTab: (String) -> Unit
) {
    val (icon, iconColor, bgHeader) = when (notification.type) {
        "MAINTENANCE" -> Triple(Icons.Default.Build, AmberAccent, AmberAccentContainer)
        "REPORT_DEADLINE" -> Triple(Icons.Default.AssignmentLate, DangerRed, DangerRedContainer)
        "LOW_STOCK" -> Triple(Icons.Default.Inventory2, DangerRed, DangerRedContainer)
        "PROJECT_STATUS" -> Triple(Icons.Default.ShowChart, BlueprintBlue, BlueprintBlueContainer)
        "TEAM_BROADCAST" -> Triple(Icons.Default.Campaign, OrangePrimary, OrangePrimaryContainer)
        else -> Triple(Icons.Default.Info, SlateSecondary, SlateSecondaryContainer)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarkRead() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!notification.isRead) 2.dp else 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgHeader, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
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
                        text = notification.title,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(OrangePrimary, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(notification.timestampMillis),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    if (!notification.isRead) {
                        TextButton(
                            onClick = onMarkRead,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Tandai Dibaca", fontSize = 11.sp, color = OrangePrimary)
                        }
                    }
                }
            }
        }
    }
}
