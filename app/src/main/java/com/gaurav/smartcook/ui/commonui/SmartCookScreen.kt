package com.gaurav.smartcook.ui.commonui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gaurav.smartcook.R
import com.gaurav.smartcook.ui.theme.AppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.room.Room
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.ui.Favourate.FavouriteScreen
import com.gaurav.smartcook.ui.Home.HomeScreen
import com.gaurav.smartcook.ui.Inventory.AddIngredientScreen
import com.gaurav.smartcook.ui.Inventory.IngredientViewModel
import com.gaurav.smartcook.ui.Inventory.InventoryScreen
import com.gaurav.smartcook.ui.Login.ForgetScreen
import com.gaurav.smartcook.ui.Login.LoginScreen
import com.gaurav.smartcook.ui.Login.RegistrationScreen
import com.gaurav.smartcook.ui.Setting.SettingScreen
import com.gaurav.smartcook.ui.runrecipie.DishSelectionScreen
import com.gaurav.smartcook.ui.runrecipie.PrerequisitScreen
import com.gaurav.smartcook.ui.runrecipie.steps.StepsScreen
import com.gaurav.smartcook.viewmodel.AuthViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlin.jvm.java


enum class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Home(route = Screen.Home.route, title = "Home", icon = Icons.Default.Home),
    Ingredients(route = Screen.Ingredients.route, title = "Pantry", icon = Icons.Default.Check),
    Favorites(route = Screen.Favorites.route, title = "Favorites", icon = Icons.Default.Favorite),
    Settings(route =  Screen.Settings.route, title = "Settings", icon = Icons.Default.Settings)
}

sealed class Screen(val route: String) {
    // Top-Level Screens
    object Home : Screen("Home")
    object Ingredients : Screen("ingredients")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")

    // Recipe Wizard Flow (Nested)
    object DishSelection : Screen("dish_selection")
    object Prerequisites : Screen("prerequisites")
    object RecipeSteps : Screen("recipe_steps")

    // Ingredient Actions (Nested)
    object AddIngredient : Screen("add_ingredient_form")

    //Login and registration
    object Login : Screen("login_screen")
    object Registration : Screen("registration_screen")
    object ForgetPassword : Screen("forget_password_screen")

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCookTopBar(  canNavigateBack: Boolean,
                      navigateUp: () -> Unit,
                      modifier: Modifier){
    TopAppBar(
        title = { Text(stringResource( R.string.app_name))},
        modifier = modifier,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }

    )


}

@Composable
fun SmartCookBottonBar(modifier: Modifier,navController: NavHostController,navlist:List<BottomBarScreen>) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 18.dp,topEnd = 18.dp)),
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 6.dp,


    ) {

        navlist.forEach { screen ->

            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = {
                    if(currentRoute==screen.route){
                        Text(screen.title)
                    }

                },
                selected = (currentRoute == screen.route),
                onClick = {
                    navController.navigate(screen.route){
                        popUpTo(navController.graph.startDestinationId){ saveState = true}
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Composable
fun SmartCookScreen(

    navController:NavHostController = rememberNavController()
){
    // Get current back stack entry to track state for TopBar
    val backStackEntry by navController.currentBackStackEntryAsState()

    val navlist = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Ingredients,
        BottomBarScreen.Favorites,
        BottomBarScreen.Settings
    )

     //auth viewmodel
    val authViewModel: AuthViewModel = viewModel()
    val IngviewModel: IngredientViewModel = viewModel()


    var db = Room.databaseBuilder(
        LocalContext.current,
        AppDatabase::class.java, "ingredient_database"
    ).build()

    val dao = db.ingredientDao()



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if(backStackEntry?.destination?.route != Screen.Login.route &&
                backStackEntry?.destination?.route != Screen.Registration.route
                && backStackEntry?.destination?.route != Screen.ForgetPassword.route){
                SmartCookTopBar(modifier = Modifier,
                    canNavigateBack = false,
//                    canNavigateBack = navController.previousBackStackEntry != null
//                            && navController.currentDestination?.route != Screen.Home.route,
                    navigateUp = {  navController.navigateUp() })
            }

        },
        bottomBar = {

            if(backStackEntry?.destination?.route != Screen.Login.route &&
                backStackEntry?.destination?.route != Screen.Registration.route
                && backStackEntry?.destination?.route != Screen.ForgetPassword.route){
                SmartCookBottonBar(modifier = Modifier, navController, navlist)
            }

        },

        floatingActionButton = {
            if(false)
            FloatingActionButton(
                onClick = { /* Handle click */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Help, contentDescription = "Add")
            }

        },

        containerColor = MaterialTheme.colorScheme.primaryContainer

    ) {innerPadding->




        NavHost(
            navController = navController,
            startDestination = "auth",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){

            authNavGraph(navController, authViewModel)

        composable(route =BottomBarScreen.Home.route ){
            HomeScreen()
        }
            composable(route =BottomBarScreen.Ingredients.route ){
               InventoryScreen(
                   onAddClickedexp = {
                       navController.navigate(Screen.AddIngredient.route)
                   },
                   db = db
               )
            }


            recipeNavGraph(navController)




            composable(route =BottomBarScreen.Favorites.route ){
                FavouriteScreen(

                )
            }
            composable(route =BottomBarScreen.Settings.route ){
                SettingScreen(viewModel = authViewModel,
                     onLogoutSuccess = {
                         navController.navigate(Screen.Login.route)
                     }
                )
            }

            composable(route = Screen.AddIngredient.route) {
                AddIngredientScreen(

                    onBackClick = { navController.popBackStack() },
                    onAddClick = { name, quantity ->
                        // Add to your data source here
                       IngviewModel.quantity = quantity.toString()
                        IngviewModel.name = name

                        IngviewModel.addIngredient()
                        navController.popBackStack()
                    }

                )
            }
        }
    }
}

@Preview
@Composable
fun prev(){
    AppTheme {
        SmartCookScreen()
    }
}
