package com.arvian.jelajahkota.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.component.DestinationCollage
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.OrangeRibbonTag
import com.arvian.jelajahkota.core.designsystem.component.PageIndicatorBars
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White

data class OnboardingSlideData(
    val titlePrefix: String,
    val highlightedWord: String,
    val description: String,
    val topLeftDrawable: Int,
    val topRightDrawable: Int,
    val bottomWideDrawable: Int
)

val OnboardingSlides = listOf(
    // Node 52:702 — Onboarding 1
    OnboardingSlideData(
        titlePrefix = "Jelajahi Destinasi",
        highlightedWord = "Kotamu!",
        description = "Temukan ratusan destinasi wisata menarik di sudut kotamu!",
        topLeftDrawable = DesignR.drawable.img_destination_jodipan,
        topRightDrawable = DesignR.drawable.img_destination_alun_tugu,
        bottomWideDrawable = DesignR.drawable.img_destination_kayutangan
    ),
    // Node 1:5 — Onboarding 2
    OnboardingSlideData(
        titlePrefix = "Rencanakan",
        highlightedWord = "Kunjunganmu",
        description = "Catat dan jadwalkan kunjunganmu ke tempat-tempat impian agar petualanganmu lebih teratur",
        topLeftDrawable = DesignR.drawable.img_destination_bromo,
        topRightDrawable = DesignR.drawable.img_destination_waterfall,
        bottomWideDrawable = DesignR.drawable.img_destination_cafe
    ),
    // Node 1:6 — Onboarding 3
    OnboardingSlideData(
        titlePrefix = "Bagikan",
        highlightedWord = "Pengalamanmu",
        description = "Berikan ulasan, rating, dan unggah foto terbaikmu untuk membantu sesama penjelajah kota",
        topLeftDrawable = DesignR.drawable.img_destination_culinary,
        topRightDrawable = DesignR.drawable.img_destination_night_view,
        bottomWideDrawable = DesignR.drawable.img_destination_photo
    )
)

/**
 * Onboarding Pager Screen implementing Figma Nodes:
 * - 52:702 (Onboarding Slide 1)
 * - 1:5 (Onboarding Slide 2)
 * - 1:6 (Onboarding Slide 3)
 */
@Composable
fun OnboardingScreen(
    onSkipClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPageSelected: (Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingSlides.size })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageSelected(page)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar with Skip (Lewati) Action
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Lewati",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = DarkText,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        role = Role.Button,
                        onClick = onSkipClick
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = "Lewati onboarding"
                    }
                    .padding(8.dp)
            )
        }

        // Horizontal Pager for the 3 Onboarding Slides
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val slide = OnboardingSlides[pageIndex]
            OnboardingSlideContent(slide = slide)
        }

        // Bottom Section: Indicator & Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 3-Bar Page Indicator reflecting active slide
            PageIndicatorBars(
                pageCount = OnboardingSlides.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Primary CTA: Masuk (filled blue pill)
            JelajahKotaPrimaryButton(
                text = "Masuk",
                onClick = onSignInClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary CTA: Daftar Akun (outlined pill)
            JelajahKotaOutlinedButton(
                text = "Daftar Akun",
                onClick = onSignUpClick
            )
        }
    }
}

@Composable
private fun OnboardingSlideContent(
    slide: OnboardingSlideData,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Upper Asymmetric 3-Image Destination Collage
        DestinationCollage(
            topLeftDrawable = slide.topLeftDrawable,
            topRightDrawable = slide.topRightDrawable,
            bottomWideDrawable = slide.bottomWideDrawable,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Headline with Orange Ribbon Tag
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = slide.titlePrefix,
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                textAlign = TextAlign.Center,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(4.dp))

            OrangeRibbonTag(
                text = slide.highlightedWord,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Supporting Copy
        Text(
            text = slide.description,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            color = SubtitleText,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(name = "Onboarding Slide 1 (52:702)", widthDp = 393, heightDp = 852, showBackground = true)
@Composable
private fun OnboardingSlide1Preview() {
    JelajahKotaTheme {
        OnboardingScreen(
            onSkipClick = {},
            onSignInClick = {},
            onSignUpClick = {}
        )
    }
}
