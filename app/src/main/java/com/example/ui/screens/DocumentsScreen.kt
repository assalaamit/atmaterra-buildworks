package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectDocumentEntity
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.ui.components.formatDate
import com.example.ui.theme.*

@Composable
fun DocumentsScreen(
    documents: List<ProjectDocumentEntity>,
    currentUser: UserProfile,
    folderFilter: String,
    searchQuery: String,
    onFolderFilterChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenUploadDialog: () -> Unit,
    onOpenImportExcelDialog: () -> Unit,
    onDeleteDocument: (ProjectDocumentEntity) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val folders = listOf(
        "SEMUA",
        "Gambar Kerja & Shop Drawing",
        "RAB, RAP & Kontrak",
        "Surat Jalan & PO",
        "Laporan & BA",
        "K3 & Izin Kerja (PTW)",
        "Foto Dokumentasi Proyek"
    )

    val filteredDocuments = documents.filter { doc ->
        val matchFolder = folderFilter == "SEMUA" || doc.folderCategory.equals(folderFilter, ignoreCase = true)
        val matchSearch = searchQuery.isBlank() ||
                doc.title.contains(searchQuery, ignoreCase = true) ||
                doc.fileName.contains(searchQuery, ignoreCase = true) ||
                doc.uploadedByName.contains(searchQuery, ignoreCase = true)
        matchFolder && matchSearch
    }

    var selectedDocumentForPreview by remember { mutableStateOf<ProjectDocumentEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 65.dp)
            ) {
                FloatingActionButton(
                    onClick = onOpenImportExcelDialog,
                    containerColor = SuccessGreen,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("import_excel_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Impor Excel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = onOpenUploadDialog,
                    icon = { Icon(Icons.Default.UploadFile, contentDescription = null) },
                    text = { Text("Unggah Dokumen", fontWeight = FontWeight.Bold) },
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("upload_doc_fab")
                )
            }
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Cari judul dokumen, nama file, pengunggah...", fontSize = 13.sp) },
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
                modifier = Modifier.fillMaxWidth().testTag("doc_search_field")
            )

            // Folder Categories Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(folders) { folder ->
                    val isSelected = folderFilter.equals(folder, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFolderFilterChange(if (isSelected) "SEMUA" else folder) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (folder == "SEMUA") Icons.Default.FolderOpen else Icons.Default.Folder,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(folder, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SlateSecondaryContainer,
                            selectedLabelColor = SlateOnSecondaryContainer
                        )
                    )
                }
            }

            // Documents List
            if (filteredDocuments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FolderOff, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(52.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Belum ada dokumen dalam folder ini", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(filteredDocuments, key = { it.id }) { doc ->
                        DocumentItemCard(
                            doc = doc,
                            currentUser = currentUser,
                            onClick = { selectedDocumentForPreview = doc },
                            onDelete = { onDeleteDocument(doc) }
                        )
                    }
                }
            }
        }
    }

    // Document Preview Modal Dialog
    selectedDocumentForPreview?.let { doc ->
        val (hasAccess, accessNote) = checkUserDocAccess(currentUser.role, doc.allowedRoles)

        AlertDialog(
            onDismissRequest = { selectedDocumentForPreview = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(getFileIcon(doc.fileType), contentDescription = null, tint = getFileColor(doc.fileType), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doc.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = if (hasAccess) SuccessGreenContainer else DangerRedContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasAccess) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (hasAccess) SuccessOnGreenContainer else DangerOnRedContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasAccess) "Hak Akses: Diizinkan ($accessNote)" else "Akses Terbatas: Role ${currentUser.role.title} tidak memiliki izin dokumen ini.",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hasAccess) SuccessOnGreenContainer else DangerOnRedContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text("Folder: ${doc.folderCategory}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("File: ${doc.fileName} (${doc.fileType} • ${(doc.fileSizeBytes / 1024 / 1024.0).let { "%.1f MB".format(it) }})", style = MaterialTheme.typography.bodySmall)
                    Text("Versi: ${doc.version}", style = MaterialTheme.typography.bodySmall)
                    Text("Pengunggah: ${doc.uploadedByName} (${doc.uploadedByRole})", style = MaterialTheme.typography.bodySmall)
                    Text("Tanggal: ${formatDate(doc.uploadDateMillis)}", style = MaterialTheme.typography.bodySmall)

                    if (doc.notes.isNotBlank()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Catatan & Instruksi: ${doc.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (hasAccess) {
                            onShowMessage("Membuka dokumen: ${doc.fileName}")
                            selectedDocumentForPreview = null
                        } else {
                            onShowMessage("Akses ditolak: Hubungi Site Manager untuk membuka dokumen ini.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (hasAccess) OrangePrimary else MaterialTheme.colorScheme.outline)
                ) {
                    Text(if (hasAccess) "Buka File & Unduh" else "Akses Terkunci")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDocumentForPreview = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}

fun checkUserDocAccess(userRole: UserRole, allowedRoles: String): Pair<Boolean, String> {
    return when (allowedRoles) {
        "MANAGEMENT_ONLY" -> {
            val isMgmt = userRole == UserRole.CEO || userRole == UserRole.FINANCE || userRole == UserRole.PROJECT_MANAGER || userRole == UserRole.ADMINISTRATOR
            Pair(isMgmt, "Khusus Direksi, Finance, PM & Admin")
        }
        "LOGISTIK" -> {
            val isLog = userRole == UserRole.LOGISTIC || userRole == UserRole.CEO || userRole == UserRole.PROJECT_MANAGER
            Pair(isLog, "Logistik & Management")
        }
        "SPV_MANDOR" -> {
            val isSpv = userRole == UserRole.PROJECT_LEADER_GPA || userRole == UserRole.PROJECT_LEADER_GOR || 
                        userRole == UserRole.PROJECT_LEADER_MYKOST || userRole == UserRole.PROJECT_LEADER_LO_VILLA ||
                        userRole == UserRole.PROJECT_MANAGER || userRole == UserRole.CEO
            Pair(isSpv, "Project Leader & Lapangan")
        }
        else -> Pair(true, "Akses Terbuka untuk Seluruh Tim")
    }
}

fun getFileIcon(type: String): ImageVector {
    return when (type.uppercase()) {
        "PDF" -> Icons.Default.PictureAsPdf
        "IMG", "JPG", "PNG" -> Icons.Default.Image
        "DWG", "DXF" -> Icons.Default.Architecture
        "XLS", "XLSX" -> Icons.Default.TableChart
        "DOC", "DOCX" -> Icons.Default.Description
        else -> Icons.Default.InsertDriveFile
    }
}

fun getFileColor(type: String): Color {
    return when (type.uppercase()) {
        "PDF" -> DangerRed
        "IMG", "JPG", "PNG" -> BlueprintBlue
        "DWG", "DXF" -> OrangePrimary
        "XLS", "XLSX" -> SuccessGreen
        "DOC", "DOCX" -> BlueprintBlue
        else -> SlateSecondary
    }
}

@Composable
fun DocumentItemCard(
    doc: ProjectDocumentEntity,
    currentUser: UserProfile,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val (hasAccess, _) = checkUserDocAccess(currentUser.role, doc.allowedRoles)
    val canDelete = currentUser.role == UserRole.CEO || currentUser.role == UserRole.PROJECT_MANAGER || currentUser.role == UserRole.ADMINISTRATOR

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
                    .size(46.dp)
                    .background(getFileColor(doc.fileType).copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getFileIcon(doc.fileType),
                    contentDescription = null,
                    tint = getFileColor(doc.fileType),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = doc.folderCategory,
                        style = MaterialTheme.typography.labelSmall,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = doc.version,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = doc.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${doc.fileName} • ${(doc.fileSizeBytes / 1024 / 1024.0).let { "%.1f MB".format(it) }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${doc.uploadedByName} (${doc.uploadedByRole}) • ${formatDate(doc.uploadDateMillis)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (!hasAccess) {
                    Icon(Icons.Default.Lock, contentDescription = "Terkunci", tint = DangerRed, modifier = Modifier.size(18.dp))
                }

                if (canDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
