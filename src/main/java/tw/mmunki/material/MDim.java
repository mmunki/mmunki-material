package tw.mmunki.material;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

public final class MDim {

    private MDim() {
    }

    public static float dp(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float dp(Resources resources, float dp) {
        return dp * resources.getDisplayMetrics().density;
    }

    public static int dpi(Context context, float value) {
        return Math.round(dp(context, value));
    }

    public static int dpi(Resources resources, float value) {
        return Math.round(dp(resources, value));
    }

    public static float sp(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public static float pxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int screenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isRtl(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection()
                == View.LAYOUT_DIRECTION_RTL;
    }

    public static int statusBarHeight(Context context) {
        int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return id > 0 ? context.getResources().getDimensionPixelSize(id) : 0;
    }

    public static int navigationBarHeight(Context context) {
        int id = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return id > 0 ? context.getResources().getDimensionPixelSize(id) : 0;
    }
}
