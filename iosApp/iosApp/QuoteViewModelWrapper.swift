//
//  QuoteViewModelWrapper.swift
//  iosApp
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

class QuoteViewModelWrapper: ObservableObject {
    private let viewModel: QuoteViewModel
    
    @Published var currentQuote: Quote? = nil
    @Published var favorites: [Quote] = []
    @Published var isLoading: Bool = false
    @Published var isRefreshing: Bool = false
    @Published var error: String? = nil
    
    init(viewModel: QuoteViewModel) {
        self.viewModel = viewModel
        
        // Set up a timer to poll the state (as a fallback approach)
        startObservingState()
    }
    
    private func startObservingState() {
        // Initial state update
        updateStateFromViewModel()
        
        // Set up a timer to poll for state changes
        Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
            self?.updateStateFromViewModel()
        }
    }
    
    private func updateStateFromViewModel() {
        let state = viewModel.uiState.value as? QuoteViewModelWrapper
        if let state = state {
            DispatchQueue.main.async {
                self.currentQuote = state.currentQuote
                self.favorites = state.favorites
                self.isLoading = state.isLoading
                self.isRefreshing = state.isRefreshing
                self.error = state.error
            }
        }
    }
    
    func loadDailyQuote() {
        viewModel.loadDailyQuote()
    }
    
    func loadRandomQuote() {
        viewModel.loadRandomQuote()
    }
    
    func toggleFavorite() {
        viewModel.toggleFavorite()
    }
    
    func refreshQuotes() {
        viewModel.refreshQuotes()
    }
    
    func dismissError() {
        viewModel.dismissError()
    }
}
