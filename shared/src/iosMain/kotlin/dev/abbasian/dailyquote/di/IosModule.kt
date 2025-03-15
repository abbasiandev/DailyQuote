package dev.abbasian.dailyquote.di

import dev.abbasian.dailyquote.data.remote.QuoteRemoteDataSource
import dev.abbasian.dailyquote.data.source.QuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.local.IOSQuoteLocalDataSource
import dev.abbasian.dailyquote.data.source.remote.IOSQuoteRemoteDataSource
import dev.abbasian.dailyquote.presentation.QuoteViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

fun initKoinIOS() = initKoin(
    module {
        single<QuoteLocalDataSource> { IOSQuoteLocalDataSource() }
        single<QuoteRemoteDataSource> { IOSQuoteRemoteDataSource() }
    }
)

class QuoteHelper : KoinComponent {
    private val viewModel: QuoteViewModel by inject()

    fun getViewModel(): QuoteViewModel = viewModel
}