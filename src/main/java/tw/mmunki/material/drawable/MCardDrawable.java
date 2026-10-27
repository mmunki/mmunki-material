package tw.mmunki.material.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import tw.mmunki.material.MShapes;
import tw.mmunki.material.MaterialColor;

/**
 * Flat Material surface with an optional outline.
 */
public final class MCardDrawable extends Drawable {

    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    private int fillColor = 0xFFFFFBFE;
    private int borderColor = 0xFFCAC4D0;
    private float borderWidth = 1f;
    private float radius;
    private int alpha = 255;

    public MCardDrawable() {
        fill.setStyle(Paint.Style.FILL);
        stroke.setStyle(Paint.Style.STROKE);
    }

    public void setFillColor(int color) {
        fillColor = color;
        invalidateSelf();
    }

    public void setBorder(int color, float widthPx) {
        borderColor = color;
        borderWidth = widthPx;
        invalidateSelf();
    }

    public void setRadius(float radiusPx) {
        radius = radiusPx;
        invalidateSelf();
    }

    public void setShowHighlight(boolean showHighlight) {
        invalidateSelf();
    }

    public void setShowShade(boolean showShade) {
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float width = bounds.width();
        float height = bounds.height();
        if (width <= 0f || height <= 0f) {
            return;
        }
        rect.set(bounds);
        float r = MShapes.clampRadius(radius, width, height);

        fill.setColor(applyAlpha(fillColor));
        canvas.drawRoundRect(rect, r, r, fill);

        if (borderWidth > 0f) {
            stroke.setColor(applyAlpha(borderColor));
            stroke.setStrokeWidth(borderWidth);
            float inset = borderWidth * 0.5f;
            rect.inset(inset, inset);
            canvas.drawRoundRect(rect, Math.max(0f, r - inset), Math.max(0f, r - inset), stroke);
            rect.set(bounds);
        }
    }

    @Override
    public void getOutline(Outline outline) {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            return;
        }
        float r = MShapes.clampRadius(radius, bounds.width(), bounds.height());
        outline.setRoundRect(bounds, r);
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        fill.setColorFilter(colorFilter);
        stroke.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private int applyAlpha(int color) {
        return MaterialColor.withAlpha(color, MaterialColor.alpha(color) * alpha / 255);
    }
}
