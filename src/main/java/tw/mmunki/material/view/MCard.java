package tw.mmunki.material.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MCardDrawable;
import tw.mmunki.material.drawable.MRipple;
import tw.mmunki.material.motion.MSpring;

public class MCard extends FrameLayout {

    private final MTheme theme;
    private final MCardDrawable surface = new MCardDrawable();
    private final MSpring scaleSpring = new MSpring();

    private MRipple ripple;
    private boolean interactive;
    private float cornerRadiusDp = -1f;
    private float scale = 1f;

    public MCard(Context context) {
        this(context, null);
    }

    public MCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setWillNotDraw(false);
        setClipToOutline(true);
        setPadding(MDim.dpi(context, 20f), MDim.dpi(context, 20f),
                MDim.dpi(context, 20f), MDim.dpi(context, 20f));
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), currentRadius());
            }
        });
        applySurface();
        updateBackground();
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        setClickable(interactive);
        setFocusable(interactive);
        updateBackground();
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void setCornerRadiusDp(float radiusDp) {
        cornerRadiusDp = radiusDp;
        updateRadius();
    }

    public void setGlass(boolean glass) {
        surface.setFillColor(glass ? theme.palette.glass : theme.palette.surface);
        surface.setBorder(glass
                ? MaterialColor.withAlpha(theme.palette.label, 0.10f)
                : theme.palette.separator, Math.max(1f, MDim.dp(getContext(), 0.75f)));
        invalidate();
    }

    public void setSurfaceColor(int color) {
        surface.setFillColor(color);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!interactive || !isEnabled()) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                animateScale(0.985f);
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
    public boolean performClick() {
        if (interactive) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
        return super.performClick();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        updateRadius();
    }

    @Override
    protected void onDetachedFromWindow() {
        scaleSpring.cancel();
        super.onDetachedFromWindow();
    }

    private void applySurface() {
        surface.setFillColor(theme.palette.surface);
        surface.setBorder(theme.palette.separator, Math.max(1f, MDim.dp(getContext(), 0.75f)));
        surface.setRadius(currentRadius());
        setElevation(MDim.dp(getContext(), 1.5f));
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            setOutlineAmbientShadowColor(MaterialColor.withAlpha(theme.palette.label, 0.12f));
            setOutlineSpotShadowColor(MaterialColor.withAlpha(theme.palette.label, 0.18f));
        }
    }

    private void updateBackground() {
        if (interactive) {
            if (ripple == null) {
                ripple = new MRipple(ColorStateList.valueOf(
                        MaterialColor.withAlpha(theme.palette.primary, 0.10f)), surface, currentRadius());
            }
            setBackground(ripple);
        } else {
            setBackground(surface);
        }
        setPadding(MDim.dpi(getContext(), 20f), MDim.dpi(getContext(), 20f),
                MDim.dpi(getContext(), 20f), MDim.dpi(getContext(), 20f));
        updateRadius();
    }

    private void updateRadius() {
        float radius = currentRadius();
        surface.setRadius(radius);
        if (ripple != null) {
            ripple.setRadius(radius);
        }
        invalidateOutline();
        invalidate();
    }

    private float currentRadius() {
        if (cornerRadiusDp >= 0f) {
            return MDim.dp(getContext(), cornerRadiusDp);
        }
        return MDim.dp(getContext(), theme.shapes.large);
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
}
