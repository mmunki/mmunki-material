package tw.mmunki.material.motion;

public final class SpringSolver {

    private final SpringSpec spec;
    private float value;
    private float velocity;
    private float target;

    public SpringSolver(SpringSpec spec) {
        if (spec == null) {
            throw new IllegalArgumentException("spec must not be null");
        }
        this.spec = spec;
    }

    public SpringSpec getSpec() {
        return spec;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float step(float deltaSeconds) {
        if (!(deltaSeconds > 0f)) {
            return value;
        }
        double dt = Math.min(deltaSeconds, 0.25f);
        double x0 = value - target;
        double v0 = velocity;
        double angular = Math.sqrt(spec.stiffness / spec.mass);
        double ratio = spec.damping / (2.0 * Math.sqrt(spec.stiffness * spec.mass));
        double x;
        double v;

        if (ratio < 1.0 - 1e-6) {
            double damped = angular * Math.sqrt(1.0 - ratio * ratio);
            double envelope = Math.exp(-ratio * angular * dt);
            double cos = Math.cos(damped * dt);
            double sin = Math.sin(damped * dt);
            double a = x0;
            double b = (v0 + ratio * angular * x0) / damped;
            x = envelope * (a * cos + b * sin);
            v = envelope * (v0 * cos - ((ratio * angular * v0 + x0 * angular * angular) / damped) * sin);
        } else if (ratio <= 1.0 + 1e-6) {
            double envelope = Math.exp(-angular * dt);
            double a = x0;
            double b = v0 + angular * x0;
            x = (a + b * dt) * envelope;
            v = (v0 - angular * b * dt) * envelope;
        } else {
            double root = Math.sqrt(ratio * ratio - 1.0);
            double r1 = -angular * (ratio - root);
            double r2 = -angular * (ratio + root);
            double a = (v0 - r2 * x0) / (r1 - r2);
            double b = x0 - a;
            double e1 = Math.exp(r1 * dt);
            double e2 = Math.exp(r2 * dt);
            x = a * e1 + b * e2;
            v = a * r1 * e1 + b * r2 * e2;
        }

        value = (float) (target + x);
        velocity = (float) v;
        return value;
    }

    public boolean isSettled(float valueEpsilon, float velocityEpsilon) {
        return Math.abs(value - target) <= valueEpsilon && Math.abs(velocity) <= velocityEpsilon;
    }

    public void snapToTarget() {
        value = target;
        velocity = 0f;
    }
}
