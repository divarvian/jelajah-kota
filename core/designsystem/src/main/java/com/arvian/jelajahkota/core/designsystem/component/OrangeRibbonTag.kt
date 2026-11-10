package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.OrangeRibbon
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun OrangeRibbonTag(
    text: String,
    modifier: Modifier = Modifier,
    rotation: Float = -2.5f
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .background(
                color = OrangeRibbon,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = JosefinSans,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = White
        )
    }
}
