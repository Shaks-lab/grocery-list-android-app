package com.example.groceryapp.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.groceryapp.R
import com.example.groceryapp.data.Item
import com.example.groceryapp.data.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

// ---------------- Models used by UI ----------------

// Matches what HomePage / SearchPage / AlertsPage expect.
data class SearchableItem(
    val imageResId: Int,      // used for product thumbnail
    val nameResId: Int,       // string resource for the name
    val price: Double,        // shown in Search / Home
    val description: String   // short description
)

enum class AlertType { PRICE_DROP, IN_STOCK, PROMOTION }

// Alerts reference products via nameResId, and UI sometimes resolves imageResId from allStoreItems.
data class AlertItem(
    val imageResId: Int,
    val nameResId: Int,
    val descriptionResId: Int,
    val type: AlertType
)

class ShoppingViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: StorageReference = FirebaseStorage.getInstance().reference // Added this line

    // ------------- User profile -------------

    private val _userName = MutableStateFlow("Shopper")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // ------------- Shopping lists -------------

    private val _shoppingLists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val shoppingLists: StateFlow<List<ShoppingList>> = _shoppingLists.asStateFlow()

    // ------------- Alerts -------------

    // Static sample alerts; safe & matches existing code.
    private val _recentAlerts = MutableStateFlow(
        listOf(
            AlertItem(R.drawable.product_strawberry, R.string.item_organic_strawberries, R.string.alert_price_drop_1, AlertType.PRICE_DROP),
            AlertItem(R.drawable.product_avocado, R.string.item_avocado, R.string.alert_back_in_stock, AlertType.IN_STOCK),
            AlertItem(R.drawable.product_chickenbreast, R.string.item_chicken_breast, R.string.alert_special_2for1, AlertType.PROMOTION),
        )
    )
    val recentAlerts: StateFlow<List<AlertItem>> = _recentAlerts.asStateFlow()

    // ------------- Store items / search catalogue -------------

    /**
     * allStoreItems is what:
     * - HomePage uses for "Popular items"/"Explore"
     * - SearchPage uses
     * - AlertsPage uses to resolve imageResId for a given alert nameResId
     */
    private val _allStoreItems = MutableStateFlow<List<SearchableItem>>(
        listOf(
            SearchableItem(R.drawable.product_milk, R.string.item_organic_milk, 24.99, "Organic whole milk, 1L"),
            SearchableItem(R.drawable.product_milk, R.string.item_lactose_free_milk, 26.99, "Lactose-free"),
            SearchableItem(R.drawable.product_milk, R.string.item_almond_milk, 32.50, "Almond milk"),
            SearchableItem(R.drawable.product_banana, R.string.item_organic_bananas, 18.99, "Fresh bananas"),
            SearchableItem(R.drawable.product_chickenbreast, R.string.item_chicken_breast, 64.99, "500g fillets"),
            SearchableItem(R.drawable.product_coffeebean, R.string.item_coffee_beans, 89.99, "Roasted beans"),
            SearchableItem(R.drawable.product_eggs, R.string.item_eggs, 29.99, "Free-range eggs"),
            SearchableItem(R.drawable.product_oliveoil, R.string.item_olive_oil, 74.99, "Extra virgin oil")
        )
    )
    val allStoreItems: StateFlow<List<SearchableItem>> = _allStoreItems.asStateFlow()

    // ------------- Helpers -------------

    private val uid: String?
        get() = auth.currentUser?.uid

    init {
        loadUserProfile()
        observeLists()
    }

    // ========== Profile ==========

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        _userEmail.value = user.email ?: ""

        // Optional: load saved display name from DB
        db.child("users").child(user.uid).child("profile").child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    if (!name.isNullOrBlank()) _userName.value = name
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("ShoppingVM", "Profile load cancelled", error.toException())
                }
            })
    }

    fun updateUserName(newName: String) {
        val u = uid ?: return
        if (newName.isBlank()) return
        _userName.value = newName
        db.child("users").child(u).child("profile").child("name").setValue(newName)
    }

    fun updateUserEmail(newEmail: String) {
        val u = uid ?: return
        if (newEmail.isBlank()) return
        _userEmail.value = newEmail
        db.child("users").child(u).child("profile").child("email").setValue(newEmail)
        // NOTE: not updating FirebaseAuth email here; this is for profile display.
    }

    // ========== Lists (backed by Firebase) ==========

    private fun observeLists() {
        val u = uid ?: return
        db.child("users").child(u).child("lists")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lists = mutableListOf<ShoppingList>()

                    for (listSnap in snapshot.children) {
                        val idStr = listSnap.child("id").getValue(String::class.java)
                            ?: listSnap.key
                            ?: continue
                        val id = runCatching { UUID.fromString(idStr) }.getOrElse { UUID.randomUUID() }

                        val customName = listSnap.child("customName").getValue(String::class.java)
                        val isOffline = listSnap.child("isAvailableOffline").getValue(Boolean::class.java) ?: false

                        val items = mutableListOf<Item>()
                        val itemsSnap = listSnap.child("items")
                        for (itemSnap in itemsSnap.children) {
                            val itemIdStr = itemSnap.child("id").getValue(String::class.java)
                                ?: itemSnap.key
                                ?: continue
                            val itemId = runCatching { UUID.fromString(itemIdStr) }.getOrElse { UUID.randomUUID() }
                            val customItemName = itemSnap.child("customName").getValue(String::class.java)
                            val isChecked = itemSnap.child("isChecked").getValue(Boolean::class.java) ?: false

                            items.add(
                                Item(
                                    id = itemId,
                                    customName = customItemName,
                                    isChecked = isChecked
                                )
                            )
                        }

                        lists.add(
                            ShoppingList(
                                id = id,
                                customName = customName,
                                items = items,
                                isAvailableOffline = isOffline
                            )
                        )
                    }

                    _shoppingLists.value = lists
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("ShoppingVM", "Lists listener cancelled", error.toException())
                }
            })
    }

    fun addShoppingList(listName: String, imageUri: Uri? = null) {
        val u = uid ?: return
        if (listName.isBlank()) return

        val id = UUID.randomUUID()
        val base = mutableMapOf<String, Any>(
            "id" to id.toString(),
            "customName" to listName,
            "isAvailableOffline" to false
        )

        if (imageUri == null) {
            db.child("users").child(u).child("lists").child(id.toString()).setValue(base)
        } else {
            val ref = storage.child("users/$u/lists/$id/cover.jpg")
            ref.putFile(imageUri)
                .continueWithTask { task: com.google.android.gms.tasks.Task<com.google.firebase.storage.UploadTask.TaskSnapshot> -> // Explicit type
                    if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                    ref.downloadUrl
                }
                .addOnSuccessListener { downloadUri: Uri? -> // Explicit type
                    base["imageUrl"] = downloadUri.toString()
                    db.child("users").child(u).child("lists").child(id.toString()).setValue(base)
                }
                .addOnFailureListener {
                    // fallback: save list without image
                    db.child("users").child(u).child("lists").child(id.toString()).setValue(base)
                }
        }
    }

    fun deleteList(listId: UUID) {
        val u = uid ?: return
        db.child("users").child(u).child("lists").child(listId.toString()).removeValue()
    }

    fun toggleOfflineAccess(listId: UUID, enabled: Boolean) {
        val u = uid ?: return
        db.child("users").child(u)
            .child("lists").child(listId.toString())
            .child("isAvailableOffline")
            .setValue(enabled)
    }

    // ========== Items inside lists ==========

    // Used when an item comes from the search catalogue
    fun addSearchableItemToList(listId: UUID, item: SearchableItem, context: Context) {
        val u = uid ?: return
        val itemName = context.getString(item.nameResId)
        addItemInternal(u, listId, itemName)
    }

    // Used when user types a custom name
    fun addItemToListByName(listId: UUID, itemName: String) {
        val u = uid ?: return
        if (itemName.isBlank()) return
        addItemInternal(u, listId, itemName)
    }

    private fun addItemInternal(uid: String, listId: UUID, itemName: String) {
        val itemId = UUID.randomUUID()
        val remote = mapOf(
            "id" to itemId.toString(),
            "customName" to itemName,
            "isChecked" to false
        )

        db.child("users").child(uid)
            .child("lists").child(listId.toString())
            .child("items").child(itemId.toString())
            .setValue(remote)
    }

    fun uploadItemImage(
        listId: UUID,
        itemId: UUID,
        uri: Uri,
        onResult: (String?) -> Unit
    ) {
        val u = uid ?: return onResult(null)

        val ref = storage.child("users/$u/lists/$listId/items/$itemId.jpg")
        ref.putFile(uri)
            .continueWithTask { task: com.google.android.gms.tasks.Task<com.google.firebase.storage.UploadTask.TaskSnapshot> -> // Explicit type
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri: Uri? -> // Explicit type
                val url = downloadUri.toString()
                db.child("users").child(u)
                    .child("lists").child(listId.toString())
                    .child("items").child(itemId.toString())
                    .child("imageUrl")
                    .setValue(url)
                onResult(url)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun toggleItemChecked(listId: UUID, itemId: UUID, isChecked: Boolean) {
        val u = uid ?: return

        // optimistic local update for instant UI feedback
        _shoppingLists.update { lists ->
            lists.map { list ->
                if (list.id == listId) {
                    val newItems = list.items.map { item ->
                        if (item.id == itemId) item.copy(isChecked = isChecked) else item
                    }.toMutableList()
                    list.copy(items = newItems)
                } else list
            }
        }

        // write to Firebase
        db.child("users").child(u)
            .child("lists").child(listId.toString())
            .child("items").child(itemId.toString())
            .child("isChecked")
            .setValue(isChecked)
    }

    fun deleteItemFromList(listId: UUID, itemId: UUID) {
        val u = uid ?: return

        // instant local update
        _shoppingLists.update { lists ->
            lists.map { list ->
                if (list.id == listId) {
                    list.copy(items = list.items.filterNot { it.id == itemId }.toMutableList())
                } else list
            }
        }

        // persist
        db.child("users").child(u)
            .child("lists").child(listId.toString())
            .child("items").child(itemId.toString())
            .removeValue()
    }

    // ========== Search ==========

    fun searchItems(query: String, context: Context): List<SearchableItem> {
        val items = _allStoreItems.value
        if (query.isBlank()) return items
        val q = query.trim().lowercase()
        return items.filter { item ->
            val name = context.getString(item.nameResId).lowercase()
            name.contains(q) || item.description.lowercase().contains(q)
        }
    }
}