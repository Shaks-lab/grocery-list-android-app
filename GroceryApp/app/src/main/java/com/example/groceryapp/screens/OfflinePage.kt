package com.example.groceryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.data.ShoppingList
import com.example.groceryapp.ui.theme.TextGray
import com.example.groceryapp.viewmodels.ShoppingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflinePage(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel(),
    offlineSyncViewModel: OfflineSyncViewModel
) {
    val shoppingLists by viewModel.shoppingLists.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.offline_settings), style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        stringResource(id = R.string.offline_access),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.offline_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                        Text(
                            text = stringResource(id = R.string.data_management),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        shoppingLists.forEach { list ->
                            OfflineListItem(
                                list = list,
                                onToggle = { isEnabled ->
                                    viewModel.toggleOfflineAccess(list.id, isEnabled)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineListItem(list: ShoppingList, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(list.getDisplayName(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = list.isAvailableOffline,
            onCheckedChange = onToggle
        )
    }
}