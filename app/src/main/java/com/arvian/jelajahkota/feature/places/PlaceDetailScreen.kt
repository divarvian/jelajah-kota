package com.arvian.jelajahkota.feature.places

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.AuthRequiredDialog
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaAsyncImage
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.HeartRed
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.White
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PlaceDetailRoute(
    viewModel: PlaceDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToReviews: (String) -> Unit = {},
    onNavigateToGallery: ((String) -> Unit)? = null,
    onNavigateToSignIn: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendingAuthAction by remember { mutableStateOf<String?>(null) }

    PlaceDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onToggleFavorite = {
            if (uiState.isLoggedIn) {
                viewModel.toggleFavorite()
            } else {
                pendingAuthAction = "menyimpan destinasi ke daftar favorit"
            }
        },
        onToggleDescription = viewModel::toggleDescriptionExpanded,
        onShareClick = {
            val place = uiState.place ?: return@PlaceDetailScreen
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, place.name)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Yuk kunjungi ${place.name} di ${place.location}! Harga tiket: ${place.ticketPrice}. Temukan destinasi menarik lainnya di JelajahKota."
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Bagikan destinasi"))
        },
        onPlanClick = {
            if (uiState.isLoggedIn) {
                onNavigateToSchedule()
            } else {
                pendingAuthAction = "menjadwalkan rencana kunjungan"
            }
        },
        onOpenMapsClick = {
            val place = uiState.place ?: return@PlaceDetailScreen
            val encodedName = Uri.encode(place.name)
            val geoUri = if (place.latitude != null && place.longitude != null) {
                "geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}($encodedName)".toUri()
            } else {
                "geo:0,0?q=${Uri.encode("${place.name}, ${place.location}")}".toUri()
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
            runCatching {
                context.startActivity(mapIntent)
            }.onFailure {
                val webUri = if (place.latitude != null && place.longitude != null) {
                    "https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}".toUri()
                } else {
                    "https://www.google.com/maps/search/?api=1&query=$encodedName".toUri()
                }
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, webUri)) }
            }
        },
        onNavigateToReviews = { uiState.place?.id?.let(onNavigateToReviews) },
        onNavigateToGallery = onNavigateToGallery?.let { nav ->
            { uiState.place?.id?.let(nav) }
        },
        modifier = modifier
    )

    val authReason = pendingAuthAction
    if (authReason != null) {
        AuthRequiredDialog(
            actionLabel = authReason,
            onConfirmSignIn = {
                pendingAuthAction = null
                onNavigateToSignIn()
            },
            onDismiss = { pendingAuthAction = null }
        )
    }
}

