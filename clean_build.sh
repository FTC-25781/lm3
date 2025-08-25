#!/bin/bash

echo "Cleaning build directories..."
rm -rf TeamCode/build
rm -rf FtcRobotController/build
rm -rf build
rm -rf .gradle/build-cache
rm -rf .gradle/8.9/executionHistory
rm -rf .gradle/8.9/fileHashes

echo "Cleaning Android build cache..."
rm -rf ~/.android/build-cache

echo "Running gradle clean..."
./gradlew clean

echo "Running fresh build..."
./gradlew assembleDebug

echo "Build complete!"