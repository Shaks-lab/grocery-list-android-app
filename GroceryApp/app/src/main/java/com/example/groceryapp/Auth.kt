// In file: com/example/groceryapp/Auth.kt
package com.example.groceryapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// This object holds our app-wide preference for language selection.
// It does NOT contain any biometric code.
object AppPreferences {
    var hasSetLanguage by mutableStateOf(false)
}