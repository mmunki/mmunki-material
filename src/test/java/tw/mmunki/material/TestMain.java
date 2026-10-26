package tw.mmunki.material;

import tw.mmunki.material.motion.Easing;
import tw.mmunki.material.motion.SpringSolver;
import tw.mmunki.material.motion.SpringSpec;

public final class TestMain {

    private static int passed;

    public static void main(String[] args) {
        test("spring smooth settles", new Runnable() {
            @Override
            public void run() {
                SpringSolver solver = new SpringSolver(SpringSpec.smooth());
                solver.setValue(0f);
                solver.setTarget(1f);
                float maximum = 0f;
                for (int i = 0; i < 600; i++) {
                    solver.step(1f / 120f);
                    maximum = Math.max(maximum, solver.getValue());
                }
                check(solver.isSettled(0.001f, 0.01f), "spring did not settle");
                near(solver.getValue(), 1f, 0.001f, "settled value");
                check(maximum < 1.15f, "smooth spring overshoot too large: " + maximum);
            }
        });

        test("critical spring does not overshoot", new Runnable() {
            @Override
            public void run() {
                SpringSolver solver = new SpringSolver(SpringSpec.critical());
                solver.setValue(0f);
                solver.setTarget(1f);
                float maximum = 0f;
                for (int i = 0; i < 720; i++) {
                    solver.step(1f / 240f);
                    maximum = Math.max(maximum, solver.getValue());
                }
                check(maximum <= 1.005f, "critical spring overshot: " + maximum);
                near(solver.getValue(), 1f, 0.001f, "critical settled value");
            }
        });

        test("bouncy spring overshoots", new Runnable() {
            @Override
            public void run() {
                SpringSolver solver = new SpringSolver(SpringSpec.bouncy());
                solver.setValue(0f);
                solver.setTarget(1f);
                float maximum = 0f;
                for (int i = 0; i < 480; i++) {
                    solver.step(1f / 120f);
                    maximum = Math.max(maximum, solver.getValue());
                }
                check(maximum > 1.02f, "bouncy spring did not overshoot: " + maximum);
            }
        });

        test("spring is frame-rate independent", new Runnable() {
            @Override
            public void run() {
                SpringSolver slow = new SpringSolver(SpringSpec.smooth());
                SpringSolver fast = new SpringSolver(SpringSpec.smooth());
                slow.setTarget(1f);
                fast.setTarget(1f);
                for (int i = 0; i < 60; i++) {
                    slow.step(1f / 60f);
                }
                for (int i = 0; i < 240; i++) {
                    fast.step(1f / 240f);
                }
                near(slow.getValue(), fast.getValue(), 0.01f, "frame-rate dependence");
            }
        });

        test("retarget keeps velocity", new Runnable() {
            @Override
            public void run() {
                SpringSolver solver = new SpringSolver(SpringSpec.smooth());
                solver.setTarget(1f);
                for (int i = 0; i < 12; i++) {
                    solver.step(1f / 120f);
                }
                float velocity = solver.getVelocity();
                solver.setTarget(0.5f);
                near(solver.getVelocity(), velocity, 1e-5f, "velocity changed on retarget");
            }
        });

        test("easing endpoints and monotonicity", new Runnable() {
            @Override
            public void run() {
                Easing[] curves = {Easing.STANDARD, Easing.EMPHASIZED, Easing.DECELERATE,
                        Easing.ACCELERATE, Easing.IOS};
                for (Easing curve : curves) {
                    near(curve.get(0f), 0f, 1e-6f, "easing start");
                    near(curve.get(1f), 1f, 1e-6f, "easing end");
                    float previous = -1f;
                    for (int i = 0; i <= 1000; i++) {
                        float value = curve.get(i / 1000f);
                        check(value >= previous - 1e-5f, "easing is not monotonic");
                        previous = value;
                    }
                }
            }
        });

        test("ios easing midpoint", new Runnable() {
            @Override
            public void run() {
                float middle = Easing.IOS.get(0.5f);
                check(middle > 0.5f && middle < 1f, "ios midpoint is not ease-out: " + middle);
            }
        });

        test("color blending", new Runnable() {
            @Override
            public void run() {
                int middle = MaterialColor.blend(0xFF000000, 0xFFFFFFFF, 0.5f);
                check(MaterialColor.red(middle) >= 127 && MaterialColor.red(middle) <= 128,
                        "blend red");
                check(MaterialColor.green(middle) == MaterialColor.red(middle), "blend green");
                check(MaterialColor.blue(middle) == MaterialColor.red(middle), "blend blue");
                check(MaterialColor.alpha(middle) == 255, "blend alpha");
                check(MaterialColor.blend(0xFF112233, 0xFF445566, 0f) == 0xFF112233,
                        "blend start");
                check(MaterialColor.blend(0xFF112233, 0xFF445566, 1f) == 0xFF445566,
                        "blend end");
            }
        });

        test("alpha compositing", new Runnable() {
            @Override
            public void run() {
                check(MaterialColor.blendOver(0x00000000, 0xFF123456) == 0xFF123456,
                        "transparent over opaque");
                check(MaterialColor.blendOver(0xFFFFFFFF, 0xFF000000) == 0xFFFFFFFF,
                        "white over black");
                int mixed = MaterialColor.blendOver(0x80FF0000, 0xFF0000FF);
                check(MaterialColor.alpha(mixed) == 255, "mixed alpha");
                check(MaterialColor.red(mixed) > 100 && MaterialColor.blue(mixed) > 100,
                        "mixed channels");
            }
        });

        test("contrast ratio", new Runnable() {
            @Override
            public void run() {
                near((float) MaterialColor.contrastRatio(0xFF000000, 0xFFFFFFFF), 21f, 0.01f,
                        "black on white");
                near((float) MaterialColor.contrastRatio(0xFF123456, 0xFF123456), 1f, 0.001f,
                        "same color");
                int adjusted = MaterialColor.ensureContrast(0xFF777777, 0xFFFFFFFF, 4.5);
                check(MaterialColor.contrastRatio(adjusted, 0xFFFFFFFF) >= 4.5,
                        "ensureContrast failed");
            }
        });

        test("shape clamping", new Runnable() {
            @Override
            public void run() {
                near(MShapes.clampRadius(100f, 40f, 20f), 10f, 1e-5f, "clamp small side");
                near(MShapes.clampRadius(-4f, 40f, 20f), 0f, 1e-5f, "clamp negative");
                near(MShapes.pillRadius(120f, 32f), 16f, 1e-5f, "pill radius");
            }
        });

        test("palette contrast light", new Runnable() {
            @Override
            public void run() {
                checkPalette(MPalette.light(), false);
            }
        });

        test("palette contrast dark", new Runnable() {
            @Override
            public void run() {
                checkPalette(MPalette.dark(), true);
            }
        });

        test("spring spec response", new Runnable() {
            @Override
            public void run() {
                SpringSpec spec = SpringSpec.fromResponse(0.4f, 0.9f);
                near(spec.dampingRatio(), 0.9f, 0.001f, "damping ratio");
                near(spec.angularFrequency(), (float) (Math.PI * 2.0 / 0.4), 0.01f,
                        "angular frequency");
                check(spec.settleTime() > 0f && spec.settleTime() <= 5f, "settle time range");
            }
        });

        System.out.println("ALL TESTS PASSED: " + passed);
    }

