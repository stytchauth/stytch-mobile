//
//  ContentView.swift
//  demo
//
//  Created by Jordan Haven on 1/7/26.
//
import Combine
import SwiftUI
import StytchConsumerSDK

struct ContentView: View {
    @State private var viewModel = ViewModel()

    var body: some View {
        VStack {
            switch onEnum(of: viewModel.state.authenticationState) {
            case .loading:
                Text("Loading...")
            case .unauthenticated:
                VStack {
                    Button("Google OAuth") {
                        viewModel.googleOauth()
                    }
                    Button("Apple OAuth") {
                        viewModel.appleOauth()
                    }
                    Button("Auth Biometrics") {
                        viewModel.authBiometrics()
                    }
                    Button("Auth Passkey") {
                        viewModel.authPasskey()
                    }
                    Button("Delete biometrics") {
                        viewModel.deleteBiometrics()
                    }
                }
            case .authenticated:
                Text("Authenticated!")
                Button("Logout") {
                    Task {
                        await viewModel.logout()
                    }
                }
                Button("Register Biometrics") {
                    viewModel.registerBiometrics()
                }
                Button("Register Passkey") {
                    viewModel.registerPasskey()
                }
            }
        }
        .frame(maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct UnauthenticatedStateView: View {
    var step: Step
    @State private var input: String = ""
    @State private var inputLabel: String = "Phone Number"
    var sendSms: (String) async -> Void
    var authSms: (String) async -> Void

    private func submit() {
        Task {
            switch step {
            case .phoneNumber:
                await sendSms(input)
            case .token:
                await authSms(input)
            }
        }
    }
    var body: some View {
        VStack {
            Text("Testing SMS OTP...")
            HStack {
                TextField(inputLabel, text: $input)
                    .padding()
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                Button("Submit", systemImage: "paperplane") {
                    submit()
                }
                .labelStyle(.iconOnly)
            }.onChange(of: step) { oldValue, newValue in
                self.input = ""
                switch newValue {
                case .phoneNumber:
                    self.inputLabel = "Phone Number"
                case .token:
                    self.inputLabel = "Code"
                }
            }
        }
    }
}

extension ContentView {
    @Observable
    class ViewModel {
        private let config: StytchClientConfiguration = .init(publicToken: ProcessInfo.processInfo.environment["STYTCH_PUBLIC_TOKEN"] ?? "")
        private let consumerClient: StytchConsumer
        var state: ContentViewState = .init()

        init() {
            consumerClient = createStytchConsumer(configuration: config)
            Task {
                for await authenticationState in consumerClient.authenticationStateFlow {
                    state.authenticationState = authenticationState
                }
            }
        }
        
        func sendSms(phoneNumber: String) async {
            do {
                let request = OTPsSMSLoginOrCreateParameters.init(phoneNumber: phoneNumber)
                let response = try await consumerClient.otp.sms.loginOrCreate(request: request)
                state.methodId = response.methodId
                state.step = .token
            } catch(let error)  {
                state.methodId = nil
                state.step = .phoneNumber
                state.error = error as? StytchError
            }
        }

        func authSms(token: String) async {
            do {
                guard let methodId = state.methodId else {
                    return
                }
                let request: OTPsAuthenticateParameters = .init(token: token, methodId: methodId, sessionDurationMinutes: 5)
                let response = try await consumerClient.otp.authenticate(request: request)
                state.methodId = nil
                state.step = .phoneNumber
            } catch (let error)  {
                state.methodId = nil
                state.step = .token
                state.error = error as? StytchError
            }
        }

        func logout() async {
            do {
                let response = try await consumerClient.session.revoke()
            } catch (let error) {
                state.error = error.asStytchError
            }
        }
        
        func googleOauth() {
            Task {
                do {
                    let params: OAuthStartParameters = .init(loginRedirectUrl: "login", signupRedirectUrl: "signup")
                    let response = try await consumerClient.oauth.google.start(startParameters: params)
                    print(response)
                } catch (let error) {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
        
        func appleOauth() {
            Task {
                do {
                    let params: OAuthStartParameters = .init(loginRedirectUrl: "login", signupRedirectUrl: "signup")
                    let response = try await consumerClient.oauth.apple.start(startParameters: params)
                    print(response)
                } catch {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
        
        func registerBiometrics() {
            Task {
                do {
                    let response = try await consumerClient.biometrics.register(parameters: .init(sessionDurationMinutes: 5, promptData: .init(reason: "Test", fallbackTitle: "Cancel", cancelTitle: "Cancel")))
                    print(response)
                } catch (let error) {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
        func authBiometrics() {
            Task {
                do {
                    let response = try await consumerClient.biometrics.authenticate(parameters: .init(sessionDurationMinutes: 5, promptData: .init(reason: "Test", fallbackTitle: "Cancel", cancelTitle: "Cancel")))
                    print(response)
                } catch (let error) {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
        func registerPasskey() {
            Task {
                do {
                    let response = try await consumerClient.passkeys.register(parameters: .init(domain: "stytch.com"))
                    print(response)
                } catch (let error) {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
        func authPasskey() {
            Task {
                do {
                    let response = try await consumerClient.passkeys.authenticate(parameters: .init(domain: "stytch.com"))
                    print(response)
                } catch (let error) {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
        func deleteBiometrics() {
            Task {
                do {
                    let response = try await consumerClient.biometrics.removeRegistration()
                    print(response)
                } catch (let error) {
                    print(error.asStytchError?.cause ?? "UNKNOWN")
                }
            }
        }
    }
}


struct ContentViewState {
    var authenticationState: ConsumerAuthenticationState = .Loading()
    var methodId: String? = nil
    var step: Step = .phoneNumber
    var error: StytchError? = nil
}

enum Step {
    case phoneNumber
    case token
}
