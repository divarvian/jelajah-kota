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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPasswordTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SuccessGreen

@Composable
fun ResetPasswordRoute(
    viewModel: ResetPasswordViewModel,
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.consumeSuccess()
            onResetSuccess()
        }
    }

    ResetPasswordScreen(
        uiState = uiState,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSubmitClick = viewModel::submitReset,
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
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kata Sandi Baru",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ValidationItem(
                    text = "Minimal 8 karakter.",
                    isSatisfied = uiState.validation.hasMinLength
                )
                ValidationItem(
                    text = "Mengandung huruf kapital dan huruf kecil.",
                    isSatisfied = uiState.validation.hasUpperAndLower
                )
                ValidationItem(
                    text = "Mengandung angka.",
                    isSatisfied = uiState.validation.hasNumber
                )
                ValidationItem(
                    text = "Mengandung simbol khusus (!@#\$%...).",
                    isSatisfied = uiState.validation.hasSymbol
                )
                if (uiState.confirmPassword.isNotEmpty()) {
                    ValidationItem(
                        text = "Konfirmasi kata sandi cocok.",
                        isSatisfied = uiState.validation.isMatching
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            JelajahKotaPasswordTextField(
                value = uiState.newPassword,
                onValueChange = onNewPasswordChange,
                label = "Kata Sandi Baru",
                placeholder = "Masukkan kata sandi baru",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            JelajahKotaPasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Konfirmasi Kata Sandi",
                placeholder = "Konfirmasi kata sandi baru",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmitClick() }
                )
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
                    text = "Simpan Kata Sandi",
                    onClick = onSubmitClick,
                    enabled = uiState.validation.isValid
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            JelajahKotaOutlinedButton(
                text = "Batalkan",
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
    val colorScheme = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Default.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isSatisfied) SuccessGreen else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = if (isSatisfied) SuccessGreen else colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordScreenPreview() {
    JelajahKotaTheme {
        ResetPasswordScreen(
            uiState = ResetPasswordUiState(newPassword = "Pass123!"),
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onSubmitClick = {},
            onNavigateBack = {}
        )
    }
}
