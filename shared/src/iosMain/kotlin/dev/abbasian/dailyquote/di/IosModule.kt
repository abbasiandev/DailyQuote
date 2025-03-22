package dev.abbasian.dailyquote.di

import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.repository.QuoteRepositoryImpl
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.local.IOSQuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.remote.IOSQuoteRemoteDataSource
import dev.abbasian.dailyquote.domain.GetDailyQuoteUseCase
import dev.abbasian.dailyquote.domain.GetFavoritesUseCase
import dev.abbasian.dailyquote.domain.GetRandomQuoteUseCase
import dev.abbasian.dailyquote.domain.RefreshQuotesUseCase
import dev.abbasian.dailyquote.domain.ToggleFavoriteUseCase
import dev.abbasian.dailyquote.presentation.QuoteViewModel
import dev.abbasian.dailyquote.util.IOSScreenHeightProvider
import dev.abbasian.dailyquote.util.ScreenHeightProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@kotlin.native.ObjCName(name = "doInitKoinIOS")
fun initKoinIOS() = initKoin(
    module {
        single<QuoteLocalDataSource> { IOSQuoteLocalDataSource() }
        single<QuoteRemoteDataSource> { IOSQuoteRemoteDataSource() }
        single<ScreenHeightProvider> { IOSScreenHeightProvider() }

        single { GetDailyQuoteUseCase(get()) }
        single { GetFavoritesUseCase(get()) }
        single { ToggleFavoriteUseCase(get()) }
        single { GetRandomQuoteUseCase(get()) }
        single { RefreshQuotesUseCase(get()) }

        single {
            QuoteViewModel(
                getDailyQuoteUseCase = get(),
                getFavoritesUseCase = get(),
                toggleFavoriteUseCase = get(),
                getRandomQuoteUseCase = get(),
                refreshQuotesUseCase = get(),
                screenHeightProvider = get(),
                coroutineScope = CoroutineScope(Dispatchers.Main)
            )
        }
    }
)

object KoinHelper {
    @OptIn(ExperimentalObjCName::class)
    @kotlin.native.ObjCName(name = "setup")
    fun setup() = initKoinIOS()
}

@OptIn(ExperimentalObjCName::class)
class QuoteHelper {
    private val localDataSource = IOSQuoteLocalDataSource()
    private val remoteDataSource = IOSQuoteRemoteDataSource()

    private val quoteRepository by lazy {
        QuoteRepositoryImpl(localDataSource, remoteDataSource)
    }

    private val getDailyQuoteUseCase by lazy { GetDailyQuoteUseCase(quoteRepository) }
    private val getFavoritesUseCase by lazy { GetFavoritesUseCase(quoteRepository) }
    private val toggleFavoriteUseCase by lazy { ToggleFavoriteUseCase(quoteRepository) }
    private val getRandomQuoteUseCase by lazy { GetRandomQuoteUseCase(quoteRepository) }
    private val refreshQuotesUseCase by lazy { RefreshQuotesUseCase(quoteRepository) }
    private val screenHeightProvider by lazy { IOSScreenHeightProvider() }

    private val viewModel by lazy {
        QuoteViewModel(
            getDailyQuoteUseCase,
            getFavoritesUseCase,
            toggleFavoriteUseCase,
            getRandomQuoteUseCase,
            refreshQuotesUseCase,
            screenHeightProvider,
            CoroutineScope(Dispatchers.Main)
        )
    }

    @kotlin.native.ObjCName(name = "getViewModel")
    fun getViewModel(): QuoteViewModel = viewModel
}