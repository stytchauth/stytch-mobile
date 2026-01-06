Very basic POC, just to prove that this works the way I expect it to.

`stytch-multiplatform/sdk` is where the magic happens. this is a KMP project that currently ONLY has kotlin code in it (only a `commonMain` sourceSet). There are two packages: `shared` (contains a simple data class) and `consumer-headless` (contains a simple object/singleton with a single function that reads that shared data class). This serves as an example of how the modules may be organized and illustrates how they can depend on each other.

`stytch-multiplatform/examples` -> sample apps (native android and iOS) that depend on the outputs of the `sdk/` modules. Currently, the android app depends on it directly through gradle; the iOS app, however, has a Swift Package dependency on the `stytch-ios/` intermediary (which simply contains the binary produced by the KMP project) to illustrate how the actual integration/devex will be for an iOS developer. Take a look at the [Android implementation](./stytch-multiplatform/examples/consumer-android/src/main/java/com/stytch/examples/consumer/android/MainActivity.kt) and the [iOS implementation](./stytch-multiplatform/examples/consumer-ios/consumer-ios/ContentView.swift). They both seem pretty dang platform-idiomatic to me!

`stytch-ios` is the dummy package that contains a Package.swift file that exposes the binary produced by the KMP project; because of limitations in the Swift Package Manager, this is basically a dummy file that nees to be published at the root of a repo. So, when we productionize this repo, imagine that the two root directories go away, and the two directories just live together. iOS clients will point directly at the `stytch-mobile` repo, which reads the `Package.swift` which points to the hosted binary file. Easy-peasey.

**TODO**
Validate the flow for react-native 😬


**Publishing thoughts**
This repo publishes:
* Android/KMP artifacts directly to maven
* RN package directly to NPM (a subdirectory-TBD will house any _pure_ TS files for things like hooks/context providers)
* An XCFramework that gets copied to a separate stytch-ios repo (that repo would then ONLY include a Package.swift and the .xcframework file(s))