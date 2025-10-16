#!/bin/bash
# build_mizuiro.sh
# Automated build script for Mizuiro Music

set -e

echo "╔════════════════════════════════════════╗"
echo "║   MIZUIRO MUSIC BUILD SCRIPT           ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Colors
BLUE='\033[0;36m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
BUILD_TYPE=${1:-debug}
VERSION_NAME=$(grep "versionName" app/build.gradle.kts | awk -F'"' '{print $2}')

echo -e "${BLUE}Building version: $VERSION_NAME${NC}"
echo -e "${BLUE}Build type: $BUILD_TYPE${NC}"
echo ""

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Run tests
if [ "$BUILD_TYPE" == "release" ]; then
    echo "🧪 Running tests..."
    ./gradlew testDebugUnitTest
    
    echo "📸 Running UI tests..."
    ./gradlew connectedDebugAndroidTest || true
fi

# Build
echo "🔨 Building APK..."
./gradlew assemble${BUILD_TYPE^}

# Copy outputs
echo "📂 Copying build outputs..."
OUTPUT_DIR="builds/${VERSION_NAME}"
mkdir -p "$OUTPUT_DIR"

APK_PATH="app/build/outputs/apk/${BUILD_TYPE}/app-${BUILD_TYPE}.apk"
if [ -f "$APK_PATH" ]; then
    cp "$APK_PATH" "$OUTPUT_DIR/mizuiro-music-${VERSION_NAME}-${BUILD_TYPE}.apk"
    echo -e "${GREEN}✓ APK copied to $OUTPUT_DIR${NC}"
fi

echo ""
echo "╔════════════════════════════════════════╗"
echo "║   BUILD COMPLETE! (◠‿◠)               ║"
echo "╚════════════════════════════════════════╝"
echo ""
echo "Output directory: $OUTPUT_DIR"
ls -lh "$OUTPUT_DIR"
