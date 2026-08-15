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
import com.example.data.model.InventoryItemEntity
import com.example.data.model.MaterialItemEntity
import com.example.data.model.MaterialTransactionEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatDate
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    inventoryItems: List<InventoryItemEntity>,
    materials: List<MaterialItemEntity>,
    lowStockMaterials: List<MaterialItemEntity>,
    transactions: List<MaterialTransactionEntity>,
    selectedTab: Int, // 0: Inventaris Peralatan & APD, 1: Stok Material Konstruksi, 2: Riwayat Transaksi
    onTabSelected: (Int) -> Unit,
    categoryFilter: String,
    statusFilter: String,
    searchQuery: String,
    onCategoryFilterChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenAddInventoryDialog: () -> Unit,
    onOpenAddMaterialDialog: () -> Unit,
    onOpenUpdateInventoryDialog: (InventoryItemEntity) -> Unit,
    onOpenMaterialTransactionDialog: (MaterialItemEntity) -> Unit
) {
    val inventoryCategories = listOf(
        "SEMUA",
        "Peralatan & Hand Tools",
        "Safety & APD",
        "Perancah & Formwork",
        "MEP & Elektrikal",
        "Bahan Kimia",
        "Material Konstruksi"
    )

    val statuses = listOf("SEMUA", "TERSEDIA", "DIGUNAKAN", "DALAM_PERBAIKAN", "RUSAK", "HABIS")

    // Filter Inventory
    val filteredInventory = inventoryItems.filter { item ->
        val matchCat = categoryFilter == "SEMUA" || item.category.equals(categoryFilter, ignoreCase = true)
        val matchStatus = statusFilter == "SEMUA" || item.status.equals(statusFilter, ignoreCase = true)
        val matchSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.itemCode.contains(searchQuery, ignoreCase = true) ||
                item.storageLocation.contains(searchQuery, ignoreCase = true) ||
                item.assignedTo.contains(searchQuery, ignoreCase = true)
        matchCat && matchStatus && matchSearch
    }

    // Filter Materials
    val filteredMaterials = materials.filter { mat ->
        val matchCat = categoryFilter == "SEMUA" || mat.category.equals(categoryFilter, ignoreCase = true)
        val matchSearch = searchQuery.isBlank() ||
                mat.name.contains(searchQuery, ignoreCase = true) ||
                mat.storageLocation.contains(searchQuery, ignoreCase = true)
        matchCat && matchSearch
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedTab == 0) onOpenAddInventoryDialog()
                    else onOpenAddMaterialDialog()
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        if (selectedTab == 0) "Tambah Inventaris" else "Tambah Material",
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 65.dp)
                    .testTag("add_inventory_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Tabs: Inventaris Detail vs Material Stok vs Riwayat Transaksi
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
                        onClick = { onTabSelected(0) },
                        text = { Text("Inventaris & APD", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { onTabSelected(1) },
                        text = { Text("Material Proyek", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { onTabSelected(2) },
                        text = { Text("Riwayat Surat Jalan", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            // Search Bar & Filter Rows
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Cari item, kode SKU, lokasi gudang, penanggung jawab...", fontSize = 13.sp) },
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
                    modifier = Modifier.fillMaxWidth().testTag("inventory_search_field")
                )

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(inventoryCategories) { cat ->
                        val isSelected = categoryFilter.equals(cat, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryFilterChange(if (isSelected) "SEMUA" else cat) },
                            label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimaryContainer,
                                selectedLabelColor = OrangeOnPrimaryContainer
                            )
                        )
                    }
                }

                // Status Chips (for Tab 0)
                if (selectedTab == 0) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(statuses) { st ->
                            val isSelected = statusFilter.equals(st, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onStatusFilterChange(if (isSelected) "SEMUA" else st) },
                                label = { Text(st.replace("_", " "), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // INVENTARIS DETAIL LIST
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        // Summary Metrics
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InventorySummaryBadge("Total Item", "${inventoryItems.size}", OrangePrimary, Modifier.weight(1f))
                                InventorySummaryBadge("Tersedia", "${inventoryItems.count { it.status == "TERSEDIA" }}", SuccessGreen, Modifier.weight(1f))
                                InventorySummaryBadge("Digunakan", "${inventoryItems.count { it.status == "DIGUNAKAN" }}", BlueprintBlue, Modifier.weight(1f))
                                InventorySummaryBadge("Perbaikan", "${inventoryItems.count { it.status == "DALAM_PERBAIKAN" || it.status == "RUSAK" }}", DangerRed, Modifier.weight(1f))
                            }
                        }

                        if (filteredInventory.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Tidak ada item inventaris yang cocok", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        } else {
                            items(filteredInventory, key = { it.id }) { item ->
                                InventoryItemCard(
                                    item = item,
                                    onUpdateClick = { onOpenUpdateInventoryDialog(item) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // MATERIAL PROYEK LIST
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        // Low Stock Warning Banner
                        if (lowStockMaterials.isNotEmpty()) {
                            item {
                                Surface(
                                    color = DangerRedContainer,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Peringatan: ${lowStockMaterials.size} Material di Bawah Batas Minimum!", fontWeight = FontWeight.Bold, color = DangerOnRedContainer, fontSize = 12.sp)
                                            Text(lowStockMaterials.joinToString { it.name }, style = MaterialTheme.typography.bodySmall, color = DangerOnRedContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }

                        items(filteredMaterials, key = { it.id }) { mat ->
                            MaterialRowCard(
                                material = mat,
                                onTransactionClick = { onOpenMaterialTransactionDialog(mat) }
                            )
                        }
                    }
                }

                2 -> {
                    // RIWAYAT TRANSAKSI / SURAT JALAN
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        if (transactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Belum ada riwayat transaksi material", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(transactions, key = { it.id }) { tx ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                StatusBadge(tx.type)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(tx.materialName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            }
                                            Text(
                                                text = "${if (tx.type == "KELUAR") "-" else "+"}${tx.quantity} ${tx.unit}",
                                                fontWeight = FontWeight.Black,
                                                color = if (tx.type == "KELUAR") DangerRed else SuccessGreen,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("No. SJ: ${tx.suratJalanNo} | Peruntukan: ${tx.usedForWorkItem}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Dicatat oleh: ${tx.loggedByName} (${tx.loggedByRole}) • ${formatDate(tx.dateMillis)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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

@Composable
fun InventorySummaryBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = color)
            Text(label, fontSize = 10.sp, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItemEntity,
    onUpdateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = OrangePrimaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.itemCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangeOnPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusBadge(item.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Jumlah Stok", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${item.quantity} ${item.unit}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Estimasi Nilai", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatRupiah(item.unitPriceRp), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text("Merk / Supplier", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.supplierOrBrand, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location & Assignee
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.storageLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BlueprintBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.assignedTo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Catatan: ${item.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onUpdateClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ubah Status & Lokasi", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MaterialRowCard(
    material: MaterialItemEntity,
    onTransactionClick: () -> Unit
) {
    val isLowStock = material.currentStock <= material.minStockThreshold

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(material.category, style = MaterialTheme.typography.labelSmall, color = OrangePrimary)
                    Text(material.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                if (isLowStock) {
                    Surface(color = DangerRedContainer, shape = RoundedCornerShape(4.dp)) {
                        Text("STOK KRITIS", color = DangerOnRedContainer, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sisa Stok", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${material.currentStock} ${material.unit}",
                        fontWeight = FontWeight.Black,
                        color = if (isLowStock) DangerRed else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column {
                    Text("Batas Minimum", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${material.minStockThreshold} ${material.unit}", style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = onTransactionClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateSecondary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catat Transaksi", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Lokasi: ${material.storageLocation} | Supplier: ${material.supplierName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}
