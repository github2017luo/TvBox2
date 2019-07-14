

package com.easy.tvbox.tvview.tvRecycleView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import androidx.recyclerview.widget.RecyclerView;

import com.easy.tvbox.R;

public class SpannableGridLayoutManager extends GridLayoutManager {
    public static final String LOGTAG = "SpannableGridLM";

    private static final int DEFAULT_NUM_COLS = 3;
    private static final int DEFAULT_NUM_ROWS = 3;

    public static class SpannableItemEntry extends BaseLayoutManager.ItemEntry {
        private final int colSpan;
        private final int rowSpan;

        public SpannableItemEntry(int startLane, int anchorLane, int colSpan, int rowSpan) {
            super(startLane, anchorLane);
            this.colSpan = colSpan;
            this.rowSpan = rowSpan;
        }

        public SpannableItemEntry(Parcel in) {
            super(in);
            this.colSpan = in.readInt();
            this.rowSpan = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(colSpan);
            out.writeInt(rowSpan);
        }

        public static final Creator<SpannableItemEntry> CREATOR
                = new Creator<SpannableItemEntry>() {
            @Override
            public SpannableItemEntry createFromParcel(Parcel in) {
                return new SpannableItemEntry(in);
            }

            @Override
            public SpannableItemEntry[] newArray(int size) {
                return new SpannableItemEntry[size];
            }
        };
    }

    private boolean mMeasuring;

    public SpannableGridLayoutManager(Context context) {
        this(context, null);
    }

    public SpannableGridLayoutManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpannableGridLayoutManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, DEFAULT_NUM_COLS, DEFAULT_NUM_ROWS);
    }

    public SpannableGridLayoutManager(Orientation orientation, int numColumns, int numRows) {
        super(orientation, numColumns, numRows);
    }

    private static int getLaneSpan(LayoutParams lp, boolean isVertical) {
        return (isVertical ? lp.colSpan : lp.rowSpan);
    }

    private static int getLaneSpan(SpannableItemEntry entry, boolean isVertical) {
        return (isVertical ? entry.colSpan : entry.rowSpan);
    }

    @Override
    public boolean canScrollHorizontally() {
        return super.canScrollHorizontally() && !mMeasuring;
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically() && !mMeasuring;
    }

    @Override
    public int getLaneSpanForChild(View child) {
        return getLaneSpan((LayoutParams) child.getLayoutParams(), isVertical());
    }

    @Override
    public int getLaneSpanForPosition(int position) {
        final SpannableItemEntry entry = (SpannableItemEntry) getItemEntryForPosition(position);
        if (entry == null) {
            // add by zhousuqiang
            View view = getChildAt(position - getFirstVisiblePosition());
            if(null != view) {
                return getLaneSpanForChild(view);
            }
            throw new IllegalStateException("Could not find span for position " + position);
        }

        return getLaneSpan(entry, isVertical());
    }
    
    // add by zhousuqiang
