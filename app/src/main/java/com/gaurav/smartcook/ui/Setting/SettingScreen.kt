package com.gaurav.smartcook.ui.Setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.data.remote.firebase.UserProfile
import com.gaurav.smartcook.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    settingsViewModel: SettingsViewModel,
    onLogoutSuccess: () -> Unit
) {
    // Collect the user profile from the ViewModel
    val remoteProfile by settingsViewModel.userProfile

    LaunchedEffect(remoteProfile) {
        remoteProfile?.let { settingsViewModel.loadProfileIntoState(it) }
    }

    val scrollState = rememberScrollState()
    val userEmail = remember { FirebaseAuth.getInstance().currentUser?.email ?: "Not Logged In" }

    val dietOptions = listOf("Vegan", "Vegetarian", "Non-Vegetarian", "Keto", "Paleo", "Dairy-Free")
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        if (settingsViewModel.isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Profile Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Email Field (Read-Only)
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // Name Field
                OutlinedTextField(
                    value = settingsViewModel.name,
                    onValueChange = { settingsViewModel.name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Age Field
                    OutlinedTextField(
                        value = settingsViewModel.age,
                        onValueChange = { if (it.all { char -> char.isDigit() }) settingsViewModel.age = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) }
                    )

                    // Gender Dropdown
                    var genderExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = settingsViewModel.gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        settingsViewModel.gender = option
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }


                var dietExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = dietExpanded,
                    onExpandedChange = { dietExpanded = !dietExpanded }
                ) {
                    OutlinedTextField(
                        value = settingsViewModel.diet,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Dietary Preference") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dietExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null) }
                    )
                    ExposedDropdownMenu(
                        expanded = dietExpanded,
                        onDismissRequest = { dietExpanded = false }
                    ) {
                        dietOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    settingsViewModel.diet = option
                                    dietExpanded = false
                                }
                            )
                        }
                    }
                }


                Column {
                    Text("Default Serving Size: ${settingsViewModel.servesize.toInt()}", style = MaterialTheme.typography.bodyLarge)
                    Slider(
                        value = settingsViewModel.servesize,
                        onValueChange = { settingsViewModel.servesize = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }


                OutlinedTextField(
                    value = settingsViewModel.allergy,
                    onValueChange = { settingsViewModel.allergy = it },
                    label = { Text("Allergies") },
                    placeholder = { Text("e.g. Peanuts, Shellfish") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
                )

                OutlinedTextField(
                    value = settingsViewModel.specialNote,
                    onValueChange = { settingsViewModel.specialNote = it },
                    label = { Text("Special Cooking Notes") },
                    placeholder = { Text("e.g. Prefer low salt, spicy food...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updatedProfile = UserProfile(
                            Name = settingsViewModel.name,
                            Age = settingsViewModel.age.toIntOrNull() ?: 0,
                            Gender = settingsViewModel.gender,
                            Diet = settingsViewModel.diet,
                            Allergy = settingsViewModel.allergy,
                            Servesize = settingsViewModel.servesize.toInt(),
                            Specialcooknote = settingsViewModel.specialNote
                        )
                        settingsViewModel.updateUserData(updatedProfile) { success ->
                            println("Update success: $updatedProfile is $success")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !settingsViewModel.isUpdating.value
                ) {
                    if (settingsViewModel.isUpdating.value) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Save Changes", style = MaterialTheme.typography.titleMedium)
                    }
                }

                OutlinedButton(
                    onClick = {
                        settingsViewModel.logout()
                        onLogoutSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Logout")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
