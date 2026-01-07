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
            path: "./artifacts/StytchConsumer.xcframework"
        )
    ]
)