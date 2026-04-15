package com.example.groceryapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.groceryapp.R
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.ui.theme.BluePrimary
import com.example.groceryapp.ui.theme.GroceryAppTheme
import com.example.groceryapp.ui.theme.TextGray



data class StorePromotion(val title: String, val subtitle: String, val imageResId: Int)
data class ShoppingListItemStatus(val name: String, val status: String, val imageResId: Int, val discounted: Boolean = false)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailsPage(navController: NavController) {
    val promotions = listOf(
        StorePromotion("Limited Time Offer", "20% Off All Organic Produce", R.drawable.product_milk)
    )
    val listItems = listOf(
        ShoppingListItemStatus("Apples", "Available", R.drawable.product_milk),
        ShoppingListItemStatus("Milk", "In Stock", R.drawable.product_milk),
        ShoppingListItemStatus("Bread", "Discounted", R.drawable.product_milk, discounted = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.store_details), style = MaterialTheme.typography.headlineMedium) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { StoreHeaderImage() }
            item { StoreInformationSection() }
            item {
                SectionTitle(stringResource(R.string.current_promotions))
                promotions.forEach { promotion -> PromotionCard(promotion = promotion) }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                SectionTitle(stringResource(R.string.shopping_list_items))
                listItems.forEach { item -> ListItemCard(item = item) }
            }
        }
    }
}


@Composable
fun StoreHeaderImage() {
    Image(
        painter = painterResource(id = R.drawable.store_front),
        contentDescription = "Store Front",
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StoreInformationSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.store_information), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        InfoRow(label = stringResource(R.string.opening), value = "8:00 AM - 10:00 PM")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = stringResource(R.string.phone), value = "(083) 654-3219")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = stringResource(R.string.address), value = "123 Main St, Sandton")
    }
    HorizontalDivider()
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextGray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun PromotionCard(promotion: StorePromotion) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(promotion.title, style = MaterialTheme.typography.bodyMedium, color = BluePrimary)
                Text(promotion.subtitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = promotion.imageResId),
                contentDescription = promotion.title,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ListItemCard(item: ShoppingListItemStatus) {
    ListItem(
        modifier = Modifier.padding(horizontal = 16.dp),
        headlineContent = { Text(text = item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(text = item.status, color = if (item.discounted) Color(0xFFC70039) else TextGray) },
        leadingContent = {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun StoreDetailsPagePreview() {
    GroceryAppTheme {
        StoreDetailsPage(rememberNavController())
    }
}