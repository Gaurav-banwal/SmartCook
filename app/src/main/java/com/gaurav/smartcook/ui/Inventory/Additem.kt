package com.gaurav.smartcook.ui.Inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    onBackClick: () -> Unit = {},
    onAddClick: (String, Int, String) -> Unit = { _, _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("pcs") }
    var expanded by remember { mutableStateOf(false) }

    val units = listOf("pcs", "kg", "g", "l", "ml", "oz", "lb", "cup", "tbsp", "tsp")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Ingredient") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Track your pantry items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ingredient Name") },
                placeholder = { Text("e.g. Flour, Milk, etc.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            quantity = it
                        }
                    },
                    label = { Text("Quantity") },
                    placeholder = { Text("0") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Scale, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Box(modifier = Modifier.weight(0.6f)) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            Icon(Icons.Default.UnfoldMore, contentDescription = null, 
                                modifier = Modifier.clickable { expanded = true })
                        },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.4f)
                    ) {
                        units.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    unit = selection
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank() && quantity.isNotEmpty()) {
                        onAddClick(name, quantity.toInt(), unit)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank() && quantity.isNotEmpty()
            ) {
                Text("Add to Pantry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddIngredientScreenPreview() {
    AppTheme {
        AddIngredientScreen()
    }
}
