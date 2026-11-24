package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.Lato

@Composable
fun JelajahKotaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            },
            textStyle = TextStyle(
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = colorScheme.onSurface
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.65f),
                cursorColor = colorScheme.primary,
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surfaceContainerLow,
                disabledContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.6f),
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outlineVariant,
                disabledBorderColor = colorScheme.outlineVariant.copy(alpha = 0.6f),
                errorBorderColor = colorScheme.error,
                focusedLeadingIconColor = colorScheme.primary,
                unfocusedLeadingIconColor = colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                errorLeadingIconColor = colorScheme.error,
                focusedTrailingIconColor = colorScheme.primary,
                unfocusedTrailingIconColor = colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                errorTrailingIconColor = colorScheme.error
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )

        val showError = isError && !errorMessage.isNullOrBlank()
        if (showError || !supportingText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showError) {
                    Text(
                        text = errorMessage,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (!supportingText.isNullOrBlank()) {
                    Text(
                        text = supportingText,
                        fontFamily = Lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
