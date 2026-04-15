// In file: Navigation.kt
package com.example.groceryapp

sealed class Screen(val route: String) {
    object StartUp : Screen("startup_screen")
    object SignIn : Screen("signin_screen")
    object SignUp : Screen("signup_screen")
    object Home : Screen("home_screen")
    object NewList : Screen("newlist_screen")
    object List : Screen("list_screen")
    object StoreDetails : Screen("store_details_screen")
    object Insights : Screen("insights_screen")
    object Search : Screen("search_screen")
    object Favourites : Screen("favourites_screen")
    object Alerts : Screen("alerts_screen")
    object Settings : Screen("settings_screen")
    object Profile : Screen("profile_screen")
    object Security : Screen("security_screen")
    object Offline : Screen("offline_screen")


    object ListDetail : Screen("list_detail_screen/{listId}")


    fun withArgs(vararg args: String): String {
        return buildString {

            append(route.substringBefore("/{"))

            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}