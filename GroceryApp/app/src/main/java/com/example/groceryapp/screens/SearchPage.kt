package com.example.groceryapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.components.AddToListDialog
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.ui.theme.BackgroundGray
import com.example.groceryapp.ui.theme.TextGray
import com.example.groceryapp.viewmodels.SearchableItem
import com.example.groceryapp.viewmodels.ShoppingViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current


    val searchResults = viewModel.searchItems(searchQuery, context)

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<SearchableItem?>(null) }

    val allShoppingLists by viewModel.shoppingLists.collectAsState()

    if (showDialog && selectedItem != null) {
        AddToListDialog(
            item = selectedItem!!,
            shoppingLists = allShoppingLists,
            onDismissRequest = { showDialog = false },
            onListSelected = { listId ->
                viewModel.addSearchableItemToList(listId, selectedItem!!, context)
                showDialog = false
                Toast.makeText(context, "${context.getString(selectedItem!!.nameResId)} added to list!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.search), style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(id = R.string.search_for_items)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { if(searchQuery.isNotEmpty()) IconButton(onClick={ searchQuery = ""}) { Icon(Icons.Default.Close, null) } else null},
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundGray,
                    unfocusedContainerColor = BackgroundGray,
                    unfocusedBorderColor = BackgroundGray
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(stringResource(id = R.string.category))
                FilterChip(stringResource(id = R.string.store))
                FilterChip(stringResource(id = R.string.price))
            }

            if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.no_results_found, searchQuery))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(searchResults) { item ->
                        SearchResultCard(
                            item = item,
                            onClick = {
                                selectedItem = item
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(item: SearchableItem, onClick: () -> Unit) {

    val itemName = stringResource(id = item.nameResId)

    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(itemName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(item.description, color = TextGray) },
        leadingContent = {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = itemName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        },
        trailingContent = {
            Text(
                String.format(Locale.US, "R%.2f", item.price),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    )
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(label: String) {
    AssistChip(
        onClick = { /* TODO */ },
        label = { Text(label) },
        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) },
        shape = RoundedCornerShape(8.dp)
    )
}