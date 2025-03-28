package com.example.whatsautomessage.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.whatsautomessage.ui.theme.WhatsAutoMessageTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatsAutoMessageTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavigationHost()
                }
            }
        }
    }
}

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val userId = remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { uid: String -> // 👈 BURAYA TÜRÜ EKLİYORSUN
                    userId.value = uid
                    navController.navigate("dashboard")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = { uid: String -> // 👈 BURAYA DA
                    userId.value = uid
                    navController.navigate("dashboard")
                }
            )
        }

        composable("dashboard") {
            val uid = userId.value
            if (uid != null) {
                DashboardScreen(userId = uid, navController = navController)
            }
        }
        composable("message") {
            val uid = userId.value
            if (uid != null) {
                MessageScreen(userId = uid)
            }
        }
    }
}

