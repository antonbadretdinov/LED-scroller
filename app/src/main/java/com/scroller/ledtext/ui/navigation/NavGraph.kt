package com.scroller.ledtext.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.scroller.ledtext.ui.screens.ScrollerScreen
import com.scroller.ledtext.ui.screens.edit.EditScreen

@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Edit.route
    ) {

        composable(route = Screens.Edit.route) {
            EditScreen(
                onStartClicked = { textLength, speed ->
                    navHostController.navigate("Scroller/$textLength/$speed")
                }
            )
        }

        composable(route = Screens.Scroller.route) {
            val textLength = it.arguments?.getString("length")
            val speed = it.arguments?.getString("speed")
            if (textLength != null && speed != null) {
                ScrollerScreen(
                    textLength = textLength,
                    speed = speed
                )
            }
        }

    }
}

sealed class Screens(
    val route: String
) {
    data object Edit : Screens(
        route = "Edit"
    )

    data object Scroller : Screens(
        route = "Scroller/{length}/{speed}"
    )
}