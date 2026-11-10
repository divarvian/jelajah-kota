package com.arvian.jelajahkota.feature.plans

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun PlanConfirmationRoute(
    viewModel: PlansViewModel,
    onNavigateBack: () -> Unit,
    onPlanConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlanConfirmationScreen(
        uiState = uiState,
        onIncrementVisitors = viewModel::incrementVisitors,
        onDecrementVisitors = viewModel::decrementVisitors,
        onConfirmClick = { viewModel.confirmPlan(onPlanConfirmed) },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun PlanConfirmationScreen(
    uiState: PlansUiState,
    onIncrementVisitors: () -> Unit,
    onDecrementVisitors: () -> Unit,
    onConfirmClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        JelajahKotaTopAppBar(
            title = "Konfirmasi Rencana",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        // Breakdown Row 1: Tanggal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tanggal",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = DarkText
            )
            Text(
                text = "${uiState.startDay}-${uiState.endDay} ${uiState.monthName}",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkText
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(14.dp))

        // Breakdown Row 2: Jumlah Hari
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jumlah hari",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = DarkText
            )
            Text(
                text = "${uiState.durationDays} Hari",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkText
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(14.dp))

        // Breakdown Row 3: Jumlah Pengunjung (with Stepper)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jumlah Pengunjung",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = DarkText
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrementVisitors,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Kurang",
                        tint = DarkText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${uiState.visitorCount} Orang",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkText,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                IconButton(
                    onClick = onIncrementVisitors,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah",
                        tint = DarkText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(28.dp))

        // Destination Preview Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceGray)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = uiState.selectedPlace.imageResId),
                contentDescription = uiState.selectedPlace.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = uiState.selectedPlace.name,
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BrandBluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = uiState.selectedPlace.location,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = SubtitleText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Departure & Return Schedule Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Berangkat
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BrandBluePrimary, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "Berangkat",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = SubtitleText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.departureFormatted,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarkText
                )
            }

            // Pulang
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BrandBluePrimary, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "Pulang",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = SubtitleText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.returnFormatted,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarkText
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Confirmation CTA Button
        if (uiState.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandBluePrimary)
            }
        } else {
            JelajahKotaPrimaryButton(
                text = "Konfirmasi",
                onClick = onConfirmClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanConfirmationScreenPreview() {
    PlanConfirmationScreen(
        uiState = PlansUiState(),
        onIncrementVisitors = {},
        onDecrementVisitors = {},
        onConfirmClick = {},
        onNavigateBack = {}
    )
}
