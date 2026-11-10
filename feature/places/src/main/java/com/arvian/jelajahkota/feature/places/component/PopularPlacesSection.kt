package com.arvian.jelajahkota.feature.places.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.CardDarkOverlay
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import androidx.compose.foundation.shape.RoundedCornerShape
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Place

@Composable
fun PopularPlacesSection(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Destinasi Populer",
                fontFamily = Lato,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = DarkText
            )

            Text(
                text = "Lihat Semua",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = BrandBluePrimary,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }

        // Horizontal Card Carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(places, key = { it.id }) { place ->
                PopularPlaceCard(
                    place = place,
                    onClick = { onPlaceClick(place) }
                )
            }
        }
    }
}

@Composable
fun PopularPlaceCard(
    place: Place,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 200.dp, height = 200.dp)
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
    ) {
        // Background Place Image
        Image(
            painter = painterResource(id = place.imageResId),
            contentDescription = place.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Top Right Export Arrow Icon
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.NorthEast,
                contentDescription = "Buka detail tempat",
                tint = White,
                modifier = Modifier.size(16.dp)
            )
        }

        // Bottom Info Overlay Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .size(width = 180.dp, height = 58.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(CardDarkOverlay)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Place Name
                Text(
                    text = place.name,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Location row with Pin
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(11.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = place.location,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 9.sp,
                        color = White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Rating Stars + Score
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingStars(rating = place.rating)

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", place.rating),
                        fontFamily = Lato,
                        fontWeight = FontWeight.Medium,
                        fontSize = 9.sp,
                        color = White
                    )
                }
            }
        }
    }
}

@Composable
fun RatingStars(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val fullStars = rating.toInt().coerceIn(0, 5)
        for (i in 1..5) {
            val isFilled = i <= fullStars
            Icon(
                imageVector = if (isFilled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = StarYellow,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
