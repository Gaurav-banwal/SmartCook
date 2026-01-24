package com.gaurav.smartcook.ui.Home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gaurav.smartcook.R
import com.gaurav.smartcook.ui.commonui.FoodItem
import com.gaurav.smartcook.ui.theme.AppTheme


data class food(
    val id:Int,
    val name:String,
    val time:String,
    val image:Int,
    val favourite:Boolean = false
)

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,

    val time: String,
    val image: Int
)

val list = listOf<food>(
    food(1,"Pizza","30 mins", R.drawable.pizza, favourite = false),
    food(2,"pasta1","20 mins", R.drawable.pasta1, favourite = true),
    food(3,"pasta2","20 mins", R.drawable.pasta2, favourite = false),
    food(4,"pasta3","20 mins", R.drawable.pasta3, favourite = false),
     food(5,"pasta4","20 mins", R.drawable.pasta4, favourite = false)
)

@Composable
fun HomeHeader(username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hello, $username!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "What would you like to cook today?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

    }
}





@Composable
fun OnGoing(modifier: Modifier = Modifier
    .size(320.dp)
    ){

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .height(350.dp)
            .width(320.dp)
            ,

        color = MaterialTheme.colorScheme.secondaryContainer,

    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
          Image(
              painter = painterResource(
                  id = R.drawable.pizza
              ),
              contentDescription = "Image of Pizza",
              modifier = Modifier
                  .fillMaxWidth()
                  .height(220.dp),
              contentScale = ContentScale.Crop

          )
                  Spacer(modifier = Modifier.height(7.dp))
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceBetween
                 ){

                     Column() {
                         Text(
                             text = "Pizza",
                             style = MaterialTheme.typography.headlineMedium,
                             color = MaterialTheme.colorScheme.onSecondary,
                             modifier = Modifier.padding(start = 15.dp)
                         )
                         Text(
                             text = " 30 mins",
                             style = MaterialTheme.typography.bodyMedium,

                             color = MaterialTheme.colorScheme.onSecondary,
                             modifier = Modifier.padding(start = 15.dp)
                         )
                     }
                     Icon(
                         imageVector = Icons.Default.Refresh,
                         modifier = Modifier.padding(end = 15.dp, top = 15.dp)
                             .size(35.dp),
                         contentDescription = null
                     )

                 }








        }
    }

}



@Composable
fun PopularRecipeCard(recipe: Recipe) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = recipe.image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {


                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.time}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}





@Composable
fun HomeScreen(){
    val scrollstate = rememberScrollState()
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,

    ) {



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .verticalScroll(scrollstate),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            HomeHeader("Gaurav")
            Spacer(
                modifier = Modifier.size(10.dp)
            )

            PopularRecipeCard(
                recipe = Recipe(
                    id = 3,
                    title = "Pizza",
                  description = "cheesy slices falling well",
                    time = "25 min",
                    image = R.drawable.pizza
                )
            )


            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = {

                },
                modifier = Modifier.width(200.dp),
                enabled = true,
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("New Recipie")
            }

            Spacer(
                modifier = Modifier.size(50.dp)
            )
            Text("Previous Transactions"
                ,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,)
            Spacer(
                modifier = Modifier.size(10.dp)
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                list.forEach { item ->
                    FoodItem(item)
                }
            }
        }







    }


    }


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun prevOnGoing(){
//    OnGoing()
//}
//
@Preview()
@Composable
fun prevHomeScreen(){
    HomeScreen()
}

@Preview
@Composable
fun prevFoodItem(){
    AppTheme() {
        PopularRecipeCard(
            recipe = Recipe(
                id = 3,
                title = "Pizza",
              description = "cheesy crust and tangy puree",
                time = "25 min",
                image = R.drawable.pizza
            )
        )
    }

}