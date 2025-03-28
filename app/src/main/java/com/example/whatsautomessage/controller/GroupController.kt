package com.example.whatsautomessage.controller

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator

class GroupController {
    fun createGroup(
        userId: String,
        groupName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance().reference
        val groupRef = database.child("users").child(userId).child("groups").push()

        val groupData = mapOf(
            "name" to groupName,
            "createdAt" to System.currentTimeMillis(),
            "numbers" to listOf<String>()
        )

        groupRef.setValue(groupData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Bilinmeyen hata") }
    }
    fun addNumberToGroup(userId: String, groupId: String, number: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("groups").child(groupId).child("numbers")
        dbRef.get().addOnSuccessListener { snapshot ->
            val currentList = snapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            val updatedList = currentList + number
            dbRef.setValue(updatedList).addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
        }.addOnFailureListener { onFailure(it) }
    }
    fun fetchNumbersInGroup(
        userId: String,
        groupId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("groups")
            .child(groupId)
            .child("numbers")

        dbRef.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun saveMessageForGroup(
        userId: String,
        groupId: String,
        message: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("messages")
            .child(groupId)

        ref.setValue(message)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun fetchMessageForGroup(
        userId: String,
        groupId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("messages")
            .child(groupId)

        ref.get()
            .addOnSuccessListener { snapshot ->
                val message = snapshot.getValue(String::class.java) ?: ""
                onSuccess(message)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchUserGroups(
        userId: String,
        onSuccess: (Map<String, String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("groups")

        dbRef.get().addOnSuccessListener { snapshot ->
            val result = mutableMapOf<String, String>()
            snapshot.children.forEach { groupSnapshot ->
                val id = groupSnapshot.key
                val name = groupSnapshot.child("name").getValue(String::class.java)
                if (id != null && name != null) {
                    result[id] = name
                }
            }
            onSuccess(result)
        }.addOnFailureListener {
            onFailure(it)
        }
    }


}
