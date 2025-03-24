//
//  FavoritesView.swift
//  DailyQuote
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared
import Kingfisher

struct FavoritesView: View {
    let favorites: [Quote]
    let onToggleFavorite: (String) -> Void
    
    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all)
            
            if favorites.isEmpty {
                VStack {
                    Image(systemName: "heart.slash")
                        .font(.system(size: 48))
                        .foregroundColor(.gray.opacity(0.7))
                        .padding()
                    
                    Text("No favorites yet")
                        .font(.title2)
                        .foregroundColor(.white)
                    
                    Text("Add some quotes to your favorites!")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .padding(.top, 4)
                }
            } else {
                ScrollView {
                    LazyVStack(spacing: 16) {
                        ForEach(favorites, id: \.id) { quote in
                            QuoteCard(quote: quote) {
                                onToggleFavorite(quote.id)
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 16)
                    .padding(.bottom, 32)
                }
            }
        }
        .navigationTitle("Favorites")
        .onAppear {
            UIApplication.shared.windows.forEach { window in
                window.overrideUserInterfaceStyle = .dark
            }
        }
    }
}

struct QuoteCard: View {
    let quote: Quote
    let onToggleFavorite: () -> Void
    
    @State private var imageOpacity: Double = 0
    
    var body: some View {
        ZStack(alignment: .bottomLeading) {
            if let url = URL(string: quote.authorImageUrl ?? "") {
                KFImage(url)
                    .resizable()
                    .scaledToFill()
                    .frame(height: 180)
                    .opacity(0.3)
                    .clipped()
            }
            
            LinearGradient(
                gradient: Gradient(colors: [
                    Color.black.opacity(0.6),
                    Color.black.opacity(0.4),
                    Color.black.opacity(0.6)
                ]),
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 180)
            
            VStack(alignment: .leading, spacing: 8) {
                Text(quote.text)
                    .font(.body)
                    .foregroundColor(.white)
                    .multilineTextAlignment(.leading)
                    .padding(.horizontal, 16)
                    .padding(.top, 16)
                
                Spacer()
                
                HStack {
                    Text("― \(quote.author)")
                        .font(.caption)
                        .italic()
                        .foregroundColor(.white.opacity(0.9))
                        .padding(.horizontal, 16)
                        .padding(.bottom, 16)
                    
                    Spacer()
                    
                    Button(action: onToggleFavorite) {
                        Image(systemName: "heart.fill")
                            .foregroundColor(.red)
                            .padding(.trailing, 16)
                            .padding(.bottom, 16)
                    }
                }
            }
        }
        .frame(height: 180)
        .background(Color.gray.opacity(0.2))
        .cornerRadius(12)
        .shadow(radius: 4)
    }
}
