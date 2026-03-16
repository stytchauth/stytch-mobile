import SwiftUI
import StytchConsumerSDK

private let keyDemoType = "DEMO_APP_TYPE"
private let keyPublicToken = "STYTCH_PUBLIC_TOKEN"

// MARK: - Navigation

enum AppScreen {
    case selector
    case tokenEntry(demoType: String)
    case consumer
    case b2b
}

// MARK: - Root

struct ContentView: View {
    @State private var screen: AppScreen = Self.initialScreen()

    var body: some View {
        switch screen {
        case .selector:
            SelectorView { demoType in
                UserDefaults.standard.set(demoType, forKey: keyDemoType)
                screen = .tokenEntry(demoType: demoType)
            }
        case .tokenEntry:
            TokenEntryView { publicToken in
                UserDefaults.standard.set(publicToken, forKey: keyPublicToken)
                let demoType = UserDefaults.standard.string(forKey: keyDemoType)
                screen = demoType == "CONSUMER" ? .consumer : .b2b
            }
        case .consumer:
            ConsumerView(onSwitchDemos: switchDemos)
        case .b2b:
            B2BView(onSwitchDemos: switchDemos)
        }
    }

    private func switchDemos() {
        UserDefaults.standard.removeObject(forKey: keyPublicToken)
        UserDefaults.standard.removeObject(forKey: keyDemoType)
        screen = .selector
    }

    private static func initialScreen() -> AppScreen {
        guard let demoType = UserDefaults.standard.string(forKey: keyDemoType) else {
            return .selector
        }
        guard UserDefaults.standard.string(forKey: keyPublicToken) != nil else {
            return .tokenEntry(demoType: demoType)
        }
        return demoType == "CONSUMER" ? .consumer : .b2b
    }
}

// MARK: - Selector

struct SelectorView: View {
    let onSelect: (String) -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text("Stytch Demo")
                .font(.largeTitle).bold()
            Text("Select a demo to get started")
                .foregroundStyle(.secondary)
            Spacer().frame(height: 16)
            Button("Consumer") { onSelect("CONSUMER") }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
            Button("B2B") { onSelect("B2B") }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Token Entry

struct TokenEntryView: View {
    let onSubmit: (String) -> Void
    @State private var publicToken = ""

    var body: some View {
        VStack(spacing: 16) {
            Text("Configure SDK")
                .font(.largeTitle).bold()
            Spacer().frame(height: 8)
            TextField("Public Token", text: $publicToken)
                .textFieldStyle(.roundedBorder)
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
            Button("Continue") {
                let token = publicToken.trimmingCharacters(in: .whitespaces)
                guard !token.isEmpty else { return }
                onSubmit(token)
            }
            .buttonStyle(.borderedProminent)
            .frame(maxWidth: .infinity)
            .disabled(publicToken.trimmingCharacters(in: .whitespaces).isEmpty)
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Consumer

struct ConsumerView: View {
    let onSwitchDemos: () -> Void
    @State private var viewModel: ConsumerViewModel

    init(onSwitchDemos: @escaping () -> Void) {
        self.onSwitchDemos = onSwitchDemos
        let token = UserDefaults.standard.string(forKey: keyPublicToken) ?? ""
        _viewModel = State(initialValue: ConsumerViewModel(publicToken: token))
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(spacing: 4) {
                Text("Stytch Consumer Demo")
                    .font(.headline)
                Text(viewModel.statusText)
                    .foregroundStyle(.secondary)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)

            Divider()

            ScrollView {
                VStack(spacing: 12) {
                    Button("Google Login") { viewModel.startGoogleOAuth() }
                        .buttonStyle(.borderedProminent)
                        .frame(maxWidth: .infinity)

                    Button("Apple Login") { viewModel.startAppleOAuth() }
                        .buttonStyle(.borderedProminent)
                        .frame(maxWidth: .infinity)

                    SmsOtpForm(
                        step: viewModel.smsStep,
                        onSend: { viewModel.sendSms(phoneNumber: $0) },
                        onVerify: { viewModel.authSms(token: $0) }
                    )

                    BiometricsButton(
                        availability: viewModel.biometricsAvailability,
                        onTap: { viewModel.biometricsAction() }
                    )

                    Button("SWITCH DEMOS") {
                        Task { await viewModel.switchDemos(onComplete: onSwitchDemos) }
                    }
                    .buttonStyle(.bordered)
                    .tint(.red)
                    .frame(maxWidth: .infinity)
                }
                .padding()
            }

            if let response = viewModel.lastResponse {
                Divider()
                Text("Last response:")
                    .font(.caption.bold())
                    .padding([.horizontal, .top])
                ScrollView {
                    Text(response)
                        .font(.caption.monospaced())
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal)
                }
                .frame(height: 140)
                .padding(.bottom, 8)
            }
        }
        .task { await viewModel.refreshBiometrics() }
        .onChange(of: viewModel.authStateVersion) { _, _ in
            Task { await viewModel.refreshBiometrics() }
        }
    }
}

@Observable
class ConsumerViewModel {
    private let consumerClient: StytchConsumer
    var authState: ConsumerAuthenticationState = ConsumerAuthenticationState.Loading()
    var authStateVersion: Int = 0
    var smsStep: SmsStep = .phone
    var methodId: String? = nil
    var biometricsAvailability: BiometricsAvailability? = nil
    var lastResponse: String? = nil

