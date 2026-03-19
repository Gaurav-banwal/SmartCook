package com.gaurav.smartcook.ui.Inventory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gaurav.smartcook.R
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.Ingredient
import com.gaurav.smartcook.ui.commonui.SimpleSearchBar
import com.gaurav.smartcook.ui.theme.AppTheme
import kotlinx.coroutines.launch


@Composable
fun IngredientItem(ingredient: Ingredient,
                   modifier: Modifier= Modifier,
             onIncClick : (Ingredient) -> Unit = {},
             onDecClick : (Ingredient) -> Unit = {}
){
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 10.dp
    ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                AsyncImage(
                    model = ingredient.image.ifEmpty {
                        R.drawable.pizza 
                    }, 
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.pizza),
                    error = painterResource(R.drawable.pizza),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    modifier = Modifier
                        .padding(bottom = 2.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                .background(color = MaterialTheme.colorScheme.tertiary)
                                .clickable {
                                    onDecClick(ingredient)
                                },
                            contentDescription = "Subtract",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                        Text(
                            text = ingredient.quantity.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            modifier = Modifier
                                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                .background(color = MaterialTheme.colorScheme.tertiary)
                                .clickable {
                                    onIncClick(ingredient)
                                },
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Text(
                        text = "Unit ${ingredient.unit}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
}

@Composable
fun InventoryScreen( onAddClickedexp: () -> Unit = {},
                    ingredientViewModel: IngredientViewModel = hiltViewModel()) {

    val searchState = rememberTextFieldState()
    val allIngredients by ingredientViewModel.ingredients.collectAsState()
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var ingredientTobeDeleted by remember { mutableStateOf<Ingredient?>(null) }

    val filteredIngredients by remember {
        derivedStateOf {
            allIngredients.filter {
                it.name.contains(searchState.text, ignoreCase = true)
            }
        }
    }

    val suggestions by remember {
        derivedStateOf {
            if (searchState.text.isEmpty()) emptyList()
            else allIngredients
                .map { it.name }
                .filter { it.contains(searchState.text, ignoreCase = true) }
                .take(5)
        }
    }

    if (showDialog && ingredientTobeDeleted != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                ingredientTobeDeleted = null
            },
            title = { Text("Delete Ingredient") },
            text = { Text("Are you sure you want to delete '${ingredientTobeDeleted?.name}' from your pantry?") },
            confirmButton = {
                Button(
                    onClick = {
                        ingredientTobeDeleted?.let { 
                            ingredientViewModel.deleteIngredient(it) 
                        }
                        showDialog = false
                        ingredientTobeDeleted = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        ingredientTobeDeleted = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
         floatingActionButton = {
             FloatingActionButton(
                 onClick = { onAddClickedexp() },
                 shape = CircleShape,
                 modifier = Modifier.size(60.dp),
                 containerColor = MaterialTheme.colorScheme.primary
             ) {
                 Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
             }
         }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            SimpleSearchBar(
                textFieldState = searchState,
                onSearch = { },
                suggestions = suggestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            )
            Text(
                text = "My Pantry",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
            )

            LazyColumn(Modifier.fillMaxSize()) {
                items(filteredIngredients, key = { it.id }) { ingredient ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue != SwipeToDismissBoxValue.Settled) {
                                ingredientTobeDeleted = ingredient
                                showDialog = true
                                false // Don't dismiss immediately, wait for dialog
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
                                MaterialTheme.colorScheme.errorContainer
                            } else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color),
                                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                                    Alignment.CenterStart else Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = "Delete",
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                        }
                    ) {
                        IngredientItem(
                            ingredient = ingredient,
                            onIncClick = { ingredientViewModel.increaseAmount(it) },
                            onDecClick = { ingredientViewModel.decreaseAmount(it) }
                        )
                    }
                }
            }
        }
    }
}
