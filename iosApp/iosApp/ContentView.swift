//
//  ContentView.swift
//  iosApp
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ContentView: View {
    private let helper = QuoteHelper()
    @ObservedObject private var viewModel: QuoteViewModelWrapper
    
    init() {
        viewModel = QuoteViewModelWrapper(viewModel: helper.getViewModel())
    }
    
    var body: some View {
        TabView {
            QuoteView(
                quote: viewModel.currentQuote,
                isLoading: viewModel.isLoading,
                onToggleFavorite: { viewModel.toggleFavorite() },
                onRandomQuote: { viewModel.loadRandomQuote() }, onRefresh: { viewModel.refreshQuotes() }
            )
            .tabItem {
                Label("Daily Quote", systemImage: "quote.bubble")
            }
            
            FavoritesView(
                favorites: viewModel.favorites,
                onToggleFavorite: { viewModel.toggleFavorite() }
            )
            .tabItem {
                Label("Favorites", systemImage: "heart.fill")
            }
        }
        .alert(isPresented: Binding<Bool>(
            get: { viewModel.error != nil },
            set: { if !$0 { viewModel.dismissError() } }
        )) {
            Alert(
                title: Text("Error"),
                message: Text(viewModel.error ?? ""),
                dismissButton: .default(Text("OK"))
            )
        }
    }
}
