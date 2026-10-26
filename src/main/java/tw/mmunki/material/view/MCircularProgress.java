package tw.mmunki.material.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.motion.Easing;
import tw.mmunki.material.motion.MSpring;

public class MCircularProgress extends View {

    private final MTheme theme;
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcRect = new RectF();
    private final MSpring progressSpring = new MSpring();

    private float progress;
    private boolean indeterminate;
    private float strokeWidthDp = 4f;

    public MCircularProgress(Context context) {
        this(context, null);
    }

    public MCircularProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCircularProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        trackPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        setClickable(false);
        setFocusable(false);
    }

    public void setProgress(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        progressSpring.start(this.progress, clamped, theme.motion.settle, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                MCircularProgress.this.progress = value;
                invalidate();
            }

            @Override
            public void onEnd() {
            }
        });
    }

    public void setProgressImmediate(float progress) {
        progressSpring.cancel();
        this.progress = Math.max(0f, Math.min(1f, progress));
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        invalidate();
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public void setStrokeWidthDp(float strokeWidthDp) {
        this.strokeWidthDp = Math.max(1f, strokeWidthDp);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = MDim.dpi(getContext(), 56f)
                + getPaddingLeft() + getPaddingRight();
        int desiredHeight = MDim.dpi(getContext(), 56f)
                + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float height = getHeight() - getPaddingTop() - getPaddingBottom();
        float size = Math.min(width, height);
        if (size <= 0f) {
            return;
        }
        float stroke = MDim.dp(getContext(), strokeWidthDp);
        float radius = size * 0.5f - stroke * 0.5f;
        float centerX = getPaddingLeft() + width * 0.5f;
        float centerY = getPaddingTop() + height * 0.5f;
        arcRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        trackPaint.setStrokeWidth(stroke);
        trackPaint.setColor(theme.palette.fill);
        canvas.drawArc(arcRect, 0f, 360f, false, trackPaint);

        arcPaint.setStrokeWidth(stroke);
        SweepGradient gradient = new SweepGradient(centerX, centerY,
                new int[]{theme.palette.gradientStart, theme.palette.gradientEnd,
                        theme.palette.gradientStart},
                new float[]{0f, 0.65f, 1f});
        Matrix matrix = new Matrix();
        matrix.setRotate(-90f, centerX, centerY);
        gradient.setLocalMatrix(matrix);
        arcPaint.setShader(gradient);

        if (indeterminate) {
            float phase = (SystemClock.uptimeMillis() % 1400L) / 1400f;
            float rotation = phase * 360f;
            float sweep = 70f + 210f * Easing.STANDARD.get(phase);
            canvas.drawArc(arcRect, rotation, sweep, false, arcPaint);
            postInvalidateOnAnimation();
        } else {
            float drawProgress = Math.max(0f, Math.min(1f, progress));
            canvas.drawArc(arcRect, -90f, 360f * drawProgress, false, arcPaint);
        }
        arcPaint.setShader(null);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.ProgressBar");
        if (Build.VERSION.SDK_INT >= 19 && !indeterminate) {
            info.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(
                    AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_FLOAT, 0f, 1f, progress));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        progressSpring.cancel();
        super.onDetachedFromWindow();
    }
}
