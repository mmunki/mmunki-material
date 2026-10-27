package tw.mmunki.material.component;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.OverScroller;

import java.util.ArrayList;

import tw.mmunki.material.MDim;

/**
 * Small, dependency-free recycler view with a linear layout manager, view
 * recycling, drag scrolling and fling.
 *
 * <p>It intentionally implements the subset that the library uses: vertical
 * or horizontal linear lists with one view type per position.</p>
 */
public class RecyclerView extends ViewGroup {

    public abstract static class ViewHolder {
        public final View itemView;
        int itemViewType;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView must not be null");
            }
            this.itemView = itemView;
        }

        public final View getItemView() {
            return itemView;
        }
    }

    public abstract static class Adapter<VH extends ViewHolder> {
        private RecyclerView recyclerView;

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        public final void notifyDataSetChanged() {
            if (recyclerView != null) {
                recyclerView.onDataSetChanged();
            }
        }

        public final void notifyItemChanged(int position) {
            notifyDataSetChanged();
        }
    }

    public abstract static class LayoutManager {
        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;

        private int orientation = VERTICAL;
        private int itemSpacing;

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation == HORIZONTAL ? HORIZONTAL : VERTICAL;
        }

        public int getItemSpacing() {
            return itemSpacing;
        }

        public void setItemSpacingPx(int itemSpacing) {
            this.itemSpacing = Math.max(0, itemSpacing);
        }
    }

    public static class LinearLayoutManager extends LayoutManager {
        public LinearLayoutManager() {
        }

        public LinearLayoutManager(Context context) {
        }

        public LinearLayoutManager(int orientation) {
            setOrientation(orientation);
        }
    }

    private final SparseArray<ArrayList<ViewHolder>> scrap =
            new SparseArray<ArrayList<ViewHolder>>();
    private final SparseArray<ViewHolder> attached = new SparseArray<ViewHolder>();
    private final OverScroller scroller;
    private final int touchSlop;
    private final int minimumFlingVelocity;

    private Adapter<?> adapter;
    private LayoutManager layoutManager = new LinearLayoutManager();
    private VelocityTracker velocityTracker;
    private int[] itemSizes;
    private int[] itemStarts;
    private int measuredCrossSize = -1;
    private int contentSize;
    private int scrollOffset;
    private int lastTouchX;
    private int lastTouchY;
    private boolean dragging;
    private boolean flingVertical = true;

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        minimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        scroller = new OverScroller(context);
        setClickable(true);
        setFocusable(true);
    }

    public void setAdapter(Adapter<?> adapter) {
        if (this.adapter != null) {
            this.adapter.recyclerView = null;
        }
        this.adapter = adapter;
        if (adapter != null) {
            adapter.recyclerView = this;
        }
        recycleAll();
        resetMeasurements();
        scrollOffset = 0;
        requestLayout();
        invalidate();
    }

    public Adapter<?> getAdapter() {
        return adapter;
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager != null ? layoutManager : new LinearLayoutManager();
        resetMeasurements();
        requestLayout();
        invalidate();
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setItemSpacingDp(float spacingDp) {
        layoutManager.setItemSpacingPx(MDim.dpi(getContext(), spacingDp));
        resetMeasurements();
        requestLayout();
        invalidate();
    }

    public void scrollToPosition(int position) {
        if (adapter == null) {
            return;
        }
        int count = adapter.getItemCount();
        if (count == 0) {
            return;
        }
        ensureMeasurements(currentCrossSize());
        int clamped = Math.max(0, Math.min(count - 1, position));
        scrollOffset = itemStarts[clamped];
        requestLayout();
        invalidate();
    }

    public void smoothScrollToPosition(int position) {
        if (adapter == null) {
            return;
        }
        int count = adapter.getItemCount();
        if (count == 0) {
            return;
        }
        ensureMeasurements(currentCrossSize());
        int clamped = Math.max(0, Math.min(count - 1, position));
        int target = itemStarts[clamped];
        target = Math.max(0, Math.min(maxScroll(), target));
        flingVertical = layoutManager.getOrientation() == LayoutManager.VERTICAL;
        if (flingVertical) {
            scroller.startScroll(0, scrollOffset, 0, target - scrollOffset, 300);
        } else {
            scroller.startScroll(scrollOffset, 0, target - scrollOffset, 0, 300);
        }
        postInvalidateOnAnimation();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        boolean vertical = layoutManager.getOrientation() == LayoutManager.VERTICAL;

        if (vertical) {
            int width = widthMode == MeasureSpec.UNSPECIFIED
                    ? Math.max(getSuggestedMinimumWidth(), MDim.dpi(getContext(), 320f))
                    : widthSize;
            ensureMeasurements(Math.max(0, width - getPaddingLeft() - getPaddingRight()));
            int height = heightMode == MeasureSpec.UNSPECIFIED
                    ? contentSize + getPaddingTop() + getPaddingBottom()
                    : heightSize;
            setMeasuredDimension(width, Math.max(height, getSuggestedMinimumHeight()));
        } else {
            int height = heightMode == MeasureSpec.UNSPECIFIED
                    ? Math.max(getSuggestedMinimumHeight(), MDim.dpi(getContext(), 120f))
                    : heightSize;
            ensureMeasurements(Math.max(0, height - getPaddingTop() - getPaddingBottom()));
            int width = widthMode == MeasureSpec.UNSPECIFIED
                    ? contentSize + getPaddingLeft() + getPaddingRight()
                    : widthSize;
            setMeasuredDimension(Math.max(width, getSuggestedMinimumWidth()), height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (adapter == null) {
            recycleAll();
            return;
        }
        int count = adapter.getItemCount();
        if (count == 0) {
            recycleAll();
            resetMeasurements();
            return;
        }
        boolean vertical = layoutManager.getOrientation() == LayoutManager.VERTICAL;
        ensureMeasurements(currentCrossSize());
        clampScroll();

        int viewport = vertical
                ? getHeight() - getPaddingTop() - getPaddingBottom()
                : getWidth() - getPaddingLeft() - getPaddingRight();
        if (viewport <= 0) {
            return;
        }
        int first = -1;
        int last = -1;
        int viewportEnd = scrollOffset + viewport;
        for (int i = 0; i < count; i++) {
            int itemStart = itemStarts[i];
            int itemEnd = itemStart + itemSizes[i];
            if (itemEnd > scrollOffset && itemStart < viewportEnd) {
                if (first < 0) {
                    first = i;
                }
                last = i;
            }
        }

        for (int i = attached.size() - 1; i >= 0; i--) {
            int position = attached.keyAt(i);
            if (position < first || position > last) {
                ViewHolder holder = attached.valueAt(i);
                attached.removeAt(i);
                removeViewInLayout(holder.itemView);
                addToScrap(holder);
            }
        }

        if (first < 0) {
            return;
        }
        int crossSize = currentCrossSize();
        for (int position = first; position <= last; position++) {
            ViewHolder holder = attached.get(position);
            if (holder == null) {
                int viewType = getItemViewType(position);
                holder = obtainHolder(viewType);
                holder.itemViewType = viewType;
                bindHolder(holder, position);
                addViewInLayout(holder.itemView, -1,
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
                        true);
                attached.put(position, holder);
            }
            measureItemExact(holder.itemView, itemSizes[position], crossSize, vertical);
            if (vertical) {
                int itemTop = getPaddingTop() + itemStarts[position] - scrollOffset;
                holder.itemView.layout(getPaddingLeft(), itemTop,
                        getPaddingLeft() + crossSize, itemTop + itemSizes[position]);
            } else {
                int itemLeft = getPaddingLeft() + itemStarts[position] - scrollOffset;
                holder.itemView.layout(itemLeft, getPaddingTop(),
                        itemLeft + itemSizes[position], getPaddingTop() + crossSize);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (adapter == null || adapter.getItemCount() == 0) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                lastTouchX = (int) event.getX();
                lastTouchY = (int) event.getY();
                dragging = false;
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
                int x = (int) event.getX();
                int y = (int) event.getY();
                int dx = lastTouchX - x;
                int dy = lastTouchY - y;
                if (!dragging && (Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop)) {
                    dragging = true;
                }
                if (dragging) {
                    boolean vertical =
                            layoutManager.getOrientation() == LayoutManager.VERTICAL;
                    scrollByPixels(vertical ? dy : dx);
                    lastTouchX = x;
                    lastTouchY = y;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(1000);
                    float vx = velocityTracker.getXVelocity();
                    float vy = velocityTracker.getYVelocity();
                    velocityTracker.recycle();
                    velocityTracker = null;
                    if (dragging) {
                        flingVertical =
                                layoutManager.getOrientation() == LayoutManager.VERTICAL;
                        int velocity = (int) (flingVertical ? -vy : -vx);
                        if (Math.abs(velocity) > minimumFlingVelocity) {
                            fling(velocity);
                        }
                    } else {
                        performClick();
                    }
                }
                dragging = false;
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                dragging = false;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollOffset = flingVertical ? scroller.getCurrY() : scroller.getCurrX();
            clampScroll();
            requestLayout();
            postInvalidateOnAnimation();
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.ListView");
        info.setScrollable(adapter != null && adapter.getItemCount() > 0);
        if (Build.VERSION.SDK_INT >= 19) {
            info.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(
                    AccessibilityNodeInfo.RangeInfo.RANGE_TYPE_INT, 0,
                    adapter != null ? adapter.getItemCount() : 0, 0));
        }
    }

    private void onDataSetChanged() {
        recycleAll();
        resetMeasurements();
        scrollOffset = 0;
        requestLayout();
        invalidate();
    }

    private void fling(int velocity) {
        int max = maxScroll();
        if (max <= 0) {
            return;
        }
        if (flingVertical) {
            scroller.fling(0, scrollOffset, 0, velocity, 0, 0, 0, max);
        } else {
            scroller.fling(scrollOffset, 0, velocity, 0, 0, max, 0, 0);
        }
        postInvalidateOnAnimation();
    }

    private void scrollByPixels(int delta) {
        scrollOffset = Math.max(0, Math.min(maxScroll(), scrollOffset + delta));
        requestLayout();
        invalidate();
    }

    private int maxScroll() {
        int viewport = layoutManager.getOrientation() == LayoutManager.VERTICAL
                ? getHeight() - getPaddingTop() - getPaddingBottom()
                : getWidth() - getPaddingLeft() - getPaddingRight();
        return Math.max(0, contentSize - Math.max(0, viewport));
    }

    private void clampScroll() {
        scrollOffset = Math.max(0, Math.min(maxScroll(), scrollOffset));
    }

    private int currentCrossSize() {
        return layoutManager.getOrientation() == LayoutManager.VERTICAL
                ? getWidth() - getPaddingLeft() - getPaddingRight()
                : getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private void resetMeasurements() {
        itemSizes = null;
        itemStarts = null;
        measuredCrossSize = -1;
        contentSize = 0;
    }

    private void ensureMeasurements(int crossSize) {
        if (adapter == null) {
            resetMeasurements();
            return;
        }
        int count = adapter.getItemCount();
        if (count == 0) {
            itemSizes = new int[0];
            itemStarts = new int[0];
            contentSize = 0;
            measuredCrossSize = crossSize;
            return;
        }
        if (itemSizes != null && itemSizes.length == count && measuredCrossSize == crossSize) {
            return;
        }
        measuredCrossSize = crossSize;
        itemSizes = new int[count];
        itemStarts = new int[count];
        int spacing = layoutManager.getItemSpacing();
        boolean vertical = layoutManager.getOrientation() == LayoutManager.VERTICAL;
        int cursor = 0;
        for (int i = 0; i < count; i++) {
            int viewType = getItemViewType(i);
            ViewHolder holder = obtainHolder(viewType);
            holder.itemViewType = viewType;
            bindHolder(holder, i);
            measureItem(holder.itemView, crossSize, vertical);
            int size = vertical ? holder.itemView.getMeasuredHeight()
                    : holder.itemView.getMeasuredWidth();
            if (vertical) {
                holder.itemView.measure(
                        MeasureSpec.makeMeasureSpec(crossSize, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
            } else {
                holder.itemView.measure(
                        MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(crossSize, MeasureSpec.EXACTLY));
            }
            addToScrap(holder);
            itemStarts[i] = cursor;
            itemSizes[i] = size;
            cursor += size;
            if (i < count - 1) {
                cursor += spacing;
            }
        }
        contentSize = cursor;
    }

    private void measureItem(View view, int crossSize, boolean vertical) {
        if (vertical) {
            view.measure(MeasureSpec.makeMeasureSpec(crossSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        } else {
            view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(crossSize, MeasureSpec.EXACTLY));
        }
    }

    private void measureItemExact(View view, int size, int crossSize, boolean vertical) {
        if (vertical) {
            view.measure(MeasureSpec.makeMeasureSpec(crossSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
        } else {
            view.measure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(crossSize, MeasureSpec.EXACTLY));
        }
    }

    private int getItemViewType(int position) {
        return adapter.getItemViewType(position);
    }

    @SuppressWarnings("unchecked")
    private void bindHolder(ViewHolder holder, int position) {
        ((Adapter<ViewHolder>) adapter).onBindViewHolder(holder, position);
    }

    @SuppressWarnings("unchecked")
    private ViewHolder createHolder(int viewType) {
        return ((Adapter<ViewHolder>) adapter).onCreateViewHolder(this, viewType);
    }

    private ViewHolder obtainHolder(int viewType) {
        ArrayList<ViewHolder> pool = scrap.get(viewType);
        if (pool != null && !pool.isEmpty()) {
            return pool.remove(pool.size() - 1);
        }
        return createHolder(viewType);
    }

    private void addToScrap(ViewHolder holder) {
        ArrayList<ViewHolder> pool = scrap.get(holder.itemViewType);
        if (pool == null) {
            pool = new ArrayList<ViewHolder>();
            scrap.put(holder.itemViewType, pool);
        }
        pool.add(holder);
    }

    private void recycleAll() {
        removeAllViews();
        for (int i = 0; i < attached.size(); i++) {
            addToScrap(attached.valueAt(i));
        }
        attached.clear();
    }
}
