package tw.mmunki.material.view;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.drawable.MFieldDrawable;
import tw.mmunki.material.motion.MSpring;

public class MTextField extends FrameLayout {

    private final MTheme theme;
    private final MFieldDrawable surface = new MFieldDrawable();
    private final MSpring labelSpring = new MSpring();
    private final MSpring focusSpring = new MSpring();

    private EditText editText;
    private TextView labelView;
    private TextView errorView;
    private float labelProgress;
    private float focusProgress;
    private boolean error;

    public MTextField(Context context) {
        this(context, null);
    }

    public MTextField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTextField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        theme = MTheme.get(context);
        setWillNotDraw(false);
        setPadding(MDim.dpi(context, 16f), 0, MDim.dpi(context, 16f), 0);
        surface.setFillColor(theme.palette.surface);
        surface.setStroke(theme.palette.separator, MDim.dp(context, 1f));
        surface.setRadius(MDim.dp(context, theme.shapes.medium));
        setBackground(surface);
        buildChildren();
        updateLabel(0f);
        updateFocus(0f);
    }

    public EditText getEditText() {
        return editText;
    }

    public TextView getLabelView() {
        return labelView;
    }

    public void setLabel(CharSequence label) {
        labelView.setText(label);
        editText.setContentDescription(label);
        requestLayout();
        updateLabelTarget(false);
    }

    public CharSequence getText() {
        return editText.getText();
    }

    public void setText(CharSequence text) {
        editText.setText(text);
        editText.setSelection(editText.getText().length());
    }

    public void setInputType(int type) {
        editText.setInputType(type);
    }

    public void setError(CharSequence message) {
        error = message != null && message.length() > 0;
        errorView.setText(error ? message : null);
        errorView.setVisibility(error ? VISIBLE : GONE);
        updateFocus(focusProgress);
        updateLabelTarget(true);
        requestLayout();
        invalidate();
    }

    public void clearError() {
        setError(null);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        editText.setEnabled(enabled);
        labelView.setEnabled(enabled);
        errorView.setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int desiredWidth = widthMode == MeasureSpec.UNSPECIFIED
                ? MDim.dpi(getContext(), 280f) + getPaddingLeft() + getPaddingRight()
                : widthSize;
        int available = Math.max(0, desiredWidth - getPaddingLeft() - getPaddingRight());
        int exactWidth = MeasureSpec.makeMeasureSpec(available, MeasureSpec.EXACTLY);
        int wrapHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        editText.measure(exactWidth, wrapHeight);
        labelView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), wrapHeight);
        if (errorView.getVisibility() != GONE) {
            errorView.measure(exactWidth, wrapHeight);
        }
        int height = editText.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
        if (errorView.getVisibility() != GONE) {
            height += errorView.getMeasuredHeight();
        }
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int contentLeft = getPaddingLeft();
        int contentRight = right - left - getPaddingRight();
        int contentTop = getPaddingTop();
        editText.layout(contentLeft, contentTop, contentRight,
                contentTop + editText.getMeasuredHeight());
        labelView.layout(contentLeft, contentTop + MDim.dpi(getContext(), 6f),
                contentLeft + labelView.getMeasuredWidth(),
                contentTop + MDim.dpi(getContext(), 6f) + labelView.getMeasuredHeight());
        if (errorView.getVisibility() != GONE) {
            int errorTop = contentTop + editText.getMeasuredHeight();
            errorView.layout(contentLeft, errorTop, contentRight,
                    errorTop + errorView.getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        surface.setRadius(MDim.dp(getContext(), theme.shapes.medium));
    }

    @Override
    protected void onDetachedFromWindow() {
        labelSpring.cancel();
        focusSpring.cancel();
        super.onDetachedFromWindow();
    }

    private void buildChildren() {
        editText = new EditText(getContext());
        if (Build.VERSION.SDK_INT >= 17) {
            editText.setId(View.generateViewId());
        }
        editText.setBackground(null);
        editText.setPadding(0, MDim.dpi(getContext(), 20f), 0, MDim.dpi(getContext(), 8f));
        editText.setSingleLine(true);
        editText.setIncludeFontPadding(false);
        editText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        editText.setMinHeight(MDim.dpi(getContext(), 56f));
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        theme.typography.body.apply(editText);
        editText.setTextColor(theme.palette.label);
        editText.setHintTextColor(theme.palette.labelTertiary);
        addView(editText, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        labelView = new TextView(getContext());
        theme.typography.body.apply(labelView);
        labelView.setIncludeFontPadding(false);
        labelView.setPivotX(0f);
        labelView.setPivotY(0f);
        labelView.setTextColor(theme.palette.labelTertiary);
        if (Build.VERSION.SDK_INT >= 17) {
            labelView.setLabelFor(editText.getId());
        }
        addView(labelView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        errorView = new TextView(getContext());
        theme.typography.caption.apply(errorView);
        errorView.setIncludeFontPadding(false);
        errorView.setTextColor(theme.palette.danger);
        errorView.setVisibility(GONE);
        errorView.setPadding(0, MDim.dpi(getContext(), 6f), 0, 0);
        addView(errorView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateLabelTarget(true);
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                updateLabelTarget(true);
                updateFocusTarget(hasFocus ? 1f : 0f, true);
            }
        });
    }

    private void updateLabelTarget(boolean animate) {
        float target = editText.isFocused() || editText.getText().length() > 0 || error ? 1f : 0f;
        if (!animate) {
            labelProgress = target;
            updateLabel(labelProgress);
            return;
        }
        labelSpring.start(labelProgress, target, theme.motion.snappy, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                labelProgress = value;
                updateLabel(value);
            }

            @Override
            public void onEnd() {
            }
        });
    }

    private void updateFocusTarget(float target, boolean animate) {
        if (!animate) {
            focusProgress = target;
            updateFocus(focusProgress);
            return;
        }
        focusSpring.start(focusProgress, target, theme.motion.settle, new MSpring.Listener() {
            @Override
            public void onUpdate(float value) {
                focusProgress = value;
                updateFocus(value);
            }

            @Override
            public void onEnd() {
            }
        });
    }

    private void updateLabel(float value) {
        labelView.setScaleX(1f - 0.24f * value);
        labelView.setScaleY(1f - 0.24f * value);
        labelView.setTranslationY((1f - value) * MDim.dp(getContext(), 14f));
        int color = error
                ? MaterialColor.blend(theme.palette.labelTertiary, theme.palette.danger, value)
                : MaterialColor.blend(theme.palette.labelTertiary,
                        focusProgress > 0.01f ? theme.palette.primary : theme.palette.labelSecondary,
                        value);
        labelView.setTextColor(color);
    }

    private void updateFocus(float value) {
        int accent = error ? theme.palette.danger : theme.palette.primary;
        int stroke = MaterialColor.blend(theme.palette.separator, accent, value);
        float width = MDim.dp(getContext(), 1f) + MDim.dp(getContext(), 1f) * value;
        surface.setStroke(stroke, width);
        surface.setFillColor(MaterialColor.blend(theme.palette.surface, theme.palette.fill, value * 0.35f));
        updateLabel(labelProgress);
        invalidate();
    }
}
