package com.harlan.jxust.ui.adapter.listener;

/**
 * Created by Harlan on 2016/4/6.
 * RecyclerView点击事件监听器
 */
public interface OnRVClickListener {

    void onItemClick(int position);

    boolean onItemLongClick(int position);
}
