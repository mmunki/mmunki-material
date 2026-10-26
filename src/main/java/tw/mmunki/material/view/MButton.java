package tw.mmunki.material.view;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MButtonDrawable;
import tw.mmunki.material.drawable.MRipple;
import tw.mmunki.material.motion.MSpring;

public class MButton extends TextView {

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
    private final MSpring scaleSpring = new MSpring();
    private final Paint spinnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF spinnerRect = new RectF();

    private Variant variant = Variant.PRIMARY;
    private float cornerRadiusDp = -1f;
    private float scale = 1f;
    private boolean loading;
    private boolean userEnabled = true;
    private int normalTextColor = 0xFFFFFFFF;
    private int spinnerColor = 0xFFFFFFFF;

    public MButton(Context context) {
        this(context, null);
    }

    public MButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        ripple = new MRipple(0x33FFFFFF, surface, 0f);
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
        setPadding(MDim.dpi(context, 22f), MDim.dpi(context, 12f),
                MDim.dpi(context, 22f), MDim.dpi(context, 12f));
        setMinHeight(MDim.dpi(context, 50f));
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
        spinnerPaint.setStrokeWidth(MDim.dp(context, 2.4f));
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
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || loading) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                animateScale(0.96f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                animateScale(1f);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
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

    @Override
    protected void onDetachedFromWindow() {
        scaleSpring.cancel();
        super.onDetachedFromWindow();
    }

    private void animateScale(float target) {
        scaleSpring.start(scale, target, theme.motion.press, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                scale = value;
                setScaleX(value);
                setScaleY(value);
            }

            @Override
            public void onEnd() {
                scale = target;
                setScaleX(target);
                setScaleY(target);
            }
        });
    }

    private void updateRadius() {
        float radius = currentRadius();
        surface.setRadius(radius);
        ripple.setRadius(radius);
        invalidateOutline();
        invalidate();
    }

    private float currentRadius() {
        float height = getHeight();
        if (cornerRadiusDp >= 0f) {
            return MDim.dp(getContext(), cornerRadiusDp);
        }
        return height > 0f ? height * 0.5f : MDim.dp(getContext(), 25f);
    }

    private void applyColors() {
        setAlpha(isEnabled() ? 1f : 0.45f);
        switch (variant) {
            case TONAL:
                surface.setColors(theme.palette.primaryContainer, theme.palette.primaryContainer, false);
                surface.setStroke(0x00000000, 0f);
                surface.setShowHighlight(false);
                normalTextColor = theme.palette.onPrimaryContainer;
                spinnerColor = theme.palette.onPrimaryContainer;
                ripple.setColor(ColorStateList.valueOf(MaterialColor.withAlpha(theme.palette.primary, 0.18f)));
                setElevation(MDim.dp(getContext(), 3f));
                break;
            case SECONDARY:
                surface.setColors(theme.palette.surface, theme.palette.surface, false);
                surface.setStroke(theme.palette.separator, Math.max(1f, MDim.dp(getContext(), 0.75f)));
                surface.setShowHighlight(false);
                normalTextColor = theme.palette.primary;
                spinnerColor = theme.palette.primary;
                ripple.setColor(ColorStateList.valueOf(MaterialColor.withAlpha(theme.palette.primary, 0.14f)));
                setElevation(MDim.dp(getContext(), 2f));
                break;
            case GHOST:
                surface.setColors(0x00000000, 0x00000000, false);
                surface.setStroke(0x00000000, 0f);
                surface.setShowHighlight(false);
                normalTextColor = theme.palette.primary;
                spinnerColor = theme.palette.primary;
                ripple.setColor(ColorStateList.valueOf(MaterialColor.withAlpha(theme.palette.primary, 0.12f)));
                setElevation(0f);
                break;
            case DANGER:
                surface.setColors(theme.palette.danger, theme.palette.danger, false);
                surface.setStroke(0x00000000, 0f);
                surface.setShowHighlight(true);
                normalTextColor = MaterialColor.contrastRatio(0xFFFFFFFF, theme.palette.danger) >= 4.5
                        ? 0xFFFFFFFF : 0xFF0E0F16;
                spinnerColor = normalTextColor;
                ripple.setColor(ColorStateList.valueOf(
                        MaterialColor.luminance(theme.palette.danger) > 0.25f
                                ? 0x22000000 : 0x33FFFFFF));
                setElevation(MDim.dp(getContext(), 8f));
                break;
            case PRIMARY:
            default:
                surface.setColors(theme.palette.gradientStart, theme.palette.gradientEnd, true);
                surface.setStroke(0x00000000, 0f);
                surface.setShowHighlight(true);
                normalTextColor = theme.palette.onPrimary;
                spinnerColor = theme.palette.onPrimary;
                ripple.setColor(ColorStateList.valueOf(0x33FFFFFF));
                setElevation(MDim.dp(getContext(), 10f));
                break;
        }
        setTextColor(loading ? 0x00FFFFFF : normalTextColor);
        invalidate();
    }
}
