package com.arvian.jelajahkota.feature.reviews

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.AuthRequiredDialog
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaLocalImage
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaUserAvatar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.StarYellow
import com.arvian.jelajahkota.core.domain.model.Review
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ReviewsRoute(
    viewModel: ReviewsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSignIn: () -> Unit = {},
    onShowMessage: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.actionMessage) {
        val message = uiState.actionMessage
        if (message != null) {
            viewModel.consumeActionMessage()
            viewModel.consumeSubmitSuccess()
            onShowMessage(message)
        }
    }

    ReviewsScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSubmitReview = viewModel::submitReview,
        onUpdateReview = viewModel::updateReview,
        onDeleteReview = viewModel::deleteReview,
        onDismissError = viewModel::clearError,
        onNavigateBack = onNavigateBack,
        onNavigateToSignIn = onNavigateToSignIn,
        modifier = modifier
    )
}

@Composable
fun ReviewsScreen(
    uiState: ReviewsUiState,
    onSearchQueryChange: (String) -> Unit,
    onSubmitReview: (rating: Int, content: String) -> Unit,
    onUpdateReview: (reviewId: String, rating: Int, content: String) -> Unit,
    onDeleteReview: (reviewId: String) -> Unit,
    onDismissError: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSignIn: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    var showWriteReviewDialog by remember { mutableStateOf(false) }
    var editingReview by remember { mutableStateOf<Review?>(null) }
    var deletingReview by remember { mutableStateOf<Review?>(null) }
    var showAuthPrompt by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
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
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                                tint = if (index < filledStars) StarYellow else colorScheme.outlineVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.metrics.totalReviews > 0) {
                            String.format(Locale.US, "%.2f", uiState.metrics.overallRating)
                        } else {
                            "0.0"
                        },
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = colorScheme.onBackground
                    )
                }

                Text(
                    text = "${uiState.metrics.totalReviews} Ulasan",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Honest 5-star to 1-star distribution computed strictly from real reviews
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (star in 5 downTo 1) {
                    StarDistributionRow(
                        star = star,
                        fraction = uiState.metrics.fractionForStar(star),
                        count = uiState.metrics.countForStar(star)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
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
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    uiState.filteredReviews.forEach { review ->
                        val isOwner = uiState.currentUser != null &&
                            review.userId.isNotBlank() &&
                            review.userId == uiState.currentUser.id

                        ReviewItemCard(
                            review = review,
                            isOwner = isOwner,
                            onEditClick = { editingReview = review },
                            onDeleteClick = { deletingReview = review }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            JelajahKotaPrimaryButton(
                text = "Tulis Ulasan",
                onClick = {
                    if (uiState.isLoggedIn) {
                        onDismissError()
                        showWriteReviewDialog = true
                    } else {
                        showAuthPrompt = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showAuthPrompt) {
        AuthRequiredDialog(
            actionLabel = "menulis ulasan untuk destinasi ini",
            onConfirmSignIn = {
                showAuthPrompt = false
                onNavigateToSignIn()
            },
            onDismiss = { showAuthPrompt = false }
        )
    }

    if (showWriteReviewDialog) {
        ReviewEditorDialog(
            title = "Tulis Ulasan",
            confirmLabel = "Kirim Ulasan",
            initialRating = 5,
            initialContent = "",
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

    val reviewToEdit = editingReview
    if (reviewToEdit != null) {
        ReviewEditorDialog(
            title = "Edit Ulasan",
            confirmLabel = "Simpan",
            initialRating = reviewToEdit.rating.roundToInt().coerceIn(1, 5),
            initialContent = reviewToEdit.content,
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                onDismissError()
                editingReview = null
            },
            onSubmit = { rating, content ->
                onUpdateReview(reviewToEdit.id, rating, content)
                if (content.isNotBlank()) {
                    editingReview = null
                }
            }
        )
    }

    val reviewToDelete = deletingReview
    if (reviewToDelete != null) {
        DeleteReviewConfirmDialog(
            onConfirm = {
                onDeleteReview(reviewToDelete.id)
                deletingReview = null
            },
            onDismiss = { deletingReview = null }
        )
    }
}

@Composable
internal fun ReviewEditorDialog(
    title: String,
    confirmLabel: String,
    initialRating: Int,
    initialContent: String,
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, content: String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedRating by remember(initialRating) { mutableIntStateOf(initialRating) }
    var reviewText by remember(initialContent) { mutableStateOf(initialContent) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        titleContentColor = colorScheme.onSurface,
        title = {
            Text(
                text = title,
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
                    color = colorScheme.onSurface
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
                            tint = if (star <= selectedRating) StarYellow else colorScheme.onSurfaceVariant,
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
                        color = colorScheme.error
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
                    text = confirmLabel,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    fontFamily = Lato,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
internal fun DeleteReviewConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        titleContentColor = colorScheme.onSurface,
        textContentColor = colorScheme.onSurfaceVariant,
        title = {
            Text(
                text = "Hapus Ulasan",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Yakin ingin menghapus ulasan Anda ini? Tindakan tidak dapat dibatalkan.",
                fontFamily = Lato,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Hapus",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    fontFamily = Lato,
                    color = colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun StarDistributionRow(
    star: Int,
    fraction: Float,
    count: Int
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(42.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$star",
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = StarYellow,
                modifier = Modifier.size(14.dp)
            )
        }

        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape),
            color = colorScheme.primary,
            trackColor = colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Text(
            text = "$count",
            fontFamily = Lato,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
internal fun ReviewItemCard(
    review: Review,
    isOwner: Boolean = false,
    showPlaceName: Boolean = false,
    onPlaceClick: (() -> Unit)? = null,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant)
            .then(
                if (onPlaceClick != null) Modifier.clickable(onClick = onPlaceClick) else Modifier
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                JelajahKotaUserAvatar(
                    avatarUrl = review.authorAvatarUrl,
                    fullName = review.authorName,
                    size = 40.dp,
                    contentDescription = review.authorName
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (showPlaceName && review.placeName.isNotBlank()) review.placeName else review.authorName,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = review.date,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
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
                    text = String.format(Locale.US, "%.1f", review.rating),
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorScheme.onSurface
                )

                if (isOwner) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ulasan",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus ulasan",
                            tint = colorScheme.error,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = review.content,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorScheme.onSurface
        )

        if (review.photoResIds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(review.photoResIds) { photoRes ->
                    JelajahKotaLocalImage(
                        drawableResId = photoRes,
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
    JelajahKotaTheme {
        ReviewsScreen(
            uiState = ReviewsUiState(
                isLoading = false,
                reviews = listOf(
                    Review(
                        id = "1",
                        placeId = "place_jodipan",
                        userId = "u1",
                        authorName = "Budi Santoso",
                        rating = 5.0,
                        date = "2 hari lalu",
                        content = "Tempatnya sangat bersih dan spot fotonya indah."
                    )
                )
            ),
            onSearchQueryChange = {},
            onSubmitReview = { _, _ -> },
            onUpdateReview = { _, _, _ -> },
            onDeleteReview = {},
            onDismissError = {},
            onNavigateBack = {}
        )
    }
}
