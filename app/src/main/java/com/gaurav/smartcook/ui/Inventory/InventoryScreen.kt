package com.gaurav.smartcook.ui.Inventory

import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gaurav.smartcook.R
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.Ingredient
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
                        R.drawable.pizza // Fallback to local resource if URL is empty
                    }, // This is now the URL string
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.pizza), // Default while loading
                    error = painterResource(R.drawable.pizza),       // Default if error
                    modifier = Modifier
                        .size(100.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                                    onDecClick(ingredient)
                                },
                            contentDescription = "Subtract button to reduce items",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                        Text(text = ingredient.quantity.toString())
                        Icon(
                            imageVector = Icons.Default.Add,
                            modifier = Modifier
                                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                .background(color = MaterialTheme.colorScheme.tertiary)

                                .clickable {
                                    onIncClick(ingredient)
                                },
                            contentDescription = "Add button to add items"
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
fun InventoryScreen(db: AppDatabase, onAddClickedexp: () -> Unit = {},
                    ingredientViewModel: IngredientViewModel = viewModel()) {


    // 1. Properly remember the search state
    val searchState = rememberTextFieldState()

    var Dao = db.ingredientDao()




    val allIngredients by ingredientViewModel.getallitem().collectAsState(initial = emptyList())



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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
         floatingActionButton = {
             var size = 60.dp
             FloatingActionButton(
                 onClick = {


                Log.d("TAG", "InventoryScreen: Clicked")
                 onAddClickedexp()
                 },
                 shape = CircleShape,
                 modifier = Modifier.size(size),
                 containerColor = MaterialTheme.colorScheme.primary
             ) {
                 Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
             }
         })
             { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
//              .padding(start = innerpadding.calculateLeftPadding(LayoutDirection.Ltr),
//                end = innerpadding.calculateRightPadding(LayoutDirection.Ltr),
//                bottom = innerpadding.calculateBottomPadding(),
//                top = innerpadding.calculateTopPadding() / 2) // Try halving it or using 0.dp


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
                    IngredientItem(
                        ingredient = ingredient,
                        onIncClick = {
                           // Log.d("TAG", "InventoryScreen: Clicked")
                          //  ingredientViewModel.quantity = (ingredient.quantity +1).toString()
                            //show what error is there
                            Log.e("TAG", "InventoryScreen: ${ingredientViewModel.quantity}")

                            ingredientViewModel.increaseAmount(ingredient) },
                        onDecClick = {
                          //  ingredientViewModel.quantity = (ingredient.quantity -1).toString()

                            ingredientViewModel.decreaseAmount(ingredient)


                        }
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

//             Mock data list
    val allIngredients = remember {
        mutableStateListOf(
            ingredientData(1, "Pizza Dough", 7, R.drawable.pizza),
            ingredientData(2, "Tomato Sauce", 2, R.drawable.pizza),
            ingredientData(3, "Mozzarella", 5, R.drawable.pizza),
            ingredientData(4, "Pizza Dough", 7, R.drawable.pizza),
            ingredientData(5, "Tomato Sauce", 2, R.drawable.pizza),
            ingredientData(6, "Mozz", 5, R.drawable.pizza),
            ingredientData(7, "Pizza Dough", 7, R.drawable.pizza),
            ingredientData(8, "Tomato Sauce", 2, R.drawable.pizza),
            ingredientData(9, "Mozz", 5, R.drawable.pizza)



        )
    }
            InventoryScreen(db= AppDatabase.getDatabase(LocalContext.current))
        }

    }
