package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun SettingsRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onSignedOut: () -> Unit = {},
    isRootTab: Boolean = false,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onToggleLocation = viewModel::toggleLocation,
        onToggleDarkMode = viewModel::toggleDarkMode,
        onTogglePushNotifications = viewModel::togglePushNotifications,
        onToggleEmailNotifications = viewModel::toggleEmailNotifications,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onSignOutClick = { viewModel.signOut(onSignedOut) },
        onNavigateBack = onNavigateBack,
        isRootTab = isRootTab,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    uiState: ProfileUiState,
    onToggleLocation: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onTogglePushNotifications: (Boolean) -> Unit,
    onToggleEmailNotifications: (Boolean) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onSignOutClick: () -> Unit,
    onNavigateBack: () -> Unit,
    isRootTab: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        JelajahKotaTopAppBar(
            title = "Pengaturan",
            onNavigateBack = if (isRootTab) null else onNavigateBack,
            isTitleCentered = !isRootTab
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        // Section "Umum"
        SectionHeader(title = "Umum")
        Spacer(modifier = Modifier.height(10.dp))
        NavigationItemRow(title = "Akun", onClick = { /* Account details */ })
        NavigationItemRow(title = "Edit Profil", onClick = onNavigateToEditProfile)
        SwitchItemRow(title = "Lokasi", checked = uiState.locationEnabled, onCheckedChange = onToggleLocation)
        SwitchItemRow(title = "Dark Mode", checked = uiState.darkModeEnabled, onCheckedChange = onToggleDarkMode)

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(16.dp))

        // Section "Notifikasi"
        SectionHeader(title = "Notifikasi")
        Spacer(modifier = Modifier.height(10.dp))
        SwitchItemRow(title = "Pemberitahuan", checked = uiState.pushNotificationsEnabled, onCheckedChange = onTogglePushNotifications)
        SwitchItemRow(title = "Notifikasi Email", checked = uiState.emailNotificationsEnabled, onCheckedChange = onToggleEmailNotifications)

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(16.dp))

        // Section "Dukungan"
        SectionHeader(title = "Dukungan")
        Spacer(modifier = Modifier.height(10.dp))
        NavigationItemRow(title = "Kontak Dukungan", onClick = { /* Support */ })
        NavigationItemRow(title = "Beri Nilai", onClick = { /* Rating */ })
        NavigationItemRow(title = "Ketentuan Layanan", onClick = { /* Terms */ })

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(16.dp))

        // Section "Lainnya"
        SectionHeader(title = "Lainnya")
        Spacer(modifier = Modifier.height(10.dp))
        NavigationItemRow(title = "Bahasa", subtitle = "Bahasa Indonesia", onClick = { /* Language */ })

        Spacer(modifier = Modifier.height(16.dp))

        // Keluar (Logout)
        Text(
            text = "Keluar",
            fontFamily = Lato,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = BrandBluePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSignOutClick() }
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = Lato,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = DarkText
    )
}

@Composable
private fun NavigationItemRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = SubtitleText
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = SubtitleText,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = SubtitleText,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun SwitchItemRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = SubtitleText
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = BrandBluePrimary,
                uncheckedThumbColor = White,
                uncheckedTrackColor = SurfaceGray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        uiState = ProfileUiState(),
        onToggleLocation = {},
        onToggleDarkMode = {},
        onTogglePushNotifications = {},
        onToggleEmailNotifications = {},
        onNavigateToEditProfile = {},
        onSignOutClick = {},
        onNavigateBack = {}
    )
}
