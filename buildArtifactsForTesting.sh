#!/bin/sh
# clear out old stuff
rm -rf artifacts
mkdir -p artifacts

# get into work directory
cd stytch-multiplatform
./gradlew clean

# Publish Android Artifact
echo "Building android artifact..."
./gradlew :sdk:consumer-headless:publishAllPublicationsToTESTINGRepository :sdk:shared:publishAllPublicationsToTESTINGRepository

# Build iOS Artifact
echo "Building iOS artifact..."
./gradlew assembleStytchConsumerSDKXCFramework

# Build JS Artifact
echo "Building JS artifact..."
# ./gradlew :sdk:consumer-headless:jsBrowserProductionLibraryDistribution

# Copy artifacts
echo "Copying build artifacts to folder..."
cd ..
cp -r stytch-multiplatform/sdk/consumer-headless/build/XCFrameworks/release/StytchConsumerSDK.xcframework artifacts
cp -r stytch-multiplatform/sdk/consumer-headless/build/dist/js/productionLibrary artifacts

echo "Done."