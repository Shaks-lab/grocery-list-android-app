package com.example.groceryapp.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.groceryapp.R
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.ui.theme.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.insights), style = MaterialTheme.typography.headlineMedium) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { SpendingOverviewCard() }
            item { BudgetManagementCard() }
            item { SavingsAndTipsCard() }
        }
    }
}

@Composable
fun SpendingOverviewCard() {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(id = R.string.monthly_spending), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("R2500", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
                Text(text = ".00", style = MaterialTheme.typography.headlineMedium.copy(color = TextGray), modifier = Modifier.padding(bottom = 4.dp))
            }
            Text("Last 6 Months -- 15%", color = TextGray)
            Spacer(modifier = Modifier.height(16.dp))
            SpendingChart()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { month ->
                    Text(month, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = TextGray)
                }
            }
        }
    }
}

@Composable
fun SpendingChart() { /* ... unchanged ... */ }

@Composable
fun BudgetManagementCard() {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(id = R.string.budget_management), style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = { /* Edit Budget */ }) { Text("Edit", color = BluePrimary) }
            }

        }
    }
}

@Composable
fun SavingsAndTipsCard() {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(id = R.string.savings_tips), style = MaterialTheme.typography.headlineSmall)
            // ... The rest of this card is unchanged ...
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InsightsPagePreview() {
    GroceryAppTheme {
        InsightsPage(rememberNavController())
    }
}