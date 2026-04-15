package com.example.groceryapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.groceryapp.data.ShoppingList
import com.example.groceryapp.viewmodels.SearchableItem
import java.util.UUID

@Composable
fun AddToListDialog(
    item: SearchableItem,
    shoppingLists: List<ShoppingList>,
    onListSelected: (UUID) -> Unit,
    onDismissRequest: () -> Unit
) {
    val itemName = stringResource(id = item.nameResId)
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Add '$itemName' to a list") },
        text = {
            if (shoppingLists.isEmpty()) {
                Text("You have no shopping lists.")
            } else {
                LazyColumn {
                    items(shoppingLists) { list ->
                        Text(
                            text = list.getDisplayName(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth().clickable { onListSelected(list.id) }.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInfoDialog(
    title: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("New Value") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = { Button(onClick = { onConfirm(text) }, enabled = text.isNotBlank() && text != initialValue) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } }
    )
}

@Composable
fun LanguageSelectionDialog(
    onLanguageSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Language") },
        text = {
            Column {
                Text("English", modifier = Modifier.fillMaxWidth().clickable { onLanguageSelected("English") }.padding(vertical = 12.dp))
                HorizontalDivider()
                Text("Afrikaans", modifier = Modifier.fillMaxWidth().clickable { onLanguageSelected("Afrikaans") }.padding(vertical = 12.dp))
            }
        },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } }
    )
}