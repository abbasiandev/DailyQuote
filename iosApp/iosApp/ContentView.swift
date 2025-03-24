//
//  ContentView.swift
//  DailyQuote
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel: QuoteViewModelWrapper
    
    init() {
        let helper = QuoteHelper()
                let viewModelWrapper = QuoteViewModelWrapper(viewModel: helper.getViewModel())
                _viewModel = StateObject(wrappedValue: viewModelWrapper)
    }
    
    var body: some View {
        TabView {
            QuoteView(
                quote: viewModel.currentQuote,
                isLoading: viewModel.isLoading,
                isRefreshing: viewModel.isRefreshing,
                onToggleFavorite: { viewModel.toggleFavorite() },
                onRequestQuote: { viewModel.loadNextDailyQuote() },
                canRequestNewQuote: viewModel.canRequestNewQuote,
                timeRemaining: viewModel.timeRemaining,
                progressPercentage: viewModel.progressPercentage,
                onRefresh: { viewModel.refreshQuotes() }
            )
            .tabItem {
                Label("Daily Quote", systemImage: "quote.bubble")
            }
            
            FavoritesView(
                favorites: viewModel.favorites,
                onToggleFavorite: { id in viewModel.toggleFavorite(quoteId: id) }
            )
            .tabItem {
                Label("Favorites", systemImage: "heart.fill")
            }
        }
        .alert(item: Binding<ErrorMessage?>(
            get: { self.errorMessage },
            set: { if $0 == nil { self.viewModel.dismissError() } }
        )) { error in
            Alert(
                title: Text("Error"),
                message: Text(error.message),
                dismissButton: .default(Text("OK"))
            )
        }
        .preferredColorScheme(.dark)
    }
    
    private var errorMessage: ErrorMessage? {
        viewModel.error.map { ErrorMessage(message: $0) }
    }
    
    struct ErrorMessage: Identifiable {
        let message: String
        var id: String { message }
    }
}
