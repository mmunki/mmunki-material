#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

VERSION="${VERSION:-1.0.0}"
NAME="mmunki-material-$VERSION"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/usr/local/lib/android/sdk}}"
BUILD_API="${BUILD_API:-36}"
ANDROID_JAR="$SDK/platforms/android-$BUILD_API/android.jar"
[ -f "$ANDROID_JAR" ] || ANDROID_JAR="$(find "$SDK/platforms" -maxdepth 2 -name android.jar \
    | grep -v beta | sort -V | tail -n 1)"
BUILD_TOOLS="${BUILD_TOOLS:-$(find "$SDK/build-tools" -mindepth 1 -maxdepth 1 -type d \
    | sort -V | tail -n 1)}"
DEXDUMP="$BUILD_TOOLS/dexdump"
APKSIGNER="$BUILD_TOOLS/apksigner"
AAPT2="$BUILD_TOOLS/aapt2"
D8="$BUILD_TOOLS/d8"
JAVAC="${JAVAC:-javac}"

pass() { printf 'PASS  %s\n' "$*"; }
fail() { printf 'FAIL  %s\n' "$*" >&2; exit 1; }

echo "==> build"
VERSION="$VERSION" ./build.sh

echo
echo "==> pure JVM tests"
mkdir -p build/test-classes
"$JAVAC" -source 8 -target 8 -Xlint:-options -d build/test-classes \
    src/main/java/tw/mmunki/material/MaterialColor.java \
    src/main/java/tw/mmunki/material/MShapes.java \
    src/main/java/tw/mmunki/material/MPalette.java \
    src/main/java/tw/mmunki/material/motion/Easing.java \
    src/main/java/tw/mmunki/material/motion/SpringSpec.java \
    src/main/java/tw/mmunki/material/motion/SpringSolver.java \
    src/test/java/tw/mmunki/material/TestMain.java
java -cp build/test-classes tw.mmunki.material.TestMain
pass "pure JVM tests"

echo
echo "==> dex jar checks"
mkdir -p build/dexcheck
unzip -Z1 "dist/$NAME.jar" | grep -qx 'classes.dex' \
    || fail "classes.dex is not at the root of $NAME.jar"
unzip -p "dist/$NAME.jar" classes.dex > build/dexcheck/classes.dex
"$DEXDUMP" -f build/dexcheck/classes.dex > build/dexcheck/dexdump.txt 2>&1
grep -q "Ltw/mmunki/material/view/MButton;" build/dexcheck/dexdump.txt \
    || fail "MButton missing from classes.dex"
grep -q "Ltw/mmunki/material/MTheme;" build/dexcheck/dexdump.txt \
    || fail "MTheme missing from classes.dex"
if grep -q "Ltw/mmunki/material/R;" build/dexcheck/dexdump.txt; then
    fail "the library unexpectedly references a generated R class"
fi
CLASS_COUNT="$(grep -c "Class descriptor" build/dexcheck/dexdump.txt)"
[ "$CLASS_COUNT" -ge 40 ] || fail "only $CLASS_COUNT classes in classes.dex"
pass "dex jar: classes.dex at root, $CLASS_COUNT classes, no R references"

unzip -Z1 "dist/$NAME-classes.jar" | grep -q 'tw/mmunki/material/view/MButton.class' \
    || fail "classes jar is missing MButton.class"
pass "classes jar contains the compiled API"

unzip -Z1 "dist/$NAME.aar" | grep -qx 'classes.jar' \
    || fail "AAR is missing classes.jar"
unzip -Z1 "dist/$NAME.aar" | grep -qx 'AndroidManifest.xml' \
    || fail "AAR is missing AndroidManifest.xml"
pass "AAR contains classes.jar and AndroidManifest.xml"

echo
echo "==> APK checks"
unzip -Z1 "dist/$NAME-sample.apk" | grep -qx 'classes.dex' \
    || fail "sample APK is missing classes.dex"
"$APKSIGNER" verify --verbose --print-certs "dist/$NAME-sample.apk" \
    > build/dexcheck/apksigner.txt 2>&1 \
    || fail "apksigner verify failed"
"$AAPT2" dump badging "dist/$NAME-sample.apk" > build/dexcheck/badging.txt 2>&1
grep -q "package: name='tw.mmunki.material.sample'" build/dexcheck/badging.txt \
    || fail "sample APK has the wrong package"
grep -q "minSdkVersion:'21'" build/dexcheck/badging.txt \
    || fail "sample APK minSdk is not 21"
grep -q "targetSdkVersion:'36'" build/dexcheck/badging.txt \
    || fail "sample APK targetSdk is not 36"
grep -q "launchable-activity:" build/dexcheck/badging.txt \
    || fail "sample APK has no launchable activity"
pass "sample APK: signed, package tw.mmunki.material.sample, minSdk 21, targetSdk 36"

echo
echo "==> dex min-api matrix"
for api in 21 36; do
    mkdir -p "build/dexcheck/api$api"
    "$D8" --min-api "$api" --lib "$ANDROID_JAR" \
        --output "build/dexcheck/api$api" "build/$NAME-classes.jar"
    [ -s "build/dexcheck/api$api/classes.dex" ] \
        || fail "d8 min-api $api produced no classes.dex"
    pass "d8 --min-api $api"
done

echo
echo "==> API compile matrix"
for platform in $(find "$SDK/platforms" -maxdepth 2 -name android.jar | sort -V); do
    label="$(basename "$(dirname "$platform")")"
    out="build/dexcheck/compile-$label"
    mkdir -p "$out"
    "$JAVAC" -source 8 -target 8 -Xlint:-options \
        -bootclasspath "$platform" -d "$out" @build/library-sources.txt
    pass "javac against $label"
done

echo
echo "ALL CHECKS PASSED"
printf 'artifact: %s\n' "dist/$NAME.jar"
sha256sum "dist/$NAME.jar"
