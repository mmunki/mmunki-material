# Aurora Material for Android

`tw.mmunki.material` is a dependency-free Android view library with a calm,
iOS-inspired motion model and an original aurora palette. It is not a fork of
Miuix; it is a small, self-contained material system in the same spirit: one
theme, one set of shapes, one motion language, and controls that are drawn
entirely by the library.

- **Package:** `tw.mmunki.material`
- **Version:** `1.0.0`
- **Minimum Android:** 5.0 (API 21)
- **Compile SDK:** 36
- **Dependencies:** none (no AndroidX, no external libraries)
- **Resources:** the library has no resource ids; every color, shape and
  drawable is created in code.

## Artifacts

The release contains:

| Artifact | Purpose |
| --- | --- |
| `mmunki-material-1.0.0.jar` | A jar with `classes.dex` at its root. Use this when the issue or the loader asks for a dex jar. |
| `mmunki-material-1.0.0-classes.jar` | The same API as normal JVM class files. Useful for a classpath or for building an AAR yourself. |
| `mmunki-material-1.0.0.aar` | Android library archive with `classes.jar` and a library manifest. |
| `mmunki-material-1.0.0-sample.apk` | Signed demo application that exercises every component. |
| `mmunki-material-1.0.0-source.tar.gz` | Source archive for the tagged commit. |

The `.jar`, `.aar` and `.apk` are build outputs. They are published as release
assets and are never committed to the source tree.

## Install

### AAR

Copy `mmunki-material-1.0.0.aar` into `app/libs/` and add:

```groovy
dependencies {
    implementation files('libs/mmunki-material-1.0.0.aar')
}
```

### Classes jar

```groovy
dependencies {
    implementation files('libs/mmunki-material-1.0.0-classes.jar')
}
```

### Dex jar

The jar contains `classes.dex`, so it can be handed directly to a dex loader:

```java
DexClassLoader loader = new DexClassLoader(
        jarPath,
        getCodeCacheDir().getAbsolutePath(),
        null,
        getClassLoader());
Class<?> buttonClass = loader.loadClass("tw.mmunki.material.view.MButton");
```

The dex is built with `--min-api 21` and uses dex format 035.

## Theme

```java
MTheme theme = MTheme.get(this);        // system light/dark
MTheme.setDefault(MTheme.dark());      // force a theme for the process
MTheme.clearDefault();                 // return to the system theme
```

`MTheme` exposes `palette`, `typography`, `shapes` and `motion`. A custom theme
is just a constructor call:

```java
MTheme custom = new MTheme(
        MPalette.builder()
                .primary(0xFF4F46E5)
                .background(0xFFF7F7FB)
                .build(),
        MTypography.aurora(),
        MShapes.aurora(),
        MMotion.aurora());
```

The default palettes are checked for text contrast against their backgrounds
in the test suite. `MTheme.get` returns the process-wide default when one is
set, otherwise it follows `Configuration.UI_MODE_NIGHT_YES`.

## Components

### Button

```java
MButton button = new MButton(this);
button.setText("Continue");
button.setVariant(MButton.Variant.PRIMARY); // PRIMARY, SECONDARY, TONAL, GHOST, DANGER
button.setCornerRadiusDp(18f);
button.setIcon(getDrawable(R.drawable.ic_arrow));
button.setOnClickListener(v -> openNextScreen());
```

Variants: `PRIMARY`, `SECONDARY`, `TONAL`, `GHOST`, `DANGER`. Buttons use a
spring press scale, a ripple that follows the pointer, and a built-in loading
state:

```java
button.setLoading(true);
```

### Card

```java
MCard card = new MCard(this);
card.setInteractive(true);
card.setGlass(true);
card.setCornerRadiusDp(26f);
card.addView(content);
```

A card can be a plain surface or an interactive, pressable surface. Glass mode
uses a translucent surface and a soft border.

### Switch

```java
MSwitch toggle = new MSwitch(this);
toggle.setChecked(true, false);
toggle.setOnCheckedChangeListener((view, checked) -> apply(checked));
```

The switch supports tap and horizontal drag, a spring thumb, and exposes its
checked state to accessibility services.

### Slider

```java
MSlider slider = new MSlider(this);
slider.setRange(0f, 1f);
slider.setStepSize(0.05f);
slider.setValue(0.4f, true);
slider.setOnValueChangeListener((view, value) -> setVolume(value));
```

