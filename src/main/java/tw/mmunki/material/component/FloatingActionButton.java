package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewOutlineProvider;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MRipple;

/**
 * Material 3 floating action button. Call {@link #setText(CharSequence)} to
 * turn it into an extended FAB, or just set an icon for the regular 56 dp
 * shape.
 */
public class FloatingActionButton extends View {

    private final MTheme theme;
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF shapeRect = new RectF();
    private final MRipple ripple;

    private Drawable icon;
    private CharSequence text;
    private int containerColor;
    private int contentColor;
    private boolean extended;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        containerColor = theme.palette.primaryContainer;
        contentColor = theme.palette.onPrimaryContainer;
        textPaint.setTextSize(MDim.sp(context, 14f));
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textPaint.setTextAlign(Paint.Align.LEFT);
        setClickable(true);
        setFocusable(true);
        setElevation(MDim.dp(context, 6f));
        setClipToOutline(false);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(),
                        cornerRadius());
            }
        });
        ripple = new MRipple(MaterialColor.withAlpha(contentColor, 0.16f), null, cornerRadius());
        setBackground(ripple);
    }

    public void setImageDrawable(Drawable drawable) {
        icon = drawable;
        extended = text != null && text.length() > 0;
        requestLayout();
        invalidate();
    }

    public void setIcon(Drawable drawable) {
        setImageDrawable(drawable);
    }

    public void setText(CharSequence text) {
        this.text = text;
        extended = text != null && text.length() > 0;
        requestLayout();
        invalidate();
    }

    public CharSequence getText() {
        return text;
    }

    public void setExtended(boolean extended) {
        this.extended = extended && text != null && text.length() > 0;
        requestLayout();
        invalidate();
    }

    public boolean isExtended() {
        return extended;
    }

    public void setContainerColor(int color) {
        containerColor = color;
        ripple.setColor(android.content.res.ColorStateList.valueOf(
                MaterialColor.withAlpha(contentColor, 0.16f)));
        invalidate();
    }

    public void setContentColor(int color) {
        contentColor = color;
        ripple.setColor(android.content.res.ColorStateList.valueOf(
                MaterialColor.withAlpha(contentColor, 0.16f)));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MDim.dpi(getContext(), 56f) + getPaddingTop() + getPaddingBottom();
        int width;
        if (extended && !TextUtils.isEmpty(text)) {
            float textWidth = textPaint.measureText(text.toString());
            float iconWidth = icon != null ? MDim.dp(getContext(), 24f) : 0f;
            float gap = icon != null ? MDim.dp(getContext(), 12f) : 0f;
            width = Math.round(MDim.dp(getContext(), 20f) * 2f + iconWidth + gap + textWidth)
                    + getPaddingLeft() + getPaddingRight();
            width = Math.max(width, MDim.dpi(getContext(), 80f));
        } else {
            width = MDim.dpi(getContext(), 56f) + getPaddingLeft() + getPaddingRight();
        }
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        ripple.setRadius(cornerRadius());
        invalidateOutline();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        shapeRect.set(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        float radius = cornerRadius();
        backgroundPaint.setColor(isEnabled() ? containerColor : theme.palette.fill);
        canvas.drawRoundRect(shapeRect, radius, radius, backgroundPaint);

        float iconSize = MDim.dp(getContext(), 24f);
        float centerY = shapeRect.centerY();
        float iconLeft;
        float textBaseline = centerY - (textPaint.getFontMetrics().ascent
                + textPaint.getFontMetrics().descent) * 0.5f;

        if (extended && !TextUtils.isEmpty(text)) {
            float textWidth = textPaint.measureText(text.toString());
            float iconWidth = icon != null ? iconSize : 0f;
            float gap = icon != null ? MDim.dp(getContext(), 12f) : 0f;
            float contentWidth = iconWidth + gap + textWidth;
            float start = shapeRect.centerX() - contentWidth * 0.5f;
            iconLeft = start;
            if (icon != null) {
                drawIcon(canvas, icon, iconLeft, centerY - iconSize * 0.5f, iconSize);
            }
            textPaint.setColor(isEnabled() ? contentColor : theme.palette.labelTertiary);
            canvas.drawText(text.toString(), iconLeft + iconWidth + gap, textBaseline, textPaint);
        } else if (icon != null) {
            iconLeft = shapeRect.centerX() - iconSize * 0.5f;
            drawIcon(canvas, icon, iconLeft, centerY - iconSize * 0.5f, iconSize);
        }
    }

    @Override
    public boolean performClick() {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        return super.performClick();
    }

    private void drawIcon(Canvas canvas, Drawable drawable, float left, float top, float size) {
        if (!isEnabled()) {
            drawable.setColorFilter(theme.palette.labelTertiary, PorterDuff.Mode.SRC_IN);
        } else {
            drawable.setColorFilter(contentColor, PorterDuff.Mode.SRC_IN);
        }
        drawable.setBounds(Math.round(left), Math.round(top),
                Math.round(left + size), Math.round(top + size));
        drawable.draw(canvas);
    }

    private float cornerRadius() {
        return MDim.dp(getContext(), theme.shapes.large);
    }
}
