package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SCurveMilestoneEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun SCurveScreen(
    milestones: List<SCurveMilestoneEntity>,
    onUpdateMilestone: (SCurveMilestoneEntity, Float) -> Unit
) {
    var selectedMilestoneForEdit by remember { mutableStateOf<SCurveMilestoneEntity?>(null) }
    var editProgressValue by remember { mutableStateOf("") }

    val completedMilestones = milestones.filter { it.isCompleted }
    val latestCompleted = completedMilestones.lastOrNull()
    val plannedProgress = latestCompleted?.plannedCumulativePercent ?: 0f
    val actualProgress = latestCompleted?.actualCumulativePercent ?: 0f
    val deviation = actualProgress - plannedProgress

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Deviation Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (deviation >= 0) SuccessGreenContainer else DangerRedContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (deviation >= 0) SuccessGreen else DangerRed,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (deviation >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (deviation >= 0) "STATUS PROYEK: LEBIH CEPAT (+${String.format("%.1f", deviation)}%)" else "STATUS PROYEK: DEVIASI MINUS (${String.format("%.1f", deviation)}%)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (deviation >= 0) SuccessOnGreenContainer else DangerOnRedContainer
                        )
                        Text(
                            text = if (deviation >= 0)
                                "Realisasi (${String.format("%.1f", actualProgress)}%) melampaui target rencana (${String.format("%.1f", plannedProgress)}%). Pertahankan produktivitas!"
                            else
                                "Realisasi (${String.format("%.1f", actualProgress)}%) tertinggal dari target rencana (${String.format("%.1f", plannedProgress)}%). Perlu percepatan lembur & alat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (deviation >= 0) SuccessOnGreenContainer else DangerOnRedContainer
                        )
                    }
                }
            }
        }

        // S-Curve Canvas Chart Card
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
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GRAFIK KURVA S (PROGRESS PROYEK)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Legend
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(SlateSecondary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rencana", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(OrangePrimary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Realisasi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                            }
                        }
                    }

                    // S-Curve Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("s_curve_canvas")
                    ) {
                        if (milestones.isNotEmpty()) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val width = size.width
                                val height = size.height
                                val paddingBottom = 24.dp.toPx()
                                val paddingTop = 12.dp.toPx()
                                val paddingLeft = 32.dp.toPx()
                                val paddingRight = 12.dp.toPx()

                                val graphWidth = width - paddingLeft - paddingRight
                                val graphHeight = height - paddingTop - paddingBottom

                                // Draw Y Grid Lines & Percentages
                                val ySteps = listOf(0f, 25f, 50f, 75f, 100f)
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 24f
                                    textAlign = android.graphics.Paint.Align.RIGHT
                                }
                                val xTextPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 22f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                ySteps.forEach { pct ->
                                    val y = paddingTop + graphHeight * (1f - pct / 100f)
                                    drawLine(
                                        color = Color.LightGray.copy(alpha = 0.4f),
                                        start = Offset(paddingLeft, y),
                                        end = Offset(width - paddingRight, y),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawContext.canvas.nativeCanvas.drawText(
                                        "${pct.toInt()}%",
                                        paddingLeft - 8.dp.toPx(),
                                        y + 8f,
                                        textPaint
                                    )
                                }

                                val count = milestones.size
                                val stepX = if (count > 1) graphWidth / (count - 1) else graphWidth

                                // Draw Planned S-Curve Path
                                val plannedPath = Path()
                                val actualPath = Path()
                                var hasActualStarted = false

                                milestones.forEachIndexed { index, m ->
                                    val x = paddingLeft + index * stepX
                                    val yPlanned = paddingTop + graphHeight * (1f - m.plannedCumulativePercent / 100f)

                                    if (index == 0) {
                                        plannedPath.moveTo(x, yPlanned)
                                    } else {
                                        val prevX = paddingLeft + (index - 1) * stepX
                                        val prevY = paddingTop + graphHeight * (1f - milestones[index - 1].plannedCumulativePercent / 100f)
                                        val midX = (prevX + x) / 2
                                        plannedPath.cubicTo(midX, prevY, midX, yPlanned, x, yPlanned)
                                    }

                                    // Planned point
                                    drawCircle(
                                        color = SlateSecondary,
                                        radius = 3.dp.toPx(),
                                        center = Offset(x, yPlanned)
                                    )

                                    // Actual S-Curve
                                    if (m.isCompleted) {
                                        val yActual = paddingTop + graphHeight * (1f - m.actualCumulativePercent / 100f)
                                        if (!hasActualStarted) {
                                            actualPath.moveTo(x, yActual)
                                            hasActualStarted = true
                                        } else {
                                            val prevX = paddingLeft + (index - 1) * stepX
                                            val prevY = paddingTop + graphHeight * (1f - milestones[index - 1].actualCumulativePercent / 100f)
                                            val midX = (prevX + x) / 2
                                            actualPath.cubicTo(midX, prevY, midX, yActual, x, yActual)
                                        }

                                        // Actual point
                                        drawCircle(
                                            color = OrangePrimary,
                                            radius = 5.dp.toPx(),
                                            center = Offset(x, yActual)
                                        )
                                        drawCircle(
                                            color = Color.White,
                                            radius = 2.5f.dp.toPx(),
                                            center = Offset(x, yActual)
                                        )
                                    }

                                    // Draw X label every 2 weeks or end
                                    if (index % 2 == 0 || index == count - 1) {
                                        drawContext.canvas.nativeCanvas.drawText(
                                            "M${m.weekNumber}",
                                            x,
                                            height - 4f,
                                            xTextPaint
                                        )
                                    }
                                }

                                // Stroke planned line
                                drawPath(
                                    path = plannedPath,
                                    color = SlateSecondary,
                                    style = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )

                                // Stroke actual line
                                if (hasActualStarted) {
                                    drawPath(
                                        path = actualPath,
                                        color = OrangePrimary,
                                        style = Stroke(width = 3.5f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "💡 Tip: Klik tombol 'Update' pada tabel di bawah untuk memperbarui progress aktual mingguan.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Milestones List / Table
        item {
            Text(
                text = "RINCIAN PROGRESS MINGGUAN (WBS)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(milestones, key = { it.id }) { milestone ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (milestone.isCompleted) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (milestone.isCompleted) 1.dp else 0.dp),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Minggu ${milestone.weekNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (milestone.isCompleted) {
                                StatusBadge(status = "SELESAI")
                            } else {
                                StatusBadge(status = "STANDBY")
                            }
                        }

                        Text(
                            text = milestone.weekLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Rencana: ${milestone.plannedCumulativePercent}%",
                                fontSize = 12.sp,
                                color = SlateSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Realisasi: ${if (milestone.isCompleted) "${milestone.actualCumulativePercent}%" else "-"}",
                                fontSize = 12.sp,
                                color = if (milestone.isCompleted) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            selectedMilestoneForEdit = milestone
                            editProgressValue = if (milestone.actualCumulativePercent > 0f) milestone.actualCumulativePercent.toString() else milestone.plannedCumulativePercent.toString()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("update_milestone_btn_${milestone.weekNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Update",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    // Edit Milestone Dialog
    selectedMilestoneForEdit?.let { ms ->
        AlertDialog(
            onDismissRequest = { selectedMilestoneForEdit = null },
            title = { Text("Update Realisasi Minggu ${ms.weekNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Target Rencana: ${ms.plannedCumulativePercent}%")
                    OutlinedTextField(
                        value = editProgressValue,
                        onValueChange = { editProgressValue = it },
                        label = { Text("Realisasi Kumulatif Saat Ini (%)") },
                        placeholder = { Text("Contoh: 56.5") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val value = editProgressValue.toFloatOrNull() ?: ms.plannedCumulativePercent
                        onUpdateMilestone(ms, value)
                        selectedMilestoneForEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Simpan & Perbarui Kurva")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMilestoneForEdit = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
