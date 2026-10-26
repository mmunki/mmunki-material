package tw.mmunki.material.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

public final class MRipple extends RippleDrawable {

    private final Mask mask;

    public MRipple(int color, Drawable content, float radiusPx) {
        this(ColorStateList.valueOf(color), content, radiusPx);
    }

    public MRipple(ColorStateList color, Drawable content, float radiusPx) {
        this(color, content, new Mask(radiusPx));
    }

    private MRipple(ColorStateList color, Drawable content, Mask mask) {
        super(color, content, mask);
        this.mask = mask;
        setRadius(mask.radius);
    }

    public void setRadius(float radiusPx) {
        mask.radius = radiusPx;
        mask.invalidateSelf();
        invalidateSelf();
    }

    private static final class Mask extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float radius;

        Mask(float radius) {
            this.radius = radius;
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            float limit = Math.min(bounds.width(), bounds.height()) * 0.5f;
            float r = Math.min(radius, limit);
            if (r <= 0f) {
                canvas.drawRect(bounds, paint);
            } else {
                canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, r, r, paint);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
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
        public void getOutline(Outline outline) {
            Rect bounds = getBounds();
            float limit = Math.min(bounds.width(), bounds.height()) * 0.5f;
            float r = Math.min(radius, limit);
            if (Build.VERSION.SDK_INT >= 21) {
                outline.setRoundRect(bounds, r);
            }
        }
    }
}
