package com.arvian.jelajahkota.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaUserAvatar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AvatarUploadRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.avatarUpdated) {
        if (uiState.avatarUpdated) {
            viewModel.consumeAvatarUpdated()
            onSaved()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val readResult = withContext(Dispatchers.IO) {
                    runCatching {
                        val mimeType = context.contentResolver.getType(uri)
                        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            ?: error("Tidak dapat membaca file gambar yang dipilih.")
                        Pair(bytes, mimeType)
                    }
                }
                readResult.fold(
                    onSuccess = { (bytes, mimeType) ->
                        viewModel.uploadAvatarBytes(bytes, mimeType)
                    },
                    onFailure = { error ->
                        viewModel.reportError(error.message ?: "Gagal membaca foto yang dipilih.")
                    }
                )
            }
        }
    }

    AvatarUploadScreen(
        uiState = uiState,
        onPickPhotoClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onSaveClick = onNavigateBack,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun AvatarUploadScreen(
    uiState: ProfileUiState,
    onPickPhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        JelajahKotaTopAppBar(
            title = "Ganti Foto Profil",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clickable(enabled = !uiState.isSaving, onClick = onPickPhotoClick),
                contentAlignment = Alignment.Center
            ) {
                JelajahKotaUserAvatar(
                    avatarUrl = uiState.user?.avatarUrl,
                    fullName = uiState.user?.fullName,
                    size = 156.dp,
                    showBorder = true,
                    contentDescription = "Avatar Pengguna"
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Pilih Foto",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Format JPG, PNG, atau WebP • Maksimal 2 MB",
                fontFamily = Lato,
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage,
                    fontFamily = Lato,
                    fontSize = 13.sp,
                    color = colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            JelajahKotaOutlinedButton(
                text = "Pilih Foto dari Galeri",
                onClick = onPickPhotoClick,
                enabled = !uiState.isSaving
            )

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isSaving) {
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
                    text = "Selesai",
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
    JelajahKotaTheme {
        AvatarUploadScreen(
            uiState = ProfileUiState(),
            onPickPhotoClick = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}
