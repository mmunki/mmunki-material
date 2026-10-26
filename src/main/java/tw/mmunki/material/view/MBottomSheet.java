package tw.mmunki.material.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MCardDrawable;
import tw.mmunki.material.motion.MSpring;

public class MBottomSheet extends Dialog {

    private final MTheme theme;
    private final FrameLayout root;
    private final LinearLayout sheet;
    private final FrameLayout handleArea;
    private final FrameLayout content;
    private final MCardDrawable surface = new MCardDrawable();
    private final MSpring slideSpring = new MSpring();

    private ValueAnimator dimAnimator;
    private VelocityTracker velocityTracker;
    private float dimAmount = 0.38f;
    private float downRawY;
    private float dragStartY;
    private float cornerRadiusDp = -1f;
    private boolean dragEnabled = true;
    private boolean dragging;
    private boolean dismissing;
    private int touchSlop;

    public MBottomSheet(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        theme = MTheme.get(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Window window = getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            window.setAttributes(params);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        root = new FrameLayout(context);
        root.setClickable(true);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        sheet = new LinearLayout(context);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setClickable(true);
        sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        surface.setFillColor(theme.palette.surfaceElevated);
        surface.setBorder(MaterialColor.withAlpha(theme.palette.label, 0.08f),
                Math.max(1f, MDim.dp(context, 0.75f)));
        surface.setRadius(currentRadius());
        sheet.setBackground(surface);

        handleArea = new FrameLayout(context);
        View handle = new View(context);
        GradientDrawable handleShape = new GradientDrawable();
        handleShape.setColor(MaterialColor.withAlpha(theme.palette.labelTertiary, 0.55f));
        handleShape.setCornerRadius(MDim.dp(context, 999f));
        handle.setBackground(handleShape);
        FrameLayout.LayoutParams handleParams = new FrameLayout.LayoutParams(
                MDim.dpi(context, 40f), MDim.dpi(context, 5f), Gravity.CENTER);
        handleArea.addView(handle, handleParams);
        handleArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return onHandleTouch(event);
            }
        });
        sheet.addView(handleArea, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, MDim.dpi(context, 30f)));

        content = new FrameLayout(context);
        content.setPadding(MDim.dpi(context, 20f), 0,
                MDim.dpi(context, 20f), MDim.dpi(context, 20f));
        sheet.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        root.addView(sheet, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        setContentView(root);
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        if (Build.VERSION.SDK_INT >= 20) {
            sheet.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                    view.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
                    return insets;
                }
            });
        }
    }

    public MBottomSheet(Context context, View contentView) {
        this(context);
        setContent(contentView);
    }

    public static MBottomSheet show(Context context, View contentView) {
        MBottomSheet sheet = new MBottomSheet(context, contentView);
        sheet.show();
        return sheet;
    }

    public void setContent(View view) {
        content.removeAllViews();
        if (view != null) {
            content.addView(view, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public ViewGroup getContentContainer() {
        return content;
    }

    public void setDimAmount(float amount) {
        dimAmount = Math.max(0f, Math.min(1f, amount));
        if (isShowing()) {
            applyDim(dimAmount);
        }
    }

    public void setDragEnabled(boolean enabled) {
        dragEnabled = enabled;
    }

    public void setHandleVisible(boolean visible) {
        handleArea.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setCornerRadiusDp(float radiusDp) {
        cornerRadiusDp = radiusDp;
        surface.setRadius(currentRadius());
    }

    @Override
    public void show() {
        super.show();
        sheet.post(new Runnable() {
            @Override
            public void run() {
                float start = Math.max(sheet.getHeight(), MDim.dp(getContext(), 240f))
                        + MDim.dp(getContext(), 40f);
                sheet.setTranslationY(start);
                animateDim(0f, dimAmount);
                slideTo(0f);
            }
        });
    }

    @Override
    public void dismiss() {
        if (dismissing) {
            return;
        }
        if (!isShowing()) {
            super.dismiss();
            return;
        }
        dismissing = true;
        animateDim(getCurrentDim(), 0f);
        float target = Math.max(sheet.getHeight(), MDim.dp(getContext(), 240f))
                + MDim.dp(getContext(), 40f);
        slideTo(target);
    }

    @Override
    protected void onStop() {
        slideSpring.cancel();
        if (dimAnimator != null) {
            dimAnimator.cancel();
        }
        super.onStop();
    }

    private boolean onHandleTouch(MotionEvent event) {
        if (!dragEnabled) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downRawY = event.getRawY();
                dragStartY = sheet.getTranslationY();
                dragging = false;
                slideSpring.cancel();
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                float delta = event.getRawY() - downRawY;
                if (!dragging && delta > touchSlop) {
                    dragging = true;
                }
                if (dragging) {
                    sheet.setTranslationY(Math.max(0f, dragStartY + delta));
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(1000);
                }
                float velocity = velocityTracker != null ? velocityTracker.getYVelocity() : 0f;
                boolean dismissNow = dragging && (sheet.getTranslationY() > sheet.getHeight() * 0.25f
                        || velocity > MDim.dp(getContext(), 700f));
                dragging = false;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                if (dismissNow) {
                    dismiss();
                } else {
                    slideTo(0f);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                slideTo(0f);
                return true;
            default:
                return false;
        }
    }

    private void slideTo(float target) {
        slideSpring.start(sheet.getTranslationY(), target, theme.motion.page, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                sheet.setTranslationY(value);
            }

            @Override
            public void onEnd() {
                if (dismissing) {
                    dismissing = false;
                    dismissNow();
                }
            }
        });
    }

    private void dismissNow() {
        if (dimAnimator != null) {
            dimAnimator.cancel();
        }
        dismissing = false;
        super.dismiss();
    }

    private void animateDim(float from, float to) {
        if (dimAnimator != null) {
            dimAnimator.cancel();
        }
        dimAnimator = ValueAnimator.ofFloat(from, to);
        dimAnimator.setDuration(240L);
        dimAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return theme.motion.standard.get(input);
            }
        });
        dimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                applyDim((Float) animation.getAnimatedValue());
            }
        });
        dimAnimator.start();
    }

    private void applyDim(float amount) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = amount;
        window.setAttributes(params);
    }

    private float getCurrentDim() {
        Window window = getWindow();
        return window != null ? window.getAttributes().dimAmount : 0f;
    }

    private float currentRadius() {
        if (cornerRadiusDp >= 0f) {
            return MDim.dp(getContext(), cornerRadiusDp);
        }
        return MDim.dp(getContext(), theme.shapes.extraLarge);
    }
}
