package com.arvian.jelajahkota.feature.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.RatingTrack
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Review

@Composable
fun ReviewsRoute(
    viewModel: ReviewsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewsScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ReviewsScreen(
    uiState: ReviewsUiState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        JelajahKotaTopAppBar(
            title = "Rating dan Ulasan",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        JelajahKotaTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Cari Ulasan...",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = BrandBluePrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Overall Rating Summary Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gold Stars
                Row {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${uiState.metrics.overallRating}",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = DarkText
                )
            }

            Text(
                text = "${uiState.metrics.totalReviews} Reviews",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkText
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Metrics Breakdown Progress Bars
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricRow(name = "Accuracy", score = uiState.metrics.accuracy)
            MetricRow(name = "Location", score = uiState.metrics.location)
            MetricRow(name = "Communication", score = uiState.metrics.communication)
            MetricRow(name = "Cleanliness", score = uiState.metrics.cleanliness)
            MetricRow(name = "Check-in", score = uiState.metrics.checkIn)
            MetricRow(name = "Value", score = uiState.metrics.value)
        }

        Spacer(modifier = Modifier.height(28.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(24.dp))

        // Review List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandBluePrimary)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                uiState.filteredReviews.forEach { review ->
                    ReviewItemCard(review = review)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Write Review CTA
        JelajahKotaPrimaryButton(
            text = "Tulis Ulasan",
            onClick = { /* Submit review dialog */ }
        )

        Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MetricRow(name: String, score: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = DarkText,
            modifier = Modifier.width(130.dp)
        )

        LinearProgressIndicator(
            progress = { (score / 5.0).toFloat() },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape),
            color = DarkText,
            trackColor = RatingTrack,
            strokeCap = StrokeCap.Round
        )

        Text(
            text = "$score",
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = DarkText,
            textAlign = TextAlign.End,
            modifier = Modifier.width(44.dp)
        )
    }
}

@Composable
private fun ReviewItemCard(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceGray)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (review.authorAvatarResId != 0) {
                    Image(
                        painter = painterResource(id = review.authorAvatarResId),
                        contentDescription = review.authorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.authorName.take(1),
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = review.authorName,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText
                    )
                    Text(
                        text = review.date,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = SubtitleText
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${review.rating}",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarkText
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = review.content,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = DarkText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewsScreenPreview() {
    ReviewsScreen(
        uiState = ReviewsUiState(
            reviews = listOf(
                Review(
                    id = "1",
                    placeId = "1",
                    authorName = "Opeyemi",
                    rating = 5.0,
                    date = "2 Days ago",
                    content = "Great location, very easy to find and access. Super easy and swift check-in process. Beautiful, clean and spacious rooms."
                )
            )
        ),
        onSearchQueryChange = {},
        onNavigateBack = {}
    )
}
