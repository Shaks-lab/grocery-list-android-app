package com.example.groceryapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.Screen
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.ui.theme.TextGray
import com.example.groceryapp.viewmodels.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPage(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel(),
    offlineSyncViewModel: OfflineSyncViewModel
) {
    val shoppingLists by viewModel.shoppingLists.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.lists), style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.NewList.route) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_new_list))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            items(shoppingLists) { list ->
                ListItem(
                    modifier = Modifier.clickable { navController.navigate(Screen.ListDetail.withArgs(list.id.toString())) },

                    headlineContent = { Text(list.getDisplayName(), style = MaterialTheme.typography.bodyLarge) },
                    supportingContent = { Text(stringResource(id = R.string.item_count, list.items.size), style = MaterialTheme.typography.bodyMedium, color = TextGray) },
                    leadingContent = { Icon(Icons.Default.Share, contentDescription = "Shared List", tint = TextGray) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}