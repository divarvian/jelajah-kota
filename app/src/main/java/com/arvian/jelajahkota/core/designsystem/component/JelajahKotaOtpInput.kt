package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans

@Composable
fun JelajahKotaOtpInput(
    otpValues: List<String>,
    onOtpChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusRequesters = remember(length) { List(length) { FocusRequester() } }

    LaunchedEffect(Unit) {
        if (focusRequesters.isNotEmpty()) {
            focusRequesters[0].requestFocus()
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (length >= 6) 8.dp else 14.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until length) {
            val char = otpValues.getOrElse(i) { "" }
            val isFilled = char.isNotEmpty()

            BasicTextField(
                value = char,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    when {
                        digits.isEmpty() -> onOtpChange(i, "")
                        digits.length == 1 -> {
                            onOtpChange(i, digits)
                            if (i < length - 1) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        }
                        digits.length == 2 && char.isNotEmpty() -> {
                            val lastDigit = digits.last().toString()
                            onOtpChange(i, lastDigit)
                            if (i < length - 1) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        }
                        else -> {
                            digits.take(length - i).forEachIndexed { offset, digit ->
                                onOtpChange(i + offset, digit.toString())
                            }
                            val targetFocus = (i + digits.length).coerceAtMost(length - 1)
                            focusRequesters[targetFocus].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .focusRequester(focusRequesters[i])
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Backspace && char.isEmpty() && i > 0) {
                            focusRequesters[i - 1].requestFocus()
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                cursorBrush = SolidColor(colorScheme.primary),
                textStyle = TextStyle(
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .background(
                                if (isFilled) colorScheme.surface else colorScheme.surfaceVariant,
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = if (isFilled) 2.dp else 1.dp,
                                color = if (isFilled) colorScheme.primary else colorScheme.outlineVariant,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (char.isEmpty()) {
                            Text(
                                text = "•",
                                fontFamily = JosefinSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colorScheme.outlineVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
