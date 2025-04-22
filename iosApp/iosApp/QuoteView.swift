//
//  QuoteView.swift
//  DailyQuote
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared
import Kingfisher

struct QuoteView: View {
    let quote: Quote?
    let isLoading: Bool
    let isRefreshing: Bool
    let onToggleFavorite: () -> Void
    let onRequestQuote: () -> Void
    let canRequestNewQuote: Bool
    let timeRemaining: String
    let progressPercentage: Float
    let onRefresh: () -> Void
    
    @State private var imageOpacity: Double = 0
    
    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all)
            
            if isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .scaleEffect(1.5)
            } else if let quote = quote {
                // Background Image
                backgroundImage(for: quote)
                
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.black.opacity(0.8),
                        Color.black.opacity(0.3),
                        Color.black.opacity(0.8)
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                .edgesIgnoringSafeArea(.all)
                
                VStack(spacing: 24) {
                    Spacer()
                    
                    quoteTextSection(quote: quote)
                    
                    Spacer()
                    
                    actionButtonsSection(quote: quote)
                }
                .padding()
            } else {
                Text("No quote available. Try refreshing.")
                    .foregroundColor(.white)
            }
            
            refreshButton()
        }
        .onAppear {
            UIApplication.shared.windows.forEach { window in
                window.overrideUserInterfaceStyle = .dark
            }
        }
    }
    
    private func backgroundImage(for quote: Quote) -> some View {
        Group {
            if let urlString = quote.authorImageUrl,
               !urlString.isEmpty {
                let trimmedUrlString = urlString.trimmingCharacters(in: .whitespacesAndNewlines)
                    .addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
                
                if let url = URL(string: trimmedUrlString) {
                    KFImage(url)
                        .resizable()
                        .placeholder { Color.gray.opacity(0.3) }
                        .onFailure { error in
                            print("Image load error:", error)
                        }
                        .onSuccess { _ in
                            withAnimation {
                                imageOpacity = 1
                            }
                        }
                        .scaledToFill()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .opacity(imageOpacity)
                        .background(Color.black.opacity(0.3))
                        .edgesIgnoringSafeArea(.all)
                } else {
                    EmptyView()
                }
            } else {
                EmptyView()
            }
        }
    }
    
    private func quoteTextSection(quote: Quote) -> some View {
        VStack(spacing: 12) {
            Text(quote.text)
                .font(.system(size: 24, weight: .semibold, design: .serif))
                .italic()
                .multilineTextAlignment(.center)
                .foregroundColor(.white)
                .padding(.horizontal, 20)
                .shadow(color: .black, radius: 2, x: 1, y: 1)
            
            Text("― \(quote.author)")
                .font(.system(size: 18, weight: .medium, design: .serif))
                .italic()
                .foregroundColor(.white.opacity(0.9))
                .shadow(color: .black, radius: 1, x: 0.5, y: 0.5)
        }
    }
    
    private func actionButtonsSection(quote: Quote) -> some View {
        VStack(spacing: 20) {
            Button(action: onToggleFavorite) {
                Image(systemName: quote.isFavorite ? "heart.fill" : "heart")
                    .font(.system(size: 24))
                    .foregroundColor(quote.isFavorite ? .red : .white)
                    .padding()
                    .background(Color.white.opacity(0.2))
                    .clipShape(Circle())
            }
            
            progressCircle()
            
            requestQuoteButton()
        }
    }
    
    private func progressCircle() -> some View {
        ZStack {
            Circle()
                .stroke(lineWidth: 4)
                .opacity(0.3)
                .foregroundColor(.white)
            
            Circle()
                .trim(from: 0.0, to: CGFloat(progressPercentage))
                .stroke(style: StrokeStyle(lineWidth: 4, lineCap: .round, lineJoin: .round))
                .foregroundColor(.blue)
                .rotationEffect(Angle(degrees: 270.0))
                .animation(.linear, value: progressPercentage)
            
            if !timeRemaining.isEmpty && !canRequestNewQuote {
                Text(timeRemaining)
                    .font(.caption)
                    .foregroundColor(.white)
            }
        }
        .frame(width: 60, height: 60)
    }
    
    private func requestQuoteButton() -> some View {
        Button(action: onRequestQuote) {
            Text(canRequestNewQuote ? "Get New Quote" : "Next in \(timeRemaining)")
                .font(.headline)
                .foregroundColor(.white)
                .padding()
                .frame(width: 220)
                .background(
                    canRequestNewQuote ? Color.blue : Color.blue.opacity(0.5)
                )
                .cornerRadius(8)
        }
        .disabled(!canRequestNewQuote)
        .padding(.bottom, 30)
    }
    
    private func refreshButton() -> some View {
        VStack {
            HStack {
                Spacer()
                
                Button(action: onRefresh) {
                    ZStack {
                        Circle()
                            .fill(Color.black.opacity(0.7))
                            .frame(width: 48, height: 48)
                        
                        if isRefreshing {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Image(systemName: "arrow.clockwise")
                                .font(.system(size: 20))
                                .foregroundColor(.white)
                        }
                    }
                }
                .padding(16)
            }
            
            Spacer()
        }
    }
}
