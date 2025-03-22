package dev.abbasian.dailyquote.di

import dev.abbasian.dailyquote.data.repository.QuoteRepository
import dev.abbasian.dailyquote.data.repository.QuoteRepositoryImpl
import dev.abbasian.dailyquote.domain.GetDailyQuoteUseCase
import dev.abbasian.dailyquote.domain.GetFavoritesUseCase
import dev.abbasian.dailyquote.domain.GetRandomQuoteUseCase
import dev.abbasian.dailyquote.domain.RefreshQuotesUseCase
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

    single<QuoteRepository> { QuoteRepositoryImpl(get(), get()) }

    factory { QuoteViewModel(get(), get(), get(), get(), get(), get()) }
}