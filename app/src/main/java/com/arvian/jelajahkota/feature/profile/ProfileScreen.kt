package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.R
import com.arvian.jelajahkota.core.designsystem.component.InfoModalDialog
import com.arvian.jelajahkota.core.designsystem.component.InfoSection
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaUserAvatar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.domain.model.User

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAvatarUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToMyReviews: () -> Unit = {},
    onSignedOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.signedOut) {
        if (uiState.signedOut) {
            viewModel.consumeSignedOut()
            onSignedOut()
        }
    }

    ProfileScreen(
        uiState = uiState,
        bottomContentPadding = bottomContentPadding,
        onNavigateToSignIn = onNavigateToSignIn,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToAvatarUpload = onNavigateToAvatarUpload,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToFavorites = onNavigateToFavorites,
        onNavigateToPlans = onNavigateToPlans,
        onNavigateToMyReviews = onNavigateToMyReviews,
        onSignOutClick = viewModel::signOut,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAvatarUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToMyReviews: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val user = uiState.user

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            title = stringResource(R.string.profile_title),
            isTitleCentered = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (user == null) {
                // Guest Welcome Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Jelajahi Sebagai Tamu",
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Masuk atau buat akun untuk menyimpan destinasi favorit, mengatur jadwal kunjungan, menulis ulasan, dan berbagi foto wisata.",
                            fontFamily = Lato,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        JelajahKotaPrimaryButton(
                            text = "Masuk / Daftar",
                            onClick = onNavigateToSignIn
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
            } else {
                // Authenticated Profile Hero Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier
                                .size(80.dp)
                                .clickable { onNavigateToAvatarUpload() }
                        ) {
                            JelajahKotaUserAvatar(
                                avatarUrl = user.avatarUrl,
                                fullName = user.fullName,
                                size = 80.dp,
                                showBorder = true,
                                contentDescription = "Foto Profil"
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Ganti Foto",
                                    tint = colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.fullName,
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = user.email,
                            fontFamily = Lato,
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        if (user.city.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorScheme.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = user.city,
                                    fontFamily = Lato,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (user.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = user.bio,
                                fontFamily = Lato,
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dynamic Activity Statistics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        count = uiState.favoritesCount.toString(),
                        label = "Favorit",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        count = uiState.plansCount.toString(),
                        label = "Rencana",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        count = uiState.reviewsCount.toString(),
                        label = "Ulasan",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Aktivitas Saya
                Text(
                    text = "Aktivitas Saya",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, colorScheme.outlineVariant)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.FavoriteBorder,
                            title = "Tempat Favorit",
                            subtitle = "Destinasi yang Anda simpan",
                            onClick = onNavigateToFavorites
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.CalendarMonth,
                            title = "Rencana Kunjungan",
                            subtitle = "Jadwal dan rute wisata aktif",
                            onClick = onNavigateToPlans
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.RateReview,
                            title = "Ulasan Saya",
                            subtitle = "Kelola ulasan dan rating yang Anda tulis",
                            onClick = onNavigateToMyReviews
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section: Pengaturan & Informasi
            Text(
                text = if (user != null) "Pengaturan Akun" else "Pengaturan & Informasi",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, colorScheme.outlineVariant)
            ) {
                Column {
                    if (user != null) {
                        ProfileMenuItem(
                            icon = Icons.Default.Edit,
                            title = "Ubah Profil",
                            subtitle = "Nama, nomor telepon, dan biodata",
                            onClick = onNavigateToEditProfile
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)
                    }
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Preferensi & Notifikasi",
                        subtitle = "Mode gelap, lokasi, dan pemberitahuan",
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Bantuan & FAQ",
                        subtitle = "Pertanyaan seputar panduan JelajahKota",
                        onClick = { showHelpDialog = true }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "Tentang Aplikasi",
                        subtitle = "JelajahKota versi 1.0 (Kota Malang)",
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            if (user != null) {
                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showLogoutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.errorContainer,
                    border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar",
                            tint = colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Keluar dari Akun",
                            fontFamily = Lato,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(bottomContentPadding + 16.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface,
            textContentColor = colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontFamily = JosefinSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari akun JelajahKota?",
                    fontFamily = Lato,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onSignOutClick()
                    }
                ) {
                    Text(
                        text = "Ya, Keluar",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = "Batal",
                        fontFamily = Lato,
                        color = colorScheme.onSurface
                    )
                }
            }
        )
    }

    if (showHelpDialog) {
        InfoModalDialog(
            title = "Bantuan & FAQ",
            sections = listOf(
                InfoSection(
                    heading = "Apakah saya harus masuk untuk melihat wisata?",
                    body = "Tidak. Anda dapat menjelajahi katalog destinasi Kota Malang, detail tempat, galeri, serta membaca ulasan secara bebas sebagai Tamu."
                ),
                InfoSection(
                    heading = "Kapan saya perlu masuk akun?",
                    body = "Masuk akun dibutuhkan saat menyimpan destinasi ke Favorit, menjadwalkan Rencana Kunjungan, menulis/mengubah ulasan, atau mengunggah foto pengunjung."
                ),
                InfoSection(
                    heading = "Hubungi Tim JelajahKota",
                    body = "Butuh bantuan lebih lanjut atau ingin menyarankan destinasi baru di Kota Malang? Kirim email ke bantuan@jelajahkota.id."
                )
            ),
            onDismiss = { showHelpDialog = false }
        )
    }

    if (showAboutDialog) {
        InfoModalDialog(
            title = "Tentang JelajahKota",
            sections = listOf(
                InfoSection(
                    heading = "JelajahKota v1.0",
                    body = "Panduan eksplorasi kota berbasis Android native yang membantu wisatawan menemukan destinasi ikonik, merencanakan kunjungan, serta berbagi ulasan dan foto perjalanan di Kota Malang & sekitarnya."
                ),
                InfoSection(
                    heading = "Teknologi & Lisensi",
                    body = "Dibangun dengan Kotlin, Jetpack Compose, Material 3, dan Supabase. Font Josefin Sans & Lato didistribusikan di bawah lisensi SIL Open Font License (OFL)."
                )
            ),
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
private fun ProfileStatCard(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = Lato,
                fontSize = 11.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = Lato,
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    JelajahKotaTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                user = User(
                    id = "1",
                    fullName = "Arvian Diva",
                    email = "arvian@jelajahkota.id",
                    city = "Kota Malang"
                )
            )
        )
    }
}
