package com.example.groceryapp.screens

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class OfflineSyncRepository(
    private val shoppingListDao: ShoppingListDao,
    private val shoppingListItemDao: ShoppingListItemDao,
    private val networkManager: NetworkManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ---- SHOPPING LISTS ----
    fun getAllShoppingLists(): Flow<List<ShoppingListEntity>> = shoppingListDao.getAllLists()

    suspend fun createShoppingList(name: String): String {
        val listId = UUID.randomUUID().toString()
        val list = ShoppingListEntity(
            id = listId,
            name = name,
            isSynced = networkManager.isConnected()
        )

        shoppingListDao.insertList(list)

        // Try to sync immediately if online, otherwise queue for later
        if (networkManager.isConnected()) {
            triggerSync()
        }

        return listId
    }

    suspend fun deleteShoppingList(listId: String) {
        shoppingListDao.deleteList(listId)
        triggerSync() // Always trigger sync (don’t depend on network state)
    }

    // ---- SHOPPING LIST ITEMS ----
    fun getShoppingListItems(listId: String): Flow<List<ShoppingListItemEntity>> =
        shoppingListItemDao.getItemsByListId(listId)

    suspend fun addItemToList(listId: String, itemName: String, quantity: Int = 1): String {
        val itemId = UUID.randomUUID().toString()
        val item = ShoppingListItemEntity(
            id = itemId,
            listId = listId,
            name = itemName,
            quantity = quantity,
            isSynced = networkManager.isConnected()
        )

        shoppingListItemDao.insertItem(item)
        triggerSync()

        return itemId
    }

    suspend fun updateItemCheckedStatus(itemId: String, isChecked: Boolean) {
        shoppingListItemDao.updateItemCheckedStatus(
            itemId = itemId,
            isChecked = isChecked
        )
        triggerSync()
    }

    suspend fun deleteItem(itemId: String) {
        shoppingListItemDao.deleteItem(itemId)
        triggerSync()
    }

    // ---- SYNC FUNCTIONALITY ----
    fun triggerSync() {
        scope.launch {
            if (networkManager.isConnected()) {
                try {
                    syncData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                println("🔌 Offline — sync will retry when connected.")
            }
        }
    }

    suspend fun syncData() {
        println("🔁 Starting sync process...")

        // --- Sync shopping lists ---
        val unsyncedLists = shoppingListDao.getUnsyncedLists()
        for (list in unsyncedLists) {
            try {
                if (syncListWithServer(list)) {
                    shoppingListDao.markListAsSynced(list.id)
                }
            } catch (e: Exception) {
                println("⚠️ Failed to sync list: ${list.name} — ${e.message}")
            }
        }

        // --- Sync shopping list items ---
        val unsyncedItems = shoppingListItemDao.getUnsyncedItems()
        for (item in unsyncedItems) {
            try {
                if (syncItemWithServer(item)) {
                    shoppingListItemDao.markItemAsSynced(item.id)
                }
            } catch (e: Exception) {
                println("⚠️ Failed to sync item: ${item.name} — ${e.message}")
            }
        }

        println("✅ Sync completed successfully.")
    }

    private suspend fun syncListWithServer(list: ShoppingListEntity): Boolean {
        // TODO: Replace with real API call
        delay(100)
        return true
    }

    private suspend fun syncItemWithServer(item: ShoppingListItemEntity): Boolean {
        // TODO: Replace with real API call
        delay(100)
        return true
    }
}
