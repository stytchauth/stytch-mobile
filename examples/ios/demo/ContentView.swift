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
                UnauthenticatedStateView(
                    step: viewModel.state.step,
                    sendSms: viewModel.sendSms(phoneNumber:),
                    authSms: viewModel.authSms(token:)
                )
            case .authenticated:
                Text("Authenticated!")
                Button("Logout") {
                    Task {
                        await viewModel.logout()
                    }
                }
            }
            if let response = viewModel.state.rawResponse {
                Spacer()
                Text(response.toFriendlyDisplay())
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
        private let config: SharedStytchClientConfiguration = .init(publicToken: ProcessInfo.processInfo.environment["STYTCH_PUBLIC_TOKEN"] ?? "")
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
                let request = OtpSmsLoginOrCreateRequest(phoneNumber: phoneNumber, expirationMinutes: 5, enableAutofill: false)
                let response = try await consumerClient.otp.sms.loginOrCreate(request: request)
                state.methodId = response.methodId
                state.step = .token
                state.rawResponse = response
            } catch(let error)  {
                state.methodId = nil
                state.step = .phoneNumber
                print(error)
            }
        }

        func authSms(token: String) async {
            do {
                guard let methodId = state.methodId else {
                    return
                }
                let request: OtpAuthenticateRequest = .init(token: token, methodId: methodId, sessionDurationMinutes: 5)
                let response = try await consumerClient.otp.authenticate(request: request)
                state.methodId = nil
                state.step = .phoneNumber
                state.rawResponse = response
            } catch (let error)  {
                state.methodId = nil
                state.step = .token
                print(error)
            }
        }

        func logout() async {
            do {
                let response = try await consumerClient.session.revoke()
                state.rawResponse = response
            } catch (let error) {
                print(error)
            }
        }
    }
}


struct ContentViewState {
    var authenticationState: ConsumerAuthenticationState = .Loading()
    var methodId: String? = nil
    var step: Step = .phoneNumber
    var rawResponse: SharedStytchAPIResponse? = nil
}

enum Step {
    case phoneNumber
    case token
}

private extension SharedStytchAPIResponse {
    func toFriendlyDisplay() -> String {
        var display = if self is OtpSmsLoginOrCreateResponse {
            "Code Sent\n"
        } else if (self is AuthenticatedResponse) {
            "Logged In\n"
        } else if (self is SessionsRevokeResponse) {
            "Logged Out\n"
        } else {
            "Received Response\n"
        }
        if let response = self as? SharedBasicResponse {
            display += """
                status_code:
                \(response.statusCode)
                
                request_id:
                \(response.requestId)
            """.trimmingCharacters(in: .whitespaces)
        }
        if let response = self as? OtpSmsLoginOrCreateResponse {
            display += """
                method_id:
                \(response.methodId)
            """.trimmingCharacters(in: .whitespaces)
        }
        if let response = self as? AuthenticatedResponse {
            display += """
                session_token:
                \(response.sessionToken)
                
                session_jwt:
                \(response.sessionJwt)
            """.trimmingCharacters(in: .whitespaces)
        }
        return display
    }
}
