package com.arvian.jelajahkota.feature.places

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.UserProfile
import com.arvian.jelajahkota.feature.places.component.CategorySection
import com.arvian.jelajahkota.feature.places.component.CitySelectorSection
import com.arvian.jelajahkota.feature.places.component.HomeBottomNavBar
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
        onFilterClick = { /* Filter bottom sheet or dialog */ },
        onCityClick = viewModel::onCitySelected,
        onPlaceClick = { place -> onNavigateToPlaceDetail(place.id) },
        onSeeAllPopularClick = onNavigateToSeeAllPopular,
        onCategoryClick = viewModel::onCategorySelected,
        onSeeAllCategoriesClick = onNavigateToSeeAllCategories,
        onSettingClick = onNavigateToSettings,
        onNotificationClick = onNavigateToNotifications,
        onNavTabSelected = viewModel::onNavTabSelected,
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
    onNavTabSelected: (HomeNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(White),
        containerColor = White,
        bottomBar = {
            HomeBottomNavBar(
                selectedTab = uiState.selectedNavTab,
                onTabSelected = onNavTabSelected
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandBluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                // Destinasi Populer
                PopularPlacesSection(
                    places = uiState.popularPlaces,
                    onPlaceClick = onPlaceClick,
                    onSeeAllClick = onSeeAllPopularClick
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pilih Kategori
                CategorySection(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategory,
                    onCategoryClick = onCategoryClick,
                    onSeeAllClick = onSeeAllCategoriesClick
                )

                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 16.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun HomeScreenPreview() {
    JelajahKotaTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                userProfile = UserProfile(
                    id = "preview_user",
                    name = "AntekAntekAsing",
                    avatarResId = DesignR.drawable.img_user_avatar,
                    unreadNotificationsCount = 5
                ),
                searchQuery = "",
                cityDestinations = listOf(
                    CityDestination("jogja", "Yogyakarta", DesignR.drawable.img_city_yogyakarta),
                    CityDestination("jkt", "Jakarta", DesignR.drawable.img_city_jakarta),
                    CityDestination("solo", "Solo", DesignR.drawable.img_city_solo),
                    CityDestination("bdg", "Bandung", DesignR.drawable.img_city_bandung),
                    CityDestination("sby", "Surabaya", DesignR.drawable.img_city_surabaya)
                ),
                popularPlaces = listOf(
                    Place(
                        id = "malioboro",
                        name = "Malioboro",
                        location = "Daerah Istimewa Yogyakarta",
                        rating = 4.6,
                        reviewCount = 1240,
                        imageResId = DesignR.drawable.img_place_malioboro,
                        isPopular = true
                    ),
                    Place(
                        id = "kota_lama",
                        name = "Kota Lama Semarang",
                        location = "Kota Semarang, Jawa Tengah",
                        rating = 4.6,
                        reviewCount = 980,
                        imageResId = DesignR.drawable.img_place_kota_lama,
                        isPopular = true
                    )
                ),
                categories = listOf(
                    CategoryItem("beach", "Beach", DesignR.drawable.img_category_beach),
                    CategoryItem("mountain", "Mountain", DesignR.drawable.img_category_mountain)
                ),
                selectedNavTab = HomeNavTab.BERANDA
            ),
            onSearchQueryChange = {},
            onFilterClick = {},
            onCityClick = {},
            onPlaceClick = {},
            onSeeAllPopularClick = {},
            onCategoryClick = {},
            onSeeAllCategoriesClick = {},
            onSettingClick = {},
            onNotificationClick = {},
            onNavTabSelected = {}
        )
    }
}
