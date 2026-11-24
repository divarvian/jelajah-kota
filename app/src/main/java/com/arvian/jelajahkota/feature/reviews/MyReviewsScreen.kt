package com.arvian.jelajahkota.feature.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.domain.model.Review
import kotlin.math.roundToInt

@Composable
fun MyReviewsRoute(
    viewModel: MyReviewsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlaceDetail: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.actionMessage) {
        val message = uiState.actionMessage
        if (message != null) {
            viewModel.consumeActionMessage()
            onShowMessage(message)
        }
    }

    MyReviewsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPlaceClick = onNavigateToPlaceDetail,
        onExploreClick = onNavigateToExplore,
        onUpdateReview = viewModel::updateReview,
        onDeleteReview = viewModel::deleteReview,
        onDismissError = viewModel::clearError,
        modifier = modifier
    )
}

@Composable
fun MyReviewsScreen(
    uiState: MyReviewsUiState,
    onNavigateBack: () -> Unit,
    onPlaceClick: (String) -> Unit,
    onExploreClick: () -> Unit,
    onUpdateReview: (reviewId: String, rating: Int, content: String) -> Unit,
    onDeleteReview: (reviewId: String) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var editingReview by remember { mutableStateOf<Review?>(null) }
    var deletingReview by remember { mutableStateOf<Review?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .navigationBarsPadding()
    ) {
        JelajahKotaTopAppBar(
            title = stringResource(R.string.my_reviews_title),
            onNavigateBack = onNavigateBack
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        } else if (uiState.reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum Ada Ulasan",
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bagikan pengalaman wisata Anda di destinasi Kota Malang agar membantu penjelajah lainnya!",
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    JelajahKotaPrimaryButton(
                        text = stringResource(R.string.action_explore_destinations),
                        onClick = onExploreClick,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(uiState.reviews, key = { it.id }) { review ->
                    ReviewItemCard(
                        review = review,
                        isOwner = true,
                        showPlaceName = true,
                        onPlaceClick = { onPlaceClick(review.placeId) },
                        onEditClick = { editingReview = review },
                        onDeleteClick = { deletingReview = review }
                    )
                }
            }
        }
    }

    val toEdit = editingReview
    if (toEdit != null) {
        ReviewEditorDialog(
            title = "Edit Ulasan",
            confirmLabel = stringResource(R.string.action_save),
            initialRating = toEdit.rating.roundToInt().coerceIn(1, 5),
            initialContent = toEdit.content,
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                onDismissError()
                editingReview = null
            },
            onSubmit = { rating, content ->
                onUpdateReview(toEdit.id, rating, content)
                if (content.isNotBlank()) {
                    editingReview = null
                }
            }
        )
    }

    val toDelete = deletingReview
    if (toDelete != null) {
        DeleteReviewConfirmDialog(
            onConfirm = {
                onDeleteReview(toDelete.id)
                deletingReview = null
            },
            onDismiss = { deletingReview = null }
        )
    }
}
