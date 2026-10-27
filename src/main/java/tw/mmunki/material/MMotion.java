package tw.mmunki.material;

import tw.mmunki.material.motion.Easing;
import tw.mmunki.material.motion.SpringSpec;

/**
 * Motion tokens. Springs are used for values that track a finger, while the
 * easing curves are used for finite transitions.
 */
public final class MMotion {

    public final SpringSpec press;
    public final SpringSpec snappy;
    public final SpringSpec settle;
    public final SpringSpec page;
    public final SpringSpec expressive;
    public final SpringSpec gentle;
    public final Easing standard;
    public final Easing emphasized;
    public final Easing decelerate;

    public MMotion(SpringSpec press, SpringSpec snappy, SpringSpec settle, SpringSpec page,
                   SpringSpec expressive, SpringSpec gentle, Easing standard, Easing emphasized,
                   Easing decelerate) {
        this.press = press;
        this.snappy = snappy;
        this.settle = settle;
        this.page = page;
        this.expressive = expressive;
        this.gentle = gentle;
        this.standard = standard;
        this.emphasized = emphasized;
        this.decelerate = decelerate;
    }

    public static MMotion standard() {
        return new MMotion(
                SpringSpec.fromResponse(0.18f, 1.0f),
                SpringSpec.fromResponse(0.28f, 0.92f),
                SpringSpec.fromResponse(0.38f, 0.96f),
                SpringSpec.fromResponse(0.48f, 0.96f),
                SpringSpec.fromResponse(0.42f, 0.70f),
                SpringSpec.fromResponse(0.60f, 1.0f),
                Easing.STANDARD,
                Easing.EMPHASIZED,
                Easing.DECELERATE);
    }
}
