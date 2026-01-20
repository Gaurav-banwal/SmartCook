package com.gaurav.smartcook.ui.Inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.R
import com.gaurav.smartcook.ui.Home.food
import com.gaurav.smartcook.ui.theme.AppTheme


data class ingredientData(
    val  id:Int,
    val name:String,
    val Quantity:Int,
    val image:Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<String>,
    modifier: Modifier = Modifier
) {
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier

            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Display search results in a scrollable column
            Column(Modifier.verticalScroll(rememberScrollState())) {
                searchResults.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result) }
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}



@Composable
fun IngredientItem(ingredientData: ingredientData,modifier: Modifier= Modifier,
             onIncClick : (ingredientData) -> Unit = {},
             onDecClick : (ingredientData) -> Unit = {}
){


    Surface(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth().padding(10.dp),
        color = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(30.dp)
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
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

                Text(text = ingredientData.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
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

                            .clickable{
                            onIncClick(ingredientData)
                        },
                        contentDescription = "Add button to add items")
                }
            }


        }
    }
}

@Composable
fun InventoryScreen(){
    Box(modifier = Modifier.fillMaxSize()
        .background(color = MaterialTheme.colorScheme.surface),

        ) {




        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 1.dp)

        ) {

            SimpleSearchBar(
                textFieldState = TextFieldState(),
                onSearch = { },
                searchResults = listOf("Pizza", "Pasta", "Salad"),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )


            Column() {
                Text(
                    text = "Inventory Items",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )

                // For now, hardcoded item. In future, use LazyColumn here.
                IngredientItem(ingredientData(1, "Pizza Dough", 7, R.drawable.pizza))
                IngredientItem(ingredientData(2, "Tomato Sauce", 2, R.drawable.pizza))
            }

        }

        FloatingActionButton(
            onClick = { /* Navigate to Add Screen */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {

            Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
        }
    }
}



@Preview
@Composable
fun previewInv(){

       AppTheme() {
           InventoryScreen()
       }

}
