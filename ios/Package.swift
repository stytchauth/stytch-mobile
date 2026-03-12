// swift-tools-version:5.5

import PackageDescription

let package = Package(
    name: "Stytch",
    platforms: [
        .iOS("15.0")
      ],
    products: [
        .library(name: "StytchConsumerSDK", targets: ["StytchConsumerTarget"]),
        .library(name: "StytchB2BSDK", targets: ["StytchB2BTarget"]),
    ],
    dependencies: [
        .package(url: "https://github.com/GoogleCloudPlatform/recaptcha-enterprise-mobile-sdk", from: "18.8.1"),
        .package(url: "https://github.com/stytchauth/stytch-ios-dfp.git", from: "1.0.5"),
    ],
    targets: [
        .binaryTarget(
            name: "StytchConsumerFramework",
            path: "artifacts/ios/StytchConsumerSDK.xcframework",
        ),
        .binaryTarget(
            name: "StytchB2BFramework",
            path: "artifacts/ios/StytchB2BSDK.xcframework",
        ),
        .binaryTarget(
            name: "StytchSwiftUtilsFramework",
            path: "artifacts/ios/StytchSwiftUtils.xcframework",
        ),
        .binaryTarget(
            name: "StytchSharedFramework",
            path: "artifacts/ios/StytchSharedSDK.xcframework",
        ),
        .target(
            name: "StytchConsumerTarget",
            path: "artifacts/ios/Sources/StytchConsumerTarget",
            dependencies: [
                "StytchConsumerFramework",
                "StytchSwiftUtilsFramework",
                "StytchSharedFramework",
                .product(name: "StytchDFP", package: "stytch-ios-dfp")
            ]
        ),
        .target(
            name: "StytchB2BTarget",
            path: "artifacts/ios/Sources/StytchB2BTarget",
            dependencies: [
                "StytchB2BFramework",
                "StytchSwiftUtilsFramework",
                "StytchSharedFramework",
                .product(name: "StytchDFP", package: "stytch-ios-dfp")
            ]
        ),
    ]
)
