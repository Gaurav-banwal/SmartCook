package com.gaurav.smartcook.ui.Inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.R
import com.gaurav.smartcook.ui.Home.food
import com.gaurav.smartcook.ui.commonui.SimpleSearchBar
import com.gaurav.smartcook.ui.theme.AppTheme
import kotlin.text.contains


data class ingredientData(
    val  id:Int,
    val name:String,
    val Quantity:Int,
    val image:Int
)

@Composable
fun IngredientItem(ingredientData: ingredientData,
                   modifier: Modifier= Modifier,
             onIncClick : (ingredientData) -> Unit = {},
             onDecClick : (ingredientData) -> Unit = {}
){


    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 10.dp
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Image(
                painter = painterResource(id = ingredientData.image),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

                Text(text = ingredientData.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Quantity")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                            .background(color = MaterialTheme.colorScheme.tertiary)

                            .clickable {
                                onDecClick(ingredientData)
                            },
                        contentDescription = "Subtract button to reduce items",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(text = ingredientData.Quantity.toString())
                    Icon(imageVector = Icons.Default.Add,
                        modifier = Modifier
                            .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                            .background(color = MaterialTheme.colorScheme.tertiary)

                            .clickable {
                                onIncClick(ingredientData)
                            },
                        contentDescription = "Add button to add items")
                }
            }


        }
    }
}

@Composable
fun InventoryScreen() {


    // 1. Properly remember the search state
    val searchState = rememberTextFieldState()
// Mock data list
    val allIngredients = remember {
        mutableStateListOf(
            ingredientData(1, "Pizza Dough", 7, R.drawable.pizza),
            ingredientData(2, "Tomato Sauce", 2, R.drawable.pizza),
            ingredientData(3, "Mozzarella", 5, R.drawable.pizza)
        )
    }


    // 2. Real-time filtering logic
    val filteredIngredients by remember {
        derivedStateOf {
            allIngredients.filter {
                it.name.contains(searchState.text, ignoreCase = true)
            }
        }
    }
    
    // 3. Dynamic suggestions based on pantry items
    val suggestions by remember {
        derivedStateOf {
            if (searchState.text.isEmpty()) emptyList()
            else allIngredients
                .map { it.name }
                .filter { it.contains(searchState.text, ignoreCase = true) }
                .take(5)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            SimpleSearchBar(
                textFieldState = searchState,
                onSearch = { },
                suggestions = suggestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "My Pantry",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
            )


            LazyColumn(Modifier.fillMaxSize()) {
                items(filteredIngredients, key = { it.id }) { ingredient ->
                    IngredientItem(
                        ingredientData = ingredient,
                        onIncClick = { /* Update logic */ },
                        onDecClick = { /* Update logic */ }
                    )
                }

            }
        }
    }
}



    @Preview
    @Composable
    fun previewInv() {

        AppTheme() {
            InventoryScreen()
        }

    }
