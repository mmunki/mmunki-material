package tw.mmunki.material;

public final class MShapes {

    public final float small;
    public final float medium;
    public final float large;
    public final float extraLarge;
    public final float pill;

    public MShapes(float small, float medium, float large, float extraLarge, float pill) {
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.extraLarge = extraLarge;
        this.pill = pill;
    }

    public static MShapes aurora() {
        return new MShapes(12f, 18f, 26f, 34f, 500f);
    }

    public static float clampRadius(float radiusPx, float widthPx, float heightPx) {
        float limit = Math.min(widthPx, heightPx) * 0.5f;
        if (limit < 0f) {
            limit = 0f;
        }
        if (radiusPx < 0f) {
            return 0f;
        }
        return Math.min(radiusPx, limit);
    }

    public static float pillRadius(float widthPx, float heightPx) {
        return clampRadius(Float.MAX_VALUE, widthPx, heightPx);
    }
}
