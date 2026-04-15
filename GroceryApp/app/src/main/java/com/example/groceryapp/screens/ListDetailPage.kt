package com.example.groceryapp.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.data.Item
import com.example.groceryapp.ui.theme.BackgroundGray
import com.example.groceryapp.viewmodels.ShoppingViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailPage(
    navController: NavController,
    viewModel: ShoppingViewModel,
    listId: String?
) { // Removed unused offlineSyncViewModel parameter

    val list by remember(listId) {
        derivedStateOf {
            viewModel.shoppingLists.value.find { it.id.toString() == listId }
        }
    }

    var selectedItemForImage by remember { mutableStateOf<Item?>(null) }
    var newItemText by remember { mutableStateOf("") }
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && selectedItemForImage != null && list != null) {
            viewModel.uploadItemImage(list!!.id, selectedItemForImage!!.id, uri) {
                // resultUrl parameter removed since it's unused
                // Optional: Show toast for success/failure
                Toast.makeText(context, "Image uploaded!", Toast.LENGTH_SHORT).show()
            }
        }
        selectedItemForImage = null // Reset after use
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        list?.customName ?: stringResource(id = R.string.list_details),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        val currentList = list
        if (currentList == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.list_not_found))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    placeholder = { Text(stringResource(id = R.string.add_new_item)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundGray,
                        unfocusedContainerColor = BackgroundGray,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (newItemText.isNotBlank()) {
                            viewModel.addItemToListByName(currentList.id, newItemText)
                            Toast.makeText(
                                context,
                                "'$newItemText' added!",
                                Toast.LENGTH_SHORT
                            ).show()
                            newItemText = ""
                        }
                    },
                    enabled = newItemText.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_new_item)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentList.items) { item ->
                    ListItemRow(
                        item = item,
                        onCheckedChange = { checked ->
                            viewModel.toggleItemChecked(currentList.id, item.id, checked)
                        },
                        onDelete = {
                            viewModel.deleteItemFromList(currentList.id, item.id)
                        },
                        onAddImage = { // Fixed: Added proper parameter
                            selectedItemForImage = item
                            imagePicker.launch("image/*")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ListItemRow(
    item: Item,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onAddImage: () -> Unit // Fixed: Changed from iconButton: Unit to proper function
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.customName ?: "Unnamed Item",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        // Add Image button
        IconButton(onClick = onAddImage) {
            Icon(
                Icons.Default.Image,
                contentDescription = "Add image"
            )
        }

        // Delete button
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete item"
            )
        }

        // Checkbox
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}