package com.scroller.ledtext.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.scroller.ledtext.ui.navigation.NavGraph
import com.scroller.ledtext.ui.theme.LEDScrollerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LEDScrollerTheme {
                val navController = rememberNavController()
                NavGraph(navHostController = navController)
            }
        }
    }
}