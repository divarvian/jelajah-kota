package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.arvian.jelajahkota.core.designsystem.theme.PageIndicatorActive
import com.arvian.jelajahkota.core.designsystem.theme.PageIndicatorInactive

@Composable
fun PageIndicatorBars(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.semantics {
            contentDescription = "Halaman ${currentPage + 1} dari $pageCount"
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isCurrent = i == currentPage
            val color by animateColorAsState(
                targetValue = if (isCurrent) PageIndicatorActive else PageIndicatorInactive,
                label = "indicator_color_$i"
            )

            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
