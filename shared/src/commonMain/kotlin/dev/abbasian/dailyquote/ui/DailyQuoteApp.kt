package dev.abbasian.dailyquote.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import dev.abbasian.dailyquote.data.model.Quote
import dev.abbasian.dailyquote.data.service.CommonTimeRemainingService
import dev.abbasian.dailyquote.data.service.TimeRemainingService
import dev.abbasian.dailyquote.presentation.QuoteViewModel
import dev.abbasian.dailyquote.util.getAsyncImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DailyQuoteApp(viewModel: QuoteViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val coroutineScope = rememberCoroutineScope()

    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context)
    }

    val timeRemainingService = uiState.timeRemainingService ?: remember {
        CommonTimeRemainingService(
            viewModel._quoteTimePreferences,
            coroutineScope
        )
    }

    LaunchedEffect(timeRemainingService) {
        if (uiState.timeRemainingService == null) {
            timeRemainingService.startCountdown()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Daily Quote") },
                    label = { Text("Daily Quote") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
            }
        }
    ) { padding ->
        CompositionLocalProvider(LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Box(modifier = Modifier.padding(padding)) {
                when (selectedTab) {
                    0 -> QuoteScreen(
                        quote = uiState.currentQuote,
                        isLoading = uiState.isLoading,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onRequestQuote = { viewModel.loadNextDailyQuote() },
                        canRequestNewQuote = uiState.canRequestNewQuote,
                        timeRemainingService = timeRemainingService,
                        onRefresh = { viewModel.refreshQuotes() },
                        isRefreshing = uiState.isRefreshing
                    )
                    1 -> FavoritesScreen(
                        favorites = uiState.favorites,
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
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
    onRequestQuote: () -> Unit,
    canRequestNewQuote: Boolean,
    timeRemainingService: TimeRemainingService,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onRefresh,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Quotes",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        when {
            isLoading -> CircularProgressIndicator()
            quote != null -> DynamicQuoteContent(
                quote = quote,
                onToggleFavorite = onToggleFavorite,
                onRequestQuote = onRequestQuote,
                canRequestNewQuote = canRequestNewQuote,
                timeRemainingService = timeRemainingService
            )
            else -> Text("No quote available. Try refreshing.")
        }
    }
}

@Composable
private fun DynamicQuoteContent(
    quote: Quote,
    onToggleFavorite: () -> Unit,
    onRequestQuote: () -> Unit,
    canRequestNewQuote: Boolean,
    timeRemainingService: TimeRemainingService
) {
    val imageScale = remember { Animatable(1f) }
    val panOffsetX = remember { Animatable(0f) }
    val panOffsetY = remember { Animatable(0f) }
    var isImageLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }

    LaunchedEffect(quote) {
        imageScale.snapTo(1f)
        panOffsetX.snapTo(0f)
        panOffsetY.snapTo(0f)

        launch { animateZoom(imageScale) }
        launch { animatePanX(panOffsetX) }
        launch { animatePanY(panOffsetY) }
    }

    Box(Modifier.fillMaxSize()) {
        BackgroundImage(
            url = quote.authorImageUrl ?: "",
            scale = imageScale.value,
            panX = panOffsetX.value,
            panY = panOffsetY.value,
            isLoading = isImageLoading,
            error = loadError,
            onLoadStateChange = { loading, error ->
                isImageLoading = loading
                loadError = error
            }
        )

        GradientOverlay()

        QuoteTextContent(
            quote = quote,
            onToggleFavorite = onToggleFavorite,
            onRequestQuote = onRequestQuote,
            canRequestNewQuote = canRequestNewQuote,
            timeRemainingService = timeRemainingService
        )
    }
}

@Composable
private fun BackgroundImage(
    url: String,
    scale: Float,
    panX: Float,
    panY: Float,
    isLoading: Boolean,
    error: Boolean,
    onLoadStateChange: (Boolean, Boolean) -> Unit
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = panX * (2 - scale)
                translationY = panY * (2 - scale)
                alpha = when {
                    isLoading || error -> 0f
                    else -> 0.7f.coerceAtMost(scale - 0.3f)
                }
            },
        onLoading = { onLoadStateChange(true, false) },
        onSuccess = { onLoadStateChange(false, false) },
        onError = { onLoadStateChange(false, true) }
    )
}

