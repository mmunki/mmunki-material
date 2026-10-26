package tw.mmunki.material;

public final class MaterialColor {

    private MaterialColor() {
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (clamp(alpha, 0, 255) << 24)
                | (clamp(red, 0, 255) << 16)
                | (clamp(green, 0, 255) << 8)
                | clamp(blue, 0, 255);
    }

    public static int alpha(int color) {
        return (color >>> 24) & 0xFF;
    }

    public static int red(int color) {
        return (color >>> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >>> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int withAlpha(int color, int alpha) {
        return (clamp(alpha, 0, 255) << 24) | (color & 0x00FFFFFF);
    }

    public static int withAlpha(int color, float alpha) {
        return withAlpha(color, Math.round(clamp(alpha, 0f, 1f) * 255f));
    }

    public static int opaque(int color) {
        return color | 0xFF000000;
    }

    public static int blend(int from, int to, float fraction) {
        float t = clamp(fraction, 0f, 1f);
        int a = Math.round(alpha(from) + (alpha(to) - alpha(from)) * t);
        int r = Math.round(red(from) + (red(to) - red(from)) * t);
        int g = Math.round(green(from) + (green(to) - green(from)) * t);
        int b = Math.round(blue(from) + (blue(to) - blue(from)) * t);
        return argb(a, r, g, b);
    }

    public static int blendOver(int source, int destination) {
        float sa = alpha(source) / 255f;
        float da = alpha(destination) / 255f;
        float outA = sa + da * (1f - sa);
        if (outA <= 0f) {
            return 0;
        }
        int r = Math.round((red(source) * sa + red(destination) * da * (1f - sa)) / outA);
        int g = Math.round((green(source) * sa + green(destination) * da * (1f - sa)) / outA);
        int b = Math.round((blue(source) * sa + blue(destination) * da * (1f - sa)) / outA);
        return argb(Math.round(outA * 255f), r, g, b);
    }

    public static int lighten(int color, float amount) {
        return blend(color, opaque(0xFFFFFFFF), amount);
    }

    public static int darken(int color, float amount) {
        return blend(color, opaque(0xFF000000), amount);
    }

    public static double luminance(int color) {
        double r = linear(red(color) / 255.0);
        double g = linear(green(color) / 255.0);
        double b = linear(blue(color) / 255.0);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    public static double contrastRatio(int first, int second) {
        double a = luminance(first);
        double b = luminance(second);
        double lighter = Math.max(a, b);
        double darker = Math.min(a, b);
        return (lighter + 0.05) / (darker + 0.05);
    }

    public static boolean isDark(int color) {
        return luminance(color) < 0.5;
    }

    public static int ensureContrast(int foreground, int background, double minimum) {
        if (contrastRatio(foreground, background) >= minimum) {
            return foreground;
        }
        boolean goLight = luminance(background) < 0.5;
        int result = foreground;
        for (int i = 0; i < 20; i++) {
            result = goLight ? lighten(result, 0.12f) : darken(result, 0.12f);
            if (contrastRatio(result, background) >= minimum) {
                return result;
            }
        }
        return goLight ? 0xFFFFFFFF : 0xFF000000;
    }

    public static int compositeOver(int foreground, int background) {
        return blendOver(foreground, background);
    }

    private static double linear(double channel) {
        if (channel <= 0.04045) {
            return channel / 12.92;
        }
        return Math.pow((channel + 0.055) / 1.055, 2.4);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }

    private static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }
}
