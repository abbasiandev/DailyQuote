package dev.abbasian.dailyquote.di

import dev.abbasian.dailyquote.data.preferences.IOSQuoteDataStore
import dev.abbasian.dailyquote.data.preferences.QuoteDataStore
import dev.abbasian.dailyquote.data.preferences.QuoteTimePreferences
import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.repository.QuoteRepositoryImpl
import dev.abbasian.dailyquote.data.source.DefaultAuthorImageService
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.local.IOSQuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.remote.IOSQuoteRemoteDataSource
import dev.abbasian.dailyquote.domain.CheckQuoteAvailabilityUseCase
import dev.abbasian.dailyquote.domain.GetDailyQuoteUseCase
import dev.abbasian.dailyquote.domain.GetFavoritesUseCase
import dev.abbasian.dailyquote.domain.GetNextQuoteTimeUseCase
import dev.abbasian.dailyquote.domain.GetRandomQuoteUseCase
import dev.abbasian.dailyquote.domain.GetSavedQuoteUseCase
import dev.abbasian.dailyquote.domain.RefreshQuotesUseCase
import dev.abbasian.dailyquote.domain.SaveQuoteUseCase
import dev.abbasian.dailyquote.domain.ToggleFavoriteUseCase
import dev.abbasian.dailyquote.presentation.QuoteViewModel
import dev.abbasian.dailyquote.util.IOSQuoteTimePreferences
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
        single<QuoteTimePreferences> { IOSQuoteTimePreferences() }
        single<QuoteDataStore> { IOSQuoteDataStore() }

        single { GetDailyQuoteUseCase(get()) }
        single { GetFavoritesUseCase(get()) }
        single { ToggleFavoriteUseCase(get()) }
        single { GetRandomQuoteUseCase(get()) }
        single { RefreshQuotesUseCase(get()) }
        single { DefaultAuthorImageService() }
        single { CheckQuoteAvailabilityUseCase(get()) }
        single { GetNextQuoteTimeUseCase(get()) }
        single { GetSavedQuoteUseCase(get()) }
        single { SaveQuoteUseCase(get()) }

        single {
            QuoteViewModel(
                getDailyQuoteUseCase = get(),
                getFavoritesUseCase = get(),
                toggleFavoriteUseCase = get(),
                getRandomQuoteUseCase = get(),
                refreshQuotesUseCase = get(),
                checkQuoteAvailabilityUseCase = get(),
                getNextQuoteTimeUseCase = get(),
                getSavedQuoteUseCase = get(),
                saveQuoteUseCase = get(),
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
    private val authorImageService = DefaultAuthorImageService()
    private val screenHeightProvider = IOSScreenHeightProvider()
    private val quoteTimePreferences = IOSQuoteTimePreferences()
    private val quoteDataStore = IOSQuoteDataStore()

    private val quoteRepository by lazy {
        QuoteRepositoryImpl(
            localDataSource,
            remoteDataSource,
            authorImageService,
            screenHeightProvider,
            quoteTimePreferences,
            quoteDataStore
        )
    }

    private val getDailyQuoteUseCase by lazy { GetDailyQuoteUseCase(quoteRepository) }
    private val getFavoritesUseCase by lazy { GetFavoritesUseCase(quoteRepository) }
    private val toggleFavoriteUseCase by lazy { ToggleFavoriteUseCase(quoteRepository) }
    private val getRandomQuoteUseCase by lazy { GetRandomQuoteUseCase(quoteRepository) }
    private val refreshQuotesUseCase by lazy { RefreshQuotesUseCase(quoteRepository) }
    private val checkQuoteAvailabilityUseCase by lazy { CheckQuoteAvailabilityUseCase(quoteRepository) }
    private val getNextQuoteTimeUseCase by lazy { GetNextQuoteTimeUseCase(quoteRepository) }
    private val getSavedQuoteUseCase by lazy { GetSavedQuoteUseCase(quoteRepository) }
    private val saveQuoteUseCase by lazy { SaveQuoteUseCase(quoteRepository) }

    private val viewModel by lazy {
        QuoteViewModel(
            getDailyQuoteUseCase,
            getFavoritesUseCase,
            toggleFavoriteUseCase,
            getRandomQuoteUseCase,
            refreshQuotesUseCase,
            checkQuoteAvailabilityUseCase,
            getNextQuoteTimeUseCase,
            getSavedQuoteUseCase,
            saveQuoteUseCase,
            CoroutineScope(Dispatchers.Main)
        )
    }

    @kotlin.native.ObjCName(name = "getViewModel")
    fun getViewModel(): QuoteViewModel = viewModel
}