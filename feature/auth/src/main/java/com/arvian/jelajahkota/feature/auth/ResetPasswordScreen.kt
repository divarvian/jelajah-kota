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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
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
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPasswordTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SuccessGreen
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun ResetPasswordRoute(
    viewModel: ResetPasswordViewModel,
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        uiState = uiState,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSubmitClick = { viewModel.submitReset(onResetSuccess) },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ResetPasswordScreen(
    uiState: ResetPasswordUiState,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
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

        Text(
            text = "Password Baru",
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = DarkText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Validation Rules Checklist
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ValidationItem(
                text = "Password harus terdiri dari setidaknya 8 karakter.",
                isSatisfied = uiState.validation.hasMinLength
            )
            ValidationItem(
                text = "Password harus mengandung setidaknya satu huruf kapital dan satu huruf kecil.",
                isSatisfied = uiState.validation.hasUpperAndLower
            )
            ValidationItem(
                text = "Password harus mengandung setidaknya satu angka atau karakter khusus.",
                isSatisfied = uiState.validation.hasNumber
            )
            if (uiState.confirmPassword.isNotEmpty()) {
                ValidationItem(
                    text = "Konfirmasi kata sandi cocok.",
                    isSatisfied = uiState.validation.isMatching
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input Fields
        JelajahKotaPasswordTextField(
            value = uiState.newPassword,
            onValueChange = onNewPasswordChange,
            label = "New Password",
            placeholder = "masukkan kata sandi baru"
        )

        Spacer(modifier = Modifier.height(16.dp))

        JelajahKotaPasswordTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm Password",
            placeholder = "konfirmasi kata sandi baru"
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
                text = "Submit",
                onClick = onSubmitClick,
                enabled = uiState.validation.isValid
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cancel Button
        JelajahKotaOutlinedButton(
            text = "Cancel",
            onClick = onNavigateBack
        )
        }
    }
}

@Composable
private fun ValidationItem(
    text: String,
    isSatisfied: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Default.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isSatisfied) SuccessGreen else SubtitleText.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = if (isSatisfied) SuccessGreen else SubtitleText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordScreenPreview() {
    ResetPasswordScreen(
        uiState = ResetPasswordUiState(newPassword = "Pass123!"),
        onNewPasswordChange = {},
        onConfirmPasswordChange = {},
        onSubmitClick = {},
        onNavigateBack = {}
    )
}
