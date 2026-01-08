//
//  ContentView.swift
//  demo
//
//  Created by Jordan Haven on 1/7/26.
//

import SwiftUI
import StytchConsumerSDK

struct ContentView: View {
    @State private var viewModel = ViewModel()

    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
        }
        .padding()
        .task {
            await viewModel.sendSms(phoneNumber: "+14434189653")
        }
    }
}

extension ContentView {
    @Observable
    class ViewModel {
        private let config: SharedStytchClientConfiguration = .init(publicToken: "public-token-test-13197340-f43a-409b-b9dd-e4a10307913a")
        private let consumerClient: StytchConsumer

        init() {
            consumerClient = createStytchConsumer(configuration: config)
        }
        
        func sendSms(phoneNumber: String) async {
            do {
                let response = try await consumerClient.otp.sms.loginOrCreate(request: .init(phoneNumber: phoneNumber, expirationMinutes: 5, enableAutofill: false))
                print(response)
            } catch (let error) {
                print(error)
            }
        }
    }
}


#Preview {
    ContentView()
}
