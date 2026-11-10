package com.arvian.jelajahkota.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.BrandOrangeAccent
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun ForgotPasswordRoute(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit,
    onLinkSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onSubmitClick = { viewModel.sendResetLink(onLinkSent) },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

        // Branding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "JelajahKota",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(BrandOrangeAccent)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Title & Description
        Text(
            text = "Lupa Password",
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Masukkan alamat email yang tertaut dengan akun Anda untuk mengatur ulang kata sandi Anda",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Email Field
        JelajahKotaTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "antekantekasing@gmail.com",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
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

        // Submit Button
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
                text = "Kirim Link Reset Password",
                onClick = onSubmitClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cancel Button
        JelajahKotaOutlinedButton(
            text = "Batalkan",
            onClick = onNavigateBack
        )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        uiState = ForgotPasswordUiState(email = "antekantekasing@gmail.com"),
        onEmailChange = {},
        onSubmitClick = {},
        onNavigateBack = {}
    )
}
