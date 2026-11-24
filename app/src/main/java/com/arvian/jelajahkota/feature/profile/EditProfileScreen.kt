package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun EditProfileRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onSaved()
        }
    }

    EditProfileScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onBioChange = viewModel::onBioChange,
        onCityChange = viewModel::onCityChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onGenderChange = viewModel::onGenderChange,
        onSaveClick = viewModel::saveProfile,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun EditProfileScreen(
    uiState: ProfileUiState,
    onFullNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
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
            title = "Ubah Profil",
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
                text = "Atur profil Anda agar memudahkan interaksi dengan sesama penjelajah kota!",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SubtitleText
            )

            Spacer(modifier = Modifier.height(24.dp))

            JelajahKotaTextField(
                value = uiState.user.fullName,
                onValueChange = onFullNameChange,
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                value = uiState.user.phone,
                onValueChange = onPhoneChange,
                label = "Nomor Telepon",
                placeholder = "+62 812-xxxx-xxxx",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
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
                placeholder = "Ceritakan tentang petualanganmu...",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(14.dp))

            JelajahKotaTextField(
                value = uiState.user.city,
                onValueChange = onCityChange,
                label = "Kota Domisili",
                placeholder = "Kota Malang",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                value = uiState.user.birthDate,
                onValueChange = onBirthDateChange,
                label = "Tanggal Lahir",
                placeholder = "dd/mm/yyyy",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                value = uiState.user.gender,
                onValueChange = onGenderChange,
                label = "Jenis Kelamin",
                placeholder = "Laki-laki / Perempuan",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Wc,
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
                    fontSize = 12.sp,
                    color = ErrorRed
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                    text = "Simpan & Lanjutkan",
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
        onPhoneChange = {},
        onBioChange = {},
        onCityChange = {},
        onBirthDateChange = {},
        onGenderChange = {},
        onSaveClick = {},
        onNavigateBack = {}
    )
}
