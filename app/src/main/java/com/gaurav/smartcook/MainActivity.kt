package com.gaurav.smartcook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.gaurav.smartcook.data.repository.AuthRepository
import com.gaurav.smartcook.ui.commonui.SmartCookScreen
import com.gaurav.smartcook.ui.theme.AppTheme
import com.gaurav.smartcook.ui.theme.Nunito
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    //lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        //authRepository.getSession()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme() {
                SmartCookScreen()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme() {
        SmartCookScreen()

    }
}