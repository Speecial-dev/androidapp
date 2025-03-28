package com.example.whatsautomessage.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.whatsautomessage.controller.GroupController
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(userId: String) {
    val context = LocalContext.current
    val controller = remember { GroupController() }

    var groupList by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val messageText = remember { mutableStateOf("") }

    // Grupları çek
    LaunchedEffect(Unit) {
        controller.fetchUserGroups(
            userId = userId,
            onSuccess = {
                groupList = it
                selectedGroupId = it.keys.firstOrNull()
                selectedGroupId?.let { id ->
                    controller.fetchMessageForGroup(
                        userId = userId,
                        groupId = id,
                        onSuccess = { msg -> messageText.value = msg },
                        onFailure = { messageText.value = "" }
                    )
                }
            },
            onFailure = {
                Toast.makeText(context, "Gruplar alınamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Grup değiştiğinde eski mesajı çek
    LaunchedEffect(selectedGroupId) {
        selectedGroupId?.let { id ->
            controller.fetchMessageForGroup(
                userId = userId,
                groupId = id,
                onSuccess = { msg -> messageText.value = msg },
                onFailure = { messageText.value = "" }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Mesaj Oluştur", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown
        Text("Grup Seç", style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = groupList[selectedGroupId] ?: "Grup Seçin",
                onValueChange = {},
                label = { Text("Grup Seçin") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                groupList.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedGroupId = id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TextField
        TextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },
            label = { Text("Mesajınız") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val groupId = selectedGroupId
                val msg = messageText.value.trim()

                if (groupId != null && msg.isNotEmpty()) {
                    controller.saveMessageForGroup(
                        userId = userId,
                        groupId = groupId,
                        message = msg,
                        onSuccess = {
                            Toast.makeText(context, "Mesaj kaydedildi", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mesajı Kaydet")
        }
    }
}
