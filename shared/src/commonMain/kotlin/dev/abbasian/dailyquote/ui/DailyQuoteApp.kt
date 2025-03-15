package dev.abbasian.dailyquote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.presentation.QuoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuoteApp(viewModel: QuoteViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var currentTab by remember { mutableStateOf(0) }
    val tabs = listOf("Daily Quote", "Favorites")

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Daily Quote") },
                    actions = {
                        IconButton(onClick = { viewModel.refreshQuotes() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = currentTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (currentTab) {
                    0 -> QuoteScreen(
                        quote = uiState.currentQuote,
                        isLoading = uiState.isLoading,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onRandomQuote = { viewModel.loadRandomQuote() }
                    )
                    1 -> FavoritesScreen(
                        favorites = uiState.favorites,
                        onToggleFavorite = { viewModel.toggleFavorite() }
                    )
                }

                if (uiState.error != null) {
                    ErrorSnackbar(
                        message = uiState.error,
                        onDismiss = { viewModel.dismissError() }
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteScreen(
    quote: Quote?,
    isLoading: Boolean,
    onToggleFavorite: () -> Unit,
    onRandomQuote: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (quote != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "$ {quote.text}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            if (quote.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (quote.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    }

                    Button(onClick = onRandomQuote) {
                        Text("Another Quote")
                    }
                }
            }
        } else {
            Text("No quote available. Try refreshing.")
        }
    }
}

@Composable
fun FavoritesScreen(
    favorites: List<Quote>,
    onToggleFavorite: () -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorites yet. Add some quotes!")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favorites) { quote ->
                QuoteCard(quote = quote, onToggleFavorite = onToggleFavorite)
            }
        }
    }
}

@Composable
fun QuoteCard(quote: Quote, onToggleFavorite: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = quote.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Remove from Favorites",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorSnackbar(message: String?, onDismiss: () -> Unit) {
    if (message != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(message)
        }
    }
}