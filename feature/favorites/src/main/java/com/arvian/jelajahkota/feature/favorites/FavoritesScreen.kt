package com.arvian.jelajahkota.feature.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.HeartRed
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Place

@Composable
fun FavoritesRoute(
    viewModel: FavoritesViewModel,
    onNavigateToPlaceDetail: (String) -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onToggleFavorite = viewModel::toggleFavorite,
        onPlaceClick = onNavigateToPlaceDetail,
        onExploreClick = onNavigateToExplore,
        modifier = modifier
    )
}

@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    onExploreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Sticky Header: root tab has no back button
        JelajahKotaTopAppBar(
            title = "Tempat Favorit",
            isTitleCentered = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle
            Text(
                text = "Semua destinasi yang Anda simpan tersusun rapi di sini.",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = SubtitleText
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            JelajahKotaTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Cari di daftar favorit...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BrandBluePrimary
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content Area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandBluePrimary)
                }
            } else if (uiState.filteredPlaces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SurfaceGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = SubtitleText.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "Tidak Ditemukan" else "Belum Ada Favorit",
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (uiState.searchQuery.isNotBlank())
                                "Tidak ada tempat favorit yang sesuai dengan kata kunci \"${uiState.searchQuery}\"."
                            else
                                "Jelajahi berbagai destinasi menarik dan ketuk tombol hati untuk menyimpannya di sini.",
                            fontFamily = Lato,
                            fontSize = 13.sp,
                            color = SubtitleText,
                            textAlign = TextAlign.Center
                        )

                        if (uiState.searchQuery.isBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            JelajahKotaPrimaryButton(
                                text = "Mulai Jelajahi",
                                onClick = onExploreClick,
                                modifier = Modifier.fillMaxWidth(0.6f)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tersimpan",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText
                    )
                    Text(
                        text = "${uiState.filteredPlaces.size} Tempat",
                        fontFamily = Lato,
                        fontSize = 12.sp,
                        color = SubtitleText
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 84.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = uiState.filteredPlaces,
                        key = { it.id }
                    ) { place ->
                        FavoritePlaceCard(
                            place = place,
                            onToggleFavorite = { onToggleFavorite(place.id) },
                            onPlaceClick = { onPlaceClick(place.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritePlaceCard(
    place: Place,
    onToggleFavorite: () -> Unit,
    onPlaceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BrandBluePrimary.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .background(White)
            .clickable { onPlaceClick() }
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
        ) {
            Image(
                painter = painterResource(id = place.imageResId),
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Heart Bookmark Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.92f))
                    .clickable { onToggleFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Hapus Favorit",
                    tint = HeartRed,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = place.name,
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = DarkText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = SubtitleText,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = place.location,
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = SubtitleText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    FavoritesScreen(
        uiState = FavoritesUiState(
            favoritePlaces = listOf(
                Place(
                    id = "1",
                    name = "Coban Rondo",
                    location = "Pujon, Kabupaten Malang",
                    rating = 4.8,
                    imageResId = DesignR.drawable.img_place_malioboro
                )
            )
        ),
        onSearchQueryChange = {},
        onToggleFavorite = {},
        onPlaceClick = {},
        onExploreClick = {}
    )
}
