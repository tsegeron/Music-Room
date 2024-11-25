package com.laru.musicroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.laru.auth.navigation.AuthNavHost
import com.laru.musicroom.navigation.ApplicationNavHost
import com.laru.musicroom.ui.theme.MusicRoomTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MusicRoomTheme {
                when (uiState) {
                    is MainActivityUiState.Unauthorized -> AuthNavHost()
                    is MainActivityUiState.Authorized -> ApplicationNavHost()
                }
            }
        }
    }
}
