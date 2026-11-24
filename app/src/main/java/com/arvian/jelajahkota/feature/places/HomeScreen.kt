package com.arvian.jelajahkota.feature.places

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaAsyncImage
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.User
import com.arvian.jelajahkota.feature.places.component.CategorySection
import com.arvian.jelajahkota.feature.places.component.CitySelectorSection
import com.arvian.jelajahkota.feature.places.component.HomeHeadline
import com.arvian.jelajahkota.feature.places.component.HomeSearchBar
import com.arvian.jelajahkota.feature.places.component.HomeTopBar
import com.arvian.jelajahkota.feature.places.component.PopularPlacesSection
import java.util.Locale

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    onNavigateToPlaceDetail: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToExplore: (query: String, city: String?, category: String?) -> Unit = { _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onSearchClick = {
            onNavigateToExplore("", uiState.selectedCityName, uiState.selectedCategoryName)
        },
        onFilterClick = {
            onNavigateToExplore("", uiState.selectedCityName, uiState.selectedCategoryName)
        },
        onCityClick = { city -> viewModel.onCitySelected(city.name) },
        onClearCity = { viewModel.onCitySelected(null) },
        onCategoryClick = viewModel::onCategorySelected,
        onClearFilters = viewModel::onClearFilters,
        onPlaceClick = { place -> onNavigateToPlaceDetail(place.id) },
        onSeeAllPopularClick = {
            onNavigateToExplore("", uiState.selectedCityName, uiState.selectedCategoryName)
        },
        onExploreAllClick = {
            onNavigateToExplore("", uiState.selectedCityName, uiState.selectedCategoryName)
        },
        onProfileClick = onNavigateToProfile,
        onNotificationClick = onNavigateToNotifications,
        onRefresh = viewModel::onRefresh,
        bottomContentPadding = bottomContentPadding,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCityClick: (CityDestination) -> Unit,
    onClearCity: () -> Unit,
    onCategoryClick: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onPlaceClick: (Place) -> Unit,
    onSeeAllPopularClick: () -> Unit,
    onExploreAllClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
    bottomContentPadding: Dp = 0.dp
) {
    val colorScheme = MaterialTheme.colorScheme

    if (uiState.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colorScheme.primary)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HomeTopBar(
                user = uiState.user,
                unreadNotificationsCount = uiState.unreadNotificationsCount,
                activeLocationLabel = uiState.selectedCityName
                    ?: uiState.user?.city?.takeIf { it.isNotBlank() }
                    ?: "Malang Raya & Sekitarnya",
                onProfileClick = onProfileClick,
                onNotificationClick = onNotificationClick
            )

            HomeHeadline()

            HomeSearchBar(
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick
            )

            // Quick Category Filter Chips right below Search Bar (Airbnb / Traveloka pattern)
            if (uiState.categories.isNotEmpty()) {
                CategorySection(
                    categories = uiState.categories,
                    selectedCategoryName = uiState.selectedCategoryName,
                    onCategoryClick = onCategoryClick
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val popularTitle = when {
                !uiState.selectedCityName.isNullOrBlank() -> "Populer di ${uiState.selectedCityName}"
                !uiState.selectedCategoryName.isNullOrBlank() -> "Pilihan ${uiState.selectedCategoryName}"
                else -> "Destinasi Populer"
            }

            if (uiState.popularPlaces.isNotEmpty()) {
                PopularPlacesSection(
                    places = uiState.popularPlaces,
                    title = popularTitle,
                    userLocation = uiState.userLocation,
                    onPlaceClick = onPlaceClick,
                    onSeeAllClick = onSeeAllPopularClick
                )
            } else if (uiState.hasActiveFilter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Belum ada destinasi untuk kombinasi filter ini.",
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    JelajahKotaOutlinedButton(
                        text = "Reset Filter Beranda",
                        onClick = onClearFilters
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.activeCities.isNotEmpty()) {
                CitySelectorSection(
                    cities = uiState.activeCities,
                    placeCountByCity = uiState.placeCountByCity,
                    selectedCityName = uiState.selectedCityName,
                    onCityClick = onCityClick,
                    onClearCity = onClearCity
                )
            }

            if (uiState.recommendedPlaces.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HomeRecommendedSection(
                    places = uiState.recommendedPlaces,
                    userLocation = uiState.userLocation,
                    onPlaceClick = onPlaceClick,
                    onExploreAllClick = onExploreAllClick
                )
            }

            Spacer(modifier = Modifier.height(bottomContentPadding + 16.dp))
        }
    }
}

@Composable
private fun HomeRecommendedSection(
    places: List<Place>,
    userLocation: UserCoordinates?,
    onPlaceClick: (Place) -> Unit,
    onExploreAllClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = if (userLocation != null) "Rekomendasi Terdekat" else "Rekomendasi Untukmu",
            fontFamily = Lato,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            places.forEach { place ->
                val distanceLabel = userLocation?.let { coords ->
                    place.distanceKmFrom(coords.latitude, coords.longitude)?.let(Place::formatDistanceLabel)
                }
                HomeCompactPlaceCard(
                    place = place,
                    distanceLabel = distanceLabel,
                    onClick = { onPlaceClick(place) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        JelajahKotaOutlinedButton(
            text = "Jelajahi Semua di Eksplorasi",
            onClick = onExploreAllClick
        )
    }
}

@Composable
private fun HomeCompactPlaceCard(
    place: Place,
    distanceLabel: String?,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JelajahKotaAsyncImage(
                imageUrl = place.imageUrl,
                fallbackResId = place.imageResId,
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.category.ifBlank { place.city },
                        fontFamily = Lato,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (place.hasLiveRating) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = StarYellow,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f", place.rating),
                                fontFamily = Lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = place.name,
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = buildString {
                            append(place.city)
                            if (!distanceLabel.isNullOrBlank()) {
                                append(" • ")
                                append(distanceLabel)
                            }
                        },
                        fontFamily = Lato,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val shortPrice = place.ticketPrice.substringBefore("/").substringBefore("(").trim()
                    Text(
                        text = shortPrice,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AccessTime,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = place.duration,
                            fontFamily = Lato,
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    JelajahKotaTheme {
        HomeScreen(
            uiState = HomeUiState(
                user = User(
                    id = "1",
                    fullName = "Arvian Diva",
                    email = "arvian@jelajahkota.id"
                )
            ),
            onSearchClick = {},
            onFilterClick = {},
            onCityClick = {},
            onClearCity = {},
            onCategoryClick = {},
            onClearFilters = {},
            onPlaceClick = {},
            onSeeAllPopularClick = {},
            onExploreAllClick = {},
            onProfileClick = {},
            onNotificationClick = {}
        )
    }
}
