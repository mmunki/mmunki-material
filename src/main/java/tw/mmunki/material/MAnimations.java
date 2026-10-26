package tw.mmunki.material;

import android.view.View;
import android.view.ViewGroup;

import tw.mmunki.material.motion.MSpring;
import tw.mmunki.material.motion.SpringSpec;

public final class MAnimations {

    private MAnimations() {
    }

    public static void fadeUp(final View view, final float distancePx, long delayMs,
                              final SpringSpec spec) {
        if (view == null) {
            return;
        }
        view.setAlpha(0f);
        view.setTranslationY(distancePx);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                new MSpring().start(0f, 1f, spec, new MSpring.Listener() {
                    @Override
                    public void onUpdate(float value) {
                        view.setAlpha(value);
                        view.setTranslationY(distancePx * (1f - value));
                    }

                    @Override
                    public void onEnd() {
                        view.setAlpha(1f);
                        view.setTranslationY(0f);
                    }
                });
            }
        }, delayMs);
    }

    public static void stagger(ViewGroup group, long stepMs, long initialDelayMs,
                               float distancePx, SpringSpec spec) {
        if (group == null) {
            return;
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                fadeUp(child, distancePx, initialDelayMs + stepMs * i, spec);
            }
        }
    }
}
