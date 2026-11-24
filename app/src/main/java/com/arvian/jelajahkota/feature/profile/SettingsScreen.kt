package com.arvian.jelajahkota.feature.profile

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.component.InfoModalDialog
import com.arvian.jelajahkota.core.designsystem.component.InfoSection
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.Lato

@Composable
fun SettingsRoute(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onShowMessage: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.toggleLocation(true)
            onShowMessage("Akses lokasi aktif. Jarak destinasi kini ditampilkan.")
        } else {
            viewModel.toggleLocation(false)
            onShowMessage("Izin lokasi tidak aktif. Menampilkan destinasi Malang Raya secara default.")
        }
    }

    SettingsScreen(
        uiState = uiState,
        onToggleLocation = { enable ->
            if (enable && !viewModel.hasLocationPermission()) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            } else {
                viewModel.toggleLocation(enable)
            }
        },
        onToggleDarkMode = viewModel::toggleDarkMode,
        onTogglePushNotifications = viewModel::togglePushNotifications,
        onToggleEmailNotifications = viewModel::toggleEmailNotifications,
        onNavigateBack = onNavigateBack,
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
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSupportDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            title = stringResource(R.string.settings_title),
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

            SectionHeader(title = "Tampilan & Lokasi")
            Spacer(modifier = Modifier.height(10.dp))
            SwitchItemRow(
                title = "Mode Gelap",
                subtitle = "Gunakan tampilan gelap yang nyaman di malam hari",
                checked = uiState.darkModeEnabled,
                onCheckedChange = onToggleDarkMode
            )
            SwitchItemRow(
                title = "Akses Lokasi",
                subtitle = "Bantu tampilkan destinasi terdekat di sekitar Kota Malang",
                checked = uiState.locationEnabled,
                onCheckedChange = onToggleLocation
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Notifikasi")
            Spacer(modifier = Modifier.height(10.dp))
            SwitchItemRow(
                title = "Pemberitahuan Aplikasi",
                subtitle = "Pengingat rencana kunjungan & rekomendasi tempat baru",
                checked = uiState.pushNotificationsEnabled,
                onCheckedChange = onTogglePushNotifications
            )
            SwitchItemRow(
                title = "Notifikasi Email",
                subtitle = "Ringkasan jadwal perjalanan & pembaruan akun",
                checked = uiState.emailNotificationsEnabled,
                onCheckedChange = onToggleEmailNotifications
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Dukungan & Legal")
            Spacer(modifier = Modifier.height(10.dp))
            NavigationItemRow(
                title = "Kontak Dukungan",
                subtitle = "bantuan@jelajahkota.id",
                onClick = { showSupportDialog = true }
            )
            NavigationItemRow(
                title = "Ketentuan Layanan & Privasi",
                onClick = { showTermsDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSupportDialog) {
        InfoModalDialog(
            title = "Kontak Dukungan",
            sections = listOf(
                InfoSection(
                    heading = "Email Dukungan",
                    body = "Kirim pertanyaan, laporan bug, atau usulan destinasi Kota Malang ke bantuan@jelajahkota.id."
                ),
                InfoSection(
                    heading = "Jam Operasional",
                    body = "Senin – Jumat, pukul 09.00 – 17.00 WIB."
                )
            ),
            onDismiss = { showSupportDialog = false }
        )
    }

    if (showTermsDialog) {
        InfoModalDialog(
            title = "Ketentuan Layanan & Privasi",
            sections = listOf(
                InfoSection(
                    heading = "Konten Pengguna",
                    body = "Ulasan dan foto pengunjung yang Anda unggah dapat dilihat oleh pengguna lain sebagai referensi wisata. Anda memiliki kendali penuh untuk mengubah atau menghapus ulasan maupun foto milik Anda sendiri."
                ),
                InfoSection(
                    heading = "Perlindungan Data",
                    body = "Data profil, daftar favorit, dan rencana kunjungan disimpan secara aman menggunakan autentikasi dan kebijakan Row-Level Security (RLS) sehingga hanya pemilik akun yang dapat mengakses data pribadi."
                )
            ),
            onDismiss = { showTermsDialog = false }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = Lato,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun NavigationItemRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = Lato,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = Lato,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SwitchItemRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontFamily = Lato,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = Lato,
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.onPrimary,
                checkedTrackColor = colorScheme.primary,
                uncheckedThumbColor = colorScheme.surface,
                uncheckedTrackColor = colorScheme.surfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    JelajahKotaTheme {
        SettingsScreen(
            uiState = ProfileUiState(),
            onToggleLocation = {},
            onToggleDarkMode = {},
            onTogglePushNotifications = {},
            onToggleEmailNotifications = {},
            onNavigateBack = {}
        )
    }
}
