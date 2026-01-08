**Publishing thoughts**
This repo publishes:
* Android/KMP artifacts directly to maven
* RN package directly to NPM (a subdirectory-TBD will house any _pure_ TS files for things like hooks/context providers)
* An XCFramework that gets copied to a separate stytch-ios repo (that repo would then ONLY include a `Package.swift` and the `.xcframework` file)

**While testing**
* Run `./buildArtifactsForTesting.sh`, this will build all three platforms and copy the artifacts to the `artifacts/` directory, which the apps in `examples/` consume
* Run the apps in the `examples/` directory as appropriate to test things

**Known things**
on ios, classes exported in the shared module are being prepended with "shared" (ie: StytchClientConfiguration -> SharedStytchClientConfiguration). why?
on ios, default constructor arguments are a bear (see params in loginOrCreate()). If we're codegening, we can just codegen all of the constructors (see StytchClientConfiguration.ios.kt), I guess?