    private static void checkPalette(MPalette palette, boolean dark) {
        checkContrast(palette.label, palette.background, 4.5, "label on background");
        checkContrast(palette.labelSecondary, palette.background, 4.5,
                "secondary label on background");
        checkContrast(palette.labelTertiary, palette.background, 4.5,
                "tertiary label on background");
        checkContrast(palette.label, palette.surface, 4.5, "label on surface");
        checkContrast(palette.labelSecondary, palette.surface, 4.5,
                "secondary label on surface");
        checkContrast(palette.danger, palette.surface, 4.5, "danger on surface");
        checkContrast(palette.onPrimary, palette.primary, 4.5, "onPrimary on primary");
        checkContrast(palette.onPrimaryContainer, palette.primaryContainer, 4.5,
                "onPrimaryContainer on container");
        checkContrast(palette.onSecondary, palette.secondary, 4.5, "onSecondary on secondary");
        checkContrast(palette.onPrimary, palette.gradientStart, 4.5,
                "onPrimary on gradient start");
        checkContrast(palette.onPrimary, palette.gradientEnd, 4.5,
                "onPrimary on gradient end");
        check(MaterialColor.isDark(palette.background) == dark, "palette brightness");
    }

    private static void checkContrast(int foreground, int background, double minimum, String name) {
        double ratio = MaterialColor.contrastRatio(foreground, background);
        check(ratio >= minimum, name + " contrast " + ratio);
    }

    private static void test(String name, Runnable runnable) {
        try {
            runnable.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable throwable) {
            System.err.println("FAIL " + name + ": " + throwable.getMessage());
            throwable.printStackTrace();
            System.exit(1);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void near(float actual, float expected, float epsilon, String message) {
        if (Math.abs(actual - expected) > epsilon) {
            throw new AssertionError(message + " (actual=" + actual + ", expected=" + expected + ")");
        }
    }
}
