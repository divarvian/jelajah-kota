package com.arvian.jelajahkota.feature.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.White
import kotlinx.coroutines.delay

/**
 * Screen 1 — Welcome / Custom Splash (Figma Node 415:300)
 *
 * Full-bleed city background with blue overlay gradient, centered Josefin Sans Bold
 * wordmark "JelajahKota", and 3D map pin illustration.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
    splashDelayMillis: Long = 1800L
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.92f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        delay(splashDelayMillis)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBluePrimary)
    ) {
        // Full-bleed city illustration/photo background with blue gradient overlay
        Image(
            painter = painterResource(id = DesignR.drawable.bg_splash_city),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Centered Brand Wordmark & 3D Map Pin
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "JelajahKota",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = DesignR.drawable.ic_map_pin_3d),
                contentDescription = "Pin Lokasi JelajahKota",
                modifier = Modifier.size(42.dp)
            )
        }
    }
}

@Preview(name = "Splash Reference 393x852", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun SplashScreenPreview() {
    JelajahKotaTheme {
        SplashScreen(onSplashFinished = {})
    }
}
