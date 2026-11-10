package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

/**
 * Standardized top app bar for JelajahKota with strict 56.dp height,
 * consistent 40.dp circular back button, and aligned title across all screens.
 */
@Composable
fun JelajahKotaTopAppBar(
    title: String = "",
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    titleColor: Color = DarkText,
    backButtonContainerColor: Color = SurfaceGray,
    backButtonIconColor: Color = DarkText,
    isTitleCentered: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (onNavigateBack != null) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backButtonContainerColor)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = backButtonIconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (isTitleCentered) TextAlign.Center else TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isTitleCentered) {
                            Modifier.padding(horizontal = 48.dp)
                        } else {
                            Modifier.padding(start = if (onNavigateBack != null) 52.dp else 0.dp, end = 48.dp)
                        }
                    )
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}
