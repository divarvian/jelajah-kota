package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.model.UserProfile
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PlacesRepositoryImpl : PlacesRepository {

    override fun getUserProfile(): Flow<UserProfile> = flowOf(
        UserProfile(
            id = "user_1",
            name = "AntekAntekAsing",
            avatarResId = DesignR.drawable.img_user_avatar,
            unreadNotificationsCount = 5
        )
    )

    override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(
        listOf(
            CityDestination(id = "jogja", name = "Yogyakarta", imageResId = DesignR.drawable.img_city_yogyakarta),
            CityDestination(id = "jkt", name = "Jakarta", imageResId = DesignR.drawable.img_city_jakarta),
            CityDestination(id = "solo", name = "Solo", imageResId = DesignR.drawable.img_city_solo),
            CityDestination(id = "bdg", name = "Bandung", imageResId = DesignR.drawable.img_city_bandung),
            CityDestination(id = "sby", name = "Surabaya", imageResId = DesignR.drawable.img_city_surabaya),
            CityDestination(id = "pwt", name = "Purwokerto", imageResId = DesignR.drawable.img_city_jakarta),
            CityDestination(id = "lmg", name = "Lamongan", imageResId = DesignR.drawable.img_city_solo)
        )
    )

    override fun getPopularPlaces(): Flow<List<Place>> = flowOf(
        listOf(
            Place(
                id = "place_malioboro",
                name = "Malioboro",
                location = "Daerah Istimewa Yogyakarta",
                rating = 4.6,
                reviewCount = 1240,
                imageResId = DesignR.drawable.img_place_malioboro,
                isPopular = true,
                category = "City"
            ),
            Place(
                id = "place_kota_lama",
                name = "Kota Lama Semarang",
                location = "Kota Semarang, Jawa Tengah",
                rating = 4.6,
                reviewCount = 980,
                imageResId = DesignR.drawable.img_place_kota_lama,
                isPopular = true,
                category = "Heritage"
            )
        )
    )

    override fun getCategories(): Flow<List<CategoryItem>> = flowOf(
        listOf(
            CategoryItem(id = "cat_beach", name = "Beach", iconResId = DesignR.drawable.img_category_beach),
            CategoryItem(id = "cat_mountain", name = "Mountain", iconResId = DesignR.drawable.img_category_mountain)
        )
    )
}
