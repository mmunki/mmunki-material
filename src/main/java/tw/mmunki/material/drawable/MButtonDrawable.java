package tw.mmunki.material.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import tw.mmunki.material.MShapes;
import tw.mmunki.material.MaterialColor;

public final class MButtonDrawable extends Drawable {

    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlight = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    private int startColor = 0xFF5A5CF5;
    private int endColor = 0xFF6A5CFF;
    private int strokeColor = 0x00000000;
    private float strokeWidth;
    private float radius;
    private boolean gradient = true;
    private boolean showHighlight = true;
    private int alpha = 255;

    public MButtonDrawable() {
        fill.setStyle(Paint.Style.FILL);
        stroke.setStyle(Paint.Style.STROKE);
        highlight.setStyle(Paint.Style.FILL);
    }

    public void setColors(int startColor, int endColor, boolean gradient) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.gradient = gradient;
        invalidateSelf();
    }

    public void setStroke(int color, float widthPx) {
        strokeColor = color;
        strokeWidth = widthPx;
        invalidateSelf();
    }

    public void setRadius(float radiusPx) {
        radius = radiusPx;
        invalidateSelf();
    }

    public void setShowHighlight(boolean showHighlight) {
        this.showHighlight = showHighlight;
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

        if (gradient) {
            fill.setShader(new LinearGradient(0f, 0f, width, height,
                    applyAlpha(startColor), applyAlpha(endColor), Shader.TileMode.CLAMP));
        } else {
            fill.setShader(null);
            fill.setColor(applyAlpha(startColor));
        }
        canvas.drawRoundRect(rect, r, r, fill);

        if (showHighlight && gradient) {
            highlight.setShader(new LinearGradient(0f, 0f, 0f, height * 0.72f,
                    0x30FFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, r, r, highlight);
            highlight.setShader(null);
        }

        if (strokeWidth > 0f) {
            stroke.setShader(null);
            stroke.setColor(applyAlpha(strokeColor));
            stroke.setStrokeWidth(strokeWidth);
            float inset = strokeWidth * 0.5f;
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
