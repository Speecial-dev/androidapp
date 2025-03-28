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
import com.example.whatsautomessage.model.User
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: (String) -> Unit // ✅ DÜZENLENDİ
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
        Text("Kayıt Ol", style = MaterialTheme.typography.headlineMedium)

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
                val user = User(email, password)
                controller.registerUser(
                    user,
                    onSuccess = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            onRegisterSuccess(uid)
                        } else {
                            Toast.makeText(context, "Kayıt başarılı ama UID alınamadı", Toast.LENGTH_LONG).show()
                        }
                    },
                    onFailure = {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kayıt Ol")
        }

        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text("Zaten bir hesabınız var mı? Giriş Yapın")
        }
    }
}
