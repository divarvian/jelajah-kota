package com.arvian.jelajahkota.feature.places.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.domain.model.CityDestination

@Composable
fun CitySelectorSection(
    cities: List<CityDestination>,
    selectedCityId: String?,
    onCityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Ingin berkunjung kemana?",
            fontFamily = Lato,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = DarkText,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(cities, key = { it.id }) { city ->
                val isSelected = city.id == selectedCityId
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onCityClick(city.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .rotate(-5.06f)
                            .clip(RoundedCornerShape(65.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.dp, BrandBluePrimary, RoundedCornerShape(65.dp))
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = city.imageResId),
                            contentDescription = city.name,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = city.name,
                        fontFamily = Lato,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        color = if (isSelected) BrandBluePrimary else DarkText
                    )
                }
            }
        }
    }
}
