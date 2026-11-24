package com.arvian.jelajahkota.feature.places.component

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.common.location.UserCoordinates
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaAsyncImage
import com.arvian.jelajahkota.core.designsystem.theme.CardDarkOverlay
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Place
import java.util.Locale

@Composable
fun PopularPlacesSection(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Destinasi Populer",
    userLocation: UserCoordinates? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = Lato,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Lihat Semua",
                fontFamily = Lato,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(places, key = { it.id }) { place ->
                val distanceLabel = userLocation?.let { coords ->
                    place.distanceKmFrom(coords.latitude, coords.longitude)?.let(Place::formatDistanceLabel)
                }
                PopularPlaceCard(
                    place = place,
                    distanceLabel = distanceLabel,
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
    modifier: Modifier = Modifier,
    distanceLabel: String? = null
) {
    Box(
        modifier = modifier
            .size(width = 212.dp, height = 210.dp)
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClick)
    ) {
        JelajahKotaAsyncImage(
            imageUrl = place.imageUrl,
            fallbackResId = place.imageResId,
            contentDescription = place.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (!distanceLabel.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.56f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 12.dp, start = 12.dp)
            ) {
                Text(
                    text = distanceLabel,
                    fontFamily = Lato,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.42f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.NorthEast,
                contentDescription = "Buka detail tempat",
                tint = White,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDarkOverlay)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = place.name,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.85f),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = place.city,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (place.hasLiveRating) {
                        RatingStars(rating = place.rating)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", place.rating),
                            fontFamily = Lato,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = White
                        )
                    } else {
                        Text(
                            text = "Destinasi Baru • ${place.duration}",
                            fontFamily = Lato,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = White.copy(alpha = 0.88f)
                        )
                    }
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
