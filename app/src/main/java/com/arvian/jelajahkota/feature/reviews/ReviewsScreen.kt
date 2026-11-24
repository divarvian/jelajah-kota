package com.arvian.jelajahkota.feature.reviews

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.RatingTrack
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.core.domain.model.Review
import kotlin.math.roundToInt

@Composable
fun ReviewsRoute(
    viewModel: ReviewsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onReviewSubmitted: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            viewModel.consumeSubmitSuccess()
            onReviewSubmitted()
        }
    }

    ReviewsScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSubmitReview = viewModel::submitReview,
        onDismissError = viewModel::clearError,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ReviewsScreen(
    uiState: ReviewsUiState,
    onSearchQueryChange: (String) -> Unit,
    onSubmitReview: (rating: Int, content: String) -> Unit,
    onDismissError: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWriteReviewDialog by remember { mutableStateOf(false) }

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
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            JelajahKotaTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Cari ulasan...",
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
            val filledStars = uiState.metrics.overallRating.roundToInt().coerceIn(0, 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < filledStars) StarYellow else RatingTrack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.metrics.totalReviews > 0) {
                            String.format(java.util.Locale.US, "%.2f", uiState.metrics.overallRating)
                        } else {
                            "0.0"
                        },
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = DarkText
                    )
                }

                Text(
                    text = "${uiState.metrics.totalReviews} Ulasan",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metrics Breakdown Progress Bars (Wisata-relevant Bahasa Indonesia)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricRow(name = "Kesesuaian", score = uiState.metrics.accuracy)
                MetricRow(name = "Lokasi", score = uiState.metrics.location)
                MetricRow(name = "Keramahan", score = uiState.metrics.communication)
                MetricRow(name = "Kebersihan", score = uiState.metrics.cleanliness)
                MetricRow(name = "Aksesibilitas", score = uiState.metrics.checkIn)
                MetricRow(name = "Nilai", score = uiState.metrics.value)
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
            } else if (uiState.filteredReviews.isEmpty()) {
                Text(
                    text = if (uiState.searchQuery.isNotBlank()) {
                        "Tidak ada ulasan yang cocok dengan pencarian \"${uiState.searchQuery}\"."
                    } else {
                        "Belum ada ulasan untuk destinasi ini. Jadilah yang pertama berbagi pengalaman!"
                    },
                    fontFamily = Lato,
                    fontSize = 14.sp,
                    color = SubtitleText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    uiState.filteredReviews.forEach { review ->
                        ReviewItemCard(review = review)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            JelajahKotaPrimaryButton(
                text = "Tulis Ulasan",
                onClick = {
                    onDismissError()
                    showWriteReviewDialog = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showWriteReviewDialog) {
        WriteReviewDialog(
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                onDismissError()
                showWriteReviewDialog = false
            },
            onSubmit = { rating, content ->
                onSubmitReview(rating, content)
                if (content.isNotBlank()) {
                    showWriteReviewDialog = false
                }
            }
        )
    }
}

@Composable
private fun WriteReviewDialog(
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, content: String) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tulis Ulasan",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pilih Rating",
                    fontFamily = Lato,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (star in 1..5) {
                        Icon(
                            imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Rating $star bintang",
                            tint = if (star <= selectedRating) StarYellow else SubtitleText,
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { selectedRating = star }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                JelajahKotaTextField(
                    value = reviewText,
                    onValueChange = {
                        reviewText = it
                        localError = null
                    },
                    label = "Pengalaman Anda",
                    placeholder = "Ceritakan kesan, fasilitas, atau tips berkunjung...",
                    singleLine = false
                )

                val displayError = localError ?: errorMessage
                if (!displayError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = displayError,
                        fontFamily = Lato,
                        fontSize = 12.sp,
                        color = ErrorRed
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSubmitting,
                onClick = {
                    if (reviewText.isBlank()) {
                        localError = "Isi ulasan tidak boleh kosong."
                    } else {
                        onSubmit(selectedRating, reviewText)
                    }
                }
            ) {
                Text(
                    text = "Kirim Ulasan",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    color = BrandBluePrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    fontFamily = Lato,
                    color = DarkText
                )
            }
        }
    )
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
            modifier = Modifier.width(120.dp)
        )

        LinearProgressIndicator(
            progress = { (score / 5.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape),
            color = DarkText,
            trackColor = RatingTrack,
            strokeCap = StrokeCap.Round
        )

        Text(
            text = String.format(java.util.Locale.US, "%.1f", score),
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
                            text = review.authorName.take(1).uppercase(),
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
                    text = String.format(java.util.Locale.US, "%.1f", review.rating),
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

        if (review.photoResIds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(review.photoResIds) { photoRes ->
                    Image(
                        painter = painterResource(id = photoRes),
                        contentDescription = "Foto ulasan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewsScreenPreview() {
    ReviewsScreen(
        uiState = ReviewsUiState(
            isLoading = false,
            reviews = listOf(
                Review(
                    id = "1",
                    placeId = "place_jodipan",
                    authorName = "Budi Santoso",
                    rating = 5.0,
                    date = "2 hari lalu",
                    content = "Tempatnya sangat bersih dan spot fotonya indah."
                )
            )
        ),
        onSearchQueryChange = {},
        onSubmitReview = { _, _ -> },
        onDismissError = {},
        onNavigateBack = {}
    )
}
