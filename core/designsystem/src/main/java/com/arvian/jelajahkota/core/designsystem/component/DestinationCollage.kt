package com.arvian.jelajahkota.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DestinationCollage(
    @DrawableRes topLeftDrawable: Int,
    @DrawableRes topRightDrawable: Int,
    @DrawableRes bottomWideDrawable: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: 2 smaller rounded cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .padding(end = 8.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = topLeftDrawable),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .padding(start = 8.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = topRightDrawable),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Bottom row: 1 wider rounded card layered below
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .offset(y = (-14).dp)
                .padding(horizontal = 12.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = bottomWideDrawable),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
