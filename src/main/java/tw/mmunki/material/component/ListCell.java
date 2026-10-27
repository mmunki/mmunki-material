package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MChevronDrawable;
import tw.mmunki.material.drawable.MRipple;

public class ListCell extends FrameLayout {

    private final MTheme theme;
    private final LinearLayout row;
    private final ImageView leadingView;
    private final LinearLayout textColumn;
    private final TextView titleView;
    private final TextView subtitleView;
    private final TextView trailingView;
    private final ImageView chevronView;
    private final Paint separatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean separatorVisible;
    private View accessory;

    public ListCell(Context context) {
        this(context, null);
    }

    public ListCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setWillNotDraw(false);
        setClickable(true);
        setFocusable(true);
        setMinimumHeight(MDim.dpi(context, 56f));
        setPadding(MDim.dpi(context, 16f), 0, MDim.dpi(context, 16f), 0);
        setBackground(new MRipple(MaterialColor.withAlpha(theme.palette.primary, 0.10f),
                new ColorDrawable(0x00000000), 0f));
        separatorPaint.setColor(theme.palette.separator);
        separatorPaint.setStrokeWidth(Math.max(1f, MDim.dp(context, 0.75f)));

        row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(MDim.dpi(context, 56f));

        leadingView = new ImageView(context);
        leadingView.setVisibility(GONE);
        LinearLayout.LayoutParams leadingParams = new LinearLayout.LayoutParams(
                MDim.dpi(context, 24f), MDim.dpi(context, 24f));
        leadingParams.setMarginEnd(MDim.dpi(context, 14f));
        leadingView.setLayoutParams(leadingParams);
        leadingView.setColorFilter(theme.palette.primary);
        row.addView(leadingView);

        textColumn = new LinearLayout(context);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        textColumn.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textColumn.setLayoutParams(columnParams);

        titleView = new TextView(context);
        theme.typography.body.apply(titleView);
        titleView.setTextColor(theme.palette.label);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setIncludeFontPadding(false);
        textColumn.addView(titleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        subtitleView = new TextView(context);
        theme.typography.footnote.apply(subtitleView);
        subtitleView.setTextColor(theme.palette.labelSecondary);
        subtitleView.setSingleLine(true);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        subtitleView.setIncludeFontPadding(false);
        subtitleView.setVisibility(GONE);
        subtitleView.setPadding(0, MDim.dpi(context, 2f), 0, 0);
        textColumn.addView(subtitleView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        row.addView(textColumn);

        trailingView = new TextView(context);
        theme.typography.subhead.apply(trailingView);
        trailingView.setTextColor(theme.palette.labelSecondary);
        trailingView.setGravity(Gravity.CENTER);
        trailingView.setVisibility(GONE);
        LinearLayout.LayoutParams trailingParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        trailingParams.setMarginStart(MDim.dpi(context, 8f));
        trailingView.setLayoutParams(trailingParams);
        row.addView(trailingView);

        chevronView = new ImageView(context);
        chevronView.setImageDrawable(new MChevronDrawable(theme.palette.labelTertiary,
                MDim.dp(context, 1.6f)));
        LinearLayout.LayoutParams chevronParams = new LinearLayout.LayoutParams(
                MDim.dpi(context, 10f), MDim.dpi(context, 16f));
        chevronParams.setMarginStart(MDim.dpi(context, 8f));
        chevronView.setLayoutParams(chevronParams);
        row.addView(chevronView);

        addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        updateContentDescription();
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
        updateContentDescription();
    }

    public void setSubtitle(CharSequence subtitle) {
        boolean visible = subtitle != null && subtitle.length() > 0;
        subtitleView.setText(subtitle);
        subtitleView.setVisibility(visible ? VISIBLE : GONE);
        updateContentDescription();
    }

    public void setLeadingIcon(Drawable icon) {
        leadingView.setImageDrawable(icon);
        leadingView.setVisibility(icon != null ? VISIBLE : GONE);
        updateContentDescription();
    }

    public void setLeadingIcon(Drawable icon, boolean tint) {
        setLeadingIcon(icon);
        leadingView.setColorFilter(tint ? theme.palette.primary : null);
    }

    public void setTrailingText(CharSequence text) {
        boolean visible = text != null && text.length() > 0;
        trailingView.setText(text);
        trailingView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setChevronVisible(boolean visible) {
        chevronView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setAccessory(View view) {
        if (accessory != null) {
            row.removeView(accessory);
        }
        accessory = view;
        if (view != null) {
            trailingView.setVisibility(GONE);
            chevronView.setVisibility(GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginStart(MDim.dpi(getContext(), 8f));
            row.addView(view, params);
        } else {
            chevronView.setVisibility(VISIBLE);
        }
    }

    public void setSeparatorVisible(boolean visible) {
        separatorVisible = visible;
        invalidate();
    }

    public boolean isSeparatorVisible() {
        return separatorVisible;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.45f);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!separatorVisible) {
            return;
        }
        float start = getPaddingLeft();
        if (leadingView.getVisibility() == VISIBLE) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) leadingView.getLayoutParams();
            start += leadingView.getMeasuredWidth() + params.getMarginEnd();
        }
        float end = getWidth() - getPaddingRight();
        float y = getHeight() - separatorPaint.getStrokeWidth() * 0.5f;
        canvas.drawLine(start, y, end, y, separatorPaint);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Button");
    }

    private void updateContentDescription() {
        CharSequence title = titleView.getText();
        CharSequence subtitle = subtitleView.getVisibility() == VISIBLE ? subtitleView.getText() : null;
        if (title == null || title.length() == 0) {
            return;
        }
        if (subtitle != null && subtitle.length() > 0) {
            setContentDescription(title + ", " + subtitle);
        } else {
            setContentDescription(title);
        }
    }
}
