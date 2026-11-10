package com.arvian.jelajahkota.feature.places

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.UserProfile
import com.arvian.jelajahkota.feature.places.component.CategorySection
import com.arvian.jelajahkota.feature.places.component.CitySelectorSection
import com.arvian.jelajahkota.feature.places.component.HomeHeadline
import com.arvian.jelajahkota.feature.places.component.HomeSearchBar
import com.arvian.jelajahkota.feature.places.component.HomeTopBar
import com.arvian.jelajahkota.feature.places.component.PopularPlacesSection

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onNavigateToPlaceDetail: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToSeeAllPopular: () -> Unit = {},
    onNavigateToSeeAllCategories: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onFilterClick = { /* Filter action */ },
        onCityClick = viewModel::onCitySelected,
        onPlaceClick = { place -> onNavigateToPlaceDetail(place.id) },
        onSeeAllPopularClick = onNavigateToSeeAllPopular,
        onCategoryClick = viewModel::onCategorySelected,
        onSeeAllCategoriesClick = onNavigateToSeeAllCategories,
        onSettingClick = onNavigateToSettings,
        onNotificationClick = onNavigateToNotifications,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onCityClick: (String) -> Unit,
    onPlaceClick: (Place) -> Unit,
    onSeeAllPopularClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onSeeAllCategoriesClick: () -> Unit,
    onSettingClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandBluePrimary)
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(White)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header: Avatar, Greeting, Settings & Notifications
            HomeTopBar(
                userProfile = uiState.userProfile,
                onSettingClick = onSettingClick,
                onNotificationClick = onNotificationClick
            )

            // Main Headline
            HomeHeadline()

            // Search Bar with Filter Button
            HomeSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                onFilterClick = onFilterClick
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Ingin berkunjung kemana? (City Selection)
            CitySelectorSection(
                cities = uiState.cityDestinations,
                selectedCityId = uiState.selectedCity,
                onCityClick = onCityClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Destinasi Populer / Filtered Places
            if (uiState.filteredPlaces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Destinasi Tidak Ditemukan",
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Coba gunakan kata kunci lain atau bersihkan filter kota/kategori.",
                            fontFamily = Lato,
                            fontSize = 13.sp,
                            color = SubtitleText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                PopularPlacesSection(
                    places = uiState.filteredPlaces,
                    onPlaceClick = onPlaceClick,
                    onSeeAllClick = onSeeAllPopularClick
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pilih Kategori
            CategorySection(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategory,
                onCategoryClick = onCategoryClick,
                onSeeAllClick = onSeeAllCategoriesClick
            )

            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    JelajahKotaTheme {
        HomeScreen(
            uiState = HomeUiState(
                userProfile = UserProfile(
                    id = "1",
                    name = "Arvian",
                    avatarResId = 0
                )
            ),
            onSearchQueryChange = {},
            onFilterClick = {},
            onCityClick = {},
            onPlaceClick = {},
            onSeeAllPopularClick = {},
            onCategoryClick = {},
            onSeeAllCategoriesClick = {},
            onSettingClick = {},
            onNotificationClick = {}
        )
    }
}