//    public int getColSpanForPosition(int position) {
//        final SpannableItemEntry entry = (SpannableItemEntry) getItemEntryForPosition(position);
//        if (entry == null) {
//            View view = getChildAt(position - getFirstVisiblePosition());
//            if(null != view) {
//                return getLaneSpan(entry, !isVertical());
//            }
//            throw new IllegalStateException("Could not find span for position " + position);
//        }
//
//        return getLaneSpan(entry, !isVertical());
//    }

    @Override
    public void getLaneForPosition(Lanes.LaneInfo outInfo, int position, Direction direction) {
        final SpannableItemEntry entry = (SpannableItemEntry) getItemEntryForPosition(position);
        if (entry != null) {
            outInfo.set(entry.startLane, entry.anchorLane);
            return;
        }

        outInfo.setUndefined();
    }

    @Override
    protected void getLaneForChild(Lanes.LaneInfo outInfo, View child, Direction direction) {
        super.getLaneForChild(outInfo, child, direction);
        if (outInfo.isUndefined()) {
            getLanes().findLane(outInfo, getLaneSpanForChild(child), direction);
        }
    }

    private int getChildWidth(int colSpan) {
//        return (int)(getLanes().getLaneSize() * colSpan);
        return (int)(getLanes().getLaneSize()) * colSpan;
    }

    private int getChildHeight(int rowSpan) {
//        return (int)(getLanes().getLaneSize() * rowSpan);
        return (int)(getLanes().getLaneSize()) * rowSpan;
    }

    private int getWidthUsed(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getWidth() - getPaddingLeft() - getPaddingRight() - getChildWidth(lp.colSpan);
    }

    private int getHeightUsed(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getHeight() - getPaddingTop() - getPaddingBottom() - getChildHeight(lp.rowSpan);
    }
    
    @Override
    protected void measureChildWithMargins(View child) {
        // XXX: This will disable scrolling while measuring this child to ensure that
        // both width and height can use MATCH_PARENT properly.
        mMeasuring = true;
        measureChildWithMargins(child, getWidthUsed(child), getHeightUsed(child));
        mMeasuring = false;
    }

    @Override
    protected void moveLayoutToPosition(int position, int offset, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final boolean isVertical = isVertical();
        final Lanes lanes = getLanes();

        lanes.reset(0);

        for (int i = 0; i <= position; i++) {
            SpannableItemEntry entry = (SpannableItemEntry) getItemEntryForPosition(i);
            if (entry == null) {
                final View child = recycler.getViewForPosition(i);
                entry = (SpannableItemEntry) cacheChildLaneAndSpan(child, Direction.END);
            }

            mTempLaneInfo.set(entry.startLane, entry.anchorLane);

            // The lanes might have been invalidated because an added or
            // removed item. See BaseLayoutManager.invalidateItemLanes().
            if (mTempLaneInfo.isUndefined()) {
                lanes.findLane(mTempLaneInfo, getLaneSpanForPosition(i), Direction.END);
                entry.setLane(mTempLaneInfo);
            }

            lanes.getChildFrame(mTempRect, getChildWidth(entry.colSpan),
                    getChildHeight(entry.rowSpan), mTempLaneInfo, Direction.END);

            if (i != position) {
                pushChildFrame(entry, mTempRect, entry.startLane, getLaneSpan(entry, isVertical),
                        Direction.END);
            }
        }

        lanes.getLane(mTempLaneInfo.startLane, mTempRect);
        lanes.reset(Direction.END);
        lanes.offset(offset - (isVertical ? mTempRect.bottom : mTempRect.right));
    }

    @Override
    protected ItemEntry cacheChildLaneAndSpan(View child, Direction direction) {
        final int position = getPosition(child);

        mTempLaneInfo.setUndefined();

        SpannableItemEntry entry = (SpannableItemEntry) getItemEntryForPosition(position);
        if (entry != null) {
            mTempLaneInfo.set(entry.startLane, entry.anchorLane);
        }

        if (mTempLaneInfo.isUndefined()) {
            getLaneForChild(mTempLaneInfo, child, direction);
        }

        if (entry == null) {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            entry = new SpannableItemEntry(mTempLaneInfo.startLane, mTempLaneInfo.anchorLane,
                    lp.colSpan, lp.rowSpan);
            setItemEntryForPosition(position, entry);
        } else {
            entry.setLane(mTempLaneInfo);
        }

        return entry;
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        super.checkLayoutParams(lp);
        if (lp.width != LayoutParams.MATCH_PARENT ||
            lp.height != LayoutParams.MATCH_PARENT) {
            return false;
        }

        if (lp instanceof LayoutParams) {
            final LayoutParams spannableLp = (LayoutParams) lp;
            
            if (isVertical()) {
                return (spannableLp.rowSpan >= 1 && spannableLp.colSpan >= 1 &&
                        spannableLp.colSpan <= getLaneCount());
            } else {
                return (spannableLp.colSpan >= 1 && spannableLp.rowSpan >= 1 &&
                        spannableLp.rowSpan <= getLaneCount());
            }
        }

        return false;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        final LayoutParams spannableLp = new LayoutParams((MarginLayoutParams) lp);
        spannableLp.width = LayoutParams.MATCH_PARENT;
        spannableLp.height = LayoutParams.MATCH_PARENT;

        if (lp instanceof LayoutParams) {
            final LayoutParams other = (LayoutParams) lp;
            if (isVertical()) {
                spannableLp.colSpan = Math.max(1, Math.min(other.colSpan, getLaneCount()));
                spannableLp.rowSpan = Math.max(1, other.rowSpan);
            } else {
                spannableLp.colSpan = Math.max(1, other.colSpan);
                spannableLp.rowSpan = Math.max(1, Math.min(other.rowSpan, getLaneCount()));
            }
        }
        
        return spannableLp;
    }

    @Override
    public LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    public static class LayoutParams extends TvRecyclerView.LayoutParams {
        private static final int DEFAULT_SPAN = 1;

        public int rowSpan;
        public int colSpan;

        public LayoutParams(int width, int height) {
            super(width, height);
            rowSpan = DEFAULT_SPAN;
            colSpan = DEFAULT_SPAN;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView_SpannableGridViewChild);
            colSpan = Math.max(
                    DEFAULT_SPAN, a.getInt(R.styleable.TvRecyclerView_SpannableGridViewChild_tv_colSpan, -1));
            rowSpan = Math.max(
                    DEFAULT_SPAN, a.getInt(R.styleable.TvRecyclerView_SpannableGridViewChild_tv_rowSpan, -1));
            a.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
            init(other);
        }

        public LayoutParams(MarginLayoutParams other) {
            super(other);
            init(other);
        }

        private void init(ViewGroup.LayoutParams other) {
            if (other instanceof LayoutParams) {
                final LayoutParams lp = (LayoutParams) other;
                rowSpan = lp.rowSpan;
                colSpan = lp.colSpan;
            } else {
                rowSpan = DEFAULT_SPAN;
                colSpan = DEFAULT_SPAN;
            }
        }
    }
}
