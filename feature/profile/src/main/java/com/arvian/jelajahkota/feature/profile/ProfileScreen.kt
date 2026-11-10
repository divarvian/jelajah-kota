package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.R as DesignR
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.BorderColor
import com.arvian.jelajahkota.core.designsystem.theme.BrandBlueContainer
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.ErrorRed
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.SurfaceGray
import com.arvian.jelajahkota.core.designsystem.theme.White

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAvatarUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onSignedOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToAvatarUpload = onNavigateToAvatarUpload,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToFavorites = onNavigateToFavorites,
        onNavigateToPlans = onNavigateToPlans,
        onSignOutClick = { viewModel.signOut(onSignedOut) },
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAvatarUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Sticky Header: Root tab has no back button
        JelajahKotaTopAppBar(
            title = "Profil Saya",
            isTitleCentered = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 1. Profile Hero Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceGray,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Image(
                            painter = painterResource(id = DesignR.drawable.img_user_avatar),
                            contentDescription = "Foto Profil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(2.dp, BrandBluePrimary, CircleShape)
                        )

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(BrandBluePrimary)
                                .clickable { onNavigateToAvatarUpload() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Ganti Foto",
                                tint = White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.user.fullName,
                        fontFamily = JosefinSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = uiState.user.email,
                        fontFamily = Lato,
                        fontSize = 13.sp,
                        color = SubtitleText
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandBlueContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrandBluePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.user.city,
                            fontFamily = Lato,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = BrandBluePrimary
                        )
                    }

                    if (uiState.user.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.user.bio,
                            fontFamily = Lato,
                            fontSize = 12.sp,
                            color = SubtitleText,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Activity Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    count = "12",
                    label = "Dikunjungi",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    count = "4",
                    label = "Rencana",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    count = "8",
                    label = "Ulasan",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Section: Aktivitas & Konten
            Text(
                text = "Aktivitas Saya",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.FavoriteBorder,
                        title = "Tempat Favorit",
                        subtitle = "Destinasi yang Anda simpan",
                        onClick = onNavigateToFavorites
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                    ProfileMenuItem(
                        icon = Icons.Default.CalendarMonth,
                        title = "Rencana Kunjungan",
                        subtitle = "Jadwal dan rute wisata aktif",
                        onClick = onNavigateToPlans
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                    ProfileMenuItem(
                        icon = Icons.Default.RateReview,
                        title = "Ulasan Saya",
                        subtitle = "Ulasan dan penilaian yang telah Anda buat",
                        onClick = { /* My reviews */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Section: Pengaturan Akun
            Text(
                text = "Pengaturan Akun",
                fontFamily = JosefinSans,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Edit,
                        title = "Ubah Profil",
                        subtitle = "Nama, nomor telepon, dan biodata",
                        onClick = onNavigateToEditProfile
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Preferensi & Notifikasi",
                        subtitle = "Mode tema, lokasi, dan privasi",
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Bantuan & FAQ",
                        subtitle = "Pertanyaan seputar panduan JelajahKota",
                        onClick = { /* Help */ }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderColor)
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "Tentang Aplikasi",
                        subtitle = "JelajahKota versi 1.0.0 (Kota Malang)",
                        onClick = { /* About */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Logout Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF1F0),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCC7))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Keluar",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keluar dari Akun",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(84.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
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
                    fontSize = 14.sp,
                    color = SubtitleText
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
                        color = ErrorRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = "Batal",
                        fontFamily = Lato,
                        color = DarkText
                    )
                }
            }
        )
    }
}

@Composable
private fun ProfileStatCard(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SurfaceGray,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
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
                color = BrandBluePrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = Lato,
                fontSize = 11.sp,
                color = SubtitleText
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
                    .background(BrandBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandBluePrimary,
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
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = Lato,
                    fontSize = 11.sp,
                    color = SubtitleText
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = SubtitleText.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        uiState = ProfileUiState()
    )
}
