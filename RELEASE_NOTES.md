# Material Components 2.0.0

This release replaces the aurora experiment with a standard Material 3 view
library. The palette, type scale, shape scale and motion tokens now follow the
Material baseline, and every iOS-inspired name and style has been removed.

## Highlights

- Material 3 baseline light and dark color roles, with no gradients and no
  glass surfaces.
- Rebuilt slider: flat 4 dp track, 20 dp handle, discrete tick marks, RTL
  support, keyboard control and accessibility actions.
- New components, each in its own file: `TopAppBar`, `BottomNavigationBar`,
  `FloatingNavigationBar`, `FloatingActionButton` and `RecyclerView`.
- Clean component package: `tw.mmunki.material.component`. The old `M*`
  classes remain as deprecated aliases in `tw.mmunki.material.view`.
- Flat Material 3 buttons, cards, switches, segmented controls, progress
  indicators, text fields, lists and bottom sheets.
- No AndroidX, no external dependencies and no library resource ids.

## Artifacts

- `mmunki-material-2.0.0.jar` - dex jar with `classes.dex` at the root.
- `mmunki-material-2.0.0-classes.jar` - JVM class files.
- `mmunki-material-2.0.0.aar` - Android library archive.
- `mmunki-material-2.0.0-sample.apk` - signed demo app.
- `mmunki-material-2.0.0-source.tar.gz` - source for the tagged commit.

## Compatibility

- Minimum Android: 5.0 (API 21)
- Compile SDK: 36
- Target SDK in the sample: 36
- No native libraries, so no ABI-specific builds.

## Test matrix

| Check | Result |
| --- | --- |
| Pure JVM tests | passed |
| Dex jar | `classes.dex` at root, no `R` references |
| DEX generation | `--min-api 21` passed, `--min-api 36` passed |
| API compile matrix | every installed platform from API 34 through 37.2 passed |
| Sample APK signature | `apksigner verify` passed |
| Sample APK metadata | package `tw.mmunki.material.sample`, minSdk 21, targetSdk 36, launchable activity present |
| Native ABI matrix | not applicable, the library has no native code |

## Install

```groovy
dependencies {
    implementation files('libs/mmunki-material-2.0.0.aar')
}
```

## Source and verification

The release includes the full source archive and a SHA-256 checksum for every
asset. Build with `./build.sh` and run the full verification with `./test.sh`.

## License

MIT.
