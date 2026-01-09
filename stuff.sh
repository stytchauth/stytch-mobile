cd stytch-multiplatform/sdk/shared/src/iosMain/interop
# create header file (for cinterop generation)
sudo swiftc -emit-objc-header Sources/StytchShared/StytchShared.swift

# build xcarchives for platforms
xcodebuild archive -scheme StytchShared -configuration Release -destination 'generic/platform=iOS' -archivePath "./iOS" SKIP_INSTALL=NO BUILD_LIBRARIES_FOR_DISTRIBUTION=YES &&
xcodebuild archive -scheme StytchShared -configuration Release -destination 'generic/platform=iOS Simulator' -archivePath "./Simulator" SKIP_INSTALL=NO BUILD_LIBRARIES_FOR_DISTRIBUTION=YES

# convert the objects into libraries
ar -crs libStytchIos.a ./iOS.xcarchive/Products/Users/jhaven/Objects/StytchShared.o && 
ar -crs libStytchSimulator.a ./Simulator.xcarchive/Products/Users/jhaven/Objects/StytchShared.o