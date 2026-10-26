package tw.mmunki.material;

import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.widget.TextView;

public final class MType {

    public final float sizeSp;
    public final String family;
    public final int style;
    public final float letterSpacing;
    public final float lineHeightSp;

    public MType(float sizeSp, String family, int style, float letterSpacing, float lineHeightSp) {
        this.sizeSp = sizeSp;
        this.family = family;
        this.style = style;
        this.letterSpacing = letterSpacing;
        this.lineHeightSp = lineHeightSp;
    }

    public MType apply(TextView view) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        view.setTypeface(Typeface.create(family, style));
        if (Build.VERSION.SDK_INT >= 21) {
            view.setLetterSpacing(letterSpacing);
        }
        if (lineHeightSp > 0f && sizeSp > 0f) {
            view.setLineSpacing(0f, lineHeightSp / sizeSp);
        }
        return this;
    }

    public Typeface typeface() {
        return Typeface.create(family, style);
    }

    public float sizePx(android.content.Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizeSp,
                context.getResources().getDisplayMetrics());
    }
}
