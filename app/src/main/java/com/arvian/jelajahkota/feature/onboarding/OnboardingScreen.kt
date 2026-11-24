package com.arvian.jelajahkota.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.component.DestinationCollage
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.OrangeRibbonTag
import com.arvian.jelajahkota.core.designsystem.component.PageIndicatorBars
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato

data class OnboardingSlideData(
    val titlePrefix: String,
    val highlightedWord: String,
    val description: String,
    val topLeftDrawable: Int,
    val topRightDrawable: Int,
    val bottomWideDrawable: Int
)

val OnboardingSlides = listOf(
    OnboardingSlideData(
        titlePrefix = "Jelajahi Destinasi Sudut",
        highlightedWord = "Kotamu!",
        description = "Temukan ratusan destinasi wisata menarik di sudut kotamu!",
        topLeftDrawable = R.drawable.img_destination_jodipan,
        topRightDrawable = R.drawable.img_destination_alun_tugu,
        bottomWideDrawable = R.drawable.img_destination_kayutangan
    ),
    OnboardingSlideData(
        titlePrefix = "Rencanakan",
        highlightedWord = "Kunjunganmu",
        description = "Catat dan jadwalkan kunjunganmu ke tempat-tempat impian agar petualanganmu lebih teratur",
        topLeftDrawable = R.drawable.img_destination_bromo,
        topRightDrawable = R.drawable.img_destination_waterfall,
        bottomWideDrawable = R.drawable.img_destination_cafe
    ),
    OnboardingSlideData(
        titlePrefix = "Bagikan",
        highlightedWord = "Pengalamanmu",
        description = "Berikan ulasan, rating, dan unggah foto terbaikmu untuk membantu sesama penjelajah kota",
        topLeftDrawable = R.drawable.img_destination_culinary,
        topRightDrawable = R.drawable.img_destination_night_view,
        bottomWideDrawable = R.drawable.img_destination_photo
    )
)

@Composable
fun OnboardingScreen(
    onSkipClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPageSelected: (Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingSlides.size })
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageSelected(page)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            val skipCd = stringResource(R.string.onboarding_skip_cd)
            Text(
                text = stringResource(R.string.onboarding_skip),
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        role = Role.Button,
                        onClick = onSkipClick
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = skipCd
                    }
                    .padding(8.dp)
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            OnboardingSlideContent(slide = OnboardingSlides[pageIndex])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PageIndicatorBars(
                pageCount = OnboardingSlides.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            JelajahKotaPrimaryButton(
                text = stringResource(R.string.onboarding_sign_in),
                onClick = onSignInClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            JelajahKotaOutlinedButton(
                text = stringResource(R.string.onboarding_sign_up),
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
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        DestinationCollage(
            topLeftDrawable = slide.topLeftDrawable,
            topRightDrawable = slide.topRightDrawable,
            bottomWideDrawable = slide.bottomWideDrawable,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            OrangeRibbonTag(
                text = slide.highlightedWord,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = slide.description,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(name = "Onboarding Slide 1", widthDp = 393, heightDp = 852, showBackground = true)
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
