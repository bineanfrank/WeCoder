package com.harlan.jxust.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.harlan.jxust.db.NewFriend;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.adapter.viewholder.BaseViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.NewFriendHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * @author :smile
 * @project:NewFriendAdapter
 * @date :2016-04-27-14:18
 */
public class NewFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewFriend> adds = new ArrayList<>();

    public NewFriendAdapter() {
    }

    /**
     * @param list
     */
    public void bindDatas(List<NewFriend> list) {
        adds.clear();
        if (null != list) {
            adds.addAll(list);
        }
    }

    /**
     * 移除
     *
     * @param position
     */
    public void remove(int position) {
        adds.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 获取
     *
     * @param position
     * @return
     */
    public NewFriend getItem(int position) {
        return adds.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewFriendHolder(parent.getContext(), parent, onRecyclerViewListener);
}

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(adds.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return adds.size();
    }

    private OnRVClickListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRVClickListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
}
