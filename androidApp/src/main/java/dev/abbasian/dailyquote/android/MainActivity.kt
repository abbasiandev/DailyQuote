package dev.abbasian.dailyquote.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.abbasian.dailyquote.presentation.QuoteViewModel
import dev.abbasian.dailyquote.ui.DailyQuoteApp
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val viewModel: QuoteViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                DailyQuoteApp(viewModel = viewModel)
            }
        }
    }
}
