package tw.mmunki.material.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.motion.MSpring;

public class MProgressBar extends View {

    private final MTheme theme;
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF trackRect = new RectF();
    private final RectF fillRect = new RectF();
    private final Path clipPath = new Path();
    private final MSpring progressSpring = new MSpring();

    private float progress;
    private boolean indeterminate;
    private float trackHeightDp = 6f;

    public MProgressBar(Context context) {
        this(context, null);
    }

    public MProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setClickable(false);
        setFocusable(false);
    }

    public void setProgress(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        progressSpring.start(this.progress, clamped, theme.motion.settle, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                MProgressBar.this.progress = value;
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

    public void setTrackHeightDp(float trackHeightDp) {
        this.trackHeightDp = Math.max(1f, trackHeightDp);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth();
        int desiredHeight = MDim.dpi(getContext(), trackHeightDp)
                + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float contentLeft = getPaddingLeft();
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (contentWidth <= 0f || contentHeight <= 0f) {
            return;
        }
        float height = Math.min(contentHeight, MDim.dp(getContext(), trackHeightDp));
        float top = getPaddingTop() + (contentHeight - height) * 0.5f;
        float radius = height * 0.5f;
        trackRect.set(contentLeft, top, contentLeft + contentWidth, top + height);
        trackPaint.setColor(theme.palette.fill);
        canvas.drawRoundRect(trackRect, radius, radius, trackPaint);

        if (indeterminate) {
            float phase = (SystemClock.uptimeMillis() % 1500L) / 1500f;
            float travel = contentWidth + contentWidth * 0.4f;
            float left = contentLeft - contentWidth * 0.4f + travel * phase;
            float right = left + contentWidth * 0.36f;
            clipPath.reset();
            clipPath.addRoundRect(trackRect, radius, radius, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(clipPath);
            fillPaint.setShader(new LinearGradient(left, 0f, right, 0f,
                    new int[]{0x00FFFFFF, theme.palette.gradientStart,
                            theme.palette.gradientEnd, 0x00FFFFFF},
                    new float[]{0f, 0.25f, 0.7f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(left, top, right, top + height, fillPaint);
            fillPaint.setShader(null);
            canvas.restore();
            postInvalidateOnAnimation();
            return;
        }

        float drawProgress = Math.max(0f, Math.min(1f, progress));
        float fillWidth = contentWidth * drawProgress;
        if (fillWidth > 0.5f) {
            fillRect.set(contentLeft, top, contentLeft + fillWidth, top + height);
            fillPaint.setShader(new LinearGradient(contentLeft, 0f,
                    contentLeft + contentWidth, 0f,
                    theme.palette.gradientStart, theme.palette.gradientEnd,
                    Shader.TileMode.CLAMP));
            canvas.drawRoundRect(fillRect, radius, radius, fillPaint);
            fillPaint.setShader(null);
        }
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
