package com.gaurav.smartcook.ui.commonui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        color = MaterialTheme.colorScheme.secondary,
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