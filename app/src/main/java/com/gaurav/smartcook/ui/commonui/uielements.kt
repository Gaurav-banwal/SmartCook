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
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.ui.Home.food


@Composable
fun FoodItem(food: food,modifier: Modifier= Modifier,
             onItemClick: (food) -> Unit = {},
             onFavouriteClick: (food) -> Unit = {},
             toshow: Boolean = true){
    var likeness by rememberSaveable {
        mutableStateOf(food.favourite)
    }

    Surface(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth().padding(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(30.dp)
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Image(
                painter = painterResource(id = food.image),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column() {
                Text(text = food.name, style = MaterialTheme.typography.headlineMedium)
                Text(text = food.time)
            }
            Spacer(modifier = Modifier.weight(1f))
            if(toshow){
                val imageVector: ImageVector = if (likeness) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                Icon(
                    imageVector = imageVector,
                    modifier = Modifier.padding(15.dp)
                        .size(35.dp)
                        .clickable{

                            likeness = !likeness
                            onFavouriteClick(food)
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary

                )
            }


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
