package com.example

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.FitCheckApp
import com.example.ui.FitCheckViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: FitCheckViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Process any custom shared imagery intents
    handleIncomingIntent(intent)
    
    setContent {
      MyApplicationTheme {
        FitCheckApp(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIncomingIntent(intent)
  }

  private fun handleIncomingIntent(intent: Intent?) {
    if (intent != null && intent.action == Intent.ACTION_SEND) {
      if (intent.type?.startsWith("image/") == true) {
        val streamUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
        if (streamUri != null) {
          viewModel.setIncomingSharedImage(streamUri.toString())
        }
      }
    }
  }
}

