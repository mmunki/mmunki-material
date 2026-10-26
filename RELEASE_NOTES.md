# Aurora Material 1.0.0

A dependency-free Android view library with a calm aurora palette, custom
drawn controls, and iOS-inspired spring motion. This is the first release of
`tw.mmunki.material`.

## Highlights

- Theme system: `MTheme`, `MPalette`, `MTypography`, `MShapes`, `MMotion`.
- Spring engine with frame-rate-independent solving and retargeting.
- Components: `MButton`, `MCard`, `MSwitch`, `MSlider`, `MTextField`,
  `MListSection`, `MListCell`, `MSegmentedControl`, `MProgressBar`,
  `MCircularProgress`, `MBottomSheet`.
- No AndroidX, no external dependencies, no library resource ids.
- Light and dark palettes with text contrast covered by tests.
- Primary artifact is a jar with `classes.dex` at its root; a normal classes
  jar and an AAR are included for regular Android integration.

## Artifacts

- `mmunki-material-1.0.0.jar` — dex jar, `classes.dex` at the root.
- `mmunki-material-1.0.0-classes.jar` — JVM class files.
- `mmunki-material-1.0.0.aar` — Android library archive.
- `mmunki-material-1.0.0-sample.apk` — signed demo app.
- `mmunki-material-1.0.0-source.tar.gz` — source for the tagged commit.

## Compatibility

- Minimum Android: 5.0 (API 21)
- Compile SDK: 36
- Target SDK in the sample: 36
- No native libraries, so no ABI-specific builds.

## Test matrix

| Check | Result |
| --- | --- |
| Pure JVM tests | 14 passed |
| Dex jar | `classes.dex` at root, 65 classes, no `R` references |
| DEX generation | `--min-api 21` passed, `--min-api 36` passed |
| API compile matrix | 18 installed platforms from API 34 through 37.2 passed |
| Sample APK signature | `apksigner verify` passed |
| Sample APK metadata | package `tw.mmunki.material.sample`, minSdk 21, targetSdk 36, launchable activity present |
| Native ABI matrix | not applicable, the library has no native code |

The API compile matrix covers `android-34`, `android-34-ext8/10/11/12`,
`android-35`, `android-35-ext14/15`, `android-36`, `android-36-ext18/19`,
`android-36.1`, `android-37.0`, `android-37.1`, and
`android-37.2-beta1/2/3`, and `android-37.2`.

## Install

```groovy
dependencies {
    implementation files('libs/mmunki-material-1.0.0.aar')
}
```

For dex loading:

```java
DexClassLoader loader = new DexClassLoader(
        jarPath,
        getCodeCacheDir().getAbsolutePath(),
        null,
        getClassLoader());
```

## Source and verification

The release includes the full source archive and a SHA-256 checksum for every
asset. Build with `./build.sh` and run the full verification with `./test.sh`.

## License

MIT.
