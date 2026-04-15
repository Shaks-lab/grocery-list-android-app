package com.example.groceryapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.ui.theme.BackgroundGray
import com.example.groceryapp.ui.theme.TextBlack

data class FavouriteItem(val name: String, val imageResId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesPage(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val favouriteItems = listOf(
        FavouriteItem("Organic Bananas", R.drawable.product_banana),
        FavouriteItem("Whole Milk", R.drawable.product_milk),
        FavouriteItem("Free-Range Eggs", R.drawable.product_eggs),
        FavouriteItem("Avocados", R.drawable.product_avocado),
        FavouriteItem("Chicken Breast", R.drawable.product_chickenbreast),
        FavouriteItem("Spinach", R.drawable.product_spinach),
        FavouriteItem("Tomatoes", R.drawable.product_tomato),
        FavouriteItem("Cheddar Cheese", R.drawable.product_cheese),
        FavouriteItem("Coffee Beans", R.drawable.product_coffeebean),
        FavouriteItem("Olive Oil", R.drawable.product_oliveoil)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourites", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(id = R.string.search_for_items)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundGray,
                    unfocusedContainerColor = BackgroundGray,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Favourites", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(favouriteItems) { item ->
                    FavouriteListItem(item = item)
                }
            }
        }
    }
}

@Composable
fun FavouriteListItem(item: FavouriteItem) {
    var isFavourite by remember { mutableStateOf(true) }
    ListItem(
        modifier = Modifier.clickable { /* TODO: Navigate to item details */ },
        headlineContent = { Text(item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) },
        leadingContent = {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        },
        trailingContent = {
            IconButton(onClick = { isFavourite = !isFavourite }) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Favourite",
                    tint = if (isFavourite) TextBlack else Color.LightGray
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    HorizontalDivider()
}