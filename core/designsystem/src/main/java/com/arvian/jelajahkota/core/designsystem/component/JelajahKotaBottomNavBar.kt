package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.NavInactive
import com.arvian.jelajahkota.core.designsystem.theme.White

enum class JelajahKotaTab(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    BERANDA(
        label = "Beranda",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Rounded.Home
    ),
    EKSPLORASI(
        label = "Eksplorasi",
        icon = Icons.Outlined.Explore,
        selectedIcon = Icons.Rounded.Explore
    ),
    JADWALKAN(
        label = "Jadwalkan",
        icon = Icons.Outlined.CalendarMonth,
        selectedIcon = Icons.Rounded.CalendarMonth
    ),
    FAVORIT(
        label = "Favorit",
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Rounded.Favorite
    ),
    PROFILE(
        label = "Profile",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Rounded.Person
    )
}

@Composable
fun JelajahKotaBottomNavBar(
    selectedTab: JelajahKotaTab,
    onTabSelected: (JelajahKotaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color(0xFFEEEEEE)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JelajahKotaTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab

                    val tint by animateColorAsState(
                        targetValue = if (isSelected) BrandBluePrimary else NavInactive,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "TabTintAnimation"
                    )

                    val iconSize by animateDpAsState(
                        targetValue = if (isSelected) 26.dp else 22.dp,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "TabIconSizeAnimation"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(tab) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                                contentDescription = tab.label,
                                tint = tint,
                                modifier = Modifier.size(iconSize)
                            )

                            AnimatedVisibility(
                                visible = !isSelected,
                                enter = fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(180)),
                                exit = fadeOut(animationSpec = tween(180)) + shrinkVertically(animationSpec = tween(180))
                            ) {
                                Text(
                                    text = tab.label,
                                    fontFamily = Lato,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = NavInactive,
                                    maxLines = 1,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
