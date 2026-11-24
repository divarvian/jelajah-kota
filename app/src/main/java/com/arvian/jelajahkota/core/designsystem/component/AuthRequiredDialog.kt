package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato

/**
 * Reusable prompt displayed when a Guest user attempts a write action
 * (Favorite, Visit Plan, Review, or Visitor Photo Upload).
 */
@Composable
fun AuthRequiredDialog(
    actionLabel: String,
    onConfirmSignIn: () -> Unit,
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
                text = stringResource(R.string.auth_required_title),
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
        },
        text = {
            Text(
                text = "Silakan masuk atau daftar akun JelajahKota agar Anda dapat $actionLabel serta menyinkronkan aktivitas di semua perangkat.",
                fontFamily = Lato,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmSignIn) {
                Text(
                    text = stringResource(R.string.auth_required_confirm),
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.auth_required_dismiss),
                    fontFamily = Lato,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
