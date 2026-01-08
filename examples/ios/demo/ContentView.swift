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
    @State private var input: String = ""
    @State private var inputLabel: String = "Phone Number"

    private func submit() {
        Task {
            switch viewModel.state.step {
            case .phoneNumber:
                await viewModel.sendSms(phoneNumber: input)
            case .token:
                await viewModel.authSms(token: input)
            case .authenticated:
                return
            }
        }
    }

    var body: some View {
        VStack {
            Text("Testing SMS OTP...")
            if viewModel.state.step != .authenticated {
                HStack {
                    TextField(inputLabel, text: $input)
                        .padding()
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                    Button("Submit", systemImage: "paperplane") {
                        submit()
                    }
                    .labelStyle(.iconOnly)
                }
            }
            if let response = viewModel.state.rawResponse {
                Spacer()
                Text(response.toFriendlyDisplay())
            }
        }
        .frame(maxHeight: .infinity, alignment: .top)
        .padding()
        .onChange(of: viewModel.state.step) { oldValue, newValue in
            self.input = ""
            switch newValue {
            case .phoneNumber:
                self.inputLabel = "Phone Number"
            case .token:
                self.inputLabel = "Code"
            case .authenticated:
                return
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
                let request: OtpAuthenticateRequest = .init(token: token, methodId: methodId, sessionDurationMinutes: 30)
                let response = try await consumerClient.otp.authenticate(request: request)
                switch onEnum(of: response) {
                case .success:
                    state.methodId = nil
                    state.step = .authenticated
                case .error:
                    state.methodId = nil
                    state.step = .token
                }
                state.rawResponse = response as? SharedStytchResult<AnyObject>
            } catch  {
            }
        }
    }
}


struct ContentViewState {
    var methodId: String? = nil
    var step: Step = .phoneNumber
    var rawResponse: SharedStytchResult<AnyObject>? = nil
}

enum Step {
    case phoneNumber
    case token
    case authenticated
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
                "This will never be displayed!"
            }
        case .error(let error):
            """
            Error!

            \(error.description)
            """.trimmingCharacters(in: .whitespaces)
        }
    }
}
