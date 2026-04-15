package com.example.groceryapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Settings
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
import com.example.groceryapp.ui.theme.BluePrimary
import com.example.groceryapp.viewmodels.AlertItem
import com.example.groceryapp.viewmodels.AlertType
import com.example.groceryapp.viewmodels.SearchableItem
import com.example.groceryapp.viewmodels.ShoppingViewModel
import com.google.firebase.messaging.FirebaseMessaging


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsPage(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel()
) {
    val shoppingLists by viewModel.shoppingLists.collectAsState()
    val recentAlerts by viewModel.recentAlerts.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<SearchableItem?>(null) }


    val allItems by viewModel.allStoreItems.collectAsState()

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
                title = { Text(stringResource(id = R.string.alerts), style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.settings))
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { PreferencesCard() }

            item {
                Text(
                    stringResource(id = R.string.recent_alerts),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(recentAlerts) { alert ->
                EnhancedAlertCard(
                    alert = alert,
                    onAddClick = {
                        val itemToAdd = allItems.find { it.nameResId == alert.nameResId }
                        if (itemToAdd != null) {
                            selectedItem = itemToAdd
                            showDialog = true
                        }
                    },
                    onClick = {
                        Toast.makeText(context, "Navigating to details for ${context.getString(alert.nameResId)}...", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


@Composable
fun PreferencesCard() {
    Card {
        Column(Modifier.padding(vertical = 8.dp)) {
            Text(
                stringResource(id = R.string.notification_preferences),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            NotificationPreferenceItem(
                title = stringResource(id = R.string.price_drop_alerts),
                subtitle = stringResource(id = R.string.price_drop_description)
            )
            NotificationPreferenceItem(
                title = stringResource(id = R.string.stock_alerts),
                subtitle = stringResource(id = R.string.stock_alerts_description)
            )
            NotificationPreferenceItem(
                title = stringResource(id = R.string.promo_offers),
                subtitle = stringResource(id = R.string.promo_offers_description)
            )
        }
    }
}

@Composable
fun NotificationPreferenceItem(title: String, subtitle: String) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        },
        supportingContent = {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = { checked ->
                    isChecked = checked
                    val topic = when {
                        title.contains("price", true) -> "price_drop"
                        title.contains("stock", true) -> "stock_alerts"
                        title.contains("promo", true) -> "promo_offers"
                        else -> "general"
                    }
                    if (checked) {
                        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        Toast.makeText(context, "Subscribed to $title alerts", Toast.LENGTH_SHORT).show()
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                        Toast.makeText(context, "Unsubscribed from $title alerts", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    HorizontalDivider()
}



@Composable
fun EnhancedAlertCard(
    alert: AlertItem,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val (icon, color) = when (alert.type) {
        AlertType.PRICE_DROP -> Icons.Default.ArrowDownward to Color(0xFFE52F6A)
        AlertType.IN_STOCK -> Icons.Default.Autorenew to Color(0xFF279644)
        AlertType.PROMOTION -> Icons.Default.Campaign to BluePrimary
    }

    Card(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    painter = painterResource(id = alert.imageResId),
                    contentDescription = stringResource(id = alert.nameResId),
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = icon,
                    contentDescription = "Alert type",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .background(color, shape = CircleShape)
                        .padding(4.dp)
                        .size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = alert.nameResId), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(stringResource(id = alert.descriptionResId), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = stringResource(id = R.string.add_to_list))
            }
        }
    }
}