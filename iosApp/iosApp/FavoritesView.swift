//
//  FavoritesView.swift
//  iosApp
//
//  Created by Mahdi Abbasian on 3/21/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct FavoritesView: View {
    let favorites: [Quote]
    let onToggleFavorite: () -> Void
    
    var body: some View {
        NavigationView {
            if favorites.isEmpty {
                Text("No favorites yet. Add some quotes!")
                    .navigationTitle("Favorites")
            } else {
                List {
                    ForEach(favorites, id: \.id) { quote in
                        VStack(alignment: .leading, spacing: 8) {
                            Text("\(quote.text)")
                                .font(.body)
                            
                            HStack {
                                Text("— \(quote.author)")
                                    .font(.caption)
                                    .italic()
                                
                                Spacer()
                                
                                Button(action: onToggleFavorite) {
                                    Image(systemName: "heart.fill")
                                        .foregroundColor(.red)
                                }
                            }
                        }
                        .padding(.vertical, 8)
                    }
                }
                .navigationTitle("Favorites")
            }
        }
    }
}
