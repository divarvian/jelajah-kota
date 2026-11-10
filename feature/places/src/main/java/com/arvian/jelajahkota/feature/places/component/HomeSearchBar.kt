package com.arvian.jelajahkota.feature.places.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SearchPlaceholder
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Input Pill
        Row(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(White)
                .border(
                    width = 1.dp,
                    color = BrandBluePrimary,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = DesignR.drawable.ic_search),
                contentDescription = "Search icon",
                tint = DarkText.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = SearchPlaceholder
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = DarkText
                    ),
                    cursorBrush = SolidColor(BrandBluePrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Blue Filter Button
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BrandBluePrimary)
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = DesignR.drawable.ic_filter),
                contentDescription = "Filter pencarian",
                tint = White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
