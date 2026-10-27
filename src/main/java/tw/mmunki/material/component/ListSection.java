package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.drawable.MCardDrawable;

public class ListSection extends LinearLayout {

    private final MTheme theme;
    private final MCardDrawable surface = new MCardDrawable();
    private float cornerRadiusDp = -1f;

    public ListSection(Context context) {
        this(context, null);
    }

    public ListSection(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setOrientation(VERTICAL);
        surface.setFillColor(theme.palette.surface);
        surface.setBorder(theme.palette.separator, Math.max(1f, MDim.dp(context, 0.75f)));
        surface.setRadius(currentRadius());
        setBackground(surface);
        setClipToOutline(true);
        setElevation(MDim.dp(context, 1f));
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), currentRadius());
            }
        });
    }

    public void setCornerRadiusDp(float radiusDp) {
        cornerRadiusDp = radiusDp;
        updateRadius();
    }

    public void addCell(ListCell cell) {
        int count = getChildCount();
        if (count > 0) {
            View previous = getChildAt(count - 1);
            if (previous instanceof ListCell) {
                ((ListCell) previous).setSeparatorVisible(true);
            }
        }
        cell.setSeparatorVisible(false);
        addView(cell, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public ListCell addCell(CharSequence title) {
        ListCell cell = new ListCell(getContext());
        cell.setTitle(title);
        addCell(cell);
        return cell;
    }

    public ListCell addCell(CharSequence title, CharSequence subtitle) {
        ListCell cell = new ListCell(getContext());
        cell.setTitle(title);
        cell.setSubtitle(subtitle);
        addCell(cell);
        return cell;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        updateRadius();
    }

    private void updateRadius() {
        surface.setRadius(currentRadius());
        invalidateOutline();
        invalidate();
    }

    private float currentRadius() {
        if (cornerRadiusDp >= 0f) {
            return MDim.dp(getContext(), cornerRadiusDp);
        }
        return MDim.dp(getContext(), theme.shapes.large);
    }
}
