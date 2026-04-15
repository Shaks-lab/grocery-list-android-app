package com.example.groceryapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OfflineSyncViewModel(
    private val repository: OfflineSyncRepository
) : ViewModel() {

    private val _syncStatus = MutableLiveData<String>()
    val syncStatus: LiveData<String> = _syncStatus

    fun getAllShoppingLists(): Flow<List<ShoppingListEntity>> {
        return repository.getAllShoppingLists()
    }

    fun createShoppingList(name: String) {
        viewModelScope.launch {
            try {
                repository.createShoppingList(name)
                _syncStatus.value = "Shopping list created successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error creating shopping list: ${e.message}"
            }
        }
    }

    fun getShoppingListItems(listId: String): Flow<List<ShoppingListItemEntity>> {
        return repository.getShoppingListItems(listId)
    }

    fun addItemToList(listId: String, itemName: String, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                repository.addItemToList(listId, itemName, quantity)
                _syncStatus.value = "Item added successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error adding item: ${e.message}"
            }
        }
    }

    fun updateItemCheckedStatus(itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateItemCheckedStatus(itemId, isChecked)
            } catch (e: Exception) {
                _syncStatus.value = "Error updating item: ${e.message}"
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.deleteItem(itemId)
                _syncStatus.value = "Item deleted successfully"
            } catch (e: Exception) {
                _syncStatus.value = "Error deleting item: ${e.message}"
            }
        }
    }

    // Add this method - either rename triggerManualSync to triggerSync or add both
    fun triggerSync() {
        viewModelScope.launch {
            _syncStatus.value = "Starting sync..."
            repository.syncData() // Make sure this method exists in repository
            _syncStatus.value = "Sync completed"
        }
    }

    // Keep this for backward compatibility or remove it
    fun triggerManualSync() {
        triggerSync()
    }
}