//
//  QuoteViewModelWrapper.swift
//  DailyQuote
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared
import Combine
import Foundation

class QuoteViewModelWrapper: ObservableObject {
    private let viewModel: QuoteViewModel
    
    @Published var currentQuote: Quote? = nil
    @Published var favorites: [Quote] = []
    @Published var isLoading: Bool = false
    @Published var isRefreshing: Bool = false
    @Published var error: String? = nil
    @Published var canRequestNewQuote: Bool = true
    @Published var nextQuoteAvailableAt: Int64? = nil
    @Published var timeRemaining: String = ""
    @Published var progressPercentage: Float = 0.0
    
    private var cancellables = Set<AnyCancellable>()
    
    init(viewModel: QuoteViewModel) {
        self.viewModel = viewModel
        
        observeStateFlow()
        
        startTimeObserver()
    }
    
    deinit {
        cancellables.forEach { $0.cancel() }
    }
    
    private func observeStateFlow() {
        Timer.publish(every: 0.5, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self,
                      let state = self.viewModel.uiState.value as? QuoteUiState else { return }
                
                self.currentQuote = state.currentQuote
                self.favorites = state.favorites
                self.isLoading = state.isLoading
                self.isRefreshing = state.isRefreshing
                self.error = state.error
                self.canRequestNewQuote = state.canRequestNewQuote
                self.nextQuoteAvailableAt = state.nextQuoteAvailableAt?.int64Value
                
                if let timeService = state.timeRemainingService {
                    let initialState = timeService.getCurrentTimeState()
                    self.timeRemaining = initialState.first as? String ?? ""
                    self.progressPercentage = initialState.second as? Float ?? 0.0
                }
            }
            .store(in: &cancellables)
    }
    
    private func startTimeObserver() {
        Timer.publish(every: 1.0, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self,
                      let uiState = self.viewModel.uiState.value as? QuoteUiState,
                      let service = uiState.timeRemainingService else { return }
                
                let timeState = service.getCurrentTimeState()
                self.timeRemaining = timeState.first as? String ?? ""
                self.progressPercentage = timeState.second as? Float ?? 0.0
            }
            .store(in: &cancellables)
    }
    
    func loadNextDailyQuote() {
        viewModel.loadNextDailyQuote()
    }
    
    func loadRandomQuote() {
        viewModel.loadRandomQuote()
    }
    
    func toggleFavorite() {
        viewModel.toggleFavorite()
    }
    
    func toggleFavorite(quoteId: String) {
        viewModel.toggleFavorite(quoteId: quoteId)
    }
    
    func refreshQuotes() {
        viewModel.refreshQuotes()
    }
    
    func dismissError() {
        viewModel.dismissError()
    }
}
