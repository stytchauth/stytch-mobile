// swift-tools-version:6.2
import PackageDescription

let package = Package(
    name: "StytchShared",
    platforms: [
        .iOS(.v15),
    ],
    products: [
        .library(
            name: "StytchShared",
            type: .static,
            targets: ["StytchShared"],
        )
    ],
    targets: [
        .target(
            name: "StytchShared",
            dependencies: ["RecaptchaEnterprise", "StytchDFP"]
        ),
        .binaryTarget(name: "RecaptchaEnterprise", path: "./Sources/RecaptchaEnterprise.xcframework"),
        .binaryTarget(name: "StytchDFP", path: "./Sources/StytchDFP.xcframework")
    ]
)
