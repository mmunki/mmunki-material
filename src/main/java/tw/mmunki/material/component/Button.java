package tw.mmunki.material.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MButtonDrawable;
import tw.mmunki.material.drawable.MRipple;

/**
 * Material 3 button. Variants map to the filled, tonal, outlined and text
 * button styles; the surface is drawn without gradients or transforms.
 */
public class Button extends TextView {

    public enum Variant {
        PRIMARY,
        TONAL,
        SECONDARY,
        GHOST,
        DANGER
    }

    private final MTheme theme;
    private final MButtonDrawable surface = new MButtonDrawable();
    private final MRipple ripple;
    private final Paint spinnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF spinnerRect = new RectF();

    private Variant variant = Variant.PRIMARY;
    private float cornerRadiusDp = -1f;
    private boolean loading;
    private boolean userEnabled = true;
    private int normalTextColor = 0xFFFFFFFF;
    private int spinnerColor = 0xFFFFFFFF;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        ripple = new MRipple(0x336750A4, surface, 0f);
        setBackground(ripple);
        setGravity(Gravity.CENTER);
        setClickable(true);
        setFocusable(true);
        setSingleLine(false);
        setMaxLines(2);
        setEllipsize(TextUtils.TruncateAt.END);
        setIncludeFontPadding(false);
        setAllCaps(false);
        setCompoundDrawablePadding(MDim.dpi(context, 8f));
        theme.typography.button.apply(this);
        setPadding(MDim.dpi(context, 24f), MDim.dpi(context, 10f),
                MDim.dpi(context, 24f), MDim.dpi(context, 10f));
        setMinHeight(MDim.dpi(context, 40f));
        setMinWidth(MDim.dpi(context, 64f));
        setClipToOutline(false);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = currentRadius();
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        spinnerPaint.setStyle(Paint.Style.STROKE);
        spinnerPaint.setStrokeCap(Paint.Cap.ROUND);
        spinnerPaint.setStrokeWidth(MDim.dp(context, 2.2f));
        applyColors();
    }

    public void setVariant(Variant variant) {
        if (variant == null) {
            variant = Variant.PRIMARY;
        }
        this.variant = variant;
        applyColors();
    }

    public Variant getVariant() {
        return variant;
    }

    public void setCornerRadiusDp(float radiusDp) {
        cornerRadiusDp = radiusDp;
        updateRadius();
    }

    public void setIcon(Drawable icon) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        super.setEnabled(userEnabled && !loading);
        applyColors();
        invalidate();
    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setEnabled(boolean enabled) {
        userEnabled = enabled;
        super.setEnabled(enabled && !loading);
        applyColors();
    }

    @Override
    public boolean performClick() {
        if (loading) {
            return false;
        }
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        return super.performClick();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        updateRadius();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!loading) {
            super.onDraw(canvas);
            return;
        }
        float centerX = getWidth() * 0.5f;
        float centerY = getHeight() * 0.5f;
        float radius = MDim.dp(getContext(), 9f);
        spinnerRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float phase = (SystemClock.uptimeMillis() % 900L) / 900f;
        spinnerPaint.setColor(spinnerColor);
        canvas.drawArc(spinnerRect, phase * 360f, 250f, false, spinnerPaint);
        postInvalidateOnAnimation();
    }

    private void updateRadius() {
        float radius = currentRadius();
        surface.setRadius(radius);
        ripple.setRadius(radius);
        invalidateOutline();
        invalidate();
    }

    private float currentRadius() {
        if (cornerRadiusDp >= 0f) {
            return MDim.dp(getContext(), cornerRadiusDp);
        }
        return MDim.dp(getContext(), theme.shapes.pill);
    }

    private void applyColors() {
        setAlpha(isEnabled() ? 1f : 0.38f);
        switch (variant) {
            case TONAL:
                surface.setColor(theme.palette.primaryContainer);
                surface.setStroke(0x00000000, 0f);
                normalTextColor = theme.palette.onPrimaryContainer;
                spinnerColor = theme.palette.onPrimaryContainer;
                ripple.setColor(ColorStateList.valueOf(
                        MaterialColor.withAlpha(theme.palette.onPrimaryContainer, 0.12f)));
                break;
            case SECONDARY:
                surface.setColor(0x00000000);
                surface.setStroke(theme.palette.separator, Math.max(1f, MDim.dp(getContext(), 1f)));
                normalTextColor = theme.palette.primary;
                spinnerColor = theme.palette.primary;
                ripple.setColor(ColorStateList.valueOf(
                        MaterialColor.withAlpha(theme.palette.primary, 0.12f)));
                break;
            case GHOST:
                surface.setColor(0x00000000);
                surface.setStroke(0x00000000, 0f);
                normalTextColor = theme.palette.primary;
                spinnerColor = theme.palette.primary;
                ripple.setColor(ColorStateList.valueOf(
                        MaterialColor.withAlpha(theme.palette.primary, 0.12f)));
                break;
            case DANGER:
                surface.setColor(theme.palette.danger);
                surface.setStroke(0x00000000, 0f);
                normalTextColor = MaterialColor.contrastRatio(0xFFFFFFFF, theme.palette.danger) >= 4.5
                        ? 0xFFFFFFFF : 0xFF0E0F16;
                spinnerColor = normalTextColor;
                ripple.setColor(ColorStateList.valueOf(
                        MaterialColor.withAlpha(normalTextColor, 0.12f)));
                break;
            case PRIMARY:
            default:
                surface.setColor(theme.palette.primary);
                surface.setStroke(0x00000000, 0f);
                normalTextColor = theme.palette.onPrimary;
                spinnerColor = theme.palette.onPrimary;
                ripple.setColor(ColorStateList.valueOf(
                        MaterialColor.withAlpha(theme.palette.onPrimary, 0.12f)));
                break;
        }
        setElevation(0f);
        setTextColor(loading ? 0x00FFFFFF : normalTextColor);
        invalidate();
    }
}
