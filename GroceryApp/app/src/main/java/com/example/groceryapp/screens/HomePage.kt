package com.example.groceryapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.Screen
import com.example.groceryapp.components.AddToListDialog
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.data.ShoppingList
import com.example.groceryapp.ui.theme.*
import com.example.groceryapp.viewmodels.AlertItem
import com.example.groceryapp.viewmodels.SearchableItem
import com.example.groceryapp.viewmodels.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel(),
    offlineSyncViewModel: OfflineSyncViewModel
) {
    val shoppingLists by viewModel.shoppingLists.collectAsState()
    val recentAlerts by viewModel.recentAlerts.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val allItems by viewModel.allStoreItems.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<SearchableItem?>(null) }

    if (showDialog && selectedItem != null) {
        AddToListDialog(
            item = selectedItem!!,
            shoppingLists = shoppingLists,
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
                title = { Text(stringResource(id = R.string.app_name), style = MaterialTheme.typography.headlineMedium) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.good_morning, userName.split(" ")[0]),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            item { MyListsCard(lists = shoppingLists, navController = navController) }
            item {
                RecentAlertsCard(
                    alerts = recentAlerts.take(2),
                    onAlertClick = { alert ->
                        val itemToAdd = allItems.find { it.nameResId == alert.nameResId }
                        if (itemToAdd != null) {
                            selectedItem = itemToAdd
                            showDialog = true
                        }
                    }
                )
            }
            item { FeaturedCard(navController = navController) }
        }
    }
}


@Composable
fun MyListsCard(lists: List<ShoppingList>, navController: NavController) {
    Column {
        Text(
            text = stringResource(id = R.string.my_lists),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(1.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                if (lists.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_lists_yet),
                        modifier = Modifier.padding(16.dp),
                        color = TextGray
                    )
                } else {
                    lists.take(3).forEachIndexed { index, list ->
                        ListItem(
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.ListDetail.withArgs(list.id.toString()))
                            },
                            headlineContent = { Text(list.getDisplayName()) },
                            supportingContent = { Text(stringResource(id = R.string.item_count, list.items.size)) },
                            trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
                        )
                        if (index < lists.take(3).size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentAlertsCard(alerts: List<AlertItem>, onAlertClick: (AlertItem) -> Unit) {
    Column {
        Text(
            text = stringResource(id = R.string.recent_alerts),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(1.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                if (alerts.isEmpty()) {
                    Text(
                        text = "No recent alerts.",
                        modifier = Modifier.padding(16.dp),
                        color = TextGray
                    )
                } else {
                    alerts.forEachIndexed { index, alert ->
                        ListItem(
                            modifier = Modifier.clickable { onAlertClick(alert) },
                            headlineContent = { Text(stringResource(id = alert.nameResId)) },
                            supportingContent = { Text(stringResource(id = alert.descriptionResId)) },
                            leadingContent = {
                                Image(
                                    painter = painterResource(id = alert.imageResId),
                                    contentDescription = stringResource(id = alert.nameResId),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            }
                        )
                        if (index < alerts.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedCard(navController: NavController) {
    Column {
        Text(
            text = stringResource(id = R.string.featured_offer),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            onClick = { navController.navigate(Screen.Search.route) },
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(1.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(id = R.string.special_offer), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(stringResource(id = R.string.special_offer_details), style = MaterialTheme.typography.bodyMedium, color = TextGray)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.product_avocado),
                        contentDescription = stringResource(id = R.string.special_offer),
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(Screen.Search.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text(stringResource(id = R.string.shop_now))
                }
            }
        }
    }
}