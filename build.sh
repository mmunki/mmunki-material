#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

VERSION="${VERSION:-2.0.0}"
NAME="mmunki-material-$VERSION"
DIST="$ROOT/dist"
BUILD="$ROOT/build"

SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/usr/local/lib/android/sdk}}"
BUILD_API="${BUILD_API:-36}"
ANDROID_JAR="$SDK/platforms/android-$BUILD_API/android.jar"

if [ ! -f "$ANDROID_JAR" ]; then
    PLATFORM="$(find "$SDK/platforms" -maxdepth 2 -name android.jar \
        | grep -v beta | sort -V | tail -n 1)"
    [ -n "$PLATFORM" ] || {
        echo "error: no Android platform found under $SDK/platforms" >&2
        exit 1
    }
    ANDROID_JAR="$PLATFORM"
fi

BUILD_TOOLS="${BUILD_TOOLS:-}"
if [ -z "$BUILD_TOOLS" ]; then
    BUILD_TOOLS="$(find "$SDK/build-tools" -mindepth 1 -maxdepth 1 -type d \
        | sort -V | tail -n 1)"
fi
[ -n "$BUILD_TOOLS" ] || {
    echo "error: no Android build-tools found under $SDK/build-tools" >&2
    exit 1
}

AAPT2="$BUILD_TOOLS/aapt2"
D8="$BUILD_TOOLS/d8"
ZIPALIGN="$BUILD_TOOLS/zipalign"
APKSIGNER="$BUILD_TOOLS/apksigner"
JAVAC="${JAVAC:-javac}"
JAR="${JAR:-jar}"
KEYTOOL="${KEYTOOL:-keytool}"

for tool in "$AAPT2" "$D8" "$ZIPALIGN" "$APKSIGNER"; do
    [ -x "$tool" ] || {
        echo "error: missing build tool $tool" >&2
        exit 1
    }
done

rm -rf "$BUILD" "$DIST"
mkdir -p "$BUILD/classes" "$BUILD/dex" "$BUILD/dexjar/META-INF" \
    "$BUILD/aar" "$BUILD/sample/gen" "$BUILD/sample/classes" \
    "$BUILD/sample/dex" "$DIST"

echo "==> compiling tw.mmunki.material against $ANDROID_JAR"
find src/main/java -name '*.java' | sort > "$BUILD/library-sources.txt"
"$JAVAC" -source 8 -target 8 -Xlint:-options \
    -bootclasspath "$ANDROID_JAR" \
    -d "$BUILD/classes" @"$BUILD/library-sources.txt"

echo "==> packaging classes and dex"
"$JAR" cf "$BUILD/$NAME-classes.jar" -C "$BUILD/classes" .
cp "$BUILD/$NAME-classes.jar" "$DIST/$NAME-classes.jar"
"$D8" --min-api 21 --lib "$ANDROID_JAR" \
    --output "$BUILD/dex" "$BUILD/$NAME-classes.jar"

cp "$BUILD/dex/classes.dex" "$BUILD/dexjar/classes.dex"
printf 'Manifest-Version: 1.0\nImplementation-Title: mmunki material\nImplementation-Version: %s\nCreated-By: mmunki material build\n\n' \
    "$VERSION" > "$BUILD/dexjar/META-INF/MANIFEST.MF"
( cd "$BUILD/dexjar" && "$JAR" cfm "$DIST/$NAME.jar" \
    META-INF/MANIFEST.MF classes.dex )

cp "$BUILD/$NAME-classes.jar" "$BUILD/aar/classes.jar"
cat > "$BUILD/aar/AndroidManifest.xml" <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="tw.mmunki.material">
    <uses-sdk android:minSdkVersion="21" android:targetSdkVersion="36" />
</manifest>
EOF
( cd "$BUILD/aar" && zip -q -r "$DIST/$NAME.aar" . )

echo "==> building the sample APK"
"$AAPT2" compile --dir sample/res -o "$BUILD/sample/res.zip"
"$AAPT2" link -o "$BUILD/sample/base.apk" \
    -I "$ANDROID_JAR" \
    --manifest sample/AndroidManifest.xml \
    --java "$BUILD/sample/gen" \
    --auto-add-overlay "$BUILD/sample/res.zip"

find sample/src "$BUILD/sample/gen" -name '*.java' | sort > "$BUILD/sample/sources.txt"
"$JAVAC" -source 8 -target 8 -Xlint:-options \
    -bootclasspath "$ANDROID_JAR" \
    -classpath "$BUILD/classes" \
    -d "$BUILD/sample/classes" @"$BUILD/sample/sources.txt"

"$JAR" cf "$BUILD/sample/sample-classes.jar" -C "$BUILD/sample/classes" .
"$D8" --min-api 21 --lib "$ANDROID_JAR" \
    --output "$BUILD/sample/dex" \
    "$BUILD/sample/sample-classes.jar" "$BUILD/$NAME-classes.jar"

cp "$BUILD/sample/base.apk" "$BUILD/sample/unaligned.apk"
( cd "$BUILD/sample" && zip -q -j unaligned.apk dex/classes.dex )
"$ZIPALIGN" -f -p 4 "$BUILD/sample/unaligned.apk" "$BUILD/sample/aligned.apk"

KEYSTORE="$BUILD/debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
    "$KEYTOOL" -genkeypair -keystore "$KEYSTORE" -storepass android \
        -keypass android -alias androiddebugkey \
        -dname "CN=Android Debug,O=Android,C=US" \
        -keyalg RSA -keysize 2048 -validity 10000 -noprompt >/dev/null 2>&1
fi
"$APKSIGNER" sign --ks "$KEYSTORE" --ks-pass pass:android \
    --key-pass pass:android \
    --out "$DIST/$NAME-sample.apk" "$BUILD/sample/aligned.apk"
rm -f "$DIST/$NAME-sample.apk.idsig"

echo
echo "==> artifacts in $DIST"
for artifact in "$DIST"/*; do
    printf '  %s  %s bytes  sha256=%s\n' \
        "$(basename "$artifact")" \
        "$(wc -c <"$artifact")" \
        "$(sha256sum "$artifact" | cut -d' ' -f1)"
done
