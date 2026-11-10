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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.BrandOrangeAccent
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.GoogleButtonBorder
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onAgreeToTermsChange = viewModel::onAgreeToTermsChange,
        onSignUpClick = { viewModel.signUp(onSignUpSuccess) },
        onNavigateBack = onNavigateBack,
        onNavigateToSignIn = onNavigateToSignIn,
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

        Spacer(modifier = Modifier.height(24.dp))

        // Header Title
        Text(
            text = "Create an account.",
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = DarkText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Daftar untuk mulai menjelajah!",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        JelajahKotaTextField(
            value = uiState.fullName,
            onValueChange = onFullNameChange,
            label = "Nama",
            placeholder = "masukkan nama lengkap",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        JelajahKotaTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "masukkan email Anda",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        JelajahKotaPasswordTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = "masukkan password"
        )

        Spacer(modifier = Modifier.height(14.dp))

        JelajahKotaPasswordTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Konfirmasi Password",
            placeholder = "konfirmasi password"
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

        Spacer(modifier = Modifier.height(16.dp))

        // Terms Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.agreeToTerms,
                onCheckedChange = onAgreeToTermsChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = BrandBluePrimary,
                    uncheckedColor = BorderColor
                )
            )
            val termsText = buildAnnotatedString {
                append("Saya menyetujui ")
                withStyle(style = SpanStyle(color = BrandBluePrimary, fontWeight = FontWeight.Bold)) {
                    append("Syarat Layanan")
                }
                append(" dan ")
                withStyle(style = SpanStyle(color = BrandBluePrimary, fontWeight = FontWeight.Bold)) {
                    append("Kebijakan Privasi.")
                }
            }
            Text(
                text = termsText,
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = DarkText,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                text = "Daftar",
                onClick = onSignUpClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = BorderColor
            )
            Text(
                text = "  atau daftar dengan  ",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = SubtitleText
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = BorderColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-Up Button
        OutlinedButton(
            onClick = { /* OAuth workflow */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(30.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoogleButtonBorder)
        ) {
            Text(
                text = "G  Daftar dengan Google",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DarkText
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Footer: Link to Sign In
        val annotatedString = buildAnnotatedString {
            append("Sudah punya akun? ")
            withStyle(style = SpanStyle(color = BrandBluePrimary, fontWeight = FontWeight.Bold)) {
                append("Masuk.")
            }
        }

        Text(
            text = annotatedString,
            fontFamily = Lato,
            fontSize = 14.sp,
            color = DarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToSignIn() }
        )

        Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen(
        uiState = SignUpUiState(
            fullName = "Habib Ahmad",
            email = "antekantekasing@gmail.com"
        ),
        onFullNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onAgreeToTermsChange = {},
        onSignUpClick = {},
        onNavigateBack = {},
        onNavigateToSignIn = {}
    )
}
