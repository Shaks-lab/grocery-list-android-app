package com.example.groceryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun SyncScreen(offlineSyncViewModel: OfflineSyncViewModel) {
    // Collect list flow and sync status
    val shoppingLists by offlineSyncViewModel.getAllShoppingLists()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val syncStatus by offlineSyncViewModel.syncStatus.observeAsState("")


    var newListName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(
                    onClick = { offlineSyncViewModel.triggerSync() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Sync")
                }
                FloatingActionButton(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            offlineSyncViewModel.createShoppingList(newListName)
                            newListName = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add List")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sync status text
            if (syncStatus.isNotBlank()) {
                Text(
                    text = syncStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            // Add new list input
            OutlinedTextField(
                value = newListName,
                onValueChange = { newListName = it },
                label = { Text("New Shopping List Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shopping Lists Title
            Text(
                text = "Shopping Lists (Offline Sync)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Display all shopping lists
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(shoppingLists) { list ->
                    ShoppingListCard(
                        list = list,
                        onItemClick = { /* TODO: navigate to list details if needed */ }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListCard(
    list: ShoppingListEntity,
    onItemClick: () -> Unit
) {
    Card(
        onClick = onItemClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (list.isSynced) "Synced" else "Not Synced",
                style = MaterialTheme.typography.bodySmall,
                color = if (list.isSynced) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}