@Composable
private fun GradientOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.8f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
private fun QuoteTextContent(
    quote: Quote,
    onToggleFavorite: () -> Unit,
    onRequestQuote: () -> Unit,
    canRequestNewQuote: Boolean,
    timeRemainingService: TimeRemainingService
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.weight(1f))

        QuoteText(quote.text)
        AuthorText(quote.author)

        ControlButtons(
            isFavorite = quote.isFavorite,
            onToggleFavorite = onToggleFavorite,
            onRequestQuote = onRequestQuote,
            canRequestNewQuote = canRequestNewQuote,
            timeRemainingService = timeRemainingService
        )
    }
}

@Composable
private fun QuoteText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(
            fontStyle = FontStyle.Italic
        ),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        lineHeight = 28.sp
    )
}

@Composable
private fun AuthorText(author: String) {
    Text(
        text = "— $author",
        style = MaterialTheme.typography.titleMedium.copy(
            fontStyle = FontStyle.Italic
        ),
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier.padding(bottom = 32.dp)
    )
}

@Composable
fun ControlButtons(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRequestQuote: () -> Unit,
    canRequestNewQuote: Boolean,
    timeRemainingService: TimeRemainingService
) {
    var remainingTimeText by remember { mutableStateOf("") }
    var progressPercentage by remember { mutableStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(timeRemainingService) {
        val initialState = timeRemainingService.getCurrentTimeState()
        remainingTimeText = initialState.first
        progressPercentage = initialState.second
    }

    val timeRemainingListener = remember {
        object : TimeRemainingService.TimeRemainingListener {
            override fun onTimeUpdated(remainingTime: String, progress: Float) {
                coroutineScope.launch(Dispatchers.Main) {
                    remainingTimeText = remainingTime
                    progressPercentage = progress
                }
            }

            override fun onCountdownFinished() {
                coroutineScope.launch(Dispatchers.Main) {
                    remainingTimeText = ""
                    progressPercentage = 1f
                }
            }
        }
    }

    DisposableEffect(timeRemainingService) {
        timeRemainingService.addListener(timeRemainingListener)

        onDispose {
            timeRemainingService.removeListener(timeRemainingListener)
        }
    }

    val currentTimeText by rememberUpdatedState(remainingTimeText)
    val currentProgress by rememberUpdatedState(progressPercentage)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FavoriteButton(isFavorite, onToggleFavorite)

        CircularProgressIndicator(
            progress = progressPercentage,
            modifier = Modifier.size(64.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Button(
            onClick = onRequestQuote,
            enabled = canRequestNewQuote,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .alpha(if (canRequestNewQuote) 1f else 0.6f)
        ) {
            Text(
                text = if (canRequestNewQuote) {
                    "Get New Quote"
                } else {
                    if (currentTimeText.isEmpty()) "Loading..." else "Next in $currentTimeText"
                }
            )
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.size(32.dp)
        )
    }
}

private suspend fun animateZoom(animatable: Animatable<Float, AnimationVector1D>) {
    animatable.animateTo(
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
}

private suspend fun animatePanX(animatable: Animatable<Float, AnimationVector1D>) {
    animatable.animateTo(
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 26000
                0f at 0 with LinearEasing
                60f at 13000 with FastOutSlowInEasing
                0f at 26000
            },
            repeatMode = RepeatMode.Restart
        )
    )
}

private suspend fun animatePanY(animatable: Animatable<Float, AnimationVector1D>) {
    animatable.animateTo(
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
}

@Composable
fun FavoritesScreen(
    favorites: List<Quote>,
    onToggleFavorite: (String) -> Unit
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
                FavoriteQuoteCard(quote = quote, onToggleFavorite = { onToggleFavorite(quote.id) })
            }
        }
    }
}

@Composable
fun FavoriteQuoteCard(quote: Quote, onToggleFavorite: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            var isImageLoading by remember { mutableStateOf(true) }
            var imageLoadError by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surface)
            )

            AsyncImage(
                model = quote.authorImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .graphicsLayer {
                        alpha = if (isImageLoading || imageLoadError) 0f else 0.3f
                    },
                onLoading = { isImageLoading = true },
                onSuccess = { isImageLoading = false },
                onError = {
                    isImageLoading = false
                    imageLoadError = true
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = quote.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "— ${quote.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
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