### Text field

```java
MTextField field = new MTextField(this);
field.setLabel("Email address");
field.setInputType(InputType.TYPE_CLASS_TEXT
        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
field.setError("Enter a complete email address");
field.clearError();
```

The label floats with a spring when the field gains focus or has text. The
outline, fill and label color follow the focus progress.

### Grouped list

```java
MListSection section = new MListSection(this);
MListCell profile = section.addCell("Profile", "Mmunki");
profile.setLeadingIcon(getDrawable(R.drawable.ic_person));
profile.setChevronVisible(true);

MListCell notifications = section.addCell("Notifications", "Immediate");
notifications.setTrailingText("On");
notifications.setChevronVisible(false);
```

Cells can carry a leading icon, trailing text, a chevron, or a custom accessory
view. The section owns the rounded background and clips the first and last
cell corners.

### Segmented control

```java
MSegmentedControl segments = new MSegmentedControl(this);
segments.setItems("Day", "Week", "Month");
segments.setSelectedIndex(0, false);
segments.setOnSelectionChangedListener((view, index) -> load(index));
```

### Progress

```java
MProgressBar bar = new MProgressBar(this);
bar.setProgress(0.62f);          // animated
bar.setProgressImmediate(0.62f); // no animation

MCircularProgress ring = new MCircularProgress(this);
ring.setProgress(0.72f);
ring.setIndeterminate(true);
```

### Bottom sheet

```java
MBottomSheet sheet = new MBottomSheet(this);
sheet.setContent(content);
sheet.show();

// or
MBottomSheet.show(this, content);
```

The sheet follows a downward drag on its handle and hands the remaining
distance to a spring. It animates the window dim and can be dismissed with the
back button, the scrim, or `dismiss()`.

## Motion

`MMotion` provides named springs:

| Field | Character |
| --- | --- |
| `press` | quick, slightly bouncy press response |
| `snappy` | fast UI transitions |
| `settle` | default settle |
| `page` | slower page transitions |
| `bouncy` | expressive overshoot |
| `gentle` | long, calm settle |

Springs are solved in `SpringSolver` with a fixed 1 ms integration step, so the
result is frame-rate independent. `MSpring` can drive any float value:

```java
new MSpring().start(0f, 1f, MTheme.get(this).motion.settle,
        new MSpring.Listener() {
            @Override public void onUpdate(float value) {
                view.setAlpha(value);
            }
            @Override public void onEnd() {
                view.setAlpha(1f);
            }
        });
```

`Easing` includes standard, emphasized, decelerate, accelerate and an
iOS-style curve. `MAnimations.fadeUp` and `MAnimations.stagger` provide simple
entrance animations for groups.

## Build and test

The build uses the Android SDK command line tools directly; Gradle is not
required.

```bash
export ANDROID_HOME=/path/to/android-sdk
./build.sh
./test.sh
```

`build.sh` compiles the library against `platforms/android-36/android.jar`,
builds the classes jar, the dex jar, the AAR and the signed sample APK, and
prints the SHA-256 of every artifact. `test.sh` runs the pure JVM tests and
checks the produced jars, dex, AAR and APK.

Test coverage:

- 14 pure JVM tests: spring settling, critical and bouncy springs, frame-rate
  independence, retargeting, easing endpoints and monotonicity, color blending,
  alpha compositing, contrast ratio, shape clamping, and palette contrast.
- Dex jar: `classes.dex` at the root, 65 classes, no generated `R` references.
- DEX min-api matrix: `--min-api 21` and `--min-api 36`.
- API compile matrix: every installed `android.jar` from API 34 through 37.2,
  including extension and beta platforms.
- Sample APK: signed, package `tw.mmunki.material.sample`, minSdk 21,
  targetSdk 36, launchable activity present.
- There are no native libraries, so there is no ABI matrix.

## Repository layout

```
src/main/java/tw/mmunki/material/       theme, color, motion, animations
src/main/java/tw/mmunki/material/drawable/
src/main/java/tw/mmunki/material/view/  buttons, cards, fields, lists, progress
src/test/java/tw/mmunki/material/       pure JVM tests
sample/                                  demo application
build.sh                                 library, dex jar, AAR, sample APK
test.sh                                  tests and artifact verification
.github/workflows/ci.yml                 build and test on GitHub Actions
```

## License

MIT. See [LICENSE](LICENSE).
