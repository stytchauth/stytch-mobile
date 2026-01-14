#!/bin/sh
PLATFORM=$1
ROOT_DIR=`pwd`
ARTIFACT_DIR=$ROOT_DIR/artifacts
rm -rf $ARTIFACT_DIR
mkdir $ARTIFACT_DIR
cd $ROOT_DIR/stytch-multiplatform
./gradlew clean

buildAndCopyAndroid() {
    cd $ROOT_DIR/stytch-multiplatform
    echo "Building android artifact..."
    ./gradlew :sdk:consumer-headless:publishAllPublicationsToTESTINGRepository :sdk:consumer-extensions:publishAllPublicationsToTESTINGRepository :sdk:shared:publishAllPublicationsToTESTINGRepository
}

buildAndCopyIos() {
    cd $ROOT_DIR/stytch-multiplatform
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
    cd $ROOT_DIR/stytch-multiplatform
 
    echo "Building iOS artifact..."
    ./gradlew assembleStytchConsumerSDKXCFramework assembleStytchConsumerExtensionsSDKXCFramework assembleStytchSharedSDKXCFramework
    cp -r sdk/consumer-headless/build/XCFrameworks/release/StytchConsumerSDK.xcframework $ARTIFACT_DIR
}

buildAndCopyRn() {
    cd $ROOT_DIR/stytch-multiplatform
    echo "Building RN artifacts..."
    ./gradlew :sdk:consumer-headless:jsBrowserProductionLibraryDistribution
    cp -r sdk/consumer-headless/build/dist/js/productionLibrary $ARTIFACT_DIR/react-native-consumer
}


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
fi

echo "Done."