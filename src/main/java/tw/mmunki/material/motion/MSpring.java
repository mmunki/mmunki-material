package tw.mmunki.material.motion;

import android.view.Choreographer;

public final class MSpring {

    public interface Listener {
        void onUpdate(float value);

        void onEnd();
    }

    private final Choreographer choreographer;
    private final Choreographer.FrameCallback callback;
    private SpringSpec spec;
    private SpringSolver solver;
    private Listener listener;
    private boolean running;
    private long lastFrameNanos;

    public MSpring() {
        choreographer = Choreographer.getInstance();
        callback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (!running) {
                    return;
                }
                if (lastFrameNanos == 0L) {
                    lastFrameNanos = frameTimeNanos;
                    choreographer.postFrameCallback(callback);
                    return;
                }
                float delta = (frameTimeNanos - lastFrameNanos) / 1_000_000_000f;
                lastFrameNanos = frameTimeNanos;
                if (delta > 0.064f) {
                    delta = 0.064f;
                }
                solver.step(delta);
                float value = solver.getValue();
                if (listener != null) {
                    listener.onUpdate(value);
                }
                if (solver.isSettled(0.0005f, 0.005f)) {
                    solver.snapToTarget();
                    running = false;
                    if (listener != null) {
                        listener.onUpdate(solver.getValue());
                        listener.onEnd();
                    }
                    return;
                }
                choreographer.postFrameCallback(callback);
            }
        };
    }

    public void start(float from, float to, SpringSpec spec, Listener listener) {
        cancel();
        this.spec = spec != null ? spec : SpringSpec.smooth();
        this.listener = listener;
        solver = new SpringSolver(this.spec);
        solver.setValue(from);
        solver.setTarget(to);
        running = true;
        lastFrameNanos = 0L;
        if (listener != null) {
            listener.onUpdate(from);
        }
        choreographer.postFrameCallback(callback);
    }

    public void retarget(float to) {
        if (!running || solver == null) {
            start(getValue(), to, spec, listener);
            return;
        }
        solver.setTarget(to);
    }

    public void cancel() {
        if (running) {
            choreographer.removeFrameCallback(callback);
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public float getValue() {
        return solver != null ? solver.getValue() : 0f;
    }
}
