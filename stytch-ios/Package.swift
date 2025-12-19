// swift-tools-version:5.5

import PackageDescription

let package = Package(
    name: "Stytch",
    products: [
        .library(name: "StytchConsumer", targets: ["stytch-consumer-headless"]),
    ],
    targets: [
        .binaryTarget(
            name: "stytch-consumer-headless",
            path: "../stytch-multiplatform/sdk/consumer-headless/build/XCFrameworks/release/StytchConsumer.xcframework" // for testing, in a real publish flow this would point to a permanent location
        )
    ]
)