**Publishing thoughts**
This repo publishes:
* Android/KMP artifacts directly to maven
* RN package directly to NPM (a subdirectory-TBD will house any _pure_ TS files for things like hooks/context providers)
* An XCFramework that gets copied to a separate stytch-ios repo (that repo would then ONLY include a `Package.swift` and the `.xcframework` file)

**While testing**
* Run `./buildArtifactsForTesting.sh`, this will build all three platforms and copy the artifacts to the `artifacts/` directory, which the apps in `examples/` consume
* Run the apps in the `examples/` directory as appropriate to test things