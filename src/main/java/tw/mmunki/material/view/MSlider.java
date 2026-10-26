package tw.mmunki.material.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.motion.MSpring;

public class MSlider extends View {

    public interface OnValueChangeListener {
        void onValueChanged(MSlider view, float value, boolean fromUser);
    }

    private final MTheme theme;
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF trackRect = new RectF();
    private final RectF fillRect = new RectF();
    private final MSpring progressSpring = new MSpring();
    private final MSpring thumbSpring = new MSpring();

    private OnValueChangeListener listener;
    private float min;
    private float max = 1f;
    private float value;
    private float step;
    private float progress;
    private float thumbScale = 1f;
    private boolean dragging;

    public MSlider(Context context) {
        this(context, null);
    }

    public MSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setClickable(true);
        setFocusable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        thumbPaint.setColor(0xFFFFFFFF);
        thumbPaint.setShadowLayer(MDim.dp(context, 3.5f), 0f, MDim.dp(context, 1.4f), 0x45000000);
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

    public void setStepSize(float step) {
        this.step = Math.max(0f, step);
        setValue(value);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth();
        int desiredHeight = MDim.dpi(getContext(), 48f) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float horizontalInset = MDim.dp(getContext(), 16f);
        float contentLeft = getPaddingLeft() + horizontalInset;
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight()
                - horizontalInset * 2f;
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (contentWidth <= 0f || contentHeight <= 0f) {
            return;
        }
        float trackHeight = MDim.dp(getContext(), 6f);
        float centerY = getPaddingTop() + contentHeight * 0.5f;
        float trackTop = centerY - trackHeight * 0.5f;
        float radius = trackHeight * 0.5f;
        trackRect.set(contentLeft, trackTop, contentLeft + contentWidth, trackTop + trackHeight);

        trackPaint.setColor(theme.palette.fill);
        canvas.drawRoundRect(trackRect, radius, radius, trackPaint);

        float drawProgress = Math.max(0f, Math.min(1f, progress));
        float fillWidth = contentWidth * drawProgress;
        if (fillWidth > 0.5f) {
            fillRect.set(contentLeft, trackTop, contentLeft + fillWidth, trackTop + trackHeight);
            fillPaint.setShader(new LinearGradient(contentLeft, 0f, contentLeft + contentWidth, 0f,
                    theme.palette.gradientStart, theme.palette.gradientEnd, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(fillRect, radius, radius, fillPaint);
            fillPaint.setShader(null);
        }

        float thumbRadius = MDim.dp(getContext(), 13f) * thumbScale;
        float thumbX = contentLeft + fillWidth;
        canvas.drawCircle(thumbX, centerY, thumbRadius, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dragging = true;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                animateThumb(1.12f);
                updateFromTouch(event.getX(), true);
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
                animateThumb(1f);
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                animateThumb(1f);
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
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
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.SeekBar");
        if (Build.VERSION.SDK_INT >= 19) {
            info.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(
                    AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_FLOAT, min, max, value));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        progressSpring.cancel();
        thumbSpring.cancel();
        super.onDetachedFromWindow();
    }

    private void updateFromTouch(float x, boolean fromUser) {
        float horizontalInset = MDim.dp(getContext(), 16f);
        float contentLeft = getPaddingLeft() + horizontalInset;
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight()
                - horizontalInset * 2f;
        if (contentWidth <= 0f) {
            return;
        }
        float fraction = (x - contentLeft) / contentWidth;
        fraction = Math.max(0f, Math.min(1f, fraction));
        float raw = min + (max - min) * fraction;
        setValueInternal(raw, false, fromUser);
    }

    private void setValueInternal(float requested, boolean animate, boolean fromUser) {
        float clamped = Math.max(min, Math.min(max, requested));
        if (step > 0f) {
            float steps = Math.round((clamped - min) / step);
            clamped = Math.min(max, min + steps * step);
        }
        boolean changed = Math.abs(clamped - value) > 1e-6f;
        value = clamped;
        float fraction = max > min ? (value - min) / (max - min) : 0f;
        if (animate) {
            animateProgress(fraction);
        } else {
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

    private void animateThumb(float target) {
        thumbSpring.start(thumbScale, target, theme.motion.press, new MSpring.Listener() {
            @Override
            public void onUpdate(float current) {
                thumbScale = current;
                invalidate();
            }

            @Override
            public void onEnd() {
            }
        });
    }
}
