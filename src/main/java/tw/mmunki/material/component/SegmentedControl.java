package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.motion.MSpring;

/**
 * Material 3 segmented button. Segments share an outline and the selected
 * segment uses the secondary container color.
 */
public class SegmentedControl extends View {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(SegmentedControl control, int index);
    }

    private final MTheme theme;
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF trackRect = new RectF();
    private final RectF selectedRect = new RectF();
    private final MSpring selectionSpring = new MSpring();
    private final List<String> items = new ArrayList<String>();

    private OnSelectionChangedListener listener;
    private int selectedIndex;
    private float progress;

    public SegmentedControl(Context context) {
        this(context, null);
    }

    public SegmentedControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentedControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setClickable(true);
        setFocusable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(Math.max(1f, MDim.dp(context, 1f)));
        selectedPaint.setStyle(Paint.Style.FILL);
        dividerPaint.setStyle(Paint.Style.STROKE);
        dividerPaint.setStrokeWidth(Math.max(1f, MDim.dp(context, 1f)));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(theme.typography.subhead.sizePx(context));
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setItems(String... values) {
        items.clear();
        if (values != null) {
            for (String value : values) {
                if (value != null) {
                    items.add(value);
                }
            }
        }
        selectedIndex = 0;
        progress = 0f;
        requestLayout();
        invalidate();
    }

    public void setItems(List<String> values) {
        items.clear();
        if (values != null) {
            for (String value : values) {
                if (value != null) {
                    items.add(value);
                }
            }
        }
        selectedIndex = 0;
        progress = 0f;
        requestLayout();
        invalidate();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        setSelectedIndexInternal(index, true, false);
    }

    public void setSelectedIndex(int index, boolean animate) {
        setSelectedIndexInternal(index, animate, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimum = items.size() * MDim.dpi(getContext(), 88f);
        int desiredWidth = Math.max(getSuggestedMinimumWidth(), minimum)
                + getPaddingLeft() + getPaddingRight();
        int desiredHeight = MDim.dpi(getContext(), 40f)
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
        float trackHeight = Math.min(contentHeight, MDim.dp(getContext(), 40f));
        float trackTop = getPaddingTop() + (contentHeight - trackHeight) * 0.5f;
        float radius = trackHeight * 0.5f;
        trackRect.set(contentLeft, trackTop, contentLeft + contentWidth, trackTop + trackHeight);

        int count = items.size();
        if (count == 0) {
            return;
        }
        float segmentWidth = contentWidth / count;
        float drawProgress = clamp(progress, 0f, count - 1);
        float selectedLeft = contentLeft + drawProgress * segmentWidth;
        selectedRect.set(selectedLeft, trackTop, selectedLeft + segmentWidth,
                trackTop + trackHeight);
        selectedPaint.setColor(theme.palette.secondary);
        canvas.drawRoundRect(selectedRect, radius, radius, selectedPaint);

        outlinePaint.setColor(theme.palette.separator);
        canvas.drawRoundRect(trackRect, radius, radius, outlinePaint);

        dividerPaint.setColor(theme.palette.separator);
        for (int i = 1; i < count; i++) {
            float x = contentLeft + segmentWidth * i;
            if (Math.abs(i - drawProgress) < 0.5f) {
                continue;
            }
            canvas.drawLine(x, trackTop, x, trackTop + trackHeight, dividerPaint);
        }

        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float centerY = trackTop + trackHeight * 0.5f;
        float baseline = centerY - (metrics.ascent + metrics.descent) * 0.5f;
        for (int i = 0; i < count; i++) {
            float centerX = contentLeft + segmentWidth * (i + 0.5f);
            float distance = Math.min(1f, Math.abs(drawProgress - i));
            int color = MaterialColor.blend(theme.palette.labelSecondary,
                    theme.palette.onSecondary, 1f - distance);
            textPaint.setColor(color);
            canvas.drawText(items.get(i), centerX, baseline, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || items.isEmpty()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                setSelectedIndexInternal(indexAt(event.getX()), true, true);
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.38f);
    }

    @Override
    protected void onDetachedFromWindow() {
        selectionSpring.cancel();
        super.onDetachedFromWindow();
    }

    private void setSelectedIndexInternal(int index, boolean animate, boolean fromUser) {
        if (items.isEmpty()) {
            return;
        }
        int clamped = Math.max(0, Math.min(items.size() - 1, index));
        boolean changed = selectedIndex != clamped;
        selectedIndex = clamped;
        if (animate) {
            selectionSpring.start(progress, clamped, theme.motion.snappy, new MSpring.Listener() {
                @Override
                public void onUpdate(float value) {
                    progress = value;
                    invalidate();
                }

                @Override
                public void onEnd() {
                }
            });
        } else {
            selectionSpring.cancel();
            progress = clamped;
            invalidate();
        }
        if (fromUser && changed) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
        if (changed && listener != null) {
            listener.onSelectionChanged(this, clamped);
        }
    }

    private int indexAt(float x) {
        float contentLeft = getPaddingLeft();
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int count = Math.max(1, items.size());
        float segmentWidth = contentWidth / count;
        if (segmentWidth <= 0f) {
            return selectedIndex;
        }
        int index = (int) ((x - contentLeft) / segmentWidth);
        return Math.max(0, Math.min(items.size() - 1, index));
    }

    private static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }
}
