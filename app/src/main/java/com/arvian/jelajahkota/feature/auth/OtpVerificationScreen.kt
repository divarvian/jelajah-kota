package com.arvian.jelajahkota.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.Lato

@Composable
fun OtpVerificationRoute(
    viewModel: OtpVerificationViewModel,
    onNavigateBack: () -> Unit,
    onVerified: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            viewModel.consumeVerified()
            onVerified()
        }
    }

    OtpVerificationScreen(
        uiState = uiState,
        onDigitChange = viewModel::onDigitChange,
        onVerifyClick = viewModel::verifyCode,
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
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kode Verifikasi",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masukkan 6 digit kode OTP yang telah dikirim ke ${uiState.email}.",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ganti Email",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onNavigateBack)
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            JelajahKotaOtpInput(
                otpValues = uiState.otpValues,
                onOtpChange = onDigitChange,
                length = OtpVerificationUiState.OTP_LENGTH,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (uiState.canResend) "Anda dapat mengirim ulang kode sekarang" else "Kirim ulang dalam ${uiState.timerFormatted}",
                fontFamily = Lato,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = if (uiState.canResend) colorScheme.primary else colorScheme.onSurfaceVariant,
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
                    color = colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                JelajahKotaPrimaryButton(
                    text = "Verifikasi Kode",
                    onClick = onVerifyClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
    JelajahKotaTheme {
        OtpVerificationScreen(
            uiState = OtpVerificationUiState(
                email = "penjelajah@jelajahkota.id",
                otpValues = listOf("1", "2", "3", "", "", "")
            ),
            onDigitChange = { _, _ -> },
            onVerifyClick = {},
            onResendClick = {},
            onNavigateBack = {}
        )
    }
}
