package com.example.logistic_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.logistic_app.data.repository.AuthRepository
import com.example.logistic_app.ui.login.LoginScreen
import com.example.logistic_app.ui.login.LoginViewModel
import com.example.logistic_app.ui.main.MainScreen
import com.example.logistic_app.ui.theme.Logistic_appTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI for simplicity in this scope
        val authRepository = AuthRepository(FirebaseAuth.getInstance())
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(authRepository) as T
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            Logistic_appTheme {
                val navController = rememberNavController()
                val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = if (isUserLoggedIn) "main" else "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("main") {
                        MainScreen(viewModel = viewModel)
                    }
                }

                // Handle logout navigation
                LaunchedEffect(isUserLoggedIn) {
                    if (!isUserLoggedIn) {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
