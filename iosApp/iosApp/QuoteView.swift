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
                if let url = URL(string: quote.authorImageUrl ?? "") {
                    KFImage(url)
                        .resizable()
                        .scaledToFill()
                        .opacity(imageOpacity)
                        .animation(.easeIn(duration: 1.0), value: imageOpacity)
                        .onAppear {
                            withAnimation {
                                imageOpacity = 0.5
                            }
                        }
                        .onChange(of: quote.id) { _ in
                            imageOpacity = 0
                            withAnimation {
                                imageOpacity = 0.5
                            }
                        }
                        .edgesIgnoringSafeArea(.all)
                }
                
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
                    
                    Spacer()
                    
                    VStack(spacing: 20) {
                        Button(action: onToggleFavorite) {
                            Image(systemName: quote.isFavorite ? "heart.fill" : "heart")
                                .font(.system(size: 24))
                                .foregroundColor(quote.isFavorite ? .red : .white)
                                .padding()
                                .background(Color.white.opacity(0.2))
                                .clipShape(Circle())
                        }
                        
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
                }
                .padding()
            } else {
                Text("No quote available. Try refreshing.")
                    .foregroundColor(.white)
            }
            
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
        .onAppear {
            UIApplication.shared.windows.forEach { window in
                window.overrideUserInterfaceStyle = .dark
            }
        }
    }
}
