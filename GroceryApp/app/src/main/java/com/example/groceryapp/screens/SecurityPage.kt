package com.example.groceryapp.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groceryapp.R
import com.example.groceryapp.ui.theme.TextGray
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityPage(navController: NavController) {
    val context = LocalContext.current
    val viewModel: SecurityViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.security), style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Biometric Authentication Section
            BiometricSecurityItem(viewModel = viewModel)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SecurityMenuItem(
                title = stringResource(id = R.string.manage_linked_devices),
                onClick = { Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show() }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SecurityMenuItem(
                title = stringResource(id = R.string.pin_password),
                subtitle = stringResource(id = R.string.change_pin_password),
                onClick = { Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show() }
            )
        }
    }
}

@Composable
fun BiometricSecurityItem(viewModel: SecurityViewModel) {
    val context = LocalContext.current
    val biometricStatus by viewModel.biometricStatus.observeAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.observeAsState(initial = false)

    // Get the activity context for biometric authentication
    val activity = context as? FragmentActivity

    val statusText = when (biometricStatus) {
        BiometricStatus.AVAILABLE -> "Biometric authentication available"
        BiometricStatus.NOT_ENROLLED -> "No biometrics enrolled on device"
        BiometricStatus.NOT_AVAILABLE -> "Biometric hardware not available"
        BiometricStatus.UNAVAILABLE -> "Biometric temporarily unavailable"
        BiometricStatus.UPDATE_REQUIRED -> "Security update required"
        BiometricStatus.UNSUPPORTED -> "Biometric not supported"
        BiometricStatus.UNKNOWN -> "Biometric status unknown"
        else -> "Checking biometric status..." // Added else branch to fix the error
    }

    val statusColor = when (biometricStatus) {
        BiometricStatus.AVAILABLE -> Color(0xFF4CAF50) // Green
        BiometricStatus.NOT_ENROLLED -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }

    ListItem(
        headlineContent = {
            Column {
                Text(
                    "Biometric Authentication",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        supportingContent = {
            Text(
                "Use fingerprint or face recognition to secure your app",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingContent = {
            Switch(
                checked = isBiometricEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        // Enable biometric - trigger authentication
                        if (activity != null) {
                            val biometricManager = BiometricManager(context)
                            biometricManager.authenticate(
                                activity = activity,
                                title = "Enable Biometric Authentication",
                                subtitle = "Use your fingerprint or face to secure your app",
                                onSuccess = {
                                    viewModel.setBiometricEnabled(true)
                                    Toast.makeText(
                                        context,
                                        "Biometric authentication enabled!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { errorCode, errorMessage ->
                                    Toast.makeText(
                                        context,
                                        "Biometric setup failed: $errorMessage",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    viewModel.setBiometricEnabled(false)
                                },
                                onFailed = {
                                    Toast.makeText(
                                        context,
                                        "Biometric authentication failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.setBiometricEnabled(false)
                                }
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Cannot access biometric authentication",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.setBiometricEnabled(false)
                        }
                    } else {
                        // Disable biometric
                        viewModel.setBiometricEnabled(false)
                        Toast.makeText(
                            context,
                            "Biometric authentication disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = biometricStatus == BiometricStatus.AVAILABLE && activity != null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    )
}

@Composable
fun SecurityMenuItem(title: String, subtitle: String? = null, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Normal)
        },
        supportingContent = { if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium) else null },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextGray)
        }
    )
}