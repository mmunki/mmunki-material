package tw.mmunki.material.motion;

public final class SpringSpec {

    private static final double TWO_PI = Math.PI * 2.0;

    public final float mass;
    public final float stiffness;
    public final float damping;

    public SpringSpec(float mass, float stiffness, float damping) {
        if (!(mass > 0f)) {
            throw new IllegalArgumentException("mass must be positive");
        }
        if (!(stiffness > 0f)) {
            throw new IllegalArgumentException("stiffness must be positive");
        }
        if (!(damping >= 0f)) {
            throw new IllegalArgumentException("damping must be non-negative");
        }
        this.mass = mass;
        this.stiffness = stiffness;
        this.damping = damping;
    }

    public static SpringSpec fromResponse(float responseSeconds, float dampingRatio) {
        if (!(responseSeconds > 0f)) {
            throw new IllegalArgumentException("response must be positive");
        }
        if (!(dampingRatio > 0f)) {
            throw new IllegalArgumentException("damping ratio must be positive");
        }
        double angular = TWO_PI / responseSeconds;
        float stiffness = (float) (angular * angular);
        float damping = (float) (2.0 * dampingRatio * angular);
        return new SpringSpec(1f, stiffness, damping);
    }

    public float angularFrequency() {
        return (float) Math.sqrt(stiffness / mass);
    }

    public float dampingRatio() {
        return (float) (damping / (2.0 * Math.sqrt(stiffness * mass)));
    }

    public float settleTime() {
        double angular = Math.sqrt(stiffness / mass);
        double ratio = damping / (2.0 * Math.sqrt(stiffness * mass));
        double time;
        if (ratio < 1.0) {
            time = 6.9 / (ratio * angular);
        } else if (Math.abs(ratio - 1.0) < 1e-3) {
            time = 6.9 / angular;
        } else {
            double root = -angular * (ratio - Math.sqrt(ratio * ratio - 1.0));
            time = 6.9 / Math.abs(root);
        }
        return (float) Math.max(0.05, Math.min(5.0, time));
    }

    public static SpringSpec smooth() {
        return fromResponse(0.42f, 0.90f);
    }

    public static SpringSpec snappy() {
        return fromResponse(0.28f, 0.86f);
    }

    public static SpringSpec gentle() {
        return fromResponse(0.62f, 1.0f);
    }

    public static SpringSpec bouncy() {
        return fromResponse(0.48f, 0.64f);
    }

    public static SpringSpec critical() {
        return fromResponse(0.40f, 1.0f);
    }
}
