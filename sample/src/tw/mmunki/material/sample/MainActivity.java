package tw.mmunki.material.sample;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tw.mmunki.material.MDim;
import tw.mmunki.material.MTheme;
import tw.mmunki.material.MType;
import tw.mmunki.material.MaterialColor;
import tw.mmunki.material.component.BottomNavigationBar;
import tw.mmunki.material.component.BottomSheet;
import tw.mmunki.material.component.Button;
import tw.mmunki.material.component.Card;
import tw.mmunki.material.component.CircularProgressIndicator;
import tw.mmunki.material.component.FloatingActionButton;
import tw.mmunki.material.component.FloatingNavigationBar;
import tw.mmunki.material.component.LinearProgressIndicator;
import tw.mmunki.material.component.ListCell;
import tw.mmunki.material.component.ListSection;
import tw.mmunki.material.component.RecyclerView;
import tw.mmunki.material.component.SegmentedControl;
import tw.mmunki.material.component.Slider;
import tw.mmunki.material.component.Switch;
import tw.mmunki.material.component.TextField;
import tw.mmunki.material.component.TopAppBar;

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
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(theme.palette.background);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);

        TopAppBar appBar = new TopAppBar(this);
        appBar.setTitle("Material Components");
        appBar.setSubtitle("tw.mmunki.material");
        appBar.setNavigationIcon(icon(android.R.drawable.ic_menu_sort_by_size));
        appBar.setNavigationContentDescription("Open navigation");
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Navigation", Toast.LENGTH_SHORT).show();
            }
        });
        appBar.addAction(icon(android.R.drawable.ic_menu_search),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                    }
                });
        appBar.addAction(icon(android.R.drawable.ic_menu_manage),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean dark = MaterialColor.isDark(theme.palette.background);
                        MTheme.setDefault(dark ? MTheme.light() : MTheme.dark());
                        recreate();
                    }
                });
        page.addView(appBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, 0, 0, dp(88f));
        page.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(16f), dp(8f), dp(16f), dp(24f));
        scroll.addView(column, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        column.addView(buildHero());
        column.addView(sectionLabel("Buttons"));
        column.addView(buildButtons());
        column.addView(sectionLabel("Controls"));
        column.addView(buildControls());
        column.addView(sectionLabel("Input"));
        column.addView(buildInput());
        column.addView(sectionLabel("Grouped list"));
        column.addView(buildList());
        column.addView(sectionLabel("Recycler view"));
        column.addView(buildRecycler());
        column.addView(sectionLabel("Progress"));
        column.addView(buildProgress());
        column.addView(sectionLabel("Floating navigation"));
        column.addView(buildFloatingNavigation());

        root.addView(page, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        BottomNavigationBar navigation = new BottomNavigationBar(this);
        navigation.addItem("Home", icon(android.R.drawable.ic_menu_compass));
        navigation.addItem("Components", icon(android.R.drawable.ic_menu_agenda))
                .setBadge(3);
        navigation.addItem("Settings", icon(android.R.drawable.ic_menu_preferences));
        navigation.setSelectedItem(1, false);
        navigation.setOnItemSelectedListener(new BottomNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(BottomNavigationBar bar, int index) {
                Toast.makeText(MainActivity.this, "Tab " + index, Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(navigation, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM));

        return root;
    }

    private View buildHero() {
        Card card = new Card(this);
        card.setElevated(true);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.addView(text("Build with Material", theme.typography.headline, theme.palette.label));
        TextView body = text("Flat color roles, a consistent type scale and the standard "
                + "Material 3 components. Every view is drawn by the library.",
                theme.typography.callout, theme.palette.labelSecondary);
        body.setPadding(0, dp(8f), 0, dp(16f));
        content.addView(body);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        Button open = new Button(this);
        open.setText("Open bottom sheet");
        open.setVariant(Button.Variant.PRIMARY);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheet();
            }
        });
        row.addView(open, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setImageDrawable(icon(android.R.drawable.ic_input_add));
        fab.setContentDescription("Add item");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Floating action", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams fabParams = new LinearLayout.LayoutParams(dp(56f), dp(56f));
        fabParams.leftMargin = dp(16f);
        row.addView(fab, fabParams);
        content.addView(row);

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildButtons() {
        Card card = new Card(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.addView(buttonRow(
                button("Filled", Button.Variant.PRIMARY),
                button("Tonal", Button.Variant.TONAL)));

        LinearLayout second = buttonRow(
                button("Outlined", Button.Variant.SECONDARY),
                button("Text", Button.Variant.GHOST));
        second.setPadding(0, dp(10f), 0, 0);
        content.addView(second);

        Button danger = button("Delete item", Button.Variant.DANGER);
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
        Card card = new Card(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        LinearLayout switchRow = new LinearLayout(this);
        switchRow.setOrientation(LinearLayout.HORIZONTAL);
        switchRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView switchLabel = text("Notifications", theme.typography.body, theme.palette.label);
        switchLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        switchRow.addView(switchLabel);
        final Switch toggle = new Switch(this);
        toggle.setChecked(true, false);
        switchRow.addView(toggle);
        content.addView(switchRow);

        TextView volumeLabel = text("Volume", theme.typography.footnote,
                theme.palette.labelSecondary);
        volumeLabel.setPadding(0, dp(18f), 0, dp(2f));
        content.addView(volumeLabel);

        Slider slider = new Slider(this);
        slider.setRange(0f, 100f);
        slider.setStepSize(10f);
        slider.setValue(40f, false);
        slider.setContentDescription("Volume");
        content.addView(slider, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView segmentLabel = text("Range", theme.typography.footnote,
                theme.palette.labelSecondary);
        segmentLabel.setPadding(0, dp(14f), 0, dp(8f));
        content.addView(segmentLabel);

        SegmentedControl segments = new SegmentedControl(this);
        segments.setItems("Day", "Week", "Month");
        segments.setSelectedIndex(0, false);
        content.addView(segments, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildInput() {
        Card card = new Card(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        final TextField field = new TextField(this);
        field.setLabel("Email address");
        field.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        content.addView(field, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button validate = new Button(this);
        validate.setText("Validate");
        validate.setVariant(Button.Variant.TONAL);
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
        ListSection section = new ListSection(this);

        ListCell profile = section.addCell("Profile", "Mmunki");
        profile.setLeadingIcon(icon(android.R.drawable.ic_menu_myplaces));

        ListCell notifications = section.addCell("Notifications", "Immediate delivery");
        notifications.setLeadingIcon(icon(android.R.drawable.ic_menu_info_details));
        notifications.setTrailingText("On");
        notifications.setChevronVisible(false);

        ListCell appearance = section.addCell("Appearance", "System default");
        appearance.setLeadingIcon(icon(android.R.drawable.ic_menu_preferences));

        return section;
    }

    private View buildRecycler() {
        Card card = new Card(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        RecyclerView recycler = new RecyclerView(this);
        recycler.setLayoutManager(new RecyclerView.LinearLayoutManager(this));
        recycler.setItemSpacingDp(8f);
        List<String> labels = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            labels.add("List item " + i);
        }
        recycler.setAdapter(new ItemAdapter(labels));
        content.addView(recycler, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(240f)));

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildProgress() {
        Card card = new Card(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView label = text("Syncing library", theme.typography.body, theme.palette.label);
        content.addView(label);

        LinearProgressIndicator determinate = new LinearProgressIndicator(this);
        determinate.setProgressImmediate(0.62f);
        LinearLayout.LayoutParams determinateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        determinateParams.topMargin = dp(12f);
        content.addView(determinate, determinateParams);

        LinearProgressIndicator indeterminate = new LinearProgressIndicator(this);
        indeterminate.setIndeterminate(true);
        LinearLayout.LayoutParams indeterminateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        indeterminateParams.topMargin = dp(14f);
        content.addView(indeterminate, indeterminateParams);

        LinearLayout circles = new LinearLayout(this);
        circles.setOrientation(LinearLayout.HORIZONTAL);
        circles.setGravity(Gravity.CENTER_VERTICAL);
        circles.setPadding(0, dp(18f), 0, 0);

        CircularProgressIndicator circle = new CircularProgressIndicator(this);
        circle.setProgressImmediate(0.72f);
        circles.addView(circle, new LinearLayout.LayoutParams(dp(64f), dp(64f)));

        CircularProgressIndicator spinner = new CircularProgressIndicator(this);
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

    private View buildFloatingNavigation() {
        Card card = new Card(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        FloatingNavigationBar bar = new FloatingNavigationBar(this);
        bar.addItem("Inbox", icon(android.R.drawable.ic_menu_send)).setBadge(2);
        bar.addItem("Search", icon(android.R.drawable.ic_menu_search));
        bar.addItem("Account", icon(android.R.drawable.ic_menu_myplaces));
        bar.setSelectedItem(0, false);
        bar.setOnItemSelectedListener(new BottomNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(BottomNavigationBar bar, int index) {
                Toast.makeText(MainActivity.this, "Floating tab " + index,
                        Toast.LENGTH_SHORT).show();
            }
        });
        content.addView(bar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        card.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private void showSheet() {
        final BottomSheet sheet = new BottomSheet(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(text("Material bottom sheet", theme.typography.headline,
                theme.palette.label));
        TextView body = text("Drag the handle down to dismiss. The sheet follows the finger "
                + "and finishes the movement with the shared motion token.",
                theme.typography.body, theme.palette.labelSecondary);
        body.setPadding(0, dp(8f), 0, dp(18f));
        content.addView(body);

        Switch toggle = new Switch(this);
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

        Button close = new Button(this);
        close.setText("Close");
        close.setVariant(Button.Variant.TONAL);
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

    private Button button(String label, Button.Variant variant) {
        Button button = new Button(this);
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

    private LinearLayout buttonRow(Button first, Button second) {
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
        TextView view = text(label, theme.typography.subhead, theme.palette.labelSecondary);
        view.setPadding(0, dp(24f), 0, dp(8f));
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

    private Drawable icon(int id) {
        return getResources().getDrawable(id);
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

    private static final class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        private final List<String> labels;

        ItemAdapter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListCell cell = new ListCell(parent.getContext());
            cell.setChevronVisible(true);
            return new ItemHolder(cell);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            String label = labels.get(position);
            holder.cell.setTitle(label);
            holder.cell.setSubtitle("Recycled row");
            holder.cell.setLeadingIcon(holder.cell.getContext()
                    .getResources().getDrawable(android.R.drawable.ic_menu_agenda));
        }

        @Override
        public int getItemCount() {
            return labels.size();
        }
    }

    private static final class ItemHolder extends RecyclerView.ViewHolder {
        final ListCell cell;

        ItemHolder(ListCell cell) {
            super(cell);
            this.cell = cell;
        }
    }
}
