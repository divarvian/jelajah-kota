package com.arvian.jelajahkota.feature.plans

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SuccessGreen
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun SchedulePlanRoute(
    viewModel: PlansViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDatePicker: () -> Unit,
    onNavigateToReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SchedulePlanScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onDatePickerClick = onNavigateToDatePicker,
        onReviewsClick = onNavigateToReviews,
        modifier = modifier
    )
}

@Composable
fun SchedulePlanScreen(
    uiState: PlansUiState,
    onNavigateBack: () -> Unit,
    onDatePickerClick: () -> Unit,
    onReviewsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkText)
    ) {
        // Hero Image Banner
        Image(
            painter = painterResource(id = uiState.selectedPlace.imageResId),
            contentDescription = uiState.selectedPlace.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.9f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = DarkText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Sheet Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 260.dp)
                .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .background(White)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Horizontal Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detail",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = SubtitleText,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Text(
                    text = "Ulasan",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = SubtitleText,
                    modifier = Modifier.clickable { onReviewsClick() }
                )
                Text(
                    text = "Galeri",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = SubtitleText
                )
                Text(
                    text = "Pilih Tanggal",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BrandBluePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Title and Location
            Text(
                text = uiState.selectedPlace.name,
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.selectedPlace.location,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = SubtitleText
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Map Intent */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Peta",
                        tint = BrandBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Peta",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandBluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Operational Hours Title
            Text(
                text = "Jam Operasional",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule Table
            val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Minggu")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                days.forEach { day ->
                    val isToday = day == "Jum'at"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = day,
                            fontFamily = Lato,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (isToday) BrandBluePrimary else DarkText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = uiState.selectedPlace.openingHours,
                                fontFamily = Lato,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp,
                                color = if (isToday) BrandBluePrimary else DarkText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Schedule Range Trigger Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BrandBluePrimary, RoundedCornerShape(16.dp))
                    .clickable { onDatePickerClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rencana Berangkat",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = SubtitleText
                    )
                    Text(
                        text = uiState.departureFormatted,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkText
                    )
                }

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = BrandBluePrimary,
                    modifier = Modifier.size(24.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Rencana Pulang",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = SubtitleText
                    )
                    Text(
                        text = uiState.returnFormatted,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkText
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // CTA Button
            JelajahKotaPrimaryButton(
                text = "Pilih Tanggal Kunjungan",
                onClick = onDatePickerClick
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SchedulePlanScreenPreview() {
    SchedulePlanScreen(
        uiState = PlansUiState(),
        onNavigateBack = {},
        onDatePickerClick = {},
        onReviewsClick = {}
    )
}
