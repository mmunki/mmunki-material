package tw.mmunki.material.sample;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import tw.mmunki.material.MAnimations;
import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MType;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.view.MBottomSheet;
import tw.mmunki.material.view.MButton;
import tw.mmunki.material.view.MCard;
import tw.mmunki.material.view.MCircularProgress;
import tw.mmunki.material.view.MListCell;
import tw.mmunki.material.view.MListSection;
import tw.mmunki.material.view.MProgressBar;
import tw.mmunki.material.view.MSegmentedControl;
import tw.mmunki.material.view.MSlider;
import tw.mmunki.material.view.MSwitch;
import tw.mmunki.material.view.MTextField;

public class MainActivity extends Activity {

    private MTheme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = MTheme.get(this);
        configureWindow();
        setContentView(buildScreen());
    }

    private View buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(theme.palette.background);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, MDim.statusBarHeight(this), 0, MDim.navigationBarHeight(this));

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(20f), dp(12f), dp(20f), dp(32f));
        scroll.addView(column, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        column.addView(buildHeader());
        column.addView(buildHero(), marginTop(dp(18f)));

        column.addView(sectionLabel("Buttons"));
        column.addView(buildButtons());

        column.addView(sectionLabel("Controls"));
        column.addView(buildControls());

        column.addView(sectionLabel("Input"));
        column.addView(buildInput());

        column.addView(sectionLabel("Grouped list"));
        column.addView(buildList());

        column.addView(sectionLabel("Progress"));
        column.addView(buildProgress());

        column.addView(buildSheetLauncher(), marginTop(dp(24f)));

        MAnimations.stagger(column, 42L, 60L, dp(14f), theme.motion.gentle);
        return scroll;
    }

    private View buildHeader() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = text("Aurora", theme.typography.display, theme.palette.label);
        TextView subtitle = text("tw.mmunki.material", theme.typography.subhead,
                theme.palette.labelSecondary);
        subtitle.setPadding(0, dp(4f), 0, 0);
        titles.addView(title);
        titles.addView(subtitle);
        row.addView(titles);

        final boolean dark = MaterialColor.isDark(theme.palette.background);
        MButton toggle = new MButton(this);
        toggle.setText(dark ? "Light mode" : "Dark mode");
        toggle.setVariant(MButton.Variant.GHOST);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MTheme.setDefault(dark ? MTheme.light() : MTheme.dark());
                recreate();
            }
        });
        row.addView(toggle);
        return row;
    }

    private View buildHero() {
        FrameLayout hero = new FrameLayout(this);
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{theme.palette.gradientStart, theme.palette.gradientEnd});
        background.setCornerRadius(dp(theme.shapes.large));
        hero.setBackground(background);
        hero.setClipToOutline(true);
        hero.setPadding(dp(22f), dp(24f), dp(22f), dp(22f));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(text("Smooth by design", theme.typography.title, 0xFFFFFFFF));
        TextView body = text("Spring physics, soft depth and a calm aurora palette.\n"
                + "Every control is drawn by the library, with no resource ids.",
                theme.typography.callout, 0xF2FFFFFF);
        body.setPadding(0, dp(8f), 0, dp(18f));
        content.addView(body);

        MButton open = new MButton(this);
        open.setText("Open sheet");
        open.setVariant(MButton.Variant.SECONDARY);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheet();
            }
        });
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        content.addView(open, buttonParams);

        hero.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return hero;
    }

    private View buildButtons() {
        MCard card = new MCard(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.addView(buttonRow(
                button("Continue", MButton.Variant.PRIMARY),
                button("Tonal", MButton.Variant.TONAL)));

        LinearLayout second = buttonRow(
                button("Secondary", MButton.Variant.SECONDARY),
                button("Ghost", MButton.Variant.GHOST));
        second.setPadding(0, dp(10f), 0, 0);
        content.addView(second);

        MButton danger = button("Delete item", MButton.Variant.DANGER);
        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Danger action", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams dangerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dangerParams.topMargin = dp(10f);
        content.addView(danger, dangerParams);

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildControls() {
        MCard card = new MCard(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        LinearLayout switchRow = new LinearLayout(this);
        switchRow.setOrientation(LinearLayout.HORIZONTAL);
        switchRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView switchLabel = text("Notifications", theme.typography.body, theme.palette.label);
        switchLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        switchRow.addView(switchLabel);
        final MSwitch toggle = new MSwitch(this);
        toggle.setChecked(true, false);
        switchRow.addView(toggle);
        content.addView(switchRow);

        TextView volumeLabel = text("Volume", theme.typography.footnote, theme.palette.labelSecondary);
        volumeLabel.setPadding(0, dp(18f), 0, dp(2f));
        content.addView(volumeLabel);

        MSlider slider = new MSlider(this);
        slider.setValue(0.38f, false);
        content.addView(slider, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView segmentLabel = text("Range", theme.typography.footnote, theme.palette.labelSecondary);
        segmentLabel.setPadding(0, dp(14f), 0, dp(8f));
        content.addView(segmentLabel);

        MSegmentedControl segments = new MSegmentedControl(this);
        segments.setItems("Day", "Week", "Month");
        segments.setSelectedIndex(0, false);
        content.addView(segments, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildInput() {
        MCard card = new MCard(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        final MTextField field = new MTextField(this);
        field.setLabel("Email address");
        field.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        content.addView(field, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        MButton validate = new MButton(this);
        validate.setText("Validate");
        validate.setVariant(MButton.Variant.TONAL);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = field.getText().toString().trim();
                if (value.indexOf('@') < 1 || value.endsWith("@")) {
                    field.setError("Enter a complete email address");
                } else {
                    field.clearError();
                    Toast.makeText(MainActivity.this, "Looks good", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayout.LayoutParams validateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        validateParams.topMargin = dp(12f);
        content.addView(validate, validateParams);

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildList() {
        MListSection section = new MListSection(this);

        MListCell profile = section.addCell("Profile", "Mmunki");
        profile.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_menu_myplaces));

        MListCell notifications = section.addCell("Notifications", "Immediate delivery");
        notifications.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_menu_info_details));
        notifications.setTrailingText("On");
        notifications.setChevronVisible(false);

        MListCell appearance = section.addCell("Appearance", "Aurora, system default");
        appearance.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_menu_preferences));

        return section;
    }

    private View buildProgress() {
        MCard card = new MCard(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView label = text("Syncing library", theme.typography.body, theme.palette.label);
        content.addView(label);

        MProgressBar determinate = new MProgressBar(this);
        determinate.setProgressImmediate(0.62f);
        LinearLayout.LayoutParams determinateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        determinateParams.topMargin = dp(12f);
        content.addView(determinate, determinateParams);

        MProgressBar indeterminate = new MProgressBar(this);
        indeterminate.setIndeterminate(true);
        LinearLayout.LayoutParams indeterminateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        indeterminateParams.topMargin = dp(14f);
        content.addView(indeterminate, indeterminateParams);

        LinearLayout circles = new LinearLayout(this);
        circles.setOrientation(LinearLayout.HORIZONTAL);
        circles.setGravity(Gravity.CENTER_VERTICAL);
        circles.setPadding(0, dp(18f), 0, 0);

        MCircularProgress circle = new MCircularProgress(this);
        circle.setProgressImmediate(0.72f);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(dp(64f), dp(64f));
        circles.addView(circle, circleParams);

        MCircularProgress spinner = new MCircularProgress(this);
        spinner.setIndeterminate(true);
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(dp(56f), dp(56f));
        spinnerParams.leftMargin = dp(18f);
        circles.addView(spinner, spinnerParams);

        TextView circleLabel = text("72%", theme.typography.headline, theme.palette.label);
        circleLabel.setPadding(dp(18f), 0, 0, 0);
        circles.addView(circleLabel);
        content.addView(circles);

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildSheetLauncher() {
        MButton button = new MButton(this);
        button.setText("Open bottom sheet");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheet();
            }
        });
        return button;
    }

    private void showSheet() {
        final MBottomSheet sheet = new MBottomSheet(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(text("Aurora sheet", theme.typography.headline, theme.palette.label));
        TextView body = text("Drag the handle down to dismiss. The sheet follows the finger "
                + "and hands the remaining distance to the spring.",
                theme.typography.body, theme.palette.labelSecondary);
        body.setPadding(0, dp(8f), 0, dp(18f));
        content.addView(body);

        MSwitch toggle = new MSwitch(this);
        toggle.setChecked(true, false);
        LinearLayout switchRow = new LinearLayout(this);
        switchRow.setOrientation(LinearLayout.HORIZONTAL);
        switchRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView switchLabel = text("Haptic feedback", theme.typography.body, theme.palette.label);
        switchLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        switchRow.addView(switchLabel);
        switchRow.addView(toggle);
        content.addView(switchRow);

        MButton close = new MButton(this);
        close.setText("Close");
        close.setVariant(MButton.Variant.TONAL);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheet.dismiss();
            }
        });
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        closeParams.topMargin = dp(18f);
        content.addView(close, closeParams);

        sheet.setContent(content);
        sheet.show();
    }

    private MButton button(String label, MButton.Variant variant) {
        MButton button = new MButton(this);
        button.setText(label);
        button.setVariant(variant);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, label, Toast.LENGTH_SHORT).show();
            }
        });
        return button;
    }

    private LinearLayout buttonRow(MButton first, MButton second) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams firstParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        firstParams.rightMargin = dp(10f);
        row.addView(first, firstParams);
        LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        row.addView(second, secondParams);
        return row;
    }

    private TextView sectionLabel(String label) {
        TextView view = text(label, theme.typography.footnote, theme.palette.labelSecondary);
        view.setPadding(0, dp(26f), 0, dp(8f));
        return view;
    }

    private TextView text(String value, MType type, int color) {
        TextView view = new TextView(this);
        type.apply(view);
        view.setText(value);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        return view;
    }

    private LinearLayout.LayoutParams marginTop(int top) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = top;
        return params;
    }

    private int dp(float value) {
        return MDim.dpi(this, value);
    }

    private void configureWindow() {
        Window window = getWindow();
        window.setStatusBarColor(theme.palette.background);
        window.setNavigationBarColor(theme.palette.background);
        int flags = window.getDecorView().getSystemUiVisibility();
        if (!MaterialColor.isDark(theme.palette.background)) {
            if (Build.VERSION.SDK_INT >= 23) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        } else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);
    }
}
