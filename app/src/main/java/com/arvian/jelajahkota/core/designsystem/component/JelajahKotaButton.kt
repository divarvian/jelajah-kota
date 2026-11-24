package com.arvian.jelajahkota.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueOutline
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun JelajahKotaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandBluePrimary,
            contentColor = White,
            disabledContainerColor = BrandBluePrimary.copy(alpha = 0.5f),
            disabledContentColor = White.copy(alpha = 0.7f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = text,
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (enabled) textColor else White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun JelajahKotaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = BrandBluePrimary
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, BrandBlueOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = BrandBluePrimary,
            disabledContainerColor = White.copy(alpha = 0.6f),
            disabledContentColor = BrandBluePrimary.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = text,
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (enabled) textColor else BrandBluePrimary.copy(alpha = 0.5f)
        )
    }
}
