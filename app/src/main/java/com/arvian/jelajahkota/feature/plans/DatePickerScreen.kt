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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import java.time.LocalDate
import kotlin.math.ceil

@Composable
fun DatePickerRoute(
    viewModel: PlansViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConfirmation: (startEpochDay: Long, endEpochDay: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DatePickerScreen(
        uiState = uiState,
        onDateSelected = viewModel::onDateClicked,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
        onNavigateBack = onNavigateBack,
        onConfirmClick = {
            onNavigateToConfirmation(
                uiState.startDate.toEpochDay(),
                uiState.endDate.toEpochDay()
            )
        },
        modifier = modifier
    )
}

@Composable
fun DatePickerScreen(
    uiState: PlansUiState,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNavigateBack: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val displayedMonth = uiState.displayedMonth
    val firstDayOfMonth = displayedMonth.atDay(1)
    val daysInMonth = displayedMonth.lengthOfMonth()
    val offset = firstDayOfMonth.dayOfWeek.value - 1
    val totalRows = ceil((offset + daysInMonth) / 7.0).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            title = "Pilih Tanggal",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.displayedMonthTitle,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorScheme.onBackground
                )

                Row {
                    IconButton(
                        onClick = onPreviousMonth,
                        enabled = uiState.canNavigatePreviousMonth,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Bulan Sebelumnya",
                            tint = if (uiState.canNavigatePreviousMonth) colorScheme.onBackground else colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Bulan Berikutnya",
                            tint = colorScheme.onBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val dayHeaders = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
            Row(modifier = Modifier.fillMaxWidth()) {
                dayHeaders.forEach { header ->
                    val isWeekend = header == "Sab" || header == "Min"
                    Text(
                        text = header,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isWeekend) colorScheme.error else colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            for (row in 0 until totalRows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - offset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = displayedMonth.atDay(dayNumber)
                            val isPast = date.isBefore(uiState.minSelectableDate)
                            val isStart = date == uiState.startDate
                            val isEnd = date == uiState.endDate
                            val inRange = date.isAfter(uiState.startDate) && date.isBefore(uiState.endDate)
                            val isBoundary = isStart || isEnd

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .then(
                                        if (inRange) Modifier.background(colorScheme.primaryContainer)
                                        else Modifier
                                    )
                                    .then(
                                        if (isBoundary) {
                                            Modifier
                                                .clip(CircleShape)
                                                .background(colorScheme.primary)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable(enabled = !isPast) { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontFamily = Lato,
                                    fontWeight = if (isBoundary || inRange) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = when {
                                        isPast -> colorScheme.outlineVariant
                                        isBoundary -> colorScheme.onPrimary
                                        inRange -> colorScheme.onPrimaryContainer
                                        col >= 5 -> colorScheme.error
                                        else -> colorScheme.onBackground
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, colorScheme.primary, RoundedCornerShape(20.dp))
                    .background(colorScheme.surfaceVariant)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Rentang Tanggal Dipilih",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.dateRangeFormatted} (${uiState.durationDays} Hari)",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ketuk tanggal mulai lalu ketuk tanggal selesai untuk memilih rentang.",
                        fontFamily = Lato,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

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
    JelajahKotaTheme {
        DatePickerScreen(
            uiState = PlansUiState(),
            onDateSelected = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onNavigateBack = {},
            onConfirmClick = {}
        )
    }
}
