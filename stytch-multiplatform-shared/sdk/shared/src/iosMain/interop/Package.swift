// swift-tools-version:6.2
import PackageDescription

let package = Package(
    name: "StytchShared",
    platforms: [
        .iOS(.v13),
    ],
    products: [
        .library(
            name: "StytchShared",
            type: .static,
            targets: ["StytchShared"]
        )
    ],
    targets: [
        .target(name: "StytchShared")
    ]
)