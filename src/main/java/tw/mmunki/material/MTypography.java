package tw.mmunki.material;

import android.graphics.Typeface;

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

    public static MTypography aurora() {
        return new MTypography(
                new MType(34f, "sans-serif", Typeface.BOLD, -0.02f, 40f),
                new MType(28f, "sans-serif", Typeface.BOLD, -0.015f, 34f),
                new MType(22f, "sans-serif-medium", Typeface.NORMAL, -0.01f, 28f),
                new MType(20f, "sans-serif-medium", Typeface.NORMAL, -0.005f, 26f),
                new MType(17f, "sans-serif", Typeface.NORMAL, 0f, 24f),
                new MType(16f, "sans-serif", Typeface.NORMAL, 0f, 22f),
                new MType(15f, "sans-serif-medium", Typeface.NORMAL, 0f, 20f),
                new MType(13f, "sans-serif", Typeface.NORMAL, 0.01f, 18f),
                new MType(12f, "sans-serif", Typeface.NORMAL, 0.02f, 16f),
                new MType(17f, "sans-serif-medium", Typeface.NORMAL, 0.01f, 22f),
                new MType(14f, "monospace", Typeface.NORMAL, 0f, 20f));
    }
}
