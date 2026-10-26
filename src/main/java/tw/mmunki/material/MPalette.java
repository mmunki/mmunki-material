package tw.mmunki.material;

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
    public final int glass;
    public final int scrim;
    public final int gradientStart;
    public final int gradientEnd;

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
        glass = builder.glass;
        scrim = builder.scrim;
        gradientStart = builder.gradientStart;
        gradientEnd = builder.gradientEnd;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MPalette light() {
        return builder()
                .background(0xFFF4F5FA)
                .surface(0xFFFFFFFF)
                .surfaceElevated(0xFFFFFFFF)
                .surfaceSunken(0xFFECEEF6)
                .fill(0xFFE9EBF4)
                .label(0xFF11121A)
                .labelSecondary(0xFF5A5D70)
                .labelTertiary(0xFF6C6F82)
                .separator(0xFFD9DCE8)
                .primary(0xFF5A5CF5)
                .onPrimary(0xFFFFFFFF)
                .primaryContainer(0xFFE7E6FF)
                .onPrimaryContainer(0xFF2A2A6E)
                .secondary(0xFF0A7F75)
                .onSecondary(0xFFFFFFFF)
                .success(0xFF2FBF71)
                .warning(0xFFF5A623)
                .danger(0xFFD63C44)
                .glass(0xBFFFFFFF)
                .scrim(0xFF000000)
                .gradientStart(0xFF4B4BE0)
                .gradientEnd(0xFF087A72)
                .build();
    }

    public static MPalette dark() {
        return builder()
                .background(0xFF0B0C12)
                .surface(0xFF14151D)
                .surfaceElevated(0xFF1D1F29)
                .surfaceSunken(0xFF08090E)
                .fill(0xFF242633)
                .label(0xFFF6F7FB)
                .labelSecondary(0xFFA6A9BA)
                .labelTertiary(0xFF7E8194)
                .separator(0xFF2C2E3B)
                .primary(0xFF8B8CFF)
                .onPrimary(0xFF0E0F16)
                .primaryContainer(0xFF26265A)
                .onPrimaryContainer(0xFFD9D9FF)
                .secondary(0xFF3DDBCB)
                .onSecondary(0xFF062A27)
                .success(0xFF45D483)
                .warning(0xFFFFC15E)
                .danger(0xFFFF7A80)
                .glass(0xB31D1F29)
                .scrim(0xFF000000)
                .gradientStart(0xFF8B8CFF)
                .gradientEnd(0xFF3DDBCB)
                .build();
    }

    public static final class Builder {
        private int background = 0xFFF4F5FA;
        private int surface = 0xFFFFFFFF;
        private int surfaceElevated = 0xFFFFFFFF;
        private int surfaceSunken = 0xFFECEEF6;
        private int fill = 0xFFE9EBF4;
        private int label = 0xFF11121A;
        private int labelSecondary = 0xFF5A5D70;
        private int labelTertiary = 0xFF6C6F82;
        private int separator = 0xFFD9DCE8;
        private int primary = 0xFF5A5CF5;
        private int onPrimary = 0xFFFFFFFF;
        private int primaryContainer = 0xFFE7E6FF;
        private int onPrimaryContainer = 0xFF2A2A6E;
        private int secondary = 0xFF0A7F75;
        private int onSecondary = 0xFFFFFFFF;
        private int success = 0xFF2FBF71;
        private int warning = 0xFFF5A623;
        private int danger = 0xFFD63C44;
        private int glass = 0xBFFFFFFF;
        private int scrim = 0xFF000000;
        private int gradientStart = 0xFF4B4BE0;
        private int gradientEnd = 0xFF087A72;

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

        public Builder glass(int color) {
            glass = color;
            return this;
        }

        public Builder scrim(int color) {
            scrim = color;
            return this;
        }

        public Builder gradientStart(int color) {
            gradientStart = color;
            return this;
        }

        public Builder gradientEnd(int color) {
            gradientEnd = color;
            return this;
        }

        public MPalette build() {
            return new MPalette(this);
        }
    }
}
