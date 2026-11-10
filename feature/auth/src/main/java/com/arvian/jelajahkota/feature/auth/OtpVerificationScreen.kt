package com.arvian.jelajahkota.feature.auth

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOtpInput
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun OtpVerificationRoute(
    viewModel: OtpVerificationViewModel,
    onNavigateBack: () -> Unit,
    onVerified: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OtpVerificationScreen(
        uiState = uiState,
        onDigitChange = viewModel::onDigitChange,
        onVerifyClick = { viewModel.verifyCode(onVerified) },
        onResendClick = viewModel::resendCode,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun OtpVerificationScreen(
    uiState: OtpVerificationUiState,
    onDigitChange: (Int, String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        JelajahKotaTopAppBar(
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Kode Verifikasi",
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = DarkText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masukkan kode OTP yang dikirim ke ${uiState.email}",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(36.dp))

        // OTP Input Boxes
        JelajahKotaOtpInput(
            otpValues = uiState.otpValues,
            onOtpChange = onDigitChange,
            length = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Timer
        Text(
            text = uiState.timerFormatted,
            fontFamily = Lato,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = if (uiState.canResend) BrandBluePrimary else SubtitleText,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage,
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = ErrorRed
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Verify Button
        if (uiState.isLoading) {
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
                text = "Verifikasi Kode",
                onClick = onVerifyClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resend Button
        JelajahKotaOutlinedButton(
            text = "Kirim Ulang Kode",
            onClick = onResendClick,
            enabled = uiState.canResend
        )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpVerificationScreenPreview() {
    OtpVerificationScreen(
        uiState = OtpVerificationUiState(
            email = "antekantekasing@gmail.com",
            otpValues = listOf("1", "2", "", "")
        ),
        onDigitChange = { _, _ -> },
        onVerifyClick = {},
        onResendClick = {},
        onNavigateBack = {}
    )
}
