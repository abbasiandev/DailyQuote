package dev.abbasian.dailyquote.di

import dev.abbasian.dailyquote.data.repository.QuoteRepository
import dev.abbasian.dailyquote.data.repository.QuoteRepositoryImpl
import dev.abbasian.dailyquote.data.source.AuthorImageService
import dev.abbasian.dailyquote.data.source.DefaultAuthorImageService
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
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(platformModule: Module) {
    startKoin {
        modules(
            commonModule(),
            platformModule
        )
    }
}

fun commonModule() = module {
    factory { GetDailyQuoteUseCase(get()) }
    factory { GetFavoritesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { GetRandomQuoteUseCase(get()) }
    factory { RefreshQuotesUseCase(get()) }
    factory<AuthorImageService> { DefaultAuthorImageService() }
    factory { CheckQuoteAvailabilityUseCase(get()) }
    factory { GetNextQuoteTimeUseCase(get()) }
    factory { GetSavedQuoteUseCase(get()) }
    factory { SaveQuoteUseCase(get()) }

    single<QuoteRepository> { QuoteRepositoryImpl(get(), get(), get(), get(), get(), get()) }

    factory { QuoteViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}