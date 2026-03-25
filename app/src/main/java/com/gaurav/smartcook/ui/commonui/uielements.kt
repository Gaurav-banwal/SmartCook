package com.gaurav.smartcook.ui.commonui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gaurav.smartcook.R
import com.gaurav.smartcook.ui.Home.food
import com.gaurav.smartcook.ui.Home.prevRecipie


@Composable
fun FoodItem(food: prevRecipie, modifier: Modifier= Modifier,
             onItemClick: (String) -> Unit = {}){

    Surface(
        modifier = modifier
            .height(110.dp)
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 0.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .4f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        onClick = { onItemClick(food.id) }
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){

            AsyncImage(
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                model = food.image,
                error = painterResource(R.drawable.pizza),
            )
            Column(modifier = Modifier,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = food.cookTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }




        }
    }
}


@Composable
fun HistoryRecipie(previousReipie: prevRecipie, modifier: Modifier= Modifier,
                   onItemClick: (String) -> Unit = {},
                   onFavouriteClick: (String) -> Unit = {}){

    var likeness by rememberSaveable { mutableStateOf(previousReipie.isFavourite)
    }

    Surface(
        modifier = modifier
            .height(110.dp)
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 0.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .4f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp,
        onClick = { onItemClick(previousReipie.id) }
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            AsyncImage(
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                model = previousReipie.image,
                error = painterResource(R.drawable.pizza),
//                onLoading = TODO(),
//                onError = TODO(),
                alignment = Alignment.Center
            )
            Column(modifier = Modifier.padding(vertical = 8.dp).weight(1f)) {
                Text(
                    text = previousReipie.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = previousReipie.cookTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
                val imageVector: ImageVector = if (likeness) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                Icon(
                    imageVector = imageVector,
                    modifier = Modifier.padding(15.dp)
                        .size(30.dp)
                        .clickable{
                            likeness = !likeness
                            onFavouriteClick(previousReipie.id)
                        },
                    contentDescription = null,
                    tint = if (likeness) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer

                )



        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    suggestions: List<String>,
    modifier: Modifier = Modifier
) {
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .semantics { isTraversalGroup = true }
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        SearchBar(
            modifier = Modifier
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(it)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(onClick = { textFieldState.edit { replace(0, length, "") } }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear text")
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            // Display search results in an efficient scrollable list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(suggestions) { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result) }
                                expanded = false
                                onSearch(result)
                            }
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}


@Composable
fun loadstate(){

    Image(
        painter = painterResource(R.drawable.loading_img),

        contentDescription = "Loading state ",
        modifier = Modifier.size(50.dp),
        alignment = Alignment.Center,

    )

}
