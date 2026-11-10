package com.arvian.jelajahkota.feature.places.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.NotificationBadge
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.UserProfile

@Composable
fun HomeTopBar(
    userProfile: UserProfile?,
    onSettingClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Avatar + Greeting
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avatarRes = userProfile?.avatarResId ?: DesignR.drawable.img_user_avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .rotate(-5.06f)
                    .clip(RoundedCornerShape(65.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "User profile avatar",
                    modifier = Modifier.size(46.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hai, ",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = DarkText
                )
                Text(
                    text = userProfile?.name ?: "AntekAntekAsing",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandBluePrimary
                )
            }
        }

        // Right: Setting & Notification Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Setting button
            IconButton(
                onClick = onSettingClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = DesignR.drawable.ic_setting),
                    contentDescription = "Pengaturan",
                    tint = DarkText,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Notification button with badge
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = DesignR.drawable.ic_notification_bell),
                        contentDescription = "Notifikasi",
                        tint = DarkText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                val badgeCount = userProfile?.unreadNotificationsCount ?: 0
                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 2.dp, end = 2.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(NotificationBadge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeCount.toString(),
                            fontFamily = Lato,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = White
                        )
                    }
                }
            }
        }
    }
}
