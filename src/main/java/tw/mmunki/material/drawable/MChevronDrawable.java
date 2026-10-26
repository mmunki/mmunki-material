package tw.mmunki.material.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import tw.mmunki.material.MaterialColor;

public final class MChevronDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private int color;
    private int alpha = 255;

    public MChevronDrawable(int color, float strokeWidthPx) {
        this.color = color;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidthPx);
    }

    public void setColor(int color) {
        this.color = color;
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
        path.reset();
        path.moveTo(bounds.left + width * 0.28f, bounds.top + height * 0.18f);
        path.lineTo(bounds.left + width * 0.72f, bounds.top + height * 0.5f);
        path.lineTo(bounds.left + width * 0.28f, bounds.top + height * 0.82f);
        paint.setColor(MaterialColor.withAlpha(color, MaterialColor.alpha(color) * alpha / 255));
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return 10;
    }

    @Override
    public int getIntrinsicHeight() {
        return 16;
    }
}
