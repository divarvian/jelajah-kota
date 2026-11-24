package com.arvian.jelajahkota.core.data.repository

import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.domain.model.CategoryItem
import com.arvian.jelajahkota.core.domain.model.CityDestination
import com.arvian.jelajahkota.core.domain.model.Place
import com.arvian.jelajahkota.core.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PlacesRepositoryImpl : PlacesRepository {

    private val cities = listOf(
        CityDestination(id = "malang", name = "Malang", imageResId = R.drawable.img_destination_kayutangan),
        CityDestination(id = "jogja", name = "Yogyakarta", imageResId = R.drawable.img_city_yogyakarta),
        CityDestination(id = "semarang", name = "Semarang", imageResId = R.drawable.img_place_kota_lama),
        CityDestination(id = "jkt", name = "Jakarta", imageResId = R.drawable.img_city_jakarta),
        CityDestination(id = "solo", name = "Solo", imageResId = R.drawable.img_city_solo),
        CityDestination(id = "bdg", name = "Bandung", imageResId = R.drawable.img_city_bandung),
        CityDestination(id = "sby", name = "Surabaya", imageResId = R.drawable.img_city_surabaya)
    )

    private val categories = listOf(
        CategoryItem(id = "cat_city", name = "Wisata Kota", iconResId = R.drawable.img_category_beach),
        CategoryItem(id = "cat_heritage", name = "Budaya & Sejarah", iconResId = R.drawable.img_destination_cafe),
        CategoryItem(id = "cat_mountain", name = "Pegunungan", iconResId = R.drawable.img_category_mountain),
        CategoryItem(id = "cat_nature", name = "Wisata Alam", iconResId = R.drawable.img_destination_waterfall)
    )

    private val allPlaces = MutableStateFlow(
        listOf(
            Place(
                id = "place_jodipan",
                name = "Kampung Warna-Warni Jodipan",
                location = "Jl. Juanda, Jodipan, Kec. Blimbing, Kota Malang",
                city = "Malang",
                rating = 4.7,
                reviewCount = 2120,
                imageResId = R.drawable.img_destination_jodipan,
                galleryImageResIds = listOf(
                    R.drawable.img_destination_jodipan,
                    R.drawable.img_destination_photo,
                    R.drawable.img_destination_cafe
                ),
                description = "Permukiman tepi Sungai Brantas yang disulap menjadi kanvas mural raksasa beraneka warna dengan jembatan kaca ikonik, lorong payung, dan ratusan spot foto kreatif karya mahasiswa dan seniman lokal.",
                duration = "1-2 Jam",
                ticketPrice = "Rp 5.000 / orang",
                ticketPriceAmount = 5_000,
                openingHours = "07.00 - 18.00 WIB",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = true,
                category = "Wisata Kota"
            ),
            Place(
                id = "place_kayutangan",
                name = "Kayutangan Heritage",
                location = "Jl. Jenderal Basuki Rahmat, Klojen, Kota Malang",
                city = "Malang",
                rating = 4.8,
                reviewCount = 1890,
                imageResId = R.drawable.img_destination_kayutangan,
                galleryImageResIds = listOf(
                    R.drawable.img_destination_kayutangan,
                    R.drawable.img_destination_cafe,
                    R.drawable.img_destination_night_view
                ),
                description = "Kawasan cagar budaya Kota Malang dengan deretan bangunan bersejarah peninggalan era Hindia Belanda, kafe vintage estetik, jalur pedestrian bergaya Eropa, dan pertunjukan musik jalanan akhir pekan.",
                duration = "2-3 Jam",
                ticketPrice = "Gratis",
                ticketPriceAmount = 0,
                openingHours = "24 Jam (Kafe: 08.00 - 23.00 WIB)",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = true,
                category = "Budaya & Sejarah"
            ),
            Place(
                id = "place_bromo",
                name = "Gunung Bromo & Lautan Pasir",
                location = "Taman Nasional Bromo Tengger Semeru, Jawa Timur",
                city = "Malang",
                rating = 4.9,
                reviewCount = 5600,
                imageResId = R.drawable.img_destination_bromo,
                galleryImageResIds = listOf(
                    R.drawable.img_destination_bromo,
                    R.drawable.img_destination_photo,
                    R.drawable.img_destination_night_view
                ),
                description = "Salah satu kaldera terindah di dunia dengan pemandangan magis matahari terbit dari Bukit Penanjakan, lautan pasir berbisik, Pura Luhur Poten, kawah belerang aktif, dan padang savana Bukit Teletubbies.",
                duration = "4-6 Jam",
                ticketPrice = "Rp 29.000 (Domestik) / Rp 320.000 (Mancanegara)",
                ticketPriceAmount = 29_000,
                openingHours = "00.00 - 18.00 WIB (Sunrise Tour)",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = true,
                category = "Pegunungan"
            ),
            Place(
                id = "place_alun_tugu",
                name = "Alun-Alun Tugu Malang",
                location = "Klojen, Kota Malang, Jawa Timur",
                city = "Malang",
                rating = 4.6,
                reviewCount = 1430,
                imageResId = R.drawable.img_destination_alun_tugu,
                galleryImageResIds = listOf(
                    R.drawable.img_destination_alun_tugu,
                    R.drawable.img_destination_night_view
                ),
                description = "Taman lingkar terbuka hijau pusat pemerintahan Kota Malang yang dikelilingi kolam teratai indah, tugu kemerdekaan bersejarah, dan air mancur menari dengan tata cahaya warna-warni di malam hari.",
                duration = "1-2 Jam",
                ticketPrice = "Gratis",
                ticketPriceAmount = 0,
                openingHours = "24 Jam",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = false,
                category = "Wisata Kota"
            ),
            Place(
                id = "place_waterfall",
                name = "Coban Rondo & Labirin",
                location = "Pandesari, Kec. Pujon, Kabupaten Malang",
                city = "Malang",
                rating = 4.6,
                reviewCount = 1950,
                imageResId = R.drawable.img_destination_waterfall,
                galleryImageResIds = listOf(
                    R.drawable.img_destination_waterfall,
                    R.drawable.img_destination_photo
                ),
                description = "Air terjun alami dengan ketinggian 84 meter di lereng Gunung Panderman yang sejuk, dilengkapi taman labirin tanaman hijau, wahana outbound, perkemahan pinus, dan udara pegunungan segar.",
                duration = "2-3 Jam",
                ticketPrice = "Rp 35.000 / orang",
                ticketPriceAmount = 35_000,
                openingHours = "08.00 - 17.00 WIB",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = false,
                category = "Wisata Alam"
            ),
            Place(
                id = "place_malioboro",
                name = "Malioboro",
                location = "Sosromenduran, Gedong Tengen, Kota Yogyakarta",
                city = "Yogyakarta",
                rating = 4.8,
                reviewCount = 3450,
                imageResId = R.drawable.img_place_malioboro,
                galleryImageResIds = listOf(
                    R.drawable.img_place_malioboro,
                    R.drawable.img_destination_night_view,
                    R.drawable.img_destination_culinary
                ),
                description = "Kawasan jalan ikonik di jantung kota Yogyakarta dengan pedestrian rindang, deretan seniman jalanan, pertokoan khas batik, kuliner gudeg, dan suasana malam heritage tempo dulu yang memikat jutaan pelancong.",
                duration = "2-3 Jam",
                ticketPrice = "Gratis",
                ticketPriceAmount = 0,
                openingHours = "24 Jam (Setiap Hari)",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = true,
                category = "Wisata Kota"
            ),
            Place(
                id = "place_kota_lama",
                name = "Kota Lama Semarang",
                location = "Tanjung Mas, Kec. Semarang Utara, Kota Semarang",
                city = "Semarang",
                rating = 4.7,
                reviewCount = 2800,
                imageResId = R.drawable.img_place_kota_lama,
                galleryImageResIds = listOf(
                    R.drawable.img_place_kota_lama,
                    R.drawable.img_destination_cafe
                ),
                description = "Little Netherland Indonesia seluas 31 hektare dengan lebih dari 50 gedung kuno megah yang telah direstorasi, Gereja Blenduk beratap kubah perunggu, pasar seni barang antik, dan taman srigunting.",
                duration = "2-3 Jam",
                ticketPrice = "Gratis",
                ticketPriceAmount = 0,
                openingHours = "08.00 - 22.00 WIB",
                operationalDays = "Setiap Hari (Senin - Minggu)",
                isPopular = true,
                category = "Budaya & Sejarah"
            )
        )
    )

    override fun getCityDestinations(): Flow<List<CityDestination>> = flowOf(cities)

    override fun getPopularPlaces(): Flow<List<Place>> = allPlaces.map { list ->
        list.filter { it.isPopular }
    }

    override fun getCategories(): Flow<List<CategoryItem>> = flowOf(categories)

    override fun getPlaces(): Flow<List<Place>> = allPlaces.asStateFlow()

    override fun getPlaceById(id: String): Flow<Place?> = allPlaces.map { list ->
        list.find { it.id == id }
    }

    override fun searchPlaces(
        query: String,
        cityId: String?,
        categoryId: String?
    ): Flow<List<Place>> = allPlaces.map { list ->
        val resolvedCityName = cityId?.let { id ->
            cities.find { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }?.name ?: id
        }
        val resolvedCategoryName = categoryId?.let { id ->
            categories.find { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }?.name ?: id
        }

        list.filter { place ->
            val matchesQuery = query.isBlank() ||
                place.name.contains(query, ignoreCase = true) ||
                place.location.contains(query, ignoreCase = true) ||
                place.city.contains(query, ignoreCase = true) ||
                place.category.contains(query, ignoreCase = true) ||
                place.description.contains(query, ignoreCase = true)

            val matchesCity = resolvedCityName.isNullOrBlank() ||
                place.city.equals(resolvedCityName, ignoreCase = true)

            val matchesCategory = resolvedCategoryName.isNullOrBlank() ||
                place.category.equals(resolvedCategoryName, ignoreCase = true)

            matchesQuery && matchesCity && matchesCategory
        }
    }
}
