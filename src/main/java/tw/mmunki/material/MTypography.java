package tw.mmunki.material;

import android.graphics.Typeface;

/**
 * Material 3 type scale. The roles keep their historical names so existing
 * layouts can be migrated without a visual rewrite.
 */
public final class MTypography {

    public final MType display;
    public final MType title;
    public final MType headline;
    public final MType title3;
    public final MType body;
    public final MType callout;
    public final MType subhead;
    public final MType footnote;
    public final MType caption;
    public final MType button;
    public final MType mono;

    public MTypography(MType display, MType title, MType headline, MType title3, MType body,
                       MType callout, MType subhead, MType footnote, MType caption,
                       MType button, MType mono) {
        this.display = display;
        this.title = title;
        this.headline = headline;
        this.title3 = title3;
        this.body = body;
        this.callout = callout;
        this.subhead = subhead;
        this.footnote = footnote;
        this.caption = caption;
        this.button = button;
        this.mono = mono;
    }

    public static MTypography standard() {
        return new MTypography(
                new MType(36f, "sans-serif", Typeface.NORMAL, 0f, 44f),
                new MType(28f, "sans-serif", Typeface.NORMAL, 0f, 36f),
                new MType(24f, "sans-serif", Typeface.NORMAL, 0f, 32f),
                new MType(20f, "sans-serif-medium", Typeface.NORMAL, 0f, 28f),
                new MType(16f, "sans-serif", Typeface.NORMAL, 0.01f, 24f),
                new MType(14f, "sans-serif", Typeface.NORMAL, 0.01f, 20f),
                new MType(14f, "sans-serif-medium", Typeface.NORMAL, 0.01f, 20f),
                new MType(12f, "sans-serif", Typeface.NORMAL, 0.02f, 16f),
                new MType(11f, "sans-serif", Typeface.NORMAL, 0.02f, 16f),
                new MType(14f, "sans-serif-medium", Typeface.NORMAL, 0.01f, 20f),
                new MType(14f, "monospace", Typeface.NORMAL, 0f, 20f));
    }
}
