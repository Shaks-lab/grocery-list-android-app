package com.example.groceryapp.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.groceryapp.R
import java.util.UUID

data class Item(
    val id: UUID = UUID.randomUUID(),
    val nameResId: Int? = null,
    var customName: String? = null,
    var isChecked: Boolean = false,
    val imageResId: Int = R.drawable.logo_png
) {
    @Composable
    fun getDisplayName(): String {
        return if (customName != null) {
            customName!!
        } else if (nameResId != null) {
            stringResource(id = nameResId)
        } else {
            ""
        }
    }
}

data class ShoppingList(
    val id: UUID = UUID.randomUUID(),
    val nameResId: Int? = null,
    var customName: String? = null,
    val items: MutableList<Item> = mutableListOf(),
    var isAvailableOffline: Boolean = false
) {
    @Composable
    fun getDisplayName(): String {
        return if (customName != null) {
            customName!!
        } else if (nameResId != null) {
            stringResource(id = nameResId)
        } else {
            "Unnamed List"
        }
    }
}