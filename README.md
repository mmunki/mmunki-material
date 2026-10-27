# Material Components for Android

`tw.mmunki.material` is a dependency-free Android view library that implements
the Material 3 color, type, shape and motion tokens together with a set of
components drawn entirely by the library.

There is no AndroidX dependency, no Material Components dependency and no
library resource ids. Colors, shapes and widgets are created in code.

- **Package:** `tw.mmunki.material`
- **Version:** `2.0.0`
- **Minimum Android:** 5.0 (API 21)
- **Compile SDK:** 36
- **Dependencies:** none
- **Resources:** none; every drawable is built in code

## What changed in 2.0.0

- Removed the aurora palette and all iOS-inspired naming and styling.
- The default theme is the Material 3 baseline light and dark scheme.
- The slider was rebuilt as a Material 3 slider: flat 4 dp track, 20 dp
  handle, optional tick marks for discrete steps, RTL layout, keyboard
  support and accessibility actions.
- Added `TopAppBar`, `BottomNavigationBar`, `FloatingNavigationBar`,
  `FloatingActionButton` and `RecyclerView`, each in its own file.
- Every component now has a plain, professional class name in the
  `tw.mmunki.material.component` package.
- The old `M*` classes in `tw.mmunki.material.view` are kept as deprecated
  source-compatible aliases.

## Artifacts

Every release contains:

| Artifact | Purpose |
| --- | --- |
| `mmunki-material-2.0.0.jar` | A jar with `classes.dex` at its root. |
| `mmunki-material-2.0.0-classes.jar` | The same API as JVM class files. |
| `mmunki-material-2.0.0.aar` | Android library archive with `classes.jar`. |
| `mmunki-material-2.0.0-sample.apk` | Signed demo application. |
| `mmunki-material-2.0.0-source.tar.gz` | Source archive for the tagged commit. |

Build output is published as release assets and is never committed to the
source tree.

## Install

### AAR

Copy `mmunki-material-2.0.0.aar` into `app/libs/` and add:

```groovy
dependencies {
    implementation files('libs/mmunki-material-2.0.0.aar')
}
```

### Classes jar

```groovy
dependencies {
    implementation files('libs/mmunki-material-2.0.0-classes.jar')
}
```

### Dex jar

The jar contains `classes.dex` at its root, so it can be handed to a dex
loader:

```java
DexClassLoader loader = new DexClassLoader(
        jarPath,
        getCodeCacheDir().getAbsolutePath(),
        null,
        getClassLoader());
Class<?> buttonClass = loader.loadClass("tw.mmunki.material.component.Button");
```

The dex is built with `--min-api 21` and uses dex format 035.

## Theme

```java
MTheme theme = MTheme.get(this);        // follows light/dark mode
MTheme.setDefault(MTheme.dark());      // force a theme for the process
MTheme.clearDefault();                 // return to the system theme
```

`MTheme` exposes `palette`, `typography`, `shapes` and `motion`. A custom
theme is a constructor call:

```java
MTheme custom = new MTheme(
        MPalette.builder()
                .primary(0xFF4F46E5)
                .background(0xFFFFFBFE)
                .build(),
        MTypography.standard(),
        MShapes.standard(),
        MMotion.standard());
```

The light and dark palettes use the Material 3 baseline roles. Text contrast
is covered by the test suite.

## Components

### Button

```java
Button button = new Button(this);
button.setText("Continue");
button.setVariant(Button.Variant.PRIMARY); // PRIMARY, TONAL, SECONDARY, GHOST, DANGER
button.setCornerRadiusDp(20f);
button.setIcon(getDrawable(R.drawable.ic_arrow));
button.setOnClickListener(v -> openNextScreen());
button.setLoading(true);
```

Variants map to the filled, tonal, outlined and text button styles. There is
no gradient and no press transform; state is communicated with the Material
ripple and state layer.

### Card

```java
Card card = new Card(this);
card.setInteractive(true);
card.setElevated(true);
card.setCornerRadiusDp(16f);
card.addView(content);
```

An outlined card is the default. `setElevated(true)` uses a filled surface
with a small elevation.

### Slider

```java
Slider slider = new Slider(this);
slider.setRange(0f, 100f);
slider.setStepSize(10f);      // discrete steps also draw tick marks
slider.setValue(40f, true);
slider.setOnValueChangeListener((view, value, fromUser) -> setVolume(value));
```

The slider has a flat track, a 20 dp handle, optional tick marks, correct RTL
mapping, keyboard arrow support and accessibility scroll actions.

### Switch

```java
Switch toggle = new Switch(this);
toggle.setChecked(true, false);
toggle.setOnCheckedChangeListener((view, checked) -> apply(checked));
```

### Text field

```java
TextField field = new TextField(this);
field.setLabel("Email address");
field.setInputType(InputType.TYPE_CLASS_TEXT
        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
field.setError("Enter a complete email address");
field.clearError();
```

### Segmented control

