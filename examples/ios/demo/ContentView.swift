import SwiftUI

let keyDemoType = "DEMO_APP_TYPE"
let keyPublicToken = "STYTCH_PUBLIC_TOKEN"
let keyOrgId = "STYTCH_ORG_ID"

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
        case .tokenEntry(let demoType):
            TokenEntryView(demoType: demoType) { publicToken, extraParam in
                UserDefaults.standard.set(publicToken, forKey: keyPublicToken)
                if demoType == "B2B", let orgId = extraParam {
                    UserDefaults.standard.set(orgId, forKey: keyOrgId)
                }
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
        UserDefaults.standard.removeObject(forKey: keyOrgId)
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
    let demoType: String
    let onSubmit: (String, String?) -> Void
    @State private var publicToken = ""
    @State private var extraParam = ""

    var body: some View {
        VStack(spacing: 16) {
            Text("Configure SDK")
                .font(.largeTitle).bold()
            Spacer().frame(height: 8)
            TextField("Public Token", text: $publicToken)
                .textFieldStyle(.roundedBorder)
                .autocorrectionDisabled()
                .textInputAutocapitalization(.never)
            if demoType == "B2B" {
                TextField("Organization ID (optional)", text: $extraParam)
                    .textFieldStyle(.roundedBorder)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
            }
            Button("Continue") {
                let token = publicToken.trimmingCharacters(in: .whitespaces)
                guard !token.isEmpty else { return }
                let extra = extraParam.trimmingCharacters(in: .whitespaces)
                onSubmit(token, extra.isEmpty ? nil : extra)
            }
            .buttonStyle(.borderedProminent)
            .frame(maxWidth: .infinity)
            .disabled(publicToken.trimmingCharacters(in: .whitespaces).isEmpty)
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
