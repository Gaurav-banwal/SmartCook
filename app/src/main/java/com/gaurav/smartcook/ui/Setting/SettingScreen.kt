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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.ui.theme.AppTheme
import com.gaurav.smartcook.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

data class UserSettings(
    val name: String = "Gaurav",
    val age: String = "25",
    val gender: String = "Male",
    val diet: String = "Non-Vegetarian",
    val serveSize: Int = 2,
    val allergies: String = "None",
    val specialNotes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
) {
    var settings by remember { mutableStateOf(UserSettings()) }
    val scrollState = rememberScrollState()
    
    // Retrieve email from Firebase (Read-only)
    val userEmail = remember { FirebaseAuth.getInstance().currentUser?.email ?: "Not Logged In" }

    val dietOptions = listOf("Vegan", "Vegetarian", "Non-Vegetarian", "Keto", "Paleo", "Dairy-Free")
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
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
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Name Field
            OutlinedTextField(
                value = settings.name,
                onValueChange = { settings = settings.copy(name = it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Age Field
                OutlinedTextField(
                    value = settings.age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) settings = settings.copy(age = it) },
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
                        value = settings.gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
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
                                    settings = settings.copy(gender = option)
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Diet Preference
            var dietExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = dietExpanded,
                onExpandedChange = { dietExpanded = !dietExpanded }
            ) {
                OutlinedTextField(
                    value = settings.diet,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dietary Preference") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dietExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
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
                                settings = settings.copy(diet = option)
                                dietExpanded = false
                            }
                        )
                    }
                }
            }

            // Serving Size
            Column {
                Text("Default Serving Size: ${settings.serveSize}", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = settings.serveSize.toFloat(),
                    onValueChange = { settings = settings.copy(serveSize = it.toInt()) },
                    valueRange = 1f..10f,
                    steps = 9
                )
            }

            // Allergies
            OutlinedTextField(
                value = settings.allergies,
                onValueChange = { settings = settings.copy(allergies = it) },
                label = { Text("Allergies") },
                placeholder = { Text("e.g. Peanuts, Shellfish") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
            )

            // Special Notes
            OutlinedTextField(
                value = settings.specialNotes,
                onValueChange = { settings = settings.copy(specialNotes = it) },
                label = { Text("Special Cooking Notes") },
                placeholder = { Text("e.g. Prefer low salt, spicy food...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: Persist 'settings' to Firebase Firestore/Database
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogoutSuccess()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
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
