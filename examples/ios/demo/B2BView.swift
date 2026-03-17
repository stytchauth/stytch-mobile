import SwiftUI
import StytchB2BSDK

// MARK: - B2B

struct B2BView: View {
    let onSwitchDemos: () -> Void
    @State private var viewModel: B2BViewModel

    init(onSwitchDemos: @escaping () -> Void) {
        self.onSwitchDemos = onSwitchDemos
        let token = UserDefaults.standard.string(forKey: keyPublicToken) ?? ""
        let orgId = UserDefaults.standard.string(forKey: keyOrgId)
        _viewModel = State(initialValue: B2BViewModel(publicToken: token, orgId: orgId))
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(spacing: 4) {
                Text("Stytch B2B Demo")
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
    }
}

@Observable
class B2BViewModel {
    private let b2bClient: StytchB2B
    private let orgId: String?
    var authState: B2BAuthenticationState = B2BAuthenticationState.Loading()
    var lastResponse: String? = nil

    var statusText: String {
        switch onEnum(of: authState) {
        case .loading: return "Loading..."
        case .authenticated: return "Welcome Back"
        case .unauthenticated: return "Please Login"
        }
    }

    init(publicToken: String, orgId: String?) {
        self.orgId = orgId
        b2bClient = createStytchB2B(configuration: .init(publicToken: publicToken))
        Task {
            for await state in b2bClient.authenticationStateFlow {
                authState = state
            }
        }
    }

    func startGoogleOAuth() {
        Task {
            do {
                if let orgId, !orgId.isEmpty {
                    let params = B2BOAuthStartParameters(
                        loginRedirectUrl: "my-login-redirect-url",
                        signupRedirectUrl: "my-signup-redirect-url",
                        organizationId: orgId
                    )
                    let response = try await b2bClient.oauth.google.start(parameters: params)
                    lastResponse = "\(response)"
                } else {
                    let params = B2BOAuthDiscoveryStartParameters(
                        discoveryRedirectUrl: "my-discovery-redirect-url"
                    )
                    let response = try await b2bClient.oauth.google.discovery.start(parameters: params)
                    lastResponse = "\(response)"
                }
            } catch {
                lastResponse = "\(error)"
            }
        }
    }

    func switchDemos(onComplete: () -> Void) async {
        if case .authenticated = onEnum(of: authState) {
            try? await b2bClient.session.revoke()
        }
        onComplete()
    }
}
