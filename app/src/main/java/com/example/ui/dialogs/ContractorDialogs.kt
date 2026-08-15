package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

// 1. INVENTORY: ADD NEW ITEM
@Composable
fun AddInventoryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        code: String,
        name: String,
        category: String,
        status: String,
        quantity: Double,
        unit: String,
        minStock: Double,
        priceRp: Long,
        storageLocation: String,
        assignedTo: String,
        supplierOrBrand: String,
        notes: String
    ) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Peralatan & Hand Tools") }
    var status by remember { mutableStateOf("TERSEDIA") }
    var quantityStr by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("Unit") }
    var minStockStr by remember { mutableStateOf("1") }
    var priceStr by remember { mutableStateOf("500000") }
    var storageLocation by remember { mutableStateOf("Gudang Utama Lantai 1") }
    var assignedTo by remember { mutableStateOf("Tersedia di Gudang") }
    var supplierOrBrand by remember { mutableStateOf("Bosch") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Peralatan & Hand Tools", "Safety & APD", "Perancah & Formwork", "MEP & Elektrikal", "Bahan Kimia", "Material Konstruksi")
    val statuses = listOf("TERSEDIA", "DIGUNAKAN", "DALAM_PERBAIKAN", "RUSAK", "HABIS")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Item Inventaris Baru", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Kode / SKU Item (contoh: INV-TL-01)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Peralatan / Item APD") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                // Status Selector
                Text("Status Awal:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    statuses.take(3).forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.replace("_", " "), fontSize = 10.sp) }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Jumlah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Satuan (Unit/Pcs/Set)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minStockStr,
                        onValueChange = { minStockStr = it },
                        label = { Text("Batas Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga/Estimasi (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.5f)
                    )
                }

                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Lokasi Penyimpanan (Gudang/Rak/Lantai)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = { assignedTo = it },
                    label = { Text("Penanggung Jawab / Mandor PIC") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = supplierOrBrand,
                    onValueChange = { supplierOrBrand = it },
                    label = { Text("Merk / Pabrikan / Supplier") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan / Keterangan Kondisi") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            code,
                            name,
                            category,
                            status,
                            quantityStr.toDoubleOrNull() ?: 1.0,
                            unit,
                            minStockStr.toDoubleOrNull() ?: 1.0,
                            priceStr.toLongOrNull() ?: 0L,
                            storageLocation,
                            assignedTo,
                            supplierOrBrand,
                            notes
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Simpan Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// 2. INVENTORY: UPDATE STATUS & LOCATION
@Composable
fun UpdateInventoryStatusDialog(
    item: InventoryItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (newStatus: String, newLocation: String, assignedTo: String, newQty: Double) -> Unit
) {
    var status by remember { mutableStateOf(item.status) }
    var location by remember { mutableStateOf(item.storageLocation) }
    var assignedTo by remember { mutableStateOf(item.assignedTo) }
    var quantityStr by remember { mutableStateOf(item.quantity.toString()) }

    val statuses = listOf("TERSEDIA", "DIGUNAKAN", "DALAM_PERBAIKAN", "RUSAK", "HABIS")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status: ${item.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Kode: ${item.itemCode}", style = MaterialTheme.typography.labelMedium, color = OrangePrimary)

                Text("Pilih Status Baru:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                statuses.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st.replace("_", " "), fontSize = 11.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Jumlah Item Saat Ini (${item.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi Penyimpanan Baru") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = { assignedTo = it },
                    label = { Text("Penanggung Jawab / Mandor Lapangan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        status,
                        location,
                        assignedTo,
                        quantityStr.toDoubleOrNull() ?: item.quantity
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Perbarui Status")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// 3. NOTIFICATION PREFERENCES
@Composable
fun NotificationPreferencesDialog(
    currentPreferences: NotificationPreferences,
    onDismiss: () -> Unit,
    onSave: (maintenance: Boolean, deadlines: Boolean, lowStock: Boolean, statusChange: Boolean, broadcast: Boolean) -> Unit
) {
    var maintenance by remember { mutableStateOf(currentPreferences.maintenanceAlerts) }
    var deadlines by remember { mutableStateOf(currentPreferences.reportDeadlineAlerts) }
    var lowStock by remember { mutableStateOf(currentPreferences.lowStockAlerts) }
    var statusChange by remember { mutableStateOf(currentPreferences.projectStatusAlerts) }
    var broadcast by remember { mutableStateOf(currentPreferences.teamBroadcastAlerts) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Konfigurasi Notifikasi", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Atur jenis notifikasi dan alarm proyek yang ingin Anda terima:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                NotificationToggleItem(
                    title = "Pemeliharaan & Servis Alat",
                    desc = "Peringatan hour meter, jadwal servis rutin, dan pergantian suku cadang.",
                    checked = maintenance,
                    onCheckedChange = { maintenance = it }
                )

                NotificationToggleItem(
                    title = "Tenggat Laporan Harian & Mingguan",
                    desc = "Pengingat pengisian laporan shift dan verifikasi persetujuan Site Manager.",
                    checked = deadlines,
                    onCheckedChange = { deadlines = it }
                )

                NotificationToggleItem(
                    title = "Peringatan Stok Material Menipis",
                    desc = "Alarm otomatis ketika stok material/inventaris berada di bawah batas kritis.",
                    checked = lowStock,
                    onCheckedChange = { lowStock = it }
                )

                NotificationToggleItem(
                    title = "Perubahan Status Proyek & Kurva S",
                    desc = "Notifikasi deviasi progres mingguan (ahead/behind schedule).",
                    checked = statusChange,
                    onCheckedChange = { statusChange = it }
                )

                NotificationToggleItem(
                    title = "Siaran & Instruksi Lapangan",
                    desc = "Pesan cepat dan instruksi keselamatan K3 dari Site Manager / PM.",
                    checked = broadcast,
                    onCheckedChange = { broadcast = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(maintenance, deadlines, lowStock, statusChange, broadcast)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Simpan Pengaturan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun NotificationToggleItem(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// 4. UPLOAD DOCUMENT DIALOG
@Composable
fun UploadDocumentDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        folder: String,
        title: String,
        fileName: String,
        fileType: String,
        fileSizeBytes: Long,
        version: String,
        allowedRoles: String,
        notes: String
    ) -> Unit
) {
    var folder by remember { mutableStateOf("Gambar Kerja & Shop Drawing") }
    var title by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("PDF") }
    var version by remember { mutableStateOf("Rev 01") }
    var allowedRoles by remember { mutableStateOf("SEMUA") }
    var notes by remember { mutableStateOf("") }

    val folders = listOf(
        "Gambar Kerja & Shop Drawing",
        "RAB, RAP & Kontrak",
        "Surat Jalan & PO",
        "Laporan & BA",
        "K3 & Izin Kerja (PTW)",
        "Foto Dokumentasi Proyek"
    )
    val fileTypes = listOf("PDF", "DWG", "XLS", "DOC", "IMG")
    val roleAccess = listOf("SEMUA", "MANAGEMENT_ONLY", "LOGISTIK", "SPV_MANDOR")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unggah Dokumen Proyek", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Folder Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                folders.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.forEach { f ->
                            FilterChip(
                                selected = folder == f,
                                onClick = { folder = f },
                                label = { Text(f, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Dokumen (contoh: Shop Drawing Kolom K1)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("Nama File (contoh: SD-KLM-01.pdf)") },
                        modifier = Modifier.weight(1.5f)
                    )
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Versi") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // File Type selector
                Text("Format File:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    fileTypes.forEach { ft ->
                        FilterChip(
                            selected = fileType == ft,
                            onClick = { fileType = ft },
                            label = { Text(ft, fontSize = 11.sp) }
                        )
                    }
                }

                // Role Permissions
                Text("Hak Akses Dokumen:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    roleAccess.forEach { role ->
                        FilterChip(
                            selected = allowedRoles == role,
                            onClick = { allowedRoles = role },
                            label = { Text(role.replace("_", " "), fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan Tambahan / Catatan Revisi") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalFileName = if (fileName.isNotBlank()) fileName else "${title.replace(" ", "_")}.${fileType.lowercase()}"
                        val randomSize = (2_000_000L..15_000_000L).random()
                        onConfirm(folder, title, finalFileName, fileType, randomSize, version, allowedRoles, notes)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Unggah File")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// 4.1 IMPORT EXCEL / CSV DATA DIALOG
@Composable
fun ImportExcelDataDialog(
    onDismiss: () -> Unit,
    onConfirmImport: (type: String, fileName: String, rawCsvOrText: String) -> Unit
) {
    var importType by remember { mutableStateOf("MATERIAL") }
    var fileName by remember { mutableStateOf("Data_Material_Atmaterra_2026.xlsx") }
    var rawContent by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf("SAMPLE_1") }

    val sampleMaterialCsv = """
Nama Material,Kategori,Satuan,Stok Awal,Min Stok,Harga Satuan,Lokasi Gudang,Supplier
Semen Holcim Extra 50kg,MATERIAL_UTAMA,Zak,150,30,68000,Gudang Utama,PT Semen Indonesia
Besi Beton Ulir D16,BESI_BAJA,Batang,200,40,142000,Area Pabrikasi Besi,CV Baja Purbalingga
Pasir Pasang Merapi,PASIR_AGREGAT,M3,35,8,275000,Dumping Area Barat,Tambang Pasir Progo
Bata Ringan Hebel 10cm,BATA_DINDING,M3,45,10,620000,Gudang Material Lt.1,PT Broco Aerated
Cat Tembok Dulux Weathershield,FINISHING,Pail,25,5,920000,Gudang Finishing,Toko Cat Berkah
    """.trimIndent()

    val sampleScheduleCsv = """
Kode WBS,Uraian Pekerjaan,Bobot %,Rencana %,Aktual %,Status
WBS-01.01,Pekerjaan Cor Balok & Pelat Lt.3,8.5,80,75,SEDANG_BERJALAN
WBS-01.02,Pemasangan Dinding Bata Ringan Lt.2,6.2,60,65,SEDANG_BERJALAN
WBS-01.03,Instalasi Plumbing & Pipa Air Bersih,4.8,40,40,SEDANG_BERJALAN
WBS-01.04,Plesteran & Acian Dinding Luar,5.5,30,20,TERLAMBAT
WBS-01.05,Rangka Plafon Gypsum & Hollow,3.9,0,0,BELUM_MULAI
    """.trimIndent()

    val sampleRapCsv = """
Pos Anggaran RAP,Kategori,Anggaran RAP Rp,Realisasi Aktual Rp
Pengadaan Semen & Readymix,MATERIAL,65000000,48000000
Upah Borongan Mandor Pembesian,TENAGA_KERJA,42000000,31500000
Sewa Tower Hoist & Genset 50kVA,ALAT_BERAT,18500000,12000000
Pekerjaan Subkon Elektrikal & Panel,SUBCON,28000000,14000000
Biaya Operasional Lapangan & K3,OPERASIONAL,9500000,6800000
    """.trimIndent()

    val sampleAttendanceCsv = """
Nama Pekerja,Kategori,Status,Jam Lembur,Upah Harian Rp,Tugas Lapangan
Karyono,MANDOR,HADIR,0,220000,Pengawasan Cor Lt.3
Budi Santoso,TUKANG_BESI,LEMBUR,2,160000,Rakit Pembesian Balok
Slamet Riyadi,TUKANG_BATU,HADIR,0,160000,Pasang Bata Dinding Lt.2
Agung Prasetyo,PEKERJA_HELPER,LEMBUR,3,120000,Langsir Material Semen
Dedi Kurniawan,TUKANG_LISTRIK,HADIR,0,165000,Instalasi Conduit Lampu
    """.trimIndent()

    LaunchedEffect(importType) {
        when (importType) {
            "MATERIAL" -> {
                fileName = "Data_Material_Atmaterra.xlsx"
                rawContent = sampleMaterialCsv
            }
            "SCHEDULE" -> {
                fileName = "Time_Schedule_WBS_Update.xlsx"
                rawContent = sampleScheduleCsv
            }
            "RAP" -> {
                fileName = "Anggaran_RAP_Realisasi.xlsx"
                rawContent = sampleRapCsv
            }
            "ATTENDANCE" -> {
                fileName = "Rekap_Absensi_Harian_Tukang.xlsx"
                rawContent = sampleAttendanceCsv
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TableChart, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Impor Data Excel & Spreadsheet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Parsing otomatis ke database proyek", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Pilih Modul Tujuan Impor:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("MATERIAL" to "Material", "SCHEDULE" to "Schedule", "RAP" to "RAP Biaya", "ATTENDANCE" to "Absensi").forEach { (type, label) ->
                        FilterChip(
                            selected = importType == type,
                            onClick = { importType = type },
                            label = { Text(label, fontSize = 10.sp, fontWeight = if (importType == type) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Nama File Excel / Spreadsheet") },
                    leadingIcon = { Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pratinjau Data Tabel Spreadsheet (Dapat Diedit / Ditempel):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }
                        Text("Sistem mendukung pemisah koma (,), titik koma (;), atau tab dari copy-paste Excel.", fontSize = 10.sp, color = TextSecondaryDark)
                    }
                }

                OutlinedTextField(
                    value = rawContent,
                    onValueChange = { rawContent = it },
                    label = { Text("Isi Baris Data Excel") },
                    minLines = 6,
                    maxLines = 10,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rawContent.isNotBlank()) {
                        onConfirmImport(importType, fileName, rawContent)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Proses & Integrasikan ke Database", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// 5. ADD ATTENDANCE DIALOG
@Composable
fun AddAttendanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        workerName: String,
        category: String,
        status: String,
        overtimeHours: Double,
        dailyRateRp: Long,
        workAssignedTo: String,
        notes: String
    ) -> Unit
) {
    var workerName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("TUKANG_BESI") }
    var status by remember { mutableStateOf("HADIR") }
    var overtimeStr by remember { mutableStateOf("0") }
    var dailyRateStr by remember { mutableStateOf("160000") }
    var workAssignedTo by remember { mutableStateOf("Pekerjaan Penulangan Kolom Lt. 4") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf(
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
    val statuses = listOf("HADIR", "SETENGAH_HARI", "LEMBUR", "IZIN_SAKIT", "ALFA")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Kehadiran Tenaga Kerja", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = workerName,
                    onValueChange = { workerName = it },
                    label = { Text("Nama Lengkap Pekerja / Tukang") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Kategori / Keahlian:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                categories.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = {
                                    category = cat
                                    dailyRateStr = when (cat) {
                                        "PROJECT_LEADER" -> "450000"
                                        "SUPERVISOR" -> "280000"
                                        "MANDOR" -> "220000"
                                        "TUKANG_BESI", "TUKANG_BATU", "TUKANG_KAYU" -> "160000"
                                        "TUKANG_LISTRIK" -> "165000"
                                        "SUBCON" -> "175000"
                                        else -> "120000"
                                    }
                                },
                                label = { Text(cat.replace("_", " "), fontSize = 9.sp) }
                            )
                        }
                    }
                }

                Text("Status Kehadiran Hari Ini:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    statuses.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.replace("_", " "), fontSize = 10.sp) }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = overtimeStr,
                        onValueChange = { overtimeStr = it },
                        label = { Text("Jam Lembur") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dailyRateStr,
                        onValueChange = { dailyRateStr = it },
                        label = { Text("Upah Harian (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.5f)
                    )
                }

                OutlinedTextField(
                    value = workAssignedTo,
                    onValueChange = { workAssignedTo = it },
                    label = { Text("Tugas / Lokasi Pekerjaan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan (Kondisi/Izin)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (workerName.isNotBlank()) {
                        onConfirm(
                            workerName,
                            category,
                            status,
                            overtimeStr.toDoubleOrNull() ?: 0.0,
                            dailyRateStr.toLongOrNull() ?: 150000L,
                            workAssignedTo,
                            notes
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Simpan Absensi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// 6. UPDATE SCHEDULE TASK PROGRESS
@Composable
fun UpdateScheduleProgressDialog(
    item: ProjectScheduleItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (actualProgress: Float, status: String) -> Unit
) {
    var progressVal by remember { mutableStateOf(item.actualProgressPercent) }
    var status by remember { mutableStateOf(item.status) }
    val statuses = listOf("BELUM_MULAI", "SEDANG_BERJALAN", "TERLAMBAT", "SELESAI")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Progress: WBS ${item.wbsCode}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(item.taskName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text("Rencana Target: ${item.plannedProgressPercent}% | Bobot: ${item.weightPercent}%", style = MaterialTheme.typography.bodySmall, color = OrangePrimary)

                Text("Progress Aktual Saat Ini: ${"%.1f".format(progressVal)}%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Slider(
                    value = progressVal,
                    onValueChange = {
                        progressVal = it
                        if (it >= 100f) status = "SELESAI"
                        else if (it > 0f && status == "BELUM_MULAI") status = "SEDANG_BERJALAN"
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
                )

                Text("Status Pekerjaan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                statuses.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st.replace("_", " "), fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(progressVal, status)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Simpan Progres")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// 7. UPDATE RAP REALIZATION COST
@Composable
fun UpdateRapCostDialog(
    item: ProjectRapItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (newCostRp: Long) -> Unit
) {
    var costStr by remember { mutableStateOf(item.actualCostRp.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Realisasi RAP", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(item.itemName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text("Anggaran RAP: ${formatRupiah(item.budgetRapRp)}", style = MaterialTheme.typography.bodySmall, color = OrangePrimary)

                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("Realisasi Biaya Aktual (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costStr.toLongOrNull() ?: item.actualCostRp
                    onConfirm(cost)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Simpan Realisasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
