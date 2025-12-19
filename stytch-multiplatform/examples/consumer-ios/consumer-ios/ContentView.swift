//
//  ContentView.swift
//  consumer-ios
//
//  Created by Jordan Haven on 12/19/25.
//

import SwiftUI
import StytchConsumer
struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text(
                StytchClient.shared.configure(options: .init(name: "My Name"))
            )
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
