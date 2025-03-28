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
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(userId: String, navController: NavHostController) {
    val context = LocalContext.current
    val controller = remember { GroupController() }

    val groupName = remember { mutableStateOf("") }
    val numberToAdd = remember { mutableStateOf("") }
    val addedNumbers = remember { mutableStateListOf<String>() }

    var groupList by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val groupNumbers = remember { mutableStateListOf<String>() }

    // Grupları çek
    LaunchedEffect(Unit) {
        controller.fetchUserGroups(
            userId = userId,
            onSuccess = {
                groupList = it
                selectedGroupId = it.keys.firstOrNull()
                selectedGroupId?.let { id ->
                    controller.fetchNumbersInGroup(
                        userId = userId,
                        groupId = id,
                        onSuccess = { nums ->
                            groupNumbers.clear()
                            groupNumbers.addAll(nums)
                        },
                        onFailure = {
                            Toast.makeText(context, "Numaralar alınamadı", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onFailure = {
                Toast.makeText(context, "Gruplar alınamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Yeni Grup Oluştur", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = groupName.value,
            onValueChange = { groupName.value = it },
            label = { Text("Grup Adı") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (groupName.value.isNotBlank()) {
                    controller.createGroup(
                        userId,
                        groupName.value,
                        onSuccess = {
                            Toast.makeText(context, "Grup oluşturuldu", Toast.LENGTH_SHORT).show()
                            groupName.value = ""

                            controller.fetchUserGroups(
                                userId,
                                onSuccess = { groups ->
                                    groupList = groups
                                    selectedGroupId = groups.keys.firstOrNull()
                                },
                                onFailure = {}
                            )
                        },
                        onFailure = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grup Oluştur")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Grup Seç", style = MaterialTheme.typography.titleMedium)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
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

                            controller.fetchNumbersInGroup(
                                userId = userId,
                                groupId = id,
                                onSuccess = {
                                    groupNumbers.clear()
                                    groupNumbers.addAll(it)
                                },
                                onFailure = {
                                    Toast.makeText(context, "Numaralar alınamadı", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = numberToAdd.value,
            onValueChange = { numberToAdd.value = it },
            label = { Text("Numara veya Grup İsmi") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val groupId = selectedGroupId
                val number = numberToAdd.value.trim()

                if (groupId != null && number.isNotBlank()) {
                    controller.addNumberToGroup(
                        userId = userId,
                        groupId = groupId,
                        number = number,
                        onSuccess = {
                            Toast.makeText(context, "Numara eklendi", Toast.LENGTH_SHORT).show()
                            numberToAdd.value = ""
                            controller.fetchNumbersInGroup(
                                userId = userId,
                                groupId = groupId,
                                onSuccess = {
                                    groupNumbers.clear()
                                    groupNumbers.addAll(it)
                                },
                                onFailure = {}
                            )
                        },
                        onFailure = {
                            Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Numarayı Ekle")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Grupta Kayıtlı Numaralar:", style = MaterialTheme.typography.titleSmall)
        groupNumbers.forEach { num ->
            Text(text = num)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ✅ Mesaj ekranına geçiş
        Button(
            onClick = {
                navController.navigate("message")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mesaj Ekranına Git")
        }
    }
}
