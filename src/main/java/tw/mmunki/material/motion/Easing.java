package tw.mmunki.material.motion;

public final class Easing {

    public static final Easing STANDARD = new Easing(0.2f, 0.0f, 0.0f, 1.0f);
    public static final Easing EMPHASIZED = new Easing(0.05f, 0.7f, 0.1f, 1.0f);
    public static final Easing DECELERATE = new Easing(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Easing ACCELERATE = new Easing(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Easing LINEAR_OUT_SLOW_IN = new Easing(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Easing FAST_OUT_SLOW_IN = new Easing(0.4f, 0.0f, 0.2f, 1.0f);

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    public Easing(float x1, float y1, float x2, float y2) {
        if (x1 < 0f || x1 > 1f || x2 < 0f || x2 > 1f) {
            throw new IllegalArgumentException("x control points must be in [0, 1]");
        }
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public float get(float fraction) {
        if (fraction <= 0f) {
            return 0f;
        }
        if (fraction >= 1f) {
            return 1f;
        }
        double t = solve(fraction);
        return (float) sample(t, y1, y2);
    }

    public double get(double fraction) {
        return get((float) fraction);
    }

    private double solve(double x) {
        double low = 0.0;
        double high = 1.0;
        double t = x;
        for (int i = 0; i < 8; i++) {
            double current = sample(t, x1, x2);
            double error = current - x;
            if (Math.abs(error) < 1e-7) {
                return t;
            }
            double slope = derivative(t, x1, x2);
            if (Math.abs(slope) < 1e-7) {
                break;
            }
            double next = t - error / slope;
            if (next < low || next > high) {
                break;
            }
            t = next;
        }
        for (int i = 0; i < 40; i++) {
            double current = sample(t, x1, x2);
            if (Math.abs(current - x) < 1e-7) {
                return t;
            }
            if (current < x) {
                low = t;
            } else {
                high = t;
            }
            t = (low + high) * 0.5;
        }
        return t;
    }

    private static double sample(double t, double a, double b) {
        double inv = 1.0 - t;
        return 3.0 * inv * inv * t * a + 3.0 * inv * t * t * b + t * t * t;
    }

    private static double derivative(double t, double a, double b) {
        double inv = 1.0 - t;
        return 3.0 * inv * inv * a + 6.0 * inv * t * (b - a) + 3.0 * t * t * (1.0 - b);
    }
}
