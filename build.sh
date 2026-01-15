#!/bin/sh
PLATFORM=$1
ROOT_DIR=`pwd`
WORKING_DIR=$ROOT_DIR/stytch-multiplatform
mkdir -p artifacts

buildAndCopyAndroid() {
    cd $WORKING_DIR
    echo "Building android artifact..."
    ./gradlew :sdk:consumer-headless:publishAllPublicationsToTESTINGRepository :sdk:consumer-extensions:publishAllPublicationsToTESTINGRepository :sdk:shared:publishAllPublicationsToTESTINGRepository
}

buildAndCopyIos() {
    cd $WORKING_DIR
    echo "Building iOS library..."
    cd sdk/shared/src/iosMain/interop

    # clear out any old stuff
    rm -rf iOS.xcarchive libStytch* Simulator.xcarchive StytchShared

    # create header file (for cinterop generation)
    sudo swiftc -emit-objc-header Sources/StytchShared/StytchShared.swift

    # build xcarchives for platforms
    xcodebuild archive -scheme StytchShared -configuration Release -destination 'generic/platform=iOS' -archivePath "./iOS" SKIP_INSTALL=NO BUILD_LIBRARIES_FOR_DISTRIBUTION=YES &&
    xcodebuild archive -scheme StytchShared -configuration Release -destination 'generic/platform=iOS Simulator' -archivePath "./Simulator" SKIP_INSTALL=NO BUILD_LIBRARIES_FOR_DISTRIBUTION=YES

    # convert the objects into libraries
    ar -crs libStytchIos.a ./iOS.xcarchive/Products/Users/jhaven/Objects/StytchShared.o && 
    ar -crs libStytchSimulator.a ./Simulator.xcarchive/Products/Users/jhaven/Objects/StytchShared.o

    # back to stytch-multiplatform
    cd $WORKING_DIR
 
    echo "Building iOS artifact..."
    ./gradlew assembleStytchConsumerSDKXCFramework assembleStytchConsumerExtensionsSDKXCFramework assembleStytchSharedSDKXCFramework
    cp -r sdk/consumer-headless/build/XCFrameworks/release/StytchConsumerSDK.xcframework $ROOT_DIR/stytch-ios
    cp -r sdk/consumer-headless/build/XCFrameworks/release/StytchConsumerSDK.xcframework $ROOT_DIR/react-native/consumer/ios
}

buildAndCopyRn() {
    cd $WORKING_DIR
    echo "Building RN artifacts..."
    ./gradlew kotlinUpgradeYarnLock :sdk:consumer-headless:jsBrowserProductionLibraryDistribution
    cp -r sdk/consumer-headless/build/dist/js/productionLibrary/ $ROOT_DIR/react-native/consumer/lib
    cd $ROOT_DIR/react-native/consumer
    yarn build
}

updateRnDemoApp() {
    cd $ROOT_DIR/examples/rn
    yarn remove @stytch/react-native-consumer
    yarn add file:$ROOT_DIR/react-native/consumer
}

main() {
    if [ -z "$PLATFORM" ]; then
        echo "Building for all platforms"
        buildAndCopyAndroid
        buildAndCopyIos
        buildAndCopyRn
    elif [[ "$PLATFORM" == "android" ]]; then
        buildAndCopyAndroid
    elif [[ "$PLATFORM" == "ios" ]]; then
        buildAndCopyIos
    elif [[ "$PLATFORM" == "rn" ]]; then
        buildAndCopyRn
        updateRnDemoApp
    fi
    cd $ROOT_DIR
    echo "Done."
}

main