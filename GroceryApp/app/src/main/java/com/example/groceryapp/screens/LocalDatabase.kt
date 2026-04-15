package com.example.groceryapp.screens

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * ================================
 *  ENTITY: Shopping List
 * ================================
 */
@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,

    // Sync fields
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

/**
 * ================================
 *  ENTITY: Shopping List Item
 * ================================
 */
@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listId"])] //  Added for faster joins & stability
)
data class ShoppingListItemEntity(
    @PrimaryKey val id: String,
    val listId: String,
    val name: String,
    val quantity: Int = 1,
    val category: String? = null,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),

    // Sync fields
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)

/**
 * ================================
 *  DAO: Shopping Lists
 * ================================
 */
@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_lists WHERE isDeleted = 0 ORDER BY lastModified DESC")
    fun getAllLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_lists WHERE id = :listId LIMIT 1")
    suspend fun getListById(listId: String): ShoppingListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity)

    @Update
    suspend fun updateList(list: ShoppingListEntity)

    @Query("UPDATE shopping_lists SET isDeleted = 1, lastModified = :timestamp WHERE id = :listId")
    suspend fun deleteList(listId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM shopping_lists WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedLists(): List<ShoppingListEntity>

    @Query("UPDATE shopping_lists SET isSynced = 1 WHERE id = :listId")
    suspend fun markListAsSynced(listId: String)
}

/**
 * ================================
 *  DAO: Shopping List Items
 * ================================
 */
@Dao
interface ShoppingListItemDao {

    @Query("SELECT * FROM shopping_list_items WHERE listId = :listId AND isDeleted = 0 ORDER BY createdAt ASC")
    fun getItemsByListId(listId: String): Flow<List<ShoppingListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingListItemEntity)

    @Update
    suspend fun updateItem(item: ShoppingListItemEntity)

    @Query("UPDATE shopping_list_items SET isDeleted = 1, lastModified = :timestamp WHERE id = :itemId")
    suspend fun deleteItem(itemId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM shopping_list_items WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedItems(): List<ShoppingListItemEntity>

    @Query("UPDATE shopping_list_items SET isSynced = 1 WHERE id = :itemId")
    suspend fun markItemAsSynced(itemId: String)

    @Query("UPDATE shopping_list_items SET isChecked = :isChecked, lastModified = :timestamp WHERE id = :itemId")
    suspend fun updateItemCheckedStatus(
        itemId: String,
        isChecked: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
}

/**
 * ================================
 *  DATABASE: Local Room Database
 * ================================
 */
@Database(
    entities = [ShoppingListEntity::class, ShoppingListItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingListItemDao(): ShoppingListItemDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "grocery_database"
                )
                    .fallbackToDestructiveMigration() //  ensures compatibility on version change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
