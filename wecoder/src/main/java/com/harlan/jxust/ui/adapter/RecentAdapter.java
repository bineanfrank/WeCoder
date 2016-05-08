package com.harlan.jxust.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.adapter.viewholder.BaseViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.RecentViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;

/**
 * Created by Harlan on 2016/4/6.
 */
public class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BmobIMConversation> conversations = new ArrayList<>();

    public RecentAdapter() {
    }

    /**
     * @param list
     */
    public void bindDatas(List<BmobIMConversation> list) {
        conversations.clear();
        if (null != list) {
            conversations.addAll(list);
        }
    }

    /**
     * 移除会话
     *
     * @param position
     */
    public void remove(int position) {
        conversations.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 获取会话
     *
     * @param position
     * @return
     */
    public BmobIMConversation getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecentViewHolder(parent.getContext(), parent, onRVClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(conversations.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    private OnRVClickListener onRVClickListener;

    public void setOnRVClickListener(OnRVClickListener onRVClickListener) {
        this.onRVClickListener = onRVClickListener;
    }
}
