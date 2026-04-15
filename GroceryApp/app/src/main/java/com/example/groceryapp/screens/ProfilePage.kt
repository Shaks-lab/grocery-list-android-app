package com.example.groceryapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.components.EditInfoDialog
import com.example.groceryapp.ui.theme.TextGray
import com.example.groceryapp.viewmodels.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    navController: NavController,
    viewModel: ShoppingViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (showDialog) {
        EditInfoDialog(
            title = "Edit $editingField",
            initialValue = if (editingField == "Name") userName else userEmail,
            onDismissRequest = { showDialog = false },
            onConfirm = { newValue ->
                if (editingField == "Name") {
                    viewModel.updateUserName(newValue)
                } else {
                    viewModel.updateUserEmail(newValue)
                }
                showDialog = false
                Toast.makeText(context, "$editingField updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.profile), style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(100.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(userEmail, style = MaterialTheme.typography.bodyMedium, color = TextGray)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = stringResource(id = R.string.account),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                ProfileMenuItem(text = stringResource(id = R.string.personal_information)) {
                    editingField = "Name"
                    showDialog = true
                }
            }

            item {
                ProfileMenuItem(text = stringResource(id = R.string.email)) {
                    editingField = "Email"
                    showDialog = true
                }
            }
            item {
                ProfileMenuItem(text = stringResource(id = R.string.payment_and_loyalty)) {
                    Toast.makeText(context, "Navigating to Payment...", Toast.LENGTH_SHORT).show()
                }
            }
            item {
                ProfileMenuItem(text = stringResource(id = R.string.manage_linked_devices)) {
                    Toast.makeText(context, "Navigating to Linked Accounts...", Toast.LENGTH_SHORT).show()
                }
            }
            item {
                ProfileMenuItem(
                    text = stringResource(id = R.string.orders),
                    details = stringResource(id = R.string.order_history)
                ) {
                    Toast.makeText(context, "Navigating to Order History...", Toast.LENGTH_SHORT).show()
                }
            }
            item {
                ProfileMenuItem(text = stringResource(id = R.string.receipts)) {
                    Toast.makeText(context, "Navigating to Receipts...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(text: String, details: String? = null, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text) },
        supportingContent = { if (details != null) Text(details) else null },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextGray)
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}