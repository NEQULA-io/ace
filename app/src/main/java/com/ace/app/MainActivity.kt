package com.ace.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.ace.app.ui.email.EmailLoginScreen
import com.ace.app.ui.theme.AceTheme
import com.ace.app.ui.welcome.WelcomeScreen
import com.ace.app.ui.welcome.WelcomeViewModel
import com.ace.app.ui.model.ModelDownloadScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AceTheme {
                AceApp()
            }
        }
    }
}

@Composable
fun AceApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToEmail = {
                    navController.navigate("email_login")
                },
                onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("email_login") {
            EmailLoginScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val context = LocalContext.current
            val welcomeViewModel: WelcomeViewModel = viewModel()

            ModelDownloadScreen(
                onDownloadClick = {
                    val url =
                        "https://drive.google.com/file/d/1Y2F6RNFAJs6SovHZGvZe7fXf-EdLiiRH/view?usp=sharing"

                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onSignOutClick = {
                    welcomeViewModel.onSignOut(context)
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}