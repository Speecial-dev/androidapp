package com.example.whatsautomessage.controller

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.whatsautomessage.model.User

class AuthController(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    fun registerUser(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    uid?.let {
                        db.child(it).setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onFailure("Veritabanına kayıt başarısız: ${it.message}")
                            }
                    }
                } else {
                    onFailure("Authentication başarısız: ${task.exception?.message}")
                }
            }
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    onFailure("Giriş başarısız: ${task.exception?.message}")
                }
            }
    }
}
