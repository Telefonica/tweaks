package com.telefonica.tweaks.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telefonica.tweaks.addTweakGraph
import com.telefonica.tweaks.demo.theme.DebugTweaksTheme
import com.telefonica.tweaks.navigateToTweaksOnShake

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        super.onCreate(savedInstanceState)

        setContent {
            DebugTweaksTheme {
                val navController = rememberNavController()
                navController.navigateToTweaksOnShake()
                Surface(color = MaterialTheme.colorScheme.background) {
                    DemoNavHost(
                        navController = navController,
                        initialScreen = "tweaks",
                    )
                }
            }
        }
    }

    @Composable
    private fun DemoNavHost(
        navController: NavHostController,
        initialScreen: String,
        modifier: Modifier = Modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = initialScreen,
            modifier = modifier,
        ) {
            addTweakGraph(
                navController = navController,
            ) {
                composable(route = "custom-screen") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Custom screen")
                    }
                }
            }
        }
    }
}