package tw.mmunki.material.component;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;

/**
 * A floating, pill-shaped navigation bar. The bar keeps the Material 3
 * navigation item layout and adds a raised surface with a comfortable inset
 * from the screen edges.
 */
public class FloatingNavigationBar extends BottomNavigationBar {

    private final MTheme theme;

    public FloatingNavigationBar(Context context) {
        this(context, null);
    }

    public FloatingNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        int inset = MDim.dpi(context, 12f);
        setPadding(inset, inset, inset, inset);
        setElevation(MDim.dp(context, 6f));
        setClipToOutline(false);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(),
                        view.getWidth() - view.getPaddingRight(),
                        view.getHeight() - view.getPaddingBottom(), cornerRadiusPx());
            }
        });
    }

    @Override
    protected int barHeightDp() {
        return 64;
    }

    @Override
    protected float cornerRadiusPx() {
        return MDim.dp(getContext(), theme.shapes.pill);
    }

    @Override
    protected boolean showTopDivider() {
        return false;
    }
}
