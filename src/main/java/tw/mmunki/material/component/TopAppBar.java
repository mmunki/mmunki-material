package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MRipple;

/**
 * Material 3 top app bar with a navigation icon, title/subtitle and actions.
 * The bar sits at elevation 0 on a static screen and rises when the caller
 * reports a scroll offset.
 */
public class TopAppBar extends FrameLayout {

    private final MTheme theme;
    private final LinearLayout row;
    private final ImageView navigationView;
    private final LinearLayout titleColumn;
    private final TextView titleView;
    private final TextView subtitleView;
    private final LinearLayout actionsView;
    private boolean elevated;

    public TopAppBar(Context context) {
        this(context, null);
    }

    public TopAppBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopAppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setBackgroundColor(theme.palette.surface);
        setElevation(0f);

        row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(MDim.dpi(context, 4f), 0, MDim.dpi(context, 4f), 0);
        addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        navigationView = new ImageView(context);
        navigationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        navigationView.setVisibility(GONE);
        navigationView.setBackground(new MRipple(
                MaterialColor.withAlpha(theme.palette.labelSecondary, 0.12f), null,
                MDim.dp(context, 999f)));
        LinearLayout.LayoutParams navParams = new LinearLayout.LayoutParams(
                MDim.dpi(context, 48f), MDim.dpi(context, 48f));
        navParams.setMarginEnd(MDim.dpi(context, 4f));
        row.addView(navigationView, navParams);

        titleColumn = new LinearLayout(context);
        titleColumn.setOrientation(LinearLayout.VERTICAL);
        titleColumn.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(titleColumn, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        titleView = new TextView(context);
        theme.typography.title.apply(titleView);
        titleView.setTextColor(theme.palette.label);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setIncludeFontPadding(false);
        titleColumn.addView(titleView);

        subtitleView = new TextView(context);
        theme.typography.subhead.apply(subtitleView);
        subtitleView.setTextColor(theme.palette.labelSecondary);
        subtitleView.setSingleLine(true);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        subtitleView.setIncludeFontPadding(false);
        subtitleView.setVisibility(GONE);
        titleColumn.addView(subtitleView);

        actionsView = new LinearLayout(context);
        actionsView.setOrientation(LinearLayout.HORIZONTAL);
        actionsView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(actionsView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
        updateVisibility();
    }

    public CharSequence getTitle() {
        return titleView.getText();
    }

    public void setSubtitle(CharSequence subtitle) {
        boolean visible = subtitle != null && subtitle.length() > 0;
        subtitleView.setText(subtitle);
        subtitleView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setNavigationIcon(Drawable icon) {
        navigationView.setImageDrawable(icon);
        navigationView.setVisibility(icon != null ? VISIBLE : GONE);
        tint(navigationView, theme.palette.label);
    }

    public void setNavigationOnClickListener(OnClickListener listener) {
        navigationView.setOnClickListener(listener);
        navigationView.setClickable(listener != null);
    }

    public void setNavigationContentDescription(CharSequence description) {
        navigationView.setContentDescription(description);
    }

    public void addAction(Drawable icon, OnClickListener listener) {
        ImageView action = new ImageView(getContext());
        action.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        action.setImageDrawable(icon);
        action.setClickable(true);
        action.setFocusable(true);
        action.setBackground(new MRipple(
                MaterialColor.withAlpha(theme.palette.labelSecondary, 0.12f), null,
                MDim.dp(getContext(), 999f)));
        action.setOnClickListener(listener);
        tint(action, theme.palette.label);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MDim.dpi(getContext(), 48f), MDim.dpi(getContext(), 48f));
        actionsView.addView(action, params);
    }

    public void clearActions() {
        actionsView.removeAllViews();
    }

    public void setElevated(boolean elevated) {
        this.elevated = elevated;
        setElevation(elevated ? MDim.dp(getContext(), 2f) : 0f);
        invalidate();
    }

    public boolean isElevated() {
        return elevated;
    }

    public void onScroll(int scrollY) {
        setElevated(scrollY > 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = MDim.dpi(getContext(), 64f)
                + getPaddingTop() + getPaddingBottom();
        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private void updateVisibility() {
        titleColumn.setVisibility(titleView.getText() != null
                && titleView.getText().length() > 0 ? VISIBLE : GONE);
    }

    private void tint(ImageView view, int color) {
        Drawable drawable = view.getDrawable();
        if (drawable != null) {
            drawable = drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            view.setImageDrawable(drawable);
        }
    }
}
