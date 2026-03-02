package com.gaurav.smartcook.ui.Setting

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gaurav.smartcook.viewmodel.AuthViewModel


@Composable
fun SettingScreen(
    viewModel: AuthViewModel,
    onLogoutSuccess: () -> Unit
){
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Settings Screen", 
                 style = MaterialTheme.typography.headlineMedium,
                 color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = {

                    viewModel.logout()
                    onLogoutSuccess()
                }
            ) {
                Text("logout ")
            }
        }
    }
}