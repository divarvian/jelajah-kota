package com.arvian.jelajahkota.feature.plans

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueContainer
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.HeartRed
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun PlaceDetailRoute(
    viewModel: PlansViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToReviews: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaceDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPlanClick = onNavigateToSchedule,
        onNavigateToReviews = { onNavigateToReviews(uiState.selectedPlace.id) },
        modifier = modifier
    )
}

@Composable
fun PlaceDetailScreen(
    uiState: PlansUiState,
    onNavigateBack: () -> Unit,
    onPlanClick: () -> Unit,
    onNavigateToReviews: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(uiState.selectedPlace.isFavorite) }
    var isExpandedDescription by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Consistent Sticky Top Bar
            JelajahKotaTopAppBar(
                title = "Detail Destinasi",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceGray)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = if (isFavorite) HeartRed else DarkText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { /* Share */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceGray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan",
                            tint = DarkText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )

            // 2. Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Hero Image Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = uiState.selectedPlace.imageResId),
                        contentDescription = uiState.selectedPlace.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Photo Count Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkText.copy(alpha = 0.7f),
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
                                text = "1/${(uiState.selectedPlace.galleryImageResIds.size + 1)} Foto",
                                fontFamily = Lato,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Place Name & City/Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandBlueContainer
                    ) {
                        Text(
                            text = uiState.selectedPlace.category,
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBluePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi",
                            tint = BrandBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = uiState.selectedPlace.city,
                            fontFamily = Lato,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiState.selectedPlace.name,
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Quick Info Row (Rating, Durasi, Tiket, Jam Buka)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Rating & Reviews (Clickable)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceGray,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onNavigateToReviews)
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.background(SurfaceGray)
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
                                    text = "${uiState.selectedPlace.rating}",
                                    fontFamily = Lato,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkText
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${uiState.selectedPlace.reviewCount} Ulasan",
                                fontFamily = Lato,
                                fontSize = 10.sp,
                                color = BrandBluePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Durasi
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceGray,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.background(SurfaceGray)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = SubtitleText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Durasi",
                                    fontFamily = Lato,
                                    fontSize = 11.sp,
                                    color = SubtitleText
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.selectedPlace.duration,
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DarkText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tiket
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceGray,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.background(SurfaceGray)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = SubtitleText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Tiket",
                                    fontFamily = Lato,
                                    fontSize = 11.sp,
                                    color = SubtitleText
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val displayTicket = if (uiState.selectedPlace.ticketPrice.contains("/")) {
                                uiState.selectedPlace.ticketPrice.substringBefore("/").trim()
                            } else {
                                uiState.selectedPlace.ticketPrice
                            }
                            Text(
                                text = displayTicket,
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = DarkText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Jam Buka
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceGray,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.background(SurfaceGray)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AccessTime,
                                    contentDescription = null,
                                    tint = SubtitleText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Buka",
                                    fontFamily = Lato,
                                    fontSize = 11.sp,
                                    color = SubtitleText
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.selectedPlace.openingHours,
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = DarkText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Deskripsi
                Text(
                    text = "Tentang Tempat Ini",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiState.selectedPlace.description.ifEmpty {
                        "Destinasi wisata unggulan yang menawarkan keindahan panorama, nilai sejarah, serta atmosfer yang ramah bagi wisatawan domestik maupun mancanegara."
                    },
                    fontFamily = Lato,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = SubtitleText,
                    maxLines = if (isExpandedDescription) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isExpandedDescription) "Sembunyikan" else "Baca Selengkapnya...",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandBluePrimary,
                    modifier = Modifier.clickable { isExpandedDescription = !isExpandedDescription }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Galeri Foto
                Text(
                    text = "Galeri Foto",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = painterResource(id = uiState.selectedPlace.imageResId),
                                contentDescription = "Foto Utama",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    items(uiState.selectedPlace.galleryImageResIds) { imageResId ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = "Galeri Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. Ulasan Pengguna Preview (Link ke ReviewsScreen)
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
                        color = DarkText
                    )

                    Text(
                        text = "Lihat Semua (${uiState.selectedPlace.reviewCount}) >",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandBluePrimary,
                        modifier = Modifier.clickable(onClick = onNavigateToReviews)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onNavigateToReviews)
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.background(SurfaceGray)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Diva Arvian",
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkText
                            )
                            Row {
                                repeat(5) {
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
                            text = "Tempatnya sangat asik dan instagramable! Pemandangannya luar biasa indah dan fasilitas di sekitar sangat memadai.",
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            color = SubtitleText,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // 7. Fixed Sticky Bottom Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = White,
                shadowElevation = 8.dp
            ) {
                Column {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Harga Tiket",
                                fontFamily = Lato,
                                fontSize = 12.sp,
                                color = SubtitleText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.selectedPlace.ticketPrice,
                                fontFamily = JosefinSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (uiState.selectedPlace.ticketPrice.length > 20) 14.sp else 18.sp,
                                color = BrandBluePrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Button(
                            onClick = onPlanClick,
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandBluePrimary,
                                contentColor = White
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Rencanakan",
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = White
                            )
                        }
                    }
                }
            }
        }
    }
}
