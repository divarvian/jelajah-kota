package com.arvian.jelajahkota.feature.places.component

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaAsyncImage
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.domain.model.CityDestination

@Composable
fun CitySelectorSection(
    cities: List<CityDestination>,
    placeCountByCity: Map<String, Int>,
    selectedCityName: String?,
    onCityClick: (CityDestination) -> Unit,
    onClearCity: () -> Unit,
    modifier: Modifier = Modifier
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
                text = "Jelajahi Berdasarkan Kota",
                fontFamily = Lato,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = colorScheme.onBackground
            )

            if (!selectedCityName.isNullOrBlank()) {
                Text(
                    text = "Semua Kota",
                    fontFamily = Lato,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onClearCity)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cities, key = { it.id }) { city ->
                val isSelected = selectedCityName.equals(city.name, ignoreCase = true)
                val count = placeCountByCity[city.name.lowercase()] ?: 0

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onCityClick(city) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.5.dp, colorScheme.primary, CircleShape)
                                } else {
                                    Modifier.border(1.dp, colorScheme.outlineVariant, CircleShape)
                                }
                            )
                            .padding(if (isSelected) 3.dp else 0.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        JelajahKotaAsyncImage(
                            imageUrl = city.imageUrl,
                            fallbackResId = city.imageResId,
                            contentDescription = city.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = city.name,
                        fontFamily = Lato,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (isSelected) colorScheme.primary else colorScheme.onBackground
                    )

                    if (count > 0) {
                        Text(
                            text = "$count tempat",
                            fontFamily = Lato,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
