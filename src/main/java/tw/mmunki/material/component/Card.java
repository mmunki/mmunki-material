package tw.mmunki.material.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MCardDrawable;
import tw.mmunki.material.drawable.MRipple;

/**
 * Material 3 card. A card is an outlined surface by default; call
 * {@link #setElevated(boolean)} for a filled, raised surface.
 */
public class Card extends FrameLayout {

    private final MTheme theme;
    private final MCardDrawable surface = new MCardDrawable();

    private MRipple ripple;
    private boolean interactive;
    private boolean elevated;
    private float cornerRadiusDp = -1f;

    public Card(Context context) {
        this(context, null);
    }

    public Card(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Card(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setWillNotDraw(false);
        setClipToOutline(true);
        setPadding(MDim.dpi(context, 16f), MDim.dpi(context, 16f),
                MDim.dpi(context, 16f), MDim.dpi(context, 16f));
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

    public void setElevated(boolean elevated) {
        this.elevated = elevated;
        applySurface();
    }

    public boolean isElevated() {
        return elevated;
    }

    public void setCornerRadiusDp(float radiusDp) {
        cornerRadiusDp = radiusDp;
        updateRadius();
    }

    public void setSurfaceColor(int color) {
        surface.setFillColor(color);
        invalidate();
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

    private void applySurface() {
        if (elevated) {
            surface.setFillColor(theme.palette.surfaceElevated);
            surface.setBorder(0x00000000, 0f);
            setElevation(MDim.dp(getContext(), 2f));
        } else {
            surface.setFillColor(theme.palette.surface);
            surface.setBorder(theme.palette.separator, Math.max(1f, MDim.dp(getContext(), 1f)));
            setElevation(0f);
        }
        surface.setRadius(currentRadius());
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            setOutlineAmbientShadowColor(MaterialColor.withAlpha(theme.palette.label, 0.10f));
            setOutlineSpotShadowColor(MaterialColor.withAlpha(theme.palette.label, 0.16f));
        }
        invalidate();
    }

    private void updateBackground() {
        if (interactive) {
            if (ripple == null) {
                ripple = new MRipple(ColorStateList.valueOf(
                        MaterialColor.withAlpha(theme.palette.primary, 0.10f)), surface,
                        currentRadius());
            }
            setBackground(ripple);
        } else {
            setBackground(surface);
        }
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
}
