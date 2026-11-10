package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White

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
    enabled: Boolean = true,
    singleLine: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                fontFamily = Lato,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
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
                        color = SubtitleText
                    )
                }
            },
            textStyle = TextStyle(
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = DarkText
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = White.copy(alpha = 0.5f),
                focusedBorderColor = BrandBluePrimary,
                unfocusedBorderColor = BorderColor,
                errorBorderColor = ErrorRed,
                focusedLeadingIconColor = BrandBluePrimary,
                unfocusedLeadingIconColor = SubtitleText,
                focusedTrailingIconColor = BrandBluePrimary,
                unfocusedTrailingIconColor = SubtitleText
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )

        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = ErrorRed
            )
        }
    }
}
