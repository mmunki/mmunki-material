package tw.mmunki.material.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.motion.MSpring;

public class MSwitch extends View {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(MSwitch view, boolean checked);
    }

    private final MTheme theme;
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF trackRect = new RectF();
    private final MSpring progressSpring = new MSpring();
    private final MSpring thumbSpring = new MSpring();

    private OnCheckedChangeListener listener;
    private VelocityTracker velocityTracker;
    private boolean checked;
    private boolean dragging;
    private float progress;
    private float thumbScale = 1f;
    private float downX;
    private int touchSlop;

    public MSwitch(Context context) {
        this(context, null);
    }

    public MSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setClickable(true);
        setFocusable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(0xFFFFFFFF);
        thumbPaint.setShadowLayer(MDim.dp(context, 3f), 0f, MDim.dp(context, 1.2f), 0x40000000);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        setCheckedInternal(checked, true, false);
    }

    public void setChecked(boolean checked, boolean animate) {
        setCheckedInternal(checked, animate, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = MDim.dpi(getContext(), 54f) + getPaddingLeft() + getPaddingRight();
        int desiredHeight = MDim.dpi(getContext(), 32f) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float contentLeft = getPaddingLeft();
        float contentTop = getPaddingTop();
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float trackHeight = Math.min(contentHeight, MDim.dp(getContext(), 32f));
        if (contentWidth <= 0f || trackHeight <= 0f) {
            return;
        }
        float trackTop = contentTop + (contentHeight - trackHeight) * 0.5f;
        float radius = trackHeight * 0.5f;
        trackRect.set(contentLeft, trackTop, contentLeft + contentWidth, trackTop + trackHeight);

        int offColor = theme.palette.fill;
        int onColor = theme.palette.primary;
        trackPaint.setColor(MaterialColor.blend(offColor, onColor, progress));
        canvas.drawRoundRect(trackRect, radius, radius, trackPaint);

        if (progress > 0.01f) {
            trackPaint.setColor(MaterialColor.withAlpha(theme.palette.gradientEnd,
                    Math.round(progress * 90f)));
            canvas.drawRoundRect(trackRect, radius, radius, trackPaint);
        }

        float inset = MDim.dp(getContext(), 3f);
        float baseThumbRadius = (trackHeight - inset * 2f) * 0.5f;
        float travel = contentWidth - inset * 2f - baseThumbRadius * 2f;
        float drawProgress = Math.max(0f, Math.min(1f, progress));
        float centerX = contentLeft + inset + baseThumbRadius + drawProgress * travel;
        float centerY = trackTop + trackHeight * 0.5f;
        canvas.drawCircle(centerX, centerY, baseThumbRadius * thumbScale, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                dragging = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                animateThumb(1.08f);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                if (!dragging && Math.abs(event.getX() - downX) > touchSlop) {
                    dragging = true;
                }
                if (dragging) {
                    progress = positionToProgress(event.getX());
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(1000);
                }
                if (dragging) {
                    float velocity = velocityTracker != null ? velocityTracker.getXVelocity() : 0f;
                    boolean target;
                    if (Math.abs(velocity) > MDim.dp(getContext(), 300f)) {
                        target = velocity > 0f;
                    } else {
                        target = progress >= 0.5f;
                    }
                    setCheckedInternal(target, true, true);
                } else {
                    setCheckedInternal(!checked, true, true);
                }
                dragging = false;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                animateThumb(1f);
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                dragging = false;
                animateProgress(checked ? 1f : 0f);
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
        setCheckedInternal(!checked, true, true);
        return true;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Switch");
        info.setCheckable(true);
        info.setChecked(checked);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName("android.widget.Switch");
        event.setChecked(checked);
    }

    @Override
    protected void onDetachedFromWindow() {
        progressSpring.cancel();
        thumbSpring.cancel();
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
        super.onDetachedFromWindow();
    }

    private void setCheckedInternal(boolean value, boolean animate, boolean fromUser) {
        boolean changed = checked != value;
        checked = value;
        if (animate) {
            animateProgress(value ? 1f : 0f);
        } else {
            progress = value ? 1f : 0f;
            invalidate();
        }
        if (fromUser && changed) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            if (listener != null) {
                listener.onCheckedChanged(this, checked);
            }
        }
    }

    private void animateProgress(float target) {
        progressSpring.start(progress, target, theme.motion.snappy, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                progress = value;
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
            public void onUpdate(float value) {
                thumbScale = value;
                invalidate();
            }

            @Override
            public void onEnd() {
            }
        });
    }

    private float positionToProgress(float x) {
        float contentLeft = getPaddingLeft();
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float trackHeight = Math.min(getHeight() - getPaddingTop() - getPaddingBottom(),
                MDim.dp(getContext(), 32f));
        float inset = MDim.dp(getContext(), 3f);
        float baseThumbRadius = (trackHeight - inset * 2f) * 0.5f;
        float travel = contentWidth - inset * 2f - baseThumbRadius * 2f;
        if (travel <= 0f) {
            return progress;
        }
        float value = (x - contentLeft - inset - baseThumbRadius) / travel;
        return Math.max(0f, Math.min(1f, value));
    }
}
