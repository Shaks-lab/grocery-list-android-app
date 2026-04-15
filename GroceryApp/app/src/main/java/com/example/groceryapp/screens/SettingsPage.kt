package com.example.groceryapp.screens

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.groceryapp.AppPreferences
import com.example.groceryapp.R
import com.example.groceryapp.Screen
import com.example.groceryapp.components.BottomNavigationBar
import com.example.groceryapp.components.LanguageSelectionDialog
import com.example.groceryapp.ui.theme.TextGray
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }


    val currentLanguage = remember {
        val currentLocaleTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentLocaleTag.startsWith("af")) "Afrikaans" else "English"
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismissRequest = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                showLanguageDialog = false
                val newLangCode = if (language == "Afrikaans") "af" else "en"
                val currentLangCode = if (currentLanguage == "Afrikaans") "af" else "en"


                if (newLangCode != currentLangCode) {

                    AppPreferences.hasSetLanguage = true

                    val appLocale = LocaleListCompat.forLanguageTags(newLangCode)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings), style = MaterialTheme.typography.headlineMedium) },
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item { SectionHeader(stringResource(id = R.string.account)) }
            item { SettingsItem(icon = Icons.Default.Person, text = stringResource(id = R.string.profile), details = stringResource(id = R.string.manage_profile_info)) { navController.navigate(Screen.Profile.route) } }
            item { SettingsItem(icon = Icons.Default.CreditCard, text = stringResource(id = R.string.payment_and_loyalty), details = stringResource(id = R.string.manage_payment_methods)) { Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show() } }

            item { SectionHeader(stringResource(id = R.string.app_preferences)) }
            item {
                SettingsItem(
                    icon = Icons.Default.Language,
                    text = stringResource(id = R.string.language),
                    details = currentLanguage,
                    onClick = { showLanguageDialog = true }
                )
            }
            item { SettingsItem(icon = Icons.Default.CloudOff, text = stringResource(id = R.string.offline), details = stringResource(id = R.string.manage_offline_func)) { navController.navigate(Screen.Offline.route) } }
            item { SettingsItem(icon = Icons.Default.Security, text = stringResource(id = R.string.security), details = stringResource(id = R.string.manage_app_security)) { navController.navigate(Screen.Security.route) } }

            item { SectionHeader(stringResource(id = R.string.notifications)) }
            item { SettingsItem(icon = Icons.Default.Notifications, text = stringResource(id = R.string.notification_settings_title), details = stringResource(id = R.string.manage_notifications)) { navController.navigate(Screen.Alerts.route) } }

            item { SectionHeader(stringResource(id = R.string.legal)) }
            item { SettingsItem(icon = Icons.Default.Description, text = stringResource(id = R.string.terms_of_service)) { Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show() } }
            item { SettingsItem(icon = Icons.Default.PrivacyTip, text = stringResource(id = R.string.privacy_policy)) { Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show() } }

        }
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.SignIn.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        ) {
            Text("Log out")
        }
    }
}


@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp, end = 16.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    details: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text) },
        supportingContent = { if (details != null) Text(details) else null },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = text, tint = TextGray)
        },
        trailingContent = {
            if (details != null || (text == stringResource(id = R.string.terms_of_service) || text == stringResource(id = R.string.privacy_policy))) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextGray)
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
}