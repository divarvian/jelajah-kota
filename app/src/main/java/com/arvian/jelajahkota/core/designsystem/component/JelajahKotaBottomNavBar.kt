package com.arvian.jelajahkota.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.theme.Lato

enum class JelajahKotaTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    BERANDA(
        labelRes = R.string.tab_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Rounded.Home
    ),
    EKSPLORASI(
        labelRes = R.string.tab_explore,
        icon = Icons.Outlined.Explore,
        selectedIcon = Icons.Rounded.Explore
    ),
    JADWALKAN(
        labelRes = R.string.tab_plans,
        icon = Icons.Outlined.CalendarMonth,
        selectedIcon = Icons.Rounded.CalendarMonth
    ),
    FAVORIT(
        labelRes = R.string.tab_favorites,
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Rounded.Favorite
    ),
    PROFILE(
        labelRes = R.string.tab_profile,
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
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val inactiveColor = colorScheme.onSurfaceVariant.copy(alpha = 0.72f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = colorScheme.outlineVariant.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JelajahKotaTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    val label = stringResource(id = tab.labelRes)
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val pressScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.90f else 1f,
                        animationSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = 700f
                        ),
                        label = "TabPressScale"
                    )

                    val indicatorProgress by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = 0.82f,
                            stiffness = 480f
                        ),
                        label = "TabIndicatorProgress"
                    )

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.06f else 1f,
                        animationSpec = spring(
                            dampingRatio = 0.78f,
                            stiffness = 500f
                        ),
                        label = "TabIconScale"
                    )

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) colorScheme.onPrimaryContainer else inactiveColor,
                        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                        label = "TabIconTint"
                    )

                    val labelColor by animateColorAsState(
                        targetValue = if (isSelected) colorScheme.primary else inactiveColor,
                        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                        label = "TabLabelColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .semantics(mergeDescendants = true) {
                                role = Role.Tab
                                selected = isSelected
                                contentDescription = label
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    if (!isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    onTabSelected(tab)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.graphicsLayer {
                                scaleX = pressScale
                                scaleY = pressScale
                            }
                        ) {
                            Box(
                                modifier = Modifier.size(width = 58.dp, height = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .graphicsLayer {
                                            scaleX = 0.35f + (0.65f * indicatorProgress)
                                            alpha = indicatorProgress.coerceIn(0f, 1f)
                                        }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(colorScheme.primaryContainer)
                                )

                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier
                                        .size(23.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = label,
                                fontFamily = Lato,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                color = labelColor,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
