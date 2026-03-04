package com.gaurav.smartcook.ui.commonui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.ui.Login.ForgetScreen
import com.gaurav.smartcook.ui.Login.LoginScreen
import com.gaurav.smartcook.ui.Login.RegistrationScreen
import com.gaurav.smartcook.ui.runrecipie.DishSelectionScreen
import com.gaurav.smartcook.ui.runrecipie.PrerequisitScreen
import com.gaurav.smartcook.ui.runrecipie.steps.StepsScreen
import com.gaurav.smartcook.viewmodel.AuthViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,    authViewModel: AuthViewModel,

) {
    navigation(
        startDestination = Screen.Login.route,
        route = "auth"
    ) {
        composable(route = Screen.Login.route) {
            val uiState by authViewModel.loginstate.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("auth") { inclusive = true }
                    }
                    authViewModel.resetStates()
                }
            }

            LoginScreen(
                viewModel = authViewModel,
                onLoginClick = { email, password -> authViewModel.login(email, password) },
                onLoginSucess = { /* Handled by LaunchedEffect */ },
                onForgotPasswordClick = { navController.navigate(Screen.ForgetPassword.route) },
                onSignUpClick = { navController.navigate(Screen.Registration.route) }
            )

        }
            composable(route = Screen.Registration.route){
                val uiState by authViewModel.registerstate.collectAsState()

                LaunchedEffect(uiState.isSuccess) {
                    if (uiState.isSuccess) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("auth") { inclusive = true }
                        }
                        authViewModel.resetStates()
                    }
                }

///
                RegistrationScreen(
                    viewModel = authViewModel,
                    onSignUpClick = { name, email, password ->
                        authViewModel.register(name, email, password)
                    },
                    onGoogleSignUpClick = { },
                    onLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(route = Screen.ForgetPassword.route) {
                val uiState by authViewModel.resetstate.collectAsState()

                LaunchedEffect(uiState.isSuccess) {
                    if (uiState.isSuccess) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ForgetPassword.route) { inclusive = true }
                        }
                        authViewModel.resetStates()
                    }
                }

                ForgetScreen(
                    viewModel = authViewModel,
                    onSendResetLinkClick = { email ->
                        authViewModel.forgetPassword(email.trim())
                    },
                    onBackToLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }




        }


    }



fun NavGraphBuilder.recipeNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.DishSelection.route,
        route = "recipe_wizard"
    ) {
        composable(route = Screen.DishSelection.route) { DishSelectionScreen() }
        composable(route = Screen.Prerequisites.route) { PrerequisitScreen() }
        composable(route = Screen.RecipeSteps.route) { StepsScreen() }
    }
}