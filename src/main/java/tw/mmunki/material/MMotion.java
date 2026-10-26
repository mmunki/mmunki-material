package tw.mmunki.material;

import tw.mmunki.material.motion.Easing;
import tw.mmunki.material.motion.SpringSpec;

public final class MMotion {

    public final SpringSpec press;
    public final SpringSpec snappy;
    public final SpringSpec settle;
    public final SpringSpec page;
    public final SpringSpec bouncy;
    public final SpringSpec gentle;
    public final Easing standard;
    public final Easing emphasized;
    public final Easing decelerate;

    public MMotion(SpringSpec press, SpringSpec snappy, SpringSpec settle, SpringSpec page,
                   SpringSpec bouncy, SpringSpec gentle, Easing standard, Easing emphasized,
                   Easing decelerate) {
        this.press = press;
        this.snappy = snappy;
        this.settle = settle;
        this.page = page;
        this.bouncy = bouncy;
        this.gentle = gentle;
        this.standard = standard;
        this.emphasized = emphasized;
        this.decelerate = decelerate;
    }

    public static MMotion aurora() {
        return new MMotion(
                SpringSpec.fromResponse(0.22f, 0.78f),
                SpringSpec.fromResponse(0.30f, 0.88f),
                SpringSpec.fromResponse(0.38f, 0.92f),
                SpringSpec.fromResponse(0.55f, 0.95f),
                SpringSpec.fromResponse(0.45f, 0.62f),
                SpringSpec.fromResponse(0.65f, 1.0f),
                Easing.STANDARD,
                Easing.EMPHASIZED,
                Easing.DECELERATE);
    }
}
