package com.tamil.pdfmergeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tamil.pdfmergeapp.ui.theme.PDFWritterAppTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDFWritterAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppTopActionBar(title = stringResource(id = R.string.title_activity_settings), icon =Icons.Filled.ArrowBack) {
                        onBackPressed()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun showScreen() {
    val context = LocalContext.current
    AppTopActionBar(title = context.getString(R.string.title_activity_settings), icon =Icons.Filled.ArrowBack) {
    }
}