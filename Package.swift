// swift-tools-version:5.5

import PackageDescription

let package = Package(
    name: "Stytch",
    products: [
        .library(name: "StytchConsumerSDK", targets: ["StytchConsumerTarget"]),
    ],
    targets: [
        .binaryTarget(
            name: "StytchConsumerTarget",
            path: "./artifacts/StytchConsumerSDK.xcframework",
        ),
    ]
)