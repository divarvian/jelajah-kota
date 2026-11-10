package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun JelajahKotaOtpInput(
    otpValues: List<String>,
    onOtpChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 4
) {
    val focusRequesters = remember { List(length) { FocusRequester() } }

    LaunchedEffect(Unit) {
        if (focusRequesters.isNotEmpty()) {
            focusRequesters[0].requestFocus()
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until length) {
            val char = otpValues.getOrElse(i) { "" }
            val isFilled = char.isNotEmpty()

            BasicTextField(
                value = char,
                onValueChange = { newValue ->
                    if (newValue.length <= 1) {
                        onOtpChange(i, newValue)
                        if (newValue.isNotEmpty() && i < length - 1) {
                            focusRequesters[i + 1].requestFocus()
                        }
                    } else if (newValue.length == 2) {
                        // If typed over existing char, take the latest character
                        val lastChar = newValue.last().toString()
                        onOtpChange(i, lastChar)
                        if (i < length - 1) {
                            focusRequesters[i + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .size(64.dp)
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
                cursorBrush = SolidColor(BrandBluePrimary),
                textStyle = TextStyle(
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = DarkText,
                    textAlign = TextAlign.Center
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(if (isFilled) White else SurfaceGray, RoundedCornerShape(16.dp))
                            .border(
                                width = if (isFilled) 2.dp else 1.dp,
                                color = if (isFilled) BrandBluePrimary else BorderColor,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (char.isEmpty()) {
                            Text(
                                text = "-",
                                fontFamily = JosefinSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = BorderColor
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
