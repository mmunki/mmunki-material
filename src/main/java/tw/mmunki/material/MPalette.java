package tw.mmunki.material;

/**
 * Material 3 color roles used by the library.
 *
 * <p>The palettes follow the Material 3 baseline light and dark schemes. They
 * are plain color values so the library does not need Android resources or a
 * dependency on Material Components.</p>
 */
public final class MPalette {

    public final int background;
    public final int surface;
    public final int surfaceElevated;
    public final int surfaceSunken;
    public final int fill;
    public final int label;
    public final int labelSecondary;
    public final int labelTertiary;
    public final int separator;
    public final int primary;
    public final int onPrimary;
    public final int primaryContainer;
    public final int onPrimaryContainer;
    public final int secondary;
    public final int onSecondary;
    public final int success;
    public final int warning;
    public final int danger;
    public final int scrim;

    private MPalette(Builder builder) {
        background = builder.background;
        surface = builder.surface;
        surfaceElevated = builder.surfaceElevated;
        surfaceSunken = builder.surfaceSunken;
        fill = builder.fill;
        label = builder.label;
        labelSecondary = builder.labelSecondary;
        labelTertiary = builder.labelTertiary;
        separator = builder.separator;
        primary = builder.primary;
        onPrimary = builder.onPrimary;
        primaryContainer = builder.primaryContainer;
        onPrimaryContainer = builder.onPrimaryContainer;
        secondary = builder.secondary;
        onSecondary = builder.onSecondary;
        success = builder.success;
        warning = builder.warning;
        danger = builder.danger;
        scrim = builder.scrim;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MPalette light() {
        return builder()
                .background(0xFFFFFBFE)
                .surface(0xFFFFFBFE)
                .surfaceElevated(0xFFF3EDF7)
                .surfaceSunken(0xFFECE6F0)
                .fill(0xFFE6E0E9)
                .label(0xFF1C1B1F)
                .labelSecondary(0xFF49454F)
                .labelTertiary(0xFF6F6A75)
                .separator(0xFFCAC4D0)
                .primary(0xFF6750A4)
                .onPrimary(0xFFFFFFFF)
                .primaryContainer(0xFFEADDFF)
                .onPrimaryContainer(0xFF21005D)
                .secondary(0xFF625B71)
                .onSecondary(0xFFFFFFFF)
                .success(0xFF2E6B34)
                .warning(0xFF8A5300)
                .danger(0xFFB3261E)
                .scrim(0xFF000000)
                .build();
    }

    public static MPalette dark() {
        return builder()
                .background(0xFF141218)
                .surface(0xFF1C1B1F)
                .surfaceElevated(0xFF2B2930)
                .surfaceSunken(0xFF0F0D13)
                .fill(0xFF36343B)
                .label(0xFFE6E0E9)
                .labelSecondary(0xFFCAC4D0)
                .labelTertiary(0xFF938F99)
                .separator(0xFF49454F)
                .primary(0xFFD0BCFF)
                .onPrimary(0xFF381E72)
                .primaryContainer(0xFF4F378B)
                .onPrimaryContainer(0xFFEADDFF)
                .secondary(0xFFCCC2DC)
                .onSecondary(0xFF332D41)
                .success(0xFF8FD99A)
                .warning(0xFFFFB95C)
                .danger(0xFFFFB4AB)
                .scrim(0xFF000000)
                .build();
    }

    public static final class Builder {
        private int background = 0xFFFFFBFE;
        private int surface = 0xFFFFFBFE;
        private int surfaceElevated = 0xFFF3EDF7;
        private int surfaceSunken = 0xFFECE6F0;
        private int fill = 0xFFE6E0E9;
        private int label = 0xFF1C1B1F;
        private int labelSecondary = 0xFF49454F;
        private int labelTertiary = 0xFF6F6A75;
        private int separator = 0xFFCAC4D0;
        private int primary = 0xFF6750A4;
        private int onPrimary = 0xFFFFFFFF;
        private int primaryContainer = 0xFFEADDFF;
        private int onPrimaryContainer = 0xFF21005D;
        private int secondary = 0xFF625B71;
        private int onSecondary = 0xFFFFFFFF;
        private int success = 0xFF2E6B34;
        private int warning = 0xFF8A5300;
        private int danger = 0xFFB3261E;
        private int scrim = 0xFF000000;

        public Builder background(int color) {
            background = color;
            return this;
        }

        public Builder surface(int color) {
            surface = color;
            return this;
        }

        public Builder surfaceElevated(int color) {
            surfaceElevated = color;
            return this;
        }

        public Builder surfaceSunken(int color) {
            surfaceSunken = color;
            return this;
        }

        public Builder fill(int color) {
            fill = color;
            return this;
        }

        public Builder label(int color) {
            label = color;
            return this;
        }

        public Builder labelSecondary(int color) {
            labelSecondary = color;
            return this;
        }

        public Builder labelTertiary(int color) {
            labelTertiary = color;
            return this;
        }

        public Builder separator(int color) {
            separator = color;
            return this;
        }

        public Builder primary(int color) {
            primary = color;
            return this;
        }

        public Builder onPrimary(int color) {
            onPrimary = color;
            return this;
        }

        public Builder primaryContainer(int color) {
            primaryContainer = color;
            return this;
        }

        public Builder onPrimaryContainer(int color) {
            onPrimaryContainer = color;
            return this;
        }

        public Builder secondary(int color) {
            secondary = color;
            return this;
        }

        public Builder onSecondary(int color) {
            onSecondary = color;
            return this;
        }

        public Builder success(int color) {
            success = color;
            return this;
        }

        public Builder warning(int color) {
            warning = color;
            return this;
        }

        public Builder danger(int color) {
            danger = color;
            return this;
        }

        public Builder scrim(int color) {
            scrim = color;
            return this;
        }

        public MPalette build() {
            return new MPalette(this);
        }
    }
}
