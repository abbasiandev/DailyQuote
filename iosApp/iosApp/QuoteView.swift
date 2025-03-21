//
//  QuoteView.swift
//  iosApp
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct QuoteView: View {
    let quote: Quote?
    let isLoading: Bool
    let onToggleFavorite: () -> Void
    let onRandomQuote: () -> Void
    let onRefresh: () -> Void
    
    var body: some View {
        ZStack {
            if isLoading {
                ProgressView()
            } else if let quote = quote {
                VStack(spacing: 20) {
                    Text("\(quote.text)")
                        .font(.title)
                        .multilineTextAlignment(.center)
                        .padding()
                    
                    Text("— \(quote.author)")
                        .font(.headline)
                        .italic()
                    
                    HStack(spacing: 20) {
                        Button(action: onToggleFavorite) {
                            Image(systemName: quote.isFavorite ? "heart.fill" : "heart")
                                .font(.title)
                                .foregroundColor(quote.isFavorite ? .red : .gray)
                        }
                        
                        Button("Another Quote") {
                            onRandomQuote()
                        }
                        .buttonStyle(.bordered)
                    }
                    .padding(.top, 20)
                }
                .padding()
            } else {
                Text("No quote available. Try refreshing.")
            }
        }
        .navigationTitle("Daily Quote")
        .toolbar {
            Button(action: onRefresh) {
                Image(systemName: "arrow.clockwise")
            }
        }
    }
}
