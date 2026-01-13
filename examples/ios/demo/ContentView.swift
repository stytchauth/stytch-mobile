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
                switch onEnum(of: response) {
                case .success(let data):
                    state.methodId = data.data?.methodId
                    state.step = .token
                case .error:
                    state.methodId = nil
                    state.step = .phoneNumber
                }
                state.rawResponse = response as? SharedStytchResult<AnyObject>
            } catch  {
            }
        }

        func authSms(token: String) async {
            do {
                guard let methodId = state.methodId else {
                    return
                }
                let request: OtpAuthenticateRequest = .init(token: token, methodId: methodId, sessionDurationMinutes: 5)
                let response = try await consumerClient.otp.authenticate(request: request)
                switch onEnum(of: response) {
                case .success:
                    state.methodId = nil
                    state.step = .phoneNumber
                case .error:
                    state.methodId = nil
                    state.step = .token
                }
                state.rawResponse = response as? SharedStytchResult<AnyObject>
            } catch  {
            }
        }

        func logout() async {
            do {
                let response = try await consumerClient.session.revoke()
                state.rawResponse = response as? SharedStytchResult<AnyObject>
            } catch {
            }
        }
    }
}


struct ContentViewState {
    var authenticationState: ConsumerAuthenticationState = .Loading()
    var methodId: String? = nil
    var step: Step = .phoneNumber
    var rawResponse: SharedStytchResult<AnyObject>? = nil
}

enum Step {
    case phoneNumber
    case token
}

private extension SharedStytchResult<AnyObject> {
    func toFriendlyDisplay() -> String {
        return switch onEnum(of: self) {
        case .success(let payload):
            if let res = payload.data as? OtpSmsLoginOrCreateResponse {
                """
                Code Sent!

                request_id:
                \(res.requestId)

                status_code:
                \(res.statusCode)

                method_id:
                \(res.methodId)
                """.trimmingCharacters(in: .whitespaces)
            } else if let res = payload.data as? OtpAuthenticateResponse {
                """
                Logged In!

                request_id:
                \(res.requestId)

                status_code:
                \(res.statusCode)

                session_token:
                \(res.sessionToken)
                """.trimmingCharacters(in: .whitespaces)
            } else {
                "This is a different type of response that I haven't mapped (yet)!"
            }
        case .error(let error):
            """
            Error!

            \(error.description)
            """.trimmingCharacters(in: .whitespaces)
        }
    }
}
