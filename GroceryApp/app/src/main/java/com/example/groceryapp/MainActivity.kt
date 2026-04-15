package com.example.groceryapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groceryapp.screens.*
import com.example.groceryapp.ui.theme.GroceryAppTheme
import com.example.groceryapp.viewmodels.ShoppingViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!AppPreferences.hasSetLanguage) {
            val appLocale = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        setContent {
            GroceryAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                    val shoppingViewModel: ShoppingViewModel = viewModel()

                    // Get the application instance
                    val app = application as GroceryApp

                    // Create OfflineSyncViewModel with custom factory
                    val offlineSyncViewModel: OfflineSyncViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return OfflineSyncViewModel(app.repository) as T
                            }
                        }
                    )

                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) Screen.Home.route else Screen.StartUp.route
                    ) {
                        composable(route = Screen.StartUp.route) { StartUpPage(navController = navController) }
                        composable(route = Screen.SignIn.route) { SignInPage(navController = navController) }
                        composable(route = Screen.SignUp.route ) { SignUp(navController = navController)}
                        composable(route = Screen.Home.route) {
                            HomePage(
                                navController,
                                shoppingViewModel,
                                offlineSyncViewModel // Pass the offline view model
                            )
                        }
                        composable(route = Screen.NewList.route) {
                            NewListPage(
                                navController,
                                shoppingViewModel,
                                offlineSyncViewModel // Pass the offline view model
                            )
                        }
                        composable(route = Screen.List.route) {
                            ListPage(
                                navController,
                                shoppingViewModel,
                                offlineSyncViewModel // Pass the offline view model
                            )
                        }
                        composable(
                            route = Screen.ListDetail.route,
                            arguments = listOf(navArgument("listId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            ListDetailPage(
                                navController,
                                shoppingViewModel,
                                backStackEntry.arguments?.getString("listId")
                                // Removed offlineSyncViewModel parameter
                            )
                        }
                        composable(route = Screen.Search.route) { SearchPage(navController, shoppingViewModel) }
                        composable(route = Screen.Profile.route) { ProfilePage(navController, shoppingViewModel) }
                        composable(route = Screen.Alerts.route) { AlertsPage(navController, shoppingViewModel) }
                        composable(route = Screen.Offline.route) {
                            OfflinePage(
                                navController,
                                shoppingViewModel,
                                offlineSyncViewModel // Pass the offline view model
                            )
                        }
                        composable(route = Screen.Settings.route) { SettingsPage(navController) }
                        composable(route = Screen.Favourites.route) { FavouritesPage(navController) }
                        composable(route = Screen.Security.route) { SecurityPage(navController) }
                        composable(route = Screen.Insights.route) { InsightsPage(navController) }
                        composable(route = Screen.StoreDetails.route) { StoreDetailsPage(navController) }

                        // Add the sync screen
                        composable(route = "sync") {
                            SyncScreen(offlineSyncViewModel)
                        }
                    }
                }
            }
        }
    }
}  