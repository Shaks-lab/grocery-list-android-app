package com.example.groceryapp.screens

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class GroceryApp : Application() {

    // Application-wide dependencies
    val database by lazy { LocalDatabase.getInstance(this) } //
    val networkManager by lazy { NetworkManager(context = this) }
    val repository by lazy {
        OfflineSyncRepository(
            shoppingListDao = database.shoppingListDao(),
            shoppingListItemDao = database.shoppingListItemDao(),
            networkManager = networkManager
        )
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Initialize network monitoring
        networkManager.startNetworkMonitoring()

        // Setup periodic sync
        setupPeriodicSync()
    }

    private fun setupPeriodicSync() {
        applicationScope.launch {
            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                1, TimeUnit.HOURS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(this@GroceryApp).enqueueUniquePeriodicWork(
                "grocery_sync_work",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
        }
    }
}

