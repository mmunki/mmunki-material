package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.motion.MSpring;

/**
 * Material 3 slider: a flat 4 dp track, a 20 dp handle, optional tick marks
 * for discrete steps, RTL support and keyboard/accessibility actions.
 */
public class Slider extends View {

    public interface OnValueChangeListener {
        void onValueChanged(Slider view, float value, boolean fromUser);
    }

    private final MTheme theme;
    private final Paint inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF trackRect = new RectF();
    private final RectF activeRect = new RectF();
    private final MSpring progressSpring = new MSpring();

    private OnValueChangeListener listener;
    private float min;
    private float max = 1f;
    private float value;
    private float step;
    private float progress;
    private float trackHeightDp = 4f;
    private float thumbRadiusDp = 10f;
    private boolean showTickMarks = true;
    private boolean dragging;
    private boolean pressed;

    public Slider(Context context) {
        this(context, null);
    }

    public Slider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setClickable(true);
        setFocusable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setShadowLayer(MDim.dp(context, 2f), 0f, MDim.dp(context, 1f), 0x30000000);
        haloPaint.setStyle(Paint.Style.FILL);
        tickPaint.setStyle(Paint.Style.FILL);
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public void setRange(float min, float max) {
        if (!(max > min)) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        this.min = min;
        this.max = max;
        setValue(value);
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public void setStepSize(float step) {
        this.step = Math.max(0f, step);
        setValue(value);
    }

    public float getStepSize() {
        return step;
    }

    public void setShowTickMarks(boolean show) {
        showTickMarks = show;
        invalidate();
    }

    public boolean isShowingTickMarks() {
        return showTickMarks;
    }

    public void setTrackHeightDp(float heightDp) {
        trackHeightDp = Math.max(2f, heightDp);
        invalidate();
    }

    public void setThumbRadiusDp(float radiusDp) {
        thumbRadiusDp = Math.max(4f, radiusDp);
        invalidate();
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        setValueInternal(value, true, false);
    }

    public void setValue(float value, boolean animate) {
        setValueInternal(value, animate, false);
    }

    public void setValueImmediate(float value) {
        setValueInternal(value, false, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth();
        int desiredHeight = MDim.dpi(getContext(), 48f)
                + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float thumbRadius = MDim.dp(getContext(), thumbRadiusDp);
        float contentLeft = getPaddingLeft() + thumbRadius;
        float contentRight = getWidth() - getPaddingRight() - thumbRadius;
        float contentWidth = contentRight - contentLeft;
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (contentWidth <= 0f || contentHeight <= 0f) {
            return;
        }
        float trackHeight = MDim.dp(getContext(), trackHeightDp);
        float centerY = getPaddingTop() + contentHeight * 0.5f;
        float trackTop = centerY - trackHeight * 0.5f;
        float radius = trackHeight * 0.5f;
        trackRect.set(contentLeft, trackTop, contentRight, trackTop + trackHeight);

        inactivePaint.setColor(theme.palette.fill);
        canvas.drawRoundRect(trackRect, radius, radius, inactivePaint);

        float drawProgress = clamp(progress, 0f, 1f);
        boolean rtl = isRtl();
        float activeStart;
        float activeEnd;
        if (rtl) {
            activeStart = contentRight - contentWidth * drawProgress;
            activeEnd = contentRight;
        } else {
            activeStart = contentLeft;
            activeEnd = contentLeft + contentWidth * drawProgress;
        }
        if (activeEnd - activeStart > 0.5f) {
            activeRect.set(activeStart, trackTop, activeEnd, trackTop + trackHeight);
            activePaint.setColor(theme.palette.primary);
            canvas.drawRoundRect(activeRect, radius, radius, activePaint);
        }

        if (showTickMarks && step > 0f) {
            int tickCount = Math.round((max - min) / step);
            if (tickCount > 0 && tickCount <= 100) {
                float tickRadius = MDim.dp(getContext(), 1.5f);
                for (int i = 0; i <= tickCount; i++) {
                    float fraction = (float) i / (float) tickCount;
                    float x = rtl ? contentRight - contentWidth * fraction
                            : contentLeft + contentWidth * fraction;
                    boolean active = fraction <= drawProgress + 1e-4f;
                    tickPaint.setColor(active ? theme.palette.onPrimary : theme.palette.labelTertiary);
                    canvas.drawCircle(x, centerY, tickRadius, tickPaint);
                }
            }
        }

        float thumbX = rtl ? contentRight - contentWidth * drawProgress
                : contentLeft + contentWidth * drawProgress;
        if (pressed || dragging) {
            haloPaint.setColor(MaterialColor.withAlpha(theme.palette.primary, 0.12f));
            canvas.drawCircle(thumbX, centerY, thumbRadius * 2f, haloPaint);
        }
        thumbPaint.setColor(isEnabled() ? theme.palette.primary : theme.palette.labelTertiary);
        canvas.drawCircle(thumbX, centerY, thumbRadius, thumbPaint);
        thumbPaint.setColor(isEnabled() ? theme.palette.surface : theme.palette.fill);
        canvas.drawCircle(thumbX, centerY, thumbRadius * 0.35f, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dragging = true;
                pressed = true;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                updateFromTouch(event.getX(), true);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (dragging) {
                    updateFromTouch(event.getX(), true);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (dragging) {
                    updateFromTouch(event.getX(), true);
                }
                dragging = false;
                pressed = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                performClick();
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                pressed = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        float delta = step > 0f ? step : (max - min) / 20f;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_MINUS:
                setValueInternal(value - delta, true, true);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_PLUS:
            case KeyEvent.KEYCODE_EQUALS:
                setValueInternal(value + delta, true, true);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                setValueInternal(value + delta, true, true);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                setValueInternal(value - delta, true, true);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.SeekBar");
        if (Build.VERSION.SDK_INT >= 19) {
            info.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(
                    AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_FLOAT, min, max, value));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        }
    }

    @Override
    public boolean performAccessibilityAction(int action, android.os.Bundle arguments) {
        float delta = step > 0f ? step : (max - min) / 20f;
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            setValueInternal(value + delta, true, true);
            return true;
        }
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            setValueInternal(value - delta, true, true);
            return true;
        }
        return super.performAccessibilityAction(action, arguments);
    }

    @Override
    protected void onDetachedFromWindow() {
        progressSpring.cancel();
        super.onDetachedFromWindow();
    }

    private void updateFromTouch(float x, boolean fromUser) {
        float thumbRadius = MDim.dp(getContext(), thumbRadiusDp);
        float contentLeft = getPaddingLeft() + thumbRadius;
        float contentRight = getWidth() - getPaddingRight() - thumbRadius;
        float contentWidth = contentRight - contentLeft;
        if (contentWidth <= 0f) {
            return;
        }
        float fraction = (x - contentLeft) / contentWidth;
        if (isRtl()) {
            fraction = 1f - fraction;
        }
        fraction = clamp(fraction, 0f, 1f);
        float raw = min + (max - min) * fraction;
        setValueInternal(raw, false, fromUser);
    }

    private void setValueInternal(float requested, boolean animate, boolean fromUser) {
        float clamped = clamp(requested, min, max);
        if (step > 0f) {
            double steps = Math.rint((clamped - min) / (double) step);
            clamped = (float) Math.min(max, min + steps * (double) step);
        }
        boolean changed = Math.abs(clamped - value) > 1e-6f;
        value = clamped;
        float fraction = max > min ? (value - min) / (max - min) : 0f;
        if (animate) {
            animateProgress(fraction);
        } else {
            progressSpring.cancel();
            progress = fraction;
            invalidate();
        }
        if (changed && fromUser && step > 0f) {
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        }
        if (changed && listener != null) {
            listener.onValueChanged(this, value, fromUser);
        }
    }

    private void animateProgress(float target) {
        progressSpring.start(progress, target, theme.motion.snappy, new MSpring.Listener() {
            @Override
            public void onUpdate(float current) {
                progress = current;
                invalidate();
            }

            @Override
            public void onEnd() {
            }
        });
    }

    private boolean isRtl() {
        return getLayoutDirection() == LAYOUT_DIRECTION_RTL;
    }

    private static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }
}
