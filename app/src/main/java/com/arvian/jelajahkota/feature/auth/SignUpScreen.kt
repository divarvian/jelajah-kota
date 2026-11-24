package com.arvian.jelajahkota.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPasswordTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandOrangeAccent
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onContinueAsGuest: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.consumeSuccess()
            onSignUpSuccess()
        }
    }

    SignUpScreen(
        uiState = uiState,
        canNavigateBack = canNavigateBack,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onAgreeToTermsChange = viewModel::onAgreeToTermsChange,
        onSignUpClick = viewModel::signUp,
        onNavigateBack = onNavigateBack,
        onNavigateToSignIn = onNavigateToSignIn,
        onContinueAsGuest = onContinueAsGuest,
        modifier = modifier
    )
}

@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAgreeToTermsChange: (Boolean) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onContinueAsGuest: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            onNavigateBack = if (canNavigateBack) onNavigateBack else null
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
                    color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(BrandOrangeAccent)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Buat Akun Baru",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Daftar untuk mulai merencanakan petualangan di Kota Malang",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            JelajahKotaTextField(
                value = uiState.fullName,
                onValueChange = onFullNameChange,
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            JelajahKotaTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "nama@email.com",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            JelajahKotaPasswordTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Kata Sandi",
                placeholder = "Minimal 8 karakter",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            JelajahKotaPasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Konfirmasi Kata Sandi",
                placeholder = "Ulangi kata sandi",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSignUpClick() }
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAgreeToTermsChange(!uiState.agreeToTerms) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.agreeToTerms,
                    onCheckedChange = onAgreeToTermsChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.outlineVariant
                    )
                )
                val termsText = buildAnnotatedString {
                    append("Saya menyetujui ")
                    withStyle(style = SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append("Syarat Layanan")
                    }
                    append(" dan ")
                    withStyle(style = SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append("Kebijakan Privasi.")
                    }
                }
                Text(
                    text = termsText,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                    text = "Daftar",
                    onClick = onSignUpClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val annotatedString = buildAnnotatedString {
                append("Sudah punya akun? ")
                withStyle(style = SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.Bold)) {
                    append("Masuk.")
                }
            }

            Text(
                text = annotatedString,
                fontFamily = Lato,
                fontSize = 14.sp,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSignIn() }
                    .padding(vertical = 8.dp)
            )

            if (!canNavigateBack && onContinueAsGuest != null) {
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lanjutkan sebagai Tamu",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    JelajahKotaTheme {
        SignUpScreen(
            uiState = SignUpUiState(
                fullName = "Habib Ahmad",
                email = "habib@jelajahkota.id"
            ),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onAgreeToTermsChange = {},
            onSignUpClick = {},
            onNavigateBack = {},
            onNavigateToSignIn = {},
            onContinueAsGuest = {}
        )
    }
}
