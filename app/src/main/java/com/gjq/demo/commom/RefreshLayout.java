package com.gjq.demo.commom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.gjq.demo.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * Created by Eric-t134 on 2016/11/4.
 * 将SmartRefreshLayout与RecyclerView进一步封装
 */

public class RefreshLayout extends RelativeLayout {

    private Context context;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private WrapContentLinearLayoutManager layoutManager;
    private BaseQuickAdapter adapter;
    private LayoutInflater factory;

    //最少有多少行才显示foot（没有更多数据）
    private int mLoadMoreMinSize = 10;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        factory = LayoutInflater.from(context);
        factory.inflate(R.layout.layout_refresh, this);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.myRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(layoutManager = new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public LinearLayoutManager getLinearLayoutManager(){
        return layoutManager;
    }

    /**
     * 刷新整个列表
     */
    public void notifyDataSetChanged(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 添加分割线
     *
     * @param itemDecoration
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (null != recyclerView) {
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    /**
     * 设置item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(SimpleClickListener onItemClickListener) {
        if (null != recyclerView && null != onItemClickListener) {
            recyclerView.addOnItemTouchListener(onItemClickListener);
        }
    }

    /**
     * 设置下拉刷新
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        if (null != refreshLayout && null != listener) {
            refreshLayout.setOnRefreshListener(listener);
        }
    }

    /**
     * 设置适配器和到底监听，无到底监听可设置为null
     *
     * @param adapter
     * @param requestLoadMoreListener
     * @param <T>
     * @param <K>
     */
    public <T, K extends BaseViewHolder> void setAdapterAndLoadMoreListener(BaseQuickAdapter<T, K> adapter, BaseQuickAdapter.RequestLoadMoreListener requestLoadMoreListener) {
        this.adapter = adapter;
        if (null != recyclerView) {
            adapter.openLoadAnimation();
            //关闭上拉加载
            adapter.setEnableLoadMore(false);
            if (null != requestLoadMoreListener) {
                adapter.setOnLoadMoreListener(requestLoadMoreListener, recyclerView);
            }
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (null != refreshLayout) {
            refreshLayout.autoRefresh();
        }
    }

    /**
     * 设置刷新状态
     */
    public void finishRefresh() {
        if (null != refreshLayout) {
            refreshLayout.finishRefresh();
            refreshLayout.setNoMoreData(false);//恢复上拉状态
        }
    }

    /**
     * 设置无数据时展示的布局,该方法需放在setAdapterAndLoadMoreListener之后
     *
     * @param layoutId
     */
    public void setEmptyView(int layoutId) {
        if (null != adapter) {
            adapter.setEmptyView(factory.inflate(layoutId, (ViewGroup) recyclerView.getParent(), false));
        }
    }

    /**
     * 设置无数据时展示的view,该方法需放在setAdapterAndLoadMoreListener之后
     *
     * @param view
     */
    public void setEmptyView(View view) {
        if (null != adapter) {
            adapter.setEmptyView(view);
        }
    }

    /**
     * 设置无数据时的文字提示,该方法需放在setAdapterAndLoadMoreListener之后
     *
     * @param emptyTips
     */
    public void setEmptyTips(String emptyTips) {
        if (TextUtils.isEmpty(emptyTips)) {
            throw new IllegalArgumentException("emptyTips can not null");
        }
        if (null != adapter) {
            TextView textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(getResources().getColor(R.color.c919191));
            textView.setText(emptyTips);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            textView.setLayoutParams(((ViewGroup)recyclerView.getParent()).getLayoutParams());
            adapter.setEmptyView(textView);
        }
    }

    /**
     * 设置新数据源,该方法需放在setAdapterAndLoadMoreListener之后
     *
     * @param data
     */
    public void setNewData(List data) {
        if (null != adapter) {
            adapter.setNewData(data);
            adapter.setEnableLoadMore(true);
//            adapter.setEnableLoadMore(data != null && data.size() >= mLoadMoreMinSize);
            if(data != null && data.size() < mLoadMoreMinSize) loadMoreEnd();
        }
    }

    /**
     * 获取列表数据源
     *
     * @return
     */
    public List getData() {
        if (null != adapter) {
            return adapter.getData();
        }
        return null;
    }

    /**
     * 更新列表指定位置item
     *
     * @param position
     */
    public void notifyItemChanged(int position) {
        if (null != adapter) {
            adapter.notifyItemChanged(position);
        }
    }

    /**
     * 移除列表指定位置item
     */
    public void notifyItemRemoved(int position) {
        if (null != adapter) {
            adapter.remove(position);
        }
    }

    /**
     * 新增数据,该方法需放在setAdapterAndLoadMoreListener之后
     *
     * @param data
     */
    public void addData(List data) {
        if (null != adapter) {
            adapter.addData(data);
            if(data.size() < mLoadMoreMinSize) loadMoreEnd();
        }
    }

    /**
     * 加载完成时调用该方法
     */
    public void loadComplete() {
        if (null != adapter) {
            adapter.loadMoreComplete();
        }
    }

    /**
     * 没有更多数据
     */
    public void loadMoreEnd() {
        if (null != adapter) {
//            adapter.loadMoreEnd();
            adapter.loadMoreEnd(adapter.getData().size() < mLoadMoreMinSize);
        }
    }

    /**
     * 没有更多数据
     */
    public void loadMoreFail() {
        if (null != adapter) {
            adapter.loadMoreFail();
        }
    }


    public int getmLoadMoreMinSize() {
        return mLoadMoreMinSize;
    }

    /**
     * 设置最少有多少行才显示foot
     *
     * @param mLoadMoreMinSize
     */
    public void setmLoadMoreMinSize(int mLoadMoreMinSize) {
        this.mLoadMoreMinSize = mLoadMoreMinSize;
    }

    /**
     * 加载更多失败时显示
     */
    public void showLoadMoreFailedView() {
        if (null != adapter) {
            adapter.loadMoreFail();
        }
    }

    /**
     * 设置背景色
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        if (null != recyclerView) {
            recyclerView.setBackgroundColor(color);
        }
    }

    /**
     * 添加头部
     *
     * @param view
     */
    public void addHeadView(View view) {
        if (null != adapter) {
            adapter.addHeaderView(view);
        }
    }

}
