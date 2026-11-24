package com.arvian.jelajahkota.feature.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.component.AuthRequiredDialog
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaAsyncImage
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.VisitorPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class PendingUpload(
    val previewUri: Uri,
    val bytes: ByteArray,
    val mimeType: String?
)

@Composable
fun PlaceGalleryRoute(
    viewModel: PlaceGalleryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingUpload by remember { mutableStateOf<PendingUpload?>(null) }
    var showAuthPrompt by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.actionMessage) {
        val message = uiState.actionMessage
        if (message != null) {
            viewModel.consumeActionMessage()
            onShowMessage(message)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val error = uiState.errorMessage
        if (error != null) {
            viewModel.clearError()
            onShowMessage(error)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val loaded = withContext(Dispatchers.IO) {
                    runCatching {
                        val mime = context.contentResolver.getType(uri)
                        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            ?: error("Gagal membaca gambar dari perangkat.")
                        PendingUpload(uri, bytes, mime)
                    }
                }
                loaded.fold(
                    onSuccess = { pendingUpload = it },
                    onFailure = { viewModel.reportError(it.message ?: "Gagal membuka foto yang dipilih.") }
                )
            }
        }
    }

    PlaceGalleryScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onUploadClick = {
            if (uiState.isLoggedIn) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                showAuthPrompt = true
            }
        },
        onDeletePhoto = viewModel::deletePhoto,
        modifier = modifier
    )

    if (showAuthPrompt) {
        AuthRequiredDialog(
            actionLabel = "mengunggah foto ke galeri pengunjung",
            onConfirmSignIn = {
                showAuthPrompt = false
                onNavigateToSignIn()
            },
            onDismiss = { showAuthPrompt = false }
        )
    }

    val draft = pendingUpload
    if (draft != null) {
        UploadCaptionDialog(
            previewUri = draft.previewUri,
            onDismiss = { pendingUpload = null },
            onConfirm = { caption ->
                pendingUpload = null
                viewModel.uploadPhoto(
                    bytes = draft.bytes,
                    declaredMimeType = draft.mimeType,
                    caption = caption
                )
            }
        )
    }
}

@Composable
fun PlaceGalleryScreen(
    uiState: PlaceGalleryUiState,
    onNavigateBack: () -> Unit,
    onUploadClick: () -> Unit,
    onDeletePhoto: (VisitorPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedPhoto by remember { mutableStateOf<VisitorPhoto?>(null) }
    var photoPendingDeletion by remember { mutableStateOf<VisitorPhoto?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            JelajahKotaTopAppBar(
                title = if (uiState.placeName.isNotBlank()) "Galeri • ${uiState.placeName}" else stringResource(R.string.gallery_title),
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (!uiState.isUploading) onUploadClick() },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                icon = {
                    if (uiState.isUploading) {
                        CircularProgressIndicator(
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = stringResource(R.string.gallery_upload)
                        )
                    }
                },
                text = {
                    Text(
                        text = stringResource(if (uiState.isUploading) R.string.gallery_uploading else R.string.gallery_upload),
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.photos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum Ada Foto Pengunjung",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jadilah yang pertama membagikan momen terbaikmu di destinasi ini! Format JPG/PNG/WebP maksimal 5 MB.",
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.photos, key = { it.id }) { photo ->
                        val isOwner = uiState.currentUser != null && photo.userId == uiState.currentUser.id
                        VisitorPhotoCard(
                            photo = photo,
                            isOwner = isOwner,
                            onClick = { selectedPhoto = photo },
                            onDeleteClick = { photoPendingDeletion = photo }
                        )
                    }
                }
            }
        }
    }

    val lightboxPhoto = selectedPhoto
    if (lightboxPhoto != null) {
        val isOwner = uiState.currentUser != null && lightboxPhoto.userId == uiState.currentUser.id
        PhotoLightboxDialog(
            photo = lightboxPhoto,
            isOwner = isOwner,
            onDismiss = { selectedPhoto = null },
            onDeleteClick = {
                selectedPhoto = null
                photoPendingDeletion = lightboxPhoto
            }
        )
    }

    val toDelete = photoPendingDeletion
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { photoPendingDeletion = null },
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface,
            textContentColor = colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = "Hapus Foto",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Yakin ingin menghapus foto ini dari galeri pengunjung?",
                    fontFamily = Lato,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePhoto(toDelete)
                        photoPendingDeletion = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { photoPendingDeletion = null }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        fontFamily = Lato,
                        color = colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@Composable
private fun VisitorPhotoCard(
    photo: VisitorPhoto,
    isOwner: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            JelajahKotaAsyncImage(
                imageUrl = photo.imageUrl,
                fallbackResId = photo.fallbackResId,
                contentDescription = photo.caption.ifBlank { "Foto oleh ${photo.uploaderName}" },
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                        )
                    )
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = photo.uploaderName,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (photo.caption.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = photo.caption,
                            fontFamily = Lato,
                            fontSize = 10.sp,
                            color = White.copy(alpha = 0.88f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (isOwner) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus foto saya",
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoLightboxDialog(
    photo: VisitorPhoto,
    isOwner: Boolean,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.94f))
        ) {
            JelajahKotaAsyncImage(
                imageUrl = photo.imageUrl,
                fallbackResId = photo.fallbackResId,
                contentDescription = photo.caption.ifBlank { "Foto pengunjung" },
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 72.dp, horizontal = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (isOwner) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus foto",
                            tint = White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photo.uploaderName,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = White
                    )
                    if (photo.createdAtLabel.isNotBlank()) {
                        Text(
                            text = photo.createdAtLabel,
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            color = White.copy(alpha = 0.75f)
                        )
                    }
                }
                if (photo.caption.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = photo.caption,
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = White.copy(alpha = 0.92f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadCaptionDialog(
    previewUri: Uri,
    onDismiss: () -> Unit,
    onConfirm: (caption: String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var caption by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        titleContentColor = colorScheme.onSurface,
        title = {
            Text(
                text = "Unggah Foto Pengunjung",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                JelajahKotaAsyncImage(
                    imageUrl = previewUri.toString(),
                    fallbackResId = 0,
                    contentDescription = "Pratinjau foto yang dipilih",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(modifier = Modifier.height(14.dp))
                JelajahKotaTextField(
                    value = caption,
                    onValueChange = { if (it.length <= 300) caption = it },
                    label = "Keterangan Foto (Opsional)",
                    placeholder = "Ceritakan momen di balik foto ini...",
                    singleLine = false
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(caption) }) {
                Text(
                    text = "Unggah",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    fontFamily = Lato,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
