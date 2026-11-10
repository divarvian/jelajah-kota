package com.arvian.jelajahkota.feature.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueContainer
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun DatePickerRoute(
    viewModel: PlansViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DatePickerScreen(
        uiState = uiState,
        onDateSelected = { day ->
            if (day <= uiState.startDay) {
                viewModel.selectDateRange(day, day + 3)
            } else {
                viewModel.selectDateRange(uiState.startDay, day)
            }
        },
        onNavigateBack = onNavigateBack,
        onConfirmClick = onNavigateToConfirmation,
        modifier = modifier
    )
}

@Composable
fun DatePickerScreen(
    uiState: PlansUiState,
    onDateSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        JelajahKotaTopAppBar(
            title = "Pilih Tanggal",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        // Month Selector Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "November 2026",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkText
            )

            Row {
                IconButton(onClick = { /* Previous month */ }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Bulan Sebelumnya",
                        tint = SubtitleText,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = { /* Next month */ }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Bulan Berikutnya",
                        tint = SubtitleText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Day of Week Headers
        val dayHeaders = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayHeaders.forEach { header ->
                val isWeekend = header == "Sab" || header == "Min"
                Text(
                    text = header,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isWeekend) Color(0xFFFF383C) else DarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Calendar Grid (November 2026 starting on Sunday / offset 6)
        // Days 1..30
        val offset = 6 // Nov 1 is Sunday
        val totalCells = 35

        for (row in 0 until 5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - offset + 1

                    if (dayNumber in 1..30) {
                        val isStart = dayNumber == uiState.startDay
                        val isEnd = dayNumber == uiState.endDay
                        val inRange = dayNumber > uiState.startDay && dayNumber < uiState.endDay
                        val isBoundary = isStart || isEnd

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .then(
                                    if (inRange) {
                                        Modifier.background(BrandBlueContainer)
                                    } else {
                                        Modifier
                                    }
                                )
                                .then(
                                    if (isBoundary) {
                                        Modifier
                                            .clip(CircleShape)
                                            .background(BrandBluePrimary)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { onDateSelected(dayNumber) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                fontFamily = Lato,
                                fontWeight = if (isBoundary || inRange) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp,
                                color = when {
                                    isBoundary -> White
                                    inRange -> BrandBluePrimary
                                    col >= 5 -> Color(0xFFFF383C)
                                    else -> DarkText
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Selection Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, BrandBluePrimary, RoundedCornerShape(20.dp))
                .background(SurfaceGray)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Rentang Tanggal Dipilih",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = SubtitleText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${uiState.dateRangeFormatted} (${uiState.durationDays} Hari)",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandBluePrimary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // CTA Button
        JelajahKotaPrimaryButton(
            text = "Lanjutkan ke Konfirmasi Rencana",
            onClick = onConfirmClick
        )

        Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerScreenPreview() {
    DatePickerScreen(
        uiState = PlansUiState(),
        onDateSelected = {},
        onNavigateBack = {},
        onConfirmClick = {}
    )
}
