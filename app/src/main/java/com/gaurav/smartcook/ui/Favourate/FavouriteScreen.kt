package com.gaurav.smartcook.ui.Favourate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.R
import com.gaurav.smartcook.ui.Home.food
import com.gaurav.smartcook.ui.Inventory.IngredientItem
import com.gaurav.smartcook.ui.commonui.FoodItem
import com.gaurav.smartcook.ui.commonui.SimpleSearchBar
import com.gaurav.smartcook.ui.theme.AppTheme


val list = listOf<food>(
    food(1,"Pizza","30 mins", R.drawable.pizza, favourite = false),
    food(2,"pasta1","20 mins", R.drawable.pasta1, favourite = true),
    food(3,"pasta2","20 mins", R.drawable.pasta2, favourite = false),
    food(4,"pasta3","20 mins", R.drawable.pasta3, favourite = false),
    food(5,"pasta4","20 mins", R.drawable.pasta4, favourite = false)
)
@Composable
fun FavouriteScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {


        // 1. Properly remember the search state
        val searchState = rememberTextFieldState()

        // Mock data list
        val allIngredients = remember {
            list
        }
// 2. Real-time filtering logic
        val filteredIngredients by remember {
            derivedStateOf {
                allIngredients.filter {
                    it.name.contains(searchState.text, ignoreCase = true)
                }
            }
        }



        Box( modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        ) {
            Column() {
                SimpleSearchBar(
                    textFieldState = searchState,
                    onSearch = { },
                    suggestions = emptyList(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Text(
                    text = "My Favourite",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                )

                LazyColumn(Modifier.fillMaxSize()) {
                    items(filteredIngredients, key = { it.id }) { ingredient ->
                        FoodItem(
                            food = ingredient,
                            modifier = Modifier,
                            onItemClick = { },
                            onFavouriteClick = { },
                            toshow = false
                        )
                    }

                }
            }

        }

        }
    }


@Preview
@Composable
fun prevFav(){
    AppTheme() {
        FavouriteScreen()

    }

}