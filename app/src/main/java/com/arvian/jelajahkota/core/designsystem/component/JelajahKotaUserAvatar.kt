package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.domain.validation.ProfileValidator

/**
 * Consistent user avatar across Home, Profile, Avatar Upload, and Reviews:
 * - Renders the user's real uploaded photo when [avatarUrl] is present.
 * - Renders a clean monogram initial badge (e.g. "AD") for signed-in users without a photo.
 * - Renders the standard [Icons.Rounded.Person] icon inside [primaryContainer] for Guest users.
 */
@Composable
fun JelajahKotaUserAvatar(
    avatarUrl: String?,
    fullName: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    contentDescription: String? = "Foto profil"
) {
    val colorScheme = MaterialTheme.colorScheme
    val cleanUrl = avatarUrl?.trim().takeIf { !it.isNullOrBlank() && !it.startsWith("local://") }
    val initials = ProfileValidator.initialsForName(fullName)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colorScheme.primaryContainer)
            .then(
                if (showBorder) {
                    Modifier.border(2.dp, colorScheme.primary, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            cleanUrl != null -> {
                JelajahKotaAsyncImage(
                    imageUrl = cleanUrl,
                    fallbackResId = 0,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            initials.isNotBlank() -> {
                val fontSizeSp = (size.value * 0.36f).coerceIn(13f, 44f).sp
                Text(
                    text = initials,
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSizeSp,
                    color = colorScheme.onPrimaryContainer
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = contentDescription,
                    tint = colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(size * 0.52f)
                )
            }
        }
    }
}