@Composable
fun PlaceDetailScreen(
    uiState: PlaceDetailUiState,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleDescription: () -> Unit,
    onShareClick: () -> Unit,
    onPlanClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenMapsClick: () -> Unit = {},
    onNavigateToReviews: () -> Unit = {},
    onNavigateToGallery: (() -> Unit)? = null
) {
    val place = uiState.place
    val colorScheme = MaterialTheme.colorScheme
    var previewPhotoMedia by remember { mutableStateOf<PlaceGalleryMedia?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            title = "Detail Destinasi",
            onNavigateBack = onNavigateBack,
            actions = {
                if (place != null) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (uiState.isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
                            tint = if (uiState.isFavorite) HeartRed else colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan destinasi",
                            tint = colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
            return
        }

        if (place == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Destinasi tidak ditemukan.",
                    fontFamily = Lato,
                    fontSize = 15.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
            return
        }

        val galleryMedia = uiState.galleryMedia

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        if (onNavigateToGallery != null) {
                            onNavigateToGallery()
                        } else {
                            previewPhotoMedia = PlaceGalleryMedia(
                                key = "hero_${place.id}",
                                imageUrl = place.imageUrl,
                                drawableResId = place.imageResId
                            )
                        }
                    }
            ) {
                JelajahKotaAsyncImage(
                    imageUrl = place.imageUrl,
                    fallbackResId = place.imageResId,
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkText.copy(alpha = 0.72f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${galleryMedia.size} Foto",
                            fontFamily = Lato,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorScheme.primaryContainer
                ) {
                    Text(
                        text = place.category,
                        fontFamily = Lato,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Lokasi",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = place.city,
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = place.name,
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onNavigateToReviews)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = StarYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (uiState.reviewsCount > 0 && uiState.effectiveRating > 0.0) {
                                    String.format(Locale.US, "%.1f", uiState.effectiveRating)
                                } else {
                                    "Baru"
                                },
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (uiState.reviewsCount > 0) "${uiState.reviewsCount} Ulasan" else "Beri Ulasan",
                            fontFamily = Lato,
                            fontSize = 10.sp,
                            color = colorScheme.primary
                        )
                    }
                }

                QuickInfoBadge(
                    icon = Icons.Rounded.Schedule,
                    label = "Durasi",
                    value = place.duration,
                    modifier = Modifier.weight(1f)
                )

                val displayTicket = place.ticketPrice.substringBefore("/").substringBefore("(").trim()
                QuickInfoBadge(
                    icon = Icons.Rounded.ConfirmationNumber,
                    label = "Tiket",
                    value = displayTicket,
                    modifier = Modifier.weight(1f)
                )

                QuickInfoBadge(
                    icon = Icons.Rounded.AccessTime,
                    label = "Buka",
                    value = place.openingHours.substringBefore("(").trim(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location & Direct Google Maps Action Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = place.location,
                            fontFamily = Lato,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        val distance = uiState.distanceLabel
                        if (!distance.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$distance dari lokasimu",
                                fontFamily = Lato,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        onClick = onOpenMapsClick,
                        shape = RoundedCornerShape(12.dp),
                        color = colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Directions,
                                contentDescription = "Buka rute di peta",
                                tint = colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Peta",
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tentang Tempat Ini",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = place.description.ifEmpty {
                    "Destinasi wisata unggulan yang menawarkan keindahan panorama, nilai sejarah, serta atmosfer ramah bagi wisatawan."
                },
                fontFamily = Lato,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = colorScheme.onSurfaceVariant,
                maxLines = if (uiState.isDescriptionExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (uiState.isDescriptionExpanded) "Sembunyikan" else "Baca Selengkapnya...",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onToggleDescription)
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Galeri Foto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Galeri Foto",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorScheme.onBackground
                )
                if (onNavigateToGallery != null) {
                    Text(
                        text = "Galeri Pengunjung >",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorScheme.primary,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToGallery)
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(galleryMedia, key = { it.key }) { media ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { previewPhotoMedia = media }
                    ) {
                        JelajahKotaAsyncImage(
                            imageUrl = media.imageUrl,
                            fallbackResId = media.drawableResId,
                            contentDescription = "Foto ${place.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ulasan Pengguna",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorScheme.onBackground
                )

                Text(
                    text = "Lihat Semua (${uiState.reviewsCount}) >",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onNavigateToReviews)
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val latestReview = uiState.latestReview
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onNavigateToReviews)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (latestReview != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = latestReview.authorName,
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colorScheme.onSurface
                            )
                            Row {
                                val filledStars = latestReview.rating.roundToInt().coerceIn(1, 5)
                                repeat(filledStars) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = StarYellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latestReview.content,
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "Belum ada ulasan untuk destinasi ini. Ketuk untuk menulis ulasan pertama!",
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Fixed Sticky Bottom Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Harga Tiket",
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = place.ticketPrice,
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (place.ticketPrice.length > 20) 14.sp else 18.sp,
                            color = colorScheme.primary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Button(
                        onClick = onPlanClick,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Rencanakan",
                            fontFamily = Lato,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    val lightboxMedia = previewPhotoMedia
    if (lightboxMedia != null) {
        Dialog(
            onDismissRequest = { previewPhotoMedia = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .clickable { previewPhotoMedia = null },
                contentAlignment = Alignment.Center
            ) {
                JelajahKotaAsyncImage(
                    imageUrl = lightboxMedia.imageUrl,
                    fallbackResId = lightboxMedia.drawableResId,
                    contentDescription = "Pratinjau foto ${place?.name.orEmpty()}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                IconButton(
                    onClick = { previewPhotoMedia = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup pratinjau",
                        tint = White
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickInfoBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    fontFamily = Lato,
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