    var statusText: String {
        switch onEnum(of: authState) {
        case .loading: return "Loading..."
        case .authenticated: return "Welcome Back"
        case .unauthenticated: return "Please Login"
        }
    }

    init(publicToken: String) {
        consumerClient = createStytchConsumer(configuration: .init(publicToken: publicToken))
        Task {
            for await state in consumerClient.authenticationStateFlow {
                authState = state
                authStateVersion += 1
            }
        }
    }

    func refreshBiometrics() async {
        let params = BiometricsParameters(
            sessionDurationMinutes: 5,
            promptData: .init(reason: "Authenticate", fallbackTitle: "Use Passcode", cancelTitle: "Cancel")
        )
        biometricsAvailability = try? await consumerClient.biometrics.getAvailability(parameters: params)
    }

    func biometricsAction() {
        Task {
            let params = BiometricsParameters(
                sessionDurationMinutes: 5,
                promptData: .init(reason: "Authenticate", fallbackTitle: "Use Passcode", cancelTitle: "Cancel")
            )
            do {
                guard let availability = biometricsAvailability else { return }
                switch onEnum(of: availability) {
                case .available:
                    let response = try await consumerClient.biometrics.register(parameters: params)
                    lastResponse = "\(response)"
                case .alreadyRegistered:
                    let response = try await consumerClient.biometrics.authenticate(parameters: params)
                    lastResponse = "\(response)"
                default:
                    return
                }
            } catch {
                lastResponse = "\(error)"
            }
            await refreshBiometrics()
        }
    }

    func sendSms(phoneNumber: String) {
        Task {
            do {
                let response = try await consumerClient.otp.sms.loginOrCreate(
                    request: .init(phoneNumber: phoneNumber)
                )
                methodId = response.methodId
                smsStep = .code
                lastResponse = "\(response)"
            } catch {
                lastResponse = "\(error)"
            }
        }
    }

    func authSms(token: String) {
        guard let methodId else { return }
        Task {
            do {
                let response = try await consumerClient.otp.authenticate(
                    request: .init(token: token, methodId: methodId, sessionDurationMinutes: 5)
                )
                self.methodId = nil
                smsStep = .phone
                lastResponse = "\(response)"
            } catch {
                self.methodId = nil
                smsStep = .phone
                lastResponse = "\(error)"
            }
        }
    }

    func startGoogleOAuth() {
        Task {
            do {
                let params = OAuthStartParameters(
                    loginRedirectUrl: "my-login-redirect-url",
                    signupRedirectUrl: "my-signup-redirect-url"
                )
                let response = try await consumerClient.oauth.google.start(startParameters: params)
                lastResponse = "\(response)"
            } catch {
                lastResponse = "\(error)"
            }
        }
    }

    func startAppleOAuth() {
        Task {
            do {
                let params = OAuthStartParameters(
                    loginRedirectUrl: "my-login-redirect-url",
                    signupRedirectUrl: "my-signup-redirect-url"
                )
                let response = try await consumerClient.oauth.apple.start(startParameters: params)
                lastResponse = "\(response)"
            } catch {
                lastResponse = "\(error)"
            }
        }
    }

    func switchDemos(onComplete: () -> Void) async {
        if case .authenticated = onEnum(of: authState) {
            try? await consumerClient.session.revoke()
        }
        onComplete()
    }
}

enum SmsStep { case phone, code }

// MARK: - Subviews

struct SmsOtpForm: View {
    let step: SmsStep
    let onSend: (String) -> Void
    let onVerify: (String) -> Void
    @State private var input = ""

    var body: some View {
        HStack {
            TextField(step == .phone ? "Phone Number" : "Code", text: $input)
                .textFieldStyle(.roundedBorder)
                .keyboardType(step == .phone ? .phonePad : .numberPad)
                .autocorrectionDisabled()
            Button(step == .phone ? "Send Code" : "Verify") {
                let text = input.trimmingCharacters(in: .whitespaces)
                guard !text.isEmpty else { return }
                if step == .phone { onSend(text) } else { onVerify(text) }
                input = ""
            }
            .buttonStyle(.borderedProminent)
        }
        .onChange(of: step) { _, _ in input = "" }
    }
}

struct BiometricsButton: View {
    let availability: BiometricsAvailability?
    let onTap: () -> Void

    private var label: String {
        guard let availability else { return "Checking Biometrics..." }
        switch onEnum(of: availability) {
        case .available: return "Register Biometrics"
        case .alreadyRegistered: return "Authenticate Biometrics"
        default: return "Biometrics Unavailable"
        }
    }

    private var isEnabled: Bool {
        guard let availability else { return false }
        switch onEnum(of: availability) {
        case .available, .alreadyRegistered: return true
        default: return false
        }
    }

    var body: some View {
        Button(label, action: onTap)
            .buttonStyle(.borderedProminent)
            .frame(maxWidth: .infinity)
            .disabled(!isEnabled)
    }
}

// MARK: - B2B

struct B2BView: View {
    let onSwitchDemos: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Text("B2B — coming soon!")
                .font(.largeTitle).bold()
            Button("SWITCH DEMOS", action: onSwitchDemos)
                .buttonStyle(.bordered)
                .tint(.red)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
