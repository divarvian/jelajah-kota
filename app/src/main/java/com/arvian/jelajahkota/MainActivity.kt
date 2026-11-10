package com.arvian.jelajahkota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaOutlinedButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.theme.BrandBluePrimary
import com.arvian.jelajahkota.core.designsystem.theme.DarkText
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.JosefinSans
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.designsystem.theme.SubtitleText
import com.arvian.jelajahkota.core.designsystem.theme.White
import com.arvian.jelajahkota.feature.auth.SignInPlaceholderScreen
import com.arvian.jelajahkota.feature.auth.SignUpPlaceholderScreen
import com.arvian.jelajahkota.feature.onboarding.OnboardingRoute
import com.arvian.jelajahkota.feature.onboarding.OnboardingViewModel
import com.arvian.jelajahkota.feature.onboarding.SplashScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as JelajahKotaApp
        val preferencesRepository = app.preferencesRepository

        setContent {
            JelajahKotaTheme {
                // Collect onboarding completion persistence state
                val isOnboardingCompleted by preferencesRepository
                    .isOnboardingCompleted()
                    .collectAsStateWithLifecycle(initialValue = null)

                when (val completed = isOnboardingCompleted) {
                    null -> {
                        // Transient loading indicator while DataStore reads initial preference
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandBluePrimary)
                        }
                    }
                    else -> {
                        val navController = rememberNavController()
                        // If already completed on a prior launch, start directly at "main"
                        val startDestination = if (completed) "main" else "splash"

                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Node 415:300 — Splash Screen
                            composable("splash") {
                                SplashScreen(
                                    onSplashFinished = {
                                        navController.navigate("onboarding") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // Nodes 52:702, 1:5, 1:6 — Onboarding 3-Slide Pager
                            composable("onboarding") {
                                val onboardingViewModel: OnboardingViewModel = viewModel(
                                    factory = OnboardingViewModel.Factory(preferencesRepository)
                                )
                                OnboardingRoute(
                                    viewModel = onboardingViewModel,
                                    onNavigateToMain = {
                                        navController.navigate("main") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    },
                                    onNavigateToSignIn = {
                                        navController.navigate("signin")
                                    },
                                    onNavigateToSignUp = {
                                        navController.navigate("signup")
                                    }
                                )
                            }

                            // Feature Auth Placeholders
                            composable("signin") {
                                SignInPlaceholderScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable("signup") {
                                SignUpPlaceholderScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // Main Destination (Post-Onboarding Beranda / Dashboard Screen from Figma 16:252)
                            composable("main") {
                                val homeViewModel: com.arvian.jelajahkota.feature.places.HomeViewModel = viewModel(
                                    factory = com.arvian.jelajahkota.feature.places.HomeViewModel.Factory(app.placesRepository)
                                )
                                com.arvian.jelajahkota.feature.places.HomeRoute(
                                    viewModel = homeViewModel,
                                    onNavigateToPlaceDetail = { /* Feature places detail in future phase */ },
                                    onNavigateToNotifications = { /* Notifications in future phase */ },
                                    onNavigateToSettings = { /* Settings in future phase */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainPlaceholderScreen(
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as JelajahKotaApp
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "JelajahKota Malang",
            fontFamily = JosefinSans,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = BrandBluePrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Onboarding telah selesai! Ini adalah halaman utama katalog tempat menarik Kota Malang.",
            fontFamily = Lato,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = SubtitleText
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Developer action to reset onboarding state in DataStore for testing
        JelajahKotaOutlinedButton(
            text = "Ulangi Onboarding (Reset)",
            onClick = {
                coroutineScope.launch {
                    app.preferencesRepository.setOnboardingCompleted(false)
                    onResetOnboarding()
                }
            }
        )
    }
}