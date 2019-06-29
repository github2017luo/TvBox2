package com.easy.tvbox.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.easy.tvbox.R;

public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private View footerView;// FooterView ListView的页脚

    private ProgressBar pbRefresh;// footerView中的 进图条控件

    private int currentItemCount; //表示当前已经存在的Item的数量

    private int totalCount; //全部Item的数量

    private OnLoad onLoad; //加载数据时执行的事件


    //region 构造函数
    public PullToRefreshListView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    //endregion

    //初始化方法
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        footerView = inflater.inflate(R.layout.footerview_refresh, null);
        pbRefresh = footerView.findViewById(R.id.pbRefresh);
        pbRefresh.setVisibility(GONE);

        //为ListView添加页脚
        this.addFooterView(footerView);

        this.setOnScrollListener(this);
    }

    /*
     * 回调方法:当ListView或者GridVie被滑动的时候在下一帧动滑动动画渲染完成之前调用此方法
     * */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /*
         * 如果当前的所存在的Item的数量已经是等于全部的Item的数量的那么
         * 我们需要进行自动刷新数据
         * */
        //已经滑动到底部，并且用户没有进行滑动操作时刷新数据
        if (currentItemCount == totalCount && scrollState == SCROLL_STATE_IDLE) {
            this.pbRefresh.setVisibility(VISIBLE);
            //执行刷新操作
            this.onLoad.loadData(this.currentItemCount, this.pbRefresh);
        }
    }

    /*
     * 回调方法：当list 或者 grid 正在被滑动的时候调用，调用在滑动完成之前
     * view 报告滚动状态的View
     * firstVisibleItem：第一个可见的Item的索引，如果当前没有可见的Cells 那么为0
     * visibleItemCount 当前可见列的数量
     * totalItemCount Adapter 数据项的数量
     * */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //在滚动事件中不断的更新 已经存在的Item的数量
        currentItemCount = firstVisibleItem + visibleItemCount;

        totalCount = totalItemCount;
    }

    //为onLoad赋值
    public void setOnLoad(OnLoad onLoad) {
        this.onLoad = onLoad;
    }

    /*
     * 回调函数的接口
     * */
    public interface OnLoad {
        void loadData(int beginIndex, ProgressBar pb);
    }

}
