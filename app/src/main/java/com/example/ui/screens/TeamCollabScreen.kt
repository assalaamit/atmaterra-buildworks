package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun TeamCollabScreen(
    currentUser: UserProfile,
    predefinedUsers: List<UserProfile>,
    onSwitchUser: (UserProfile) -> Unit,
    onOpenBroadcast: () -> Unit,
    onChangePassword: ((userId: String, oldPass: String, newPass: String) -> Boolean)? = null
) {
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var selectedUserForPasswordChange by remember { mutableStateOf<UserProfile?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Active User Header
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AKSES PENGGUNA AKTIF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F766E).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "PT Atmaterra Buildwork Indonesia",
                                color = Color(0xFF0F766E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(getRoleColor(currentUser.role), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.avatarInitials,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = getRoleContainerColor(currentUser.role)
                                ) {
                                    Text(
                                        text = "${currentUser.role.badge} · ${currentUser.role.title}",
                                        color = getRoleTextColor(currentUser.role),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Credentials Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Username: ", fontSize = 11.sp, color = TextSecondaryDark)
                                Text(currentUser.username, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Password: ", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("•••••• (${currentUser.password})", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0F766E))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Kontak / HP: ", fontSize = 11.sp, color = TextSecondaryDark)
                                Text(currentUser.phone, fontSize = 11.sp, color = TextPrimaryDark)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tanggung Jawab: ${currentUser.role.description}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                selectedUserForPasswordChange = currentUser
                                showChangePasswordDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ganti Password", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onOpenBroadcast,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("broadcast_announcement_btn")
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Siarkan Pesan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Role Switcher Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAFTAR 9 PENGGUNA & HAK AKSES SISTEM",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Password Default: abc123",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F766E)
                )
            }
        }

        items(predefinedUsers) { user ->
            val isCurrent = user.id == currentUser.id
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surface
                ),
                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F766E)) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 2.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchUser(user) }
                    .testTag("user_role_card_${user.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(getRoleColor(user.role), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.avatarInitials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isCurrent) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SuccessGreenContainer
                                    ) {
                                        Text(
                                            text = "Sedang Login",
                                            color = SuccessOnGreenContainer,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${user.role.badge} · ${user.role.title}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = getRoleTextColor(user.role)
                            )
                        }

                        IconButton(
                            onClick = {
                                selectedUserForPasswordChange = user
                                showChangePasswordDialog = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Key,
                                contentDescription = "Ganti Password",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Credentials Preview Row
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Username: ${user.username}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Password: ${user.password}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E)
                            )
                        }
                    }
                }
            }
        }

        // Information Section
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ℹ️ Keamanan & Akses Mandiri Pengguna",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Masing-masing dari 9 personil tim dapat mengganti password standar (abc123) secara mandiri melalui tombol kunci di samping nama mereka. Setiap data laporan, approval, logistik, dan aktivitas akan secara otomatis tercatat atas nama pengguna yang sedang aktif.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog && selectedUserForPasswordChange != null) {
        val userToEdit = selectedUserForPasswordChange!!
        var oldPasswordInput by remember { mutableStateOf("") }
        var newPasswordInput by remember { mutableStateOf("") }
        var confirmPasswordInput by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var passwordVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showChangePasswordDialog = false
                selectedUserForPasswordChange = null
            },
            title = {
                Column {
                    Text("Ganti Password Pengguna", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${userToEdit.name} (${userToEdit.username})", fontSize = 12.sp, color = TextSecondaryDark)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = oldPasswordInput,
                        onValueChange = { oldPasswordInput = it },
                        label = { Text("Password Saat Ini") },
                        placeholder = { Text("abc123") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("Password Baru (min. 4 karakter)") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it },
                        label = { Text("Konfirmasi Password Baru") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    ) {
                        Checkbox(checked = passwordVisible, onCheckedChange = { passwordVisible = it })
                        Text("Tampilkan Password", fontSize = 12.sp)
                    }

                    errorMessage?.let { err ->
                        Text(err, color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (oldPasswordInput.isBlank() || newPasswordInput.isBlank()) {
                            errorMessage = "Semua kolom password wajib diisi!"
                            return@Button
                        }
                        if (oldPasswordInput != userToEdit.password) {
                            errorMessage = "Password saat ini salah!"
                            return@Button
                        }
                        if (newPasswordInput.length < 4) {
                            errorMessage = "Password baru minimal 4 karakter!"
                            return@Button
                        }
                        if (newPasswordInput != confirmPasswordInput) {
                            errorMessage = "Konfirmasi password baru tidak cocok!"
                            return@Button
                        }

                        onChangePassword?.invoke(userToEdit.id, oldPasswordInput, newPasswordInput)
                        showChangePasswordDialog = false
                        selectedUserForPasswordChange = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Text("Simpan Password Baru")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangePasswordDialog = false
                        selectedUserForPasswordChange = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

private fun getRoleColor(role: UserRole): Color {
    return when (role) {
        UserRole.CEO -> Color(0xFF1E293B)
        UserRole.FINANCE -> Color(0xFF047857)
        UserRole.PROJECT_MANAGER -> Color(0xFF0F766E)
        UserRole.PROJECT_LEADER_GPA -> Color(0xFF2563EB)
        UserRole.PROJECT_LEADER_GOR -> Color(0xFF4F46E5)
        UserRole.PROJECT_LEADER_MYKOST -> Color(0xFF7C3AED)
        UserRole.PROJECT_LEADER_LO_VILLA -> Color(0xFF0284C7)
        UserRole.LOGISTIC -> Color(0xFFD97706)
        UserRole.ADMINISTRATOR -> Color(0xFF475569)
    }
}

private fun getRoleContainerColor(role: UserRole): Color {
    return when (role) {
        UserRole.CEO -> Color(0xFFF1F5F9)
        UserRole.FINANCE -> Color(0xFFECFDF5)
        UserRole.PROJECT_MANAGER -> Color(0xFFCCFBF1)
        UserRole.PROJECT_LEADER_GPA -> Color(0xFFEFF6FF)
        UserRole.PROJECT_LEADER_GOR -> Color(0xFFEEF2FF)
        UserRole.PROJECT_LEADER_MYKOST -> Color(0xFFF5F3FF)
        UserRole.PROJECT_LEADER_LO_VILLA -> Color(0xFFE0F2FE)
        UserRole.LOGISTIC -> Color(0xFFFEF3C7)
        UserRole.ADMINISTRATOR -> Color(0xFFF8FAFC)
    }
}

private fun getRoleTextColor(role: UserRole): Color {
    return when (role) {
        UserRole.CEO -> Color(0xFF0F172A)
        UserRole.FINANCE -> Color(0xFF047857)
        UserRole.PROJECT_MANAGER -> Color(0xFF0F766E)
        UserRole.PROJECT_LEADER_GPA -> Color(0xFF1D4ED8)
        UserRole.PROJECT_LEADER_GOR -> Color(0xFF3730A3)
        UserRole.PROJECT_LEADER_MYKOST -> Color(0xFF6D28D9)
        UserRole.PROJECT_LEADER_LO_VILLA -> Color(0xFF0369A1)
        UserRole.LOGISTIC -> Color(0xFFB45309)
        UserRole.ADMINISTRATOR -> Color(0xFF334155)
    }
}
