package com.example.ui.components

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
import com.example.data.model.EquipmentEntity
import com.example.data.model.MaterialItemEntity
import com.example.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, unit: String, stock: Double, minStock: Double, price: Long, loc: String, supplier: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Struktur") }
    var unit by remember { mutableStateOf("Sak") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var storageLoc by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }

    val categories = listOf("Struktur", "Arsitektur", "MEP", "Finishing", "Alat Bantu")
    val units = listOf("Sak", "Batang", "M3", "Ton", "Kg", "Pcs", "Lembar", "Drum", "Liter", "Roll")

    var expandedCat by remember { mutableStateOf(false) }
    var expandedUnit by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Material Baru", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Material *") },
                    placeholder = { Text("Contoh: Semen Padang 50kg") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_mat_name_input")
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }

                // Unit Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = !expandedUnit }
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Satuan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    unit = u
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stok Awal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("add_mat_stock_input")
                    )
                    OutlinedTextField(
                        value = minStock,
                        onValueChange = { minStock = it },
                        label = { Text("Batas Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("add_mat_min_stock_input")
                    )
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Harga Satuan (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("add_mat_price_input")
                )

                OutlinedTextField(
                    value = storageLoc,
                    onValueChange = { storageLoc = it },
                    label = { Text("Lokasi Gudang / Yard") },
                    placeholder = { Text("Gudang Utama A-01") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Vendor / Supplier") },
                    placeholder = { Text("PT Distributor Semen") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            name,
                            category,
                            unit,
                            stock.toDoubleOrNull() ?: 0.0,
                            minStock.toDoubleOrNull() ?: 10.0,
                            price.toLongOrNull() ?: 0L,
                            storageLoc,
                            supplier
                        )
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("save_new_material_btn")
            ) {
                Text("Simpan Material")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun MaterialTransactionDialog(
    material: MaterialItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (type: String, qty: Double, suratJalan: String, usedFor: String, notes: String) -> Unit
) {
    var type by remember { mutableStateOf("MASUK") }
    var quantity by remember { mutableStateOf("") }
    var suratJalan by remember { mutableStateOf("") }
    var usedFor by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transaksi Material", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${material.name} (Stok: ${material.currentStock} ${material.unit})",
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )

                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("MASUK", "KELUAR", "RETURN").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Jumlah (${material.unit}) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("tx_qty_input")
                )

                OutlinedTextField(
                    value = suratJalan,
                    onValueChange = { suratJalan = it },
                    label = { Text("No. Surat Jalan / DO") },
                    placeholder = { Text("SJ-2026/08/14-01") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_sj_input")
                )

                OutlinedTextField(
                    value = usedFor,
                    onValueChange = { usedFor = it },
                    label = { Text(if (type == "MASUK") "Penerimaan Dari" else "Peruntukan Pekerjaan") },
                    placeholder = { Text(if (type == "MASUK") "Supplier Krakatau Steel" else "Pengecoran Plat Lantai 4") },
                    modifier = Modifier.fillMaxWidth().testTag("tx_used_for_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan") },
                    placeholder = { Text("Kondisi baik, lolos uji visual") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    if (qty > 0) {
                        onConfirm(type, qty, suratJalan, usedFor, notes)
                    }
                },
                enabled = (quantity.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("confirm_material_tx_btn")
            ) {
                Text("Simpan Transaksi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentDialog(
    onDismiss: () -> Unit,
    onSave: (code: String, name: String, category: String, status: String, hm: Double, fuelCap: Double, currentFuel: Double, operator: String, notes: String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Alat Berat") }
    var status by remember { mutableStateOf("OPERASIONAL") }
    var hm by remember { mutableStateOf("") }
    var fuelCap by remember { mutableStateOf("") }
    var currentFuel by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Alat Berat", "Alat Angkut", "Mesin Kerja", "Scaffolding/Bekisting")
    val statuses = listOf("OPERASIONAL", "STANDBY", "MAINTENANCE", "RUSAK")

    var expandedCat by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daftarkan Alat / Mesin Proyek", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Kode Alat (e.g. EXC-02) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_eq_code_input")
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Alat & Tipe *") },
                    placeholder = { Text("Excavator Komatsu PC200") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_eq_name_input")
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status Kesiapan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    status = st
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hm,
                        onValueChange = { hm = it },
                        label = { Text("Hour Meter (HM)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fuelCap,
                        onValueChange = { fuelCap = it },
                        label = { Text("Kapasitas BBM (L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = operator,
                    onValueChange = { operator = it },
                    label = { Text("Operator / Penanggung Jawab") },
                    placeholder = { Text("Agus Santoso") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Kondisi Mesin") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        val fuelC = fuelCap.toDoubleOrNull() ?: 0.0
                        onSave(
                            code,
                            name,
                            category,
                            status,
                            hm.toDoubleOrNull() ?: 0.0,
                            fuelC,
                            fuelC * 0.7,
                            operator,
                            notes
                        )
                    }
                },
                enabled = code.isNotBlank() && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("save_new_equipment_btn")
            ) {
                Text("Daftarkan Alat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun RecordEquipmentLogDialog(
    equipment: EquipmentEntity,
    onDismiss: () -> Unit,
    onSave: (startHm: Double, endHm: Double, fuelAdded: Double, workDesc: String, operator: String, condition: String, newStatus: String) -> Unit
) {
    var startHm by remember { mutableStateOf(equipment.currentHourMeter.toString()) }
    var endHm by remember { mutableStateOf((equipment.currentHourMeter + 4.0).toString()) }
    var fuelAdded by remember { mutableStateOf("") }
    var workDesc by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf(equipment.operatorName) }
    var condition by remember { mutableStateOf("Operasi normal tanpa kendala") }
    var status by remember { mutableStateOf(equipment.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Jam Operasi (HM) & BBM", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${equipment.code} - ${equipment.name}",
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startHm,
                        onValueChange = { startHm = it },
                        label = { Text("HM Awal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endHm,
                        onValueChange = { endHm = it },
                        label = { Text("HM Akhir *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("log_end_hm_input")
                    )
                }

                OutlinedTextField(
                    value = fuelAdded,
                    onValueChange = { fuelAdded = it },
                    label = { Text("Pengisian Solar BBM (Liter)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("log_fuel_input")
                )

                OutlinedTextField(
                    value = workDesc,
                    onValueChange = { workDesc = it },
                    label = { Text("Uraian Pekerjaan Alat *") },
                    placeholder = { Text("Pengangkatan material bekisting & besi ke Lt. 4") },
                    modifier = Modifier.fillMaxWidth().testTag("log_work_desc_input")
                )

                OutlinedTextField(
                    value = operator,
                    onValueChange = { operator = it },
                    label = { Text("Operator") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Kondisi Alat & Catatan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sHm = startHm.toDoubleOrNull() ?: equipment.currentHourMeter
                    val eHm = endHm.toDoubleOrNull() ?: sHm
                    val fAdd = fuelAdded.toDoubleOrNull() ?: 0.0
                    onSave(sHm, eHm, fAdd, workDesc.ifBlank { "Operasional harian alat" }, operator, condition, status)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("save_equipment_log_btn")
            ) {
                Text("Simpan Log Alat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun CreateDailyReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (weatherM: String, weatherA: String, weatherE: String, mandor: Int, tukang: Int, pekerja: Int, subcon: Int, workDesc: String, materials: String, equipments: String, obstacles: String, solutions: String, progressAdded: Float) -> Unit
) {
    var weatherM by remember { mutableStateOf("CERAH") }
    var weatherA by remember { mutableStateOf("BERAWAN") }
    var weatherE by remember { mutableStateOf("CERAH") }

    var mandor by remember { mutableStateOf("4") }
    var tukang by remember { mutableStateOf("25") }
    var pekerja by remember { mutableStateOf("30") }
    var subcon by remember { mutableStateOf("8") }

    var workDesc by remember { mutableStateOf("") }
    var materialsSummary by remember { mutableStateOf("") }
    var equipmentSummary by remember { mutableStateOf("") }
    var obstacles by remember { mutableStateOf("") }
    var solutions by remember { mutableStateOf("") }
    var progressAdded by remember { mutableStateOf("0.8") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Laporan Harian Pekerjaan", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Weather Row
                Text("Kondisi Cuaca (Pagi / Siang / Sore):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("CERAH", "BERAWAN", "HUJAN_RINGAN", "HUJAN_LEBAT").forEach { w ->
                        FilterChip(
                            selected = weatherM == w,
                            onClick = { weatherM = w },
                            label = { Text(w.take(5), fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Labor Counts
                Text("Jumlah Tenaga Kerja Lapangan:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = mandor,
                        onValueChange = { mandor = it },
                        label = { Text("Mandor", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tukang,
                        onValueChange = { tukang = it },
                        label = { Text("Tukang", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pekerja,
                        onValueChange = { pekerja = it },
                        label = { Text("Pekerja", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = subcon,
                        onValueChange = { subcon = it },
                        label = { Text("Subcon", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = workDesc,
                    onValueChange = { workDesc = it },
                    label = { Text("Uraian Pekerjaan Hari Ini *") },
                    placeholder = { Text("1. Pengecoran Plat Lt. 4\n2. Pasang dinding hebel") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("daily_work_desc_input")
                )

                OutlinedTextField(
                    value = materialsSummary,
                    onValueChange = { materialsSummary = it },
                    label = { Text("Material Digunakan") },
                    placeholder = { Text("Ready mix 36 m3, Semen 40 sak, Besi 50 btg") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = equipmentSummary,
                    onValueChange = { equipmentSummary = it },
                    label = { Text("Alat Berat Digunakan") },
                    placeholder = { Text("Tower crane 6 jam, Excavator 4 jam") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = obstacles,
                    onValueChange = { obstacles = it },
                    label = { Text("Kendala / Masalah Lapangan") },
                    placeholder = { Text("Hujan sore 1 jam / material terlambat") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = solutions,
                    onValueChange = { solutions = it },
                    label = { Text("Tindakan / Solusi Dilakukan") },
                    placeholder = { Text("Pekerja dialihkan ke pekerjaan indoor") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = progressAdded,
                    onValueChange = { progressAdded = it },
                    label = { Text("Perkiraan Tambahan Progres (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (workDesc.isNotBlank()) {
                        onSubmit(
                            weatherM,
                            weatherA,
                            weatherE,
                            mandor.toIntOrNull() ?: 0,
                            tukang.toIntOrNull() ?: 0,
                            pekerja.toIntOrNull() ?: 0,
                            subcon.toIntOrNull() ?: 0,
                            workDesc,
                            materialsSummary,
                            equipmentSummary,
                            obstacles,
                            solutions,
                            progressAdded.toFloatOrNull() ?: 0.5f
                        )
                    }
                },
                enabled = workDesc.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("submit_daily_report_btn")
            ) {
                Text("Kirim Laporan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun BroadcastDialog(
    onDismiss: () -> Unit,
    onSend: (title: String, message: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Siarkan Instruksi Lapangan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pesan ini akan disiarkan ke seluruh Manajer, Supervisor, Logistik, dan Koordinator Alat secara real-time.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Instruksi / Pengumuman *") },
                    placeholder = { Text("Pengecoran Dimulai Pukul 08:00 WIB") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("broadcast_title_input")
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Isi Pesan / Instruksi Kerja *") },
                    placeholder = { Text("Pastikan checklist K3 dan surat jalan ready mix sudah diverifikasi sebelum cor.") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("broadcast_msg_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title, message)
                    }
                },
                enabled = title.isNotBlank() && message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.testTag("send_broadcast_btn")
            ) {
                Text("Siarkan Pesan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
