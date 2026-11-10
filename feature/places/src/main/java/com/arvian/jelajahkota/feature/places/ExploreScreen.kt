package com.arvian.jelajahkota.feature.places

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueContainer
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Place

@Composable
fun ExploreRoute(
    viewModel: ExploreViewModel,
    onNavigateToPlaceDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onCitySelected = viewModel::onCitySelected,
        onCategorySelected = viewModel::onCategorySelected,
        onSortOptionSelected = viewModel::onSortOptionSelected,
        onToggleView = viewModel::onToggleView,
        onResetFilters = viewModel::onResetFilters,
        onPlaceClick = { place -> onNavigateToPlaceDetail(place.id) },
        modifier = modifier
    )
}

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    onSearchQueryChange: (String) -> Unit,
    onCitySelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSortOptionSelected: (ExploreSortOption) -> Unit,
    onToggleView: (Boolean) -> Unit,
    onResetFilters: () -> Unit,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
    ) {
        // 1. Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Eksplorasi Destinasi",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Jelajahi wisata terbaik di Kota Malang & sekitarnya",
                fontFamily = Lato,
                fontSize = 13.sp,
                color = SubtitleText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Search Bar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SubtitleText,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = "Cari nama, kategori, atau kota...",
                                fontFamily = Lato,
                                fontSize = 14.sp,
                                color = SubtitleText
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    AnimatedVisibility(
                        visible = uiState.searchQuery.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Hapus",
                                tint = SubtitleText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Category Filter Chips (Horizontal)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.availableCategories) { category ->
                val isSelected = (uiState.selectedCategory == category) ||
                        (uiState.selectedCategory == null && category == "Semua")

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) BrandBluePrimary else SurfaceGray,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onCategorySelected(category) }
                ) {
                    Text(
                        text = category,
                        fontFamily = Lato,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) White else DarkText,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. City Filter Chips (Horizontal)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.availableCities) { city ->
                val isSelected = (uiState.selectedCity == city) ||
                        (uiState.selectedCity == null && city == "Semua")

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) BrandBluePrimary else BorderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(if (isSelected) BrandBlueContainer else White)
                        .clickable { onCitySelected(city) }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = city,
                        fontFamily = Lato,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) BrandBluePrimary else DarkText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Result Status & Control Bar (Count, Sort, Grid/List Switch)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.filteredPlaces.size} Destinasi Ditemukan",
                fontFamily = Lato,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = DarkText
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sort Dropdown Button
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.selectedSortOption.label,
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandBluePrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        ExploreSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        fontFamily = Lato,
                                        fontWeight = if (option == uiState.selectedSortOption) FontWeight.Bold else FontWeight.Normal,
                                        color = if (option == uiState.selectedSortOption) BrandBluePrimary else DarkText
                                    )
                                },
                                onClick = {
                                    onSortOptionSelected(option)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Toggle View (Grid vs List)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceGray,
                    modifier = Modifier.size(32.dp)
                ) {
                    IconButton(
                        onClick = { onToggleView(!uiState.isGridView) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = if (uiState.isGridView) "☰" else "☷",
                            fontSize = 16.sp,
                            color = DarkText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 6. Main Content (Grid or Empty State)
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandBluePrimary)
            }
        } else if (uiState.filteredPlaces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🔍",
                        fontSize = 42.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Destinasi Tidak Ditemukan",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tidak ada destinasi yang cocok dengan filter atau kata kunci Anda.",
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        color = SubtitleText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    JelajahKotaPrimaryButton(
                        text = "Reset Semua Filter",
                        onClick = onResetFilters,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (uiState.isGridView) 2 else 1),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 84.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filteredPlaces, key = { it.id }) { place ->
                    if (uiState.isGridView) {
                        ExploreGridCard(place = place, onClick = { onPlaceClick(place) })
                    } else {
                        ExploreListCard(place = place, onClick = { onPlaceClick(place) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreGridCard(
    place: Place,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = place.imageResId),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = White.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = StarYellow,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${place.rating}",
                            fontFamily = Lato,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DarkText
                        )
                    }
                }

                // City Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkText.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = place.city,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = place.name,
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = place.category,
                    fontFamily = Lato,
                    fontSize = 11.sp,
                    color = SubtitleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.ticketPrice,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BrandBluePrimary
                    )

                    Text(
                        text = place.duration,
                        fontFamily = Lato,
                        fontSize = 10.sp,
                        color = SubtitleText
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreListCard(
    place: Place,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = place.imageResId),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.city,
                        fontFamily = Lato,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandBluePrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = StarYellow,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${place.rating} (${place.reviewCount})",
                            fontFamily = Lato,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DarkText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = place.name,
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = place.location,
                    fontFamily = Lato,
                    fontSize = 11.sp,
                    color = SubtitleText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.ticketPrice,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandBluePrimary
                    )

                    Text(
                        text = "Buka: ${place.openingHours}",
                        fontFamily = Lato,
                        fontSize = 11.sp,
                        color = SubtitleText
                    )
                }
            }
        }
    }
}
