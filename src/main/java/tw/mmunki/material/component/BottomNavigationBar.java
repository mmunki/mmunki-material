package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.motion.MSpring;

/**
 * Material 3 bottom navigation bar. Items are built from a label and an
 * optional drawable icon, so the component is dependency-free.
 */
public class BottomNavigationBar extends View {

    public interface OnItemSelectedListener {
        void onItemSelected(BottomNavigationBar bar, int index);
    }

    public static final class Item {
        public final CharSequence label;
        public final Drawable icon;
        public int badgeCount;

        public Item(CharSequence label, Drawable icon) {
            this.label = label;
            this.icon = icon;
        }

        public Item setBadge(int count) {
            badgeCount = Math.max(0, count);
            return this;
        }
    }

    private final MTheme theme;
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF backgroundRect = new RectF();
    private final RectF indicatorRect = new RectF();
    private final MSpring selectionSpring = new MSpring();
    private final List<Item> items = new ArrayList<Item>();

    private OnItemSelectedListener listener;
    private int selectedIndex;
    private float selection;
    private int pressedIndex = -1;

    public BottomNavigationBar(Context context) {
        this(context, null);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setWillNotDraw(false);
        setClickable(true);
        setFocusable(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(MDim.sp(context, 12f));
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);
        badgeTextPaint.setTextSize(MDim.sp(context, 10f));
        badgeTextPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        dividerPaint.setStrokeWidth(Math.max(1f, MDim.dp(context, 1f)));
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public Item addItem(CharSequence label, Drawable icon) {
        Item item = new Item(label, icon);
        items.add(item);
        requestLayout();
        invalidate();
        return item;
    }

    public void clearItems() {
        items.clear();
        selectedIndex = 0;
        selection = 0f;
        requestLayout();
        invalidate();
    }

    public int getItemCount() {
        return items.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedItem(int index) {
        setSelectedItem(index, true);
    }

    public void setSelectedItem(int index, boolean animate) {
        setSelectedItemInternal(index, animate, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = Math.max(getSuggestedMinimumWidth(),
                items.size() * MDim.dpi(getContext(), 72f))
                + getPaddingLeft() + getPaddingRight();
        int desiredHeight = MDim.dpi(getContext(), barHeightDp())
                + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = items.size();
        if (count == 0) {
            return;
        }
        backgroundRect.set(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        float radius = cornerRadiusPx();
        backgroundPaint.setColor(isEnabled() ? theme.palette.surface : theme.palette.surfaceSunken);
        canvas.drawRoundRect(backgroundRect, radius, radius, backgroundPaint);

        if (showTopDivider()) {
            dividerPaint.setColor(theme.palette.separator);
            canvas.drawLine(backgroundRect.left, backgroundRect.top,
                    backgroundRect.right, backgroundRect.top, dividerPaint);
        }

        float itemWidth = backgroundRect.width() / count;
        float indicatorWidth = Math.min(MDim.dp(getContext(), 64f),
                Math.max(MDim.dp(getContext(), 40f), itemWidth - MDim.dp(getContext(), 16f)));
        float indicatorHeight = MDim.dp(getContext(), 32f);
        float blockHeight = indicatorHeight + MDim.dp(getContext(), 4f)
                + MDim.dp(getContext(), 16f);
        float blockTop = backgroundRect.top
                + Math.max(0f, (backgroundRect.height() - blockHeight) * 0.5f);
        float drawSelection = clamp(selection, 0f, count - 1f);
        float indicatorCenterX = backgroundRect.left + itemWidth * (drawSelection + 0.5f);

        indicatorPaint.setColor(theme.palette.primaryContainer);
        indicatorRect.set(indicatorCenterX - indicatorWidth * 0.5f, blockTop,
                indicatorCenterX + indicatorWidth * 0.5f, blockTop + indicatorHeight);
        canvas.drawRoundRect(indicatorRect, indicatorHeight * 0.5f, indicatorHeight * 0.5f,
                indicatorPaint);

        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float labelTop = blockTop + indicatorHeight + MDim.dp(getContext(), 4f);
        float labelBaseline = labelTop
                + (MDim.dp(getContext(), 16f) - (metrics.descent - metrics.ascent)) * 0.5f
                - metrics.ascent;
        float iconSize = MDim.dp(getContext(), 24f);
        float iconCenterY = blockTop + indicatorHeight * 0.5f;

        for (int i = 0; i < count; i++) {
            Item item = items.get(i);
            float centerX = backgroundRect.left + itemWidth * (i + 0.5f);
            float distance = Math.min(1f, Math.abs(drawSelection - i));
            boolean selected = distance < 0.5f;
            int iconColor = MaterialColor.blend(theme.palette.labelSecondary,
                    theme.palette.onPrimaryContainer, 1f - distance);
            int labelColor = MaterialColor.blend(theme.palette.labelSecondary,
                    theme.palette.label, 1f - distance);

            if (i == pressedIndex) {
                indicatorPaint.setColor(MaterialColor.withAlpha(theme.palette.primary, 0.12f));
                canvas.drawCircle(centerX, iconCenterY, MDim.dp(getContext(), 22f),
                        indicatorPaint);
            }

            if (item.icon != null) {
                item.icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
                item.icon.setBounds(Math.round(centerX - iconSize * 0.5f),
                        Math.round(iconCenterY - iconSize * 0.5f),
                        Math.round(centerX + iconSize * 0.5f),
                        Math.round(iconCenterY + iconSize * 0.5f));
                item.icon.draw(canvas);
            }

            textPaint.setColor(labelColor);
            if (item.label != null) {
                canvas.drawText(item.label.toString(), centerX, labelBaseline, textPaint);
            }

            if (item.badgeCount > 0) {
                float badgeRadius = MDim.dp(getContext(), 8f);
                float badgeX = centerX + iconSize * 0.5f;
                float badgeY = iconCenterY - iconSize * 0.5f;
                badgePaint.setColor(theme.palette.danger);
                canvas.drawCircle(badgeX, badgeY, badgeRadius, badgePaint);
                String badge = item.badgeCount > 9 ? "9+" : String.valueOf(item.badgeCount);
                badgeTextPaint.setColor(0xFFFFFFFF);
                Paint.FontMetrics badgeMetrics = badgeTextPaint.getFontMetrics();
                float badgeBaseline = badgeY - (badgeMetrics.ascent + badgeMetrics.descent) * 0.5f;
                canvas.drawText(badge, badgeX, badgeBaseline, badgeTextPaint);
            }

            if (selected) {
                setContentDescription(item.label);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || items.isEmpty()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressedIndex = indexAt(event.getX());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                int current = indexAt(event.getX());
                if (current != pressedIndex) {
                    pressedIndex = current;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                int index = indexAt(event.getX());
                pressedIndex = -1;
                setSelectedItemInternal(index, true, true);
                performClick();
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                pressedIndex = -1;
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
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.TabWidget");
        info.setScrollable(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        selectionSpring.cancel();
        super.onDetachedFromWindow();
    }

    protected int barHeightDp() {
        return 80;
    }

    protected float cornerRadiusPx() {
        return 0f;
    }

    protected boolean showTopDivider() {
        return true;
    }

    private void setSelectedItemInternal(int index, boolean animate, boolean fromUser) {
        if (items.isEmpty()) {
            return;
        }
        int clamped = Math.max(0, Math.min(items.size() - 1, index));
        boolean changed = selectedIndex != clamped;
        selectedIndex = clamped;
        if (animate) {
            selectionSpring.start(selection, clamped, theme.motion.snappy,
                    new MSpring.Listener() {
                        @Override
                        public void onUpdate(float value) {
                            selection = value;
                            invalidate();
                        }

                        @Override
                        public void onEnd() {
                        }
                    });
        } else {
            selectionSpring.cancel();
            selection = clamped;
            invalidate();
        }
        if (fromUser && changed) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
        if (changed && listener != null) {
            listener.onItemSelected(this, clamped);
        }
    }

    private int indexAt(float x) {
        float contentLeft = getPaddingLeft();
        float contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (items.isEmpty() || contentWidth <= 0f) {
            return 0;
        }
        float itemWidth = contentWidth / items.size();
        int index = (int) ((x - contentLeft) / itemWidth);
        return Math.max(0, Math.min(items.size() - 1, index));
    }

    private static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }
}
