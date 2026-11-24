package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueContainer
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun AvatarUploadRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.avatarUpdated) {
        if (uiState.avatarUpdated) {
            viewModel.consumeAvatarUpdated()
            onSaved()
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onSaved()
        }
    }

    AvatarUploadScreen(
        uiState = uiState,
        onPickPhotoClick = {
            viewModel.uploadAvatar("local://avatar_${System.currentTimeMillis()}.webp")
        },
        onSaveClick = viewModel::saveProfile,
        onSkipClick = onSkip,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun AvatarUploadScreen(
    uiState: ProfileUiState,
    onPickPhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSkipClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        JelajahKotaTopAppBar(
            title = "Tambah Foto",
            onNavigateBack = onNavigateBack,
            actions = {
                Text(
                    text = "Lewati",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = SubtitleText,
                    modifier = Modifier
                        .clickable { onSkipClick() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            val avatarRes = uiState.user.avatarResId.takeIf { it != 0 } ?: R.drawable.img_user_avatar
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(BrandBlueContainer)
                    .border(2.dp, BrandBluePrimary, CircleShape)
                    .clickable(onClick = onPickPhotoClick),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Avatar Pengguna",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BrandBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Pilih Foto",
                        tint = White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            JelajahKotaOutlinedButton(
                text = "Pilih Foto Profil",
                onClick = onPickPhotoClick
            )

            Spacer(modifier = Modifier.weight(1f))

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
                    text = "Simpan",
                    onClick = onSaveClick
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarUploadScreenPreview() {
    AvatarUploadScreen(
        uiState = ProfileUiState(),
        onPickPhotoClick = {},
        onSaveClick = {},
        onSkipClick = {},
        onNavigateBack = {}
    )
}
