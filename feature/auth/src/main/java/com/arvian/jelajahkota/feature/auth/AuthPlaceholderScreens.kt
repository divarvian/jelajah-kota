package com.arvian.jelajahkota.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun SignInPlaceholderScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Masuk",
            fontFamily = JosefinSans,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = BrandBluePrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Halaman autentikasi/login JelajahKota akan diintegrasikan pada tahap berikutnya dengan Supabase Auth.",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(32.dp))

        JelajahKotaPrimaryButton(
            text = "Kembali ke Onboarding",
            onClick = onNavigateBack
        )
    }
}

@Composable
fun SignUpPlaceholderScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Daftar Akun",
            fontFamily = JosefinSans,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = BrandBluePrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Halaman pendaftaran akun JelajahKota akan diintegrasikan pada tahap berikutnya dengan Supabase Auth.",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(32.dp))

        JelajahKotaPrimaryButton(
            text = "Kembali ke Onboarding",
            onClick = onNavigateBack
        )
    }
}