```java
SegmentedControl segments = new SegmentedControl(this);
segments.setItems("Day", "Week", "Month");
segments.setSelectedIndex(0, false);
segments.setOnSelectionChangedListener((view, index) -> load(index));
```

### Progress

```java
LinearProgressIndicator bar = new LinearProgressIndicator(this);
bar.setProgress(0.62f);
bar.setProgressImmediate(0.62f);

CircularProgressIndicator ring = new CircularProgressIndicator(this);
ring.setProgress(0.72f);
ring.setIndeterminate(true);
```

### Top app bar

```java
TopAppBar appBar = new TopAppBar(this);
appBar.setTitle("Inbox");
appBar.setSubtitle("12 unread");
appBar.setNavigationIcon(getDrawable(R.drawable.ic_menu));
appBar.setNavigationOnClickListener(v -> openDrawer());
appBar.addAction(getDrawable(R.drawable.ic_search), v -> search());
appBar.onScroll(scrollY); // raises the bar once the content scrolls
```

### Bottom navigation bar

```java
BottomNavigationBar navigation = new BottomNavigationBar(this);
navigation.addItem("Home", getDrawable(R.drawable.ic_home));
navigation.addItem("Inbox", getDrawable(R.drawable.ic_inbox)).setBadge(3);
navigation.addItem("Settings", getDrawable(R.drawable.ic_settings));
navigation.setSelectedItem(0, false);
navigation.setOnItemSelectedListener((bar, index) -> show(index));
```

### Floating navigation bar

```java
FloatingNavigationBar navigation = new FloatingNavigationBar(this);
navigation.addItem("Inbox", getDrawable(R.drawable.ic_inbox));
navigation.addItem("Search", getDrawable(R.drawable.ic_search));
navigation.addItem("Account", getDrawable(R.drawable.ic_person));
```

A pill-shaped, raised variant of the bottom navigation bar with a 12 dp inset
from the edges.

### Floating action button

```java
FloatingActionButton fab = new FloatingActionButton(this);
fab.setImageDrawable(getDrawable(R.drawable.ic_add));
fab.setOnClickListener(v -> compose());

fab.setText("Compose"); // extended FAB
fab.setExtended(true);
```

### Recycler view

```java
RecyclerView recycler = new RecyclerView(this);
recycler.setLayoutManager(new RecyclerView.LinearLayoutManager(this));
recycler.setItemSpacingDp(8f);
recycler.setAdapter(new Adapter<Holder>() { /* ... */ });
```

The recycler view is a small, dependency-free linear list with a holder
adapter, a scrap pool, drag scrolling and fling. It supports vertical and
horizontal orientation.

### List section

```java
ListSection section = new ListSection(this);
ListCell profile = section.addCell("Profile", "Mmunki");
profile.setLeadingIcon(getDrawable(R.drawable.ic_person));
profile.setChevronVisible(true);
```

### Bottom sheet

```java
BottomSheet sheet = new BottomSheet(this);
sheet.setContent(content);
sheet.show();
```

## Motion

`MMotion` provides named springs and easing curves. Springs are solved in
`SpringSolver` with a fixed 1 ms integration step, so the result is frame-rate
independent. The standard tokens are critically damped or close to it and are
used for finite transitions:

| Field | Character |
| --- | --- |
| `press` | quick state response |
| `snappy` | fast UI transitions |
| `settle` | default settle |
| `page` | page and sheet transitions |
| `expressive` | slight overshoot for expressive moments |
| `gentle` | long, calm settle |

`Easing` provides the Material standard, emphasized, decelerate, accelerate,
linear-out-slow-in and fast-out-slow-in curves.

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

- Pure JVM tests: spring settling, critical and expressive springs,
  frame-rate independence, retargeting, easing endpoints and monotonicity,
  color blending, alpha compositing, contrast ratio, shape clamping and
  palette contrast.
- Dex jar: `classes.dex` at the root, no generated `R` references.
- DEX min-api matrix: `--min-api 21` and `--min-api 36`.
- API compile matrix: every installed `android.jar` from API 34 through 37.2.
- Sample APK: signed, package `tw.mmunki.material.sample`, minSdk 21,
  targetSdk 36, launchable activity present.
- There are no native libraries, so there is no ABI matrix.

## Repository layout

```
src/main/java/tw/mmunki/material/            theme, color, motion, animations
src/main/java/tw/mmunki/material/component/  public components
src/main/java/tw/mmunki/material/drawable/   flat surfaces and ripple
src/main/java/tw/mmunki/material/motion/     spring solver and easing
src/main/java/tw/mmunki/material/view/       deprecated M* aliases
src/test/java/tw/mmunki/material/            pure JVM tests
sample/                                      demo application
build.sh                                     library, dex jar, AAR, sample APK
test.sh                                      tests and artifact verification
.github/workflows/ci.yml                     build and test on GitHub Actions
```

The `view.M*` classes are kept only so existing code can migrate one import at
a time. New code should use `tw.mmunki.material.component`.

## License

MIT. See [LICENSE](LICENSE).
