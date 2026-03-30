package com.gaurav.smartcook.ui.Favourate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gaurav.smartcook.data.local.Ingredient
import com.gaurav.smartcook.ui.Home.prevRecipie
import com.gaurav.smartcook.ui.commonui.FoodItem
import com.gaurav.smartcook.ui.commonui.Screen
import com.gaurav.smartcook.ui.commonui.SimpleSearchBar
import com.gaurav.smartcook.ui.theme.AppTheme



@Composable
fun FavouriteScreen(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = { _->},
    viewModel: FavouriteViewModel = viewModel()
) {
    FavouriteScreenContent(
        modifier = modifier,
        favourites = viewModel.favourites,
        isLoading = viewModel.isLoading,
        onItemClick = onItemClick,
        onRemoveFromFavourite = { viewModel.removefromFavourite(it) },
        onFetchFavourites = { viewModel.fetchFavourites() }
    )
}

@Composable
fun FavouriteScreenContent(
    modifier: Modifier = Modifier,
    favourites: List<prevRecipie>,
    isLoading: Boolean,
    onItemClick: (String) -> Unit,
    onRemoveFromFavourite: (String) -> Unit,
    onFetchFavourites: () -> Unit
) {

    LaunchedEffect(Unit) {
        onFetchFavourites()
    }

    val searchState = rememberTextFieldState()
    var showDialog by remember { mutableStateOf(false) }
    var favouritetoBeRemoved by remember { mutableStateOf<prevRecipie?>(null) }


    if (showDialog && favouritetoBeRemoved != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
               favouritetoBeRemoved = null
            },
            title = { Text("Delete Ingredient") },
            text = { Text("Are you sure you want to delete '${favouritetoBeRemoved?.name}' from your pantry?") },
            confirmButton = {
                Button(
                    onClick = {
                        favouritetoBeRemoved?.let {
                            onRemoveFromFavourite(it.id)
                        }
                        showDialog = false
                        favouritetoBeRemoved = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                       favouritetoBeRemoved= null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        // Correctly use derivedStateOf to track changes in favourites
        val filteredIngredients by remember {
            derivedStateOf {
                favourites.filter {
                    it.name.contains(searchState.text, ignoreCase = true)
                }
            }
        }

        val suggestions by remember {
            derivedStateOf {
                if (searchState.text.isEmpty()) emptyList()
                else favourites
                    .map { it.name }
                    .filter { it.contains(searchState.text, ignoreCase = true) }
                    .take(5)
            }
        }

        Box( modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        ) {
            if (isLoading && favourites.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (favourites.isEmpty() && !isLoading) {
                Text(
                    text = "No favourites found",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column {
                    SimpleSearchBar(
                        textFieldState = searchState,
                        onSearch = { },
                        suggestions = suggestions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Text(
                        text = "My Favourite",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                    )

                    LazyColumn(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(filteredIngredients, key = { it.id }) { favourite ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue != SwipeToDismissBoxValue.Settled) {

                                        showDialog = true
                                        favouritetoBeRemoved = favourite
                                     //   viewModel.removefromFavourite(ingredient.id)
                                        false
                                    } else {

                                        false
                                    }
                                },
                                positionalThreshold = { distance -> distance * 0.7f }


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
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                                            Alignment.CenterStart else Alignment.CenterEnd
                                    ) {
                                        if(dismissState.targetValue != SwipeToDismissBoxValue.Settled)
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            tint = MaterialTheme.colorScheme.error,
                                            contentDescription = "Delete"
                                        )
                                    }
                                }){
                                FoodItem(
                                    food = favourite,
                                    onItemClick = {
                                        onItemClick(favourite.id)
                                    }
                                )
                               }


                        }

                    }
                    }
                }
            }
        }
    }



@Preview
@Composable
fun prevFav(){
    AppTheme {
        // Use the stateless FavouriteScreenContent for previews to avoid ViewModel instantiation issues
        FavouriteScreenContent(
            favourites = listOf(
                prevRecipie(id = "1", name = "Pasta", isFavourite = true),
                prevRecipie(id = "2", name = "Pizza", isFavourite = true)
            ),
            isLoading = false,
            onItemClick = {},
            onRemoveFromFavourite = {},
            onFetchFavourites = {}
        )
    }
}
