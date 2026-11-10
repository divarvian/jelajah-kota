package com.arvian.jelajahkota.feature.places.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueContainer
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.CategoryItem

@Composable
fun CategorySection(
    categories: List<CategoryItem>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit,
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
                text = "Pilih Kategori",
                fontFamily = Lato,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = DarkText
            )

            Text(
                text = "See all",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = BrandBluePrimary,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }

        // Category Pills Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category.id == selectedCategoryId
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(if (isSelected) BrandBlueContainer else White)
                        .border(
                            width = 1.dp,
                            color = BrandBluePrimary,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickable { onCategoryClick(category.id) }
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Thumbnail Image
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = category.iconResId),
                            contentDescription = category.name,
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = category.name,
                        fontFamily = Lato,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = DarkText
                    )
                }
            }
        }
    }
}
