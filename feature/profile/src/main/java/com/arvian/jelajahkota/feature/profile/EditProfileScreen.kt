package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wc
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
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun EditProfileRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onBioChange = viewModel::onBioChange,
        onCityChange = viewModel::onCityChange,
        onPhoneChange = viewModel::onPhoneChange,
        onSaveClick = { viewModel.saveProfile(onSaved) },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun EditProfileScreen(
    uiState: ProfileUiState,
    onFullNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        JelajahKotaTopAppBar(
            title = "Profil",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = "Atur profil Anda agar memudahkan interaksi dengan jutaan penjelajah!",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Fields
        JelajahKotaTextField(
            value = uiState.user.fullName,
            onValueChange = onFullNameChange,
            label = "Username",
            placeholder = "Nama lengkap / username",
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
            value = uiState.user.bio,
            onValueChange = onBioChange,
            label = "Bio",
            placeholder = "Ceritakan tentang petualanganmu..."
        )

        Spacer(modifier = Modifier.height(14.dp))

        JelajahKotaTextField(
            value = uiState.user.city,
            onValueChange = onCityChange,
            label = "Lokasi",
            placeholder = "Suhat, Kota Malang",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        JelajahKotaTextField(
            value = "06/09/2000",
            onValueChange = { },
            label = "Tanggal Lahir",
            placeholder = "mm/dd/yyyy",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Cake,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        JelajahKotaTextField(
            value = "Laki-laki",
            onValueChange = { },
            label = "Jenis Kelamin",
            placeholder = "Laki-laki / Perempuan",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Wc,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
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
                text = "Lanjutkan",
                onClick = onSaveClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(
        uiState = ProfileUiState(),
        onFullNameChange = {},
        onBioChange = {},
        onCityChange = {},
        onPhoneChange = {},
        onSaveClick = {},
        onNavigateBack = {}
    )
}
