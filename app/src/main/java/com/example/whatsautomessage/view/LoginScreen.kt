package com.example.whatsautomessage.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.whatsautomessage.controller.AuthController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val controller = remember { AuthController(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Giriş Yap", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                controller.loginUser(email, password,
                    onSuccess = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            onLoginSuccess(uid)
                        } else {
                            Toast.makeText(context, "Giriş başarılı ama UID alınamadı", Toast.LENGTH_LONG).show()
                        }
                    },
                    onFailure = {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap")
        }

        TextButton(onClick = {
            navController.navigate("register")
        }) {
            Text("Hesabınız yok mu? Kayıt Olun")
        }
    }
}
