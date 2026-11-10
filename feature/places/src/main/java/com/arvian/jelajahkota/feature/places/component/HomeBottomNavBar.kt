package com.arvian.jelajahkota.feature.places.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.NavInactive
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.feature.places.HomeNavTab

@Composable
fun HomeBottomNavBar(
    selectedTab: HomeNavTab,
    onTabSelected: (HomeNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = Color(0xFFEEEEEE)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeNavItem(
                iconRes = DesignR.drawable.ic_nav_home,
                label = "Beranda",
                isSelected = selectedTab == HomeNavTab.BERANDA,
                onClick = { onTabSelected(HomeNavTab.BERANDA) }
            )

            HomeNavItem(
                iconRes = DesignR.drawable.ic_nav_explore,
                label = "Eksplorasi",
                isSelected = selectedTab == HomeNavTab.EKSPLORASI,
                onClick = { onTabSelected(HomeNavTab.EKSPLORASI) }
            )

            HomeNavItem(
                iconRes = DesignR.drawable.ic_nav_calendar,
                label = "Jadwalkan",
                isSelected = selectedTab == HomeNavTab.JADWALKAN,
                onClick = { onTabSelected(HomeNavTab.JADWALKAN) }
            )

            HomeNavItem(
                iconRes = DesignR.drawable.ic_nav_favorite,
                label = "Favorit",
                isSelected = selectedTab == HomeNavTab.FAVORIT,
                onClick = { onTabSelected(HomeNavTab.FAVORIT) }
            )

            HomeNavItem(
                iconRes = DesignR.drawable.ic_nav_profile,
                label = "Profile",
                isSelected = selectedTab == HomeNavTab.PROFILE,
                onClick = { onTabSelected(HomeNavTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun HomeNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (isSelected) BrandBluePrimary else DarkText.copy(alpha = 0.5f)
    val textColor = if (isSelected) BrandBluePrimary else NavInactive

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontFamily = Lato,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 10.sp,
            color = textColor
        )
    }
}
