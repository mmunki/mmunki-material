package tw.mmunki.material;

import android.content.Context;
import android.content.res.Configuration;

public final class MTheme {

    private static final MTheme LIGHT =
            new MTheme(MPalette.light(), MTypography.aurora(), MShapes.aurora(), MMotion.aurora());
    private static final MTheme DARK =
            new MTheme(MPalette.dark(), MTypography.aurora(), MShapes.aurora(), MMotion.aurora());

    private static volatile MTheme global;

    public final MPalette palette;
    public final MTypography typography;
    public final MShapes shapes;
    public final MMotion motion;

    public MTheme(MPalette palette, MTypography typography, MShapes shapes, MMotion motion) {
        if (palette == null || typography == null || shapes == null || motion == null) {
            throw new IllegalArgumentException("theme parts must not be null");
        }
        this.palette = palette;
        this.typography = typography;
        this.shapes = shapes;
        this.motion = motion;
    }

    public static MTheme get(Context context) {
        MTheme current = global;
        if (current != null) {
            return current;
        }
        return isDark(context) ? DARK : LIGHT;
    }

    public static boolean isDark(Context context) {
        int mode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static MTheme light() {
        return LIGHT;
    }

    public static MTheme dark() {
        return DARK;
    }

    public static MTheme auto(Context context) {
        return isDark(context) ? DARK : LIGHT;
    }

    public static void setDefault(MTheme theme) {
        global = theme;
    }

    public static void clearDefault() {
        global = null;
    }
}
