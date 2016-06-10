package com.harlan.jxust.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.adapter.viewholder.BaseViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.SearchViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harlan on 2016/4/29.
 */
public class SearchContacAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> users = new ArrayList<>();

    public SearchContacAdapter() {
    }

    public void setDatas(List<User> list) {
        users.clear();
        if (null != list) {
            users.addAll(list);
        }
    }

    /**
     * 获取好友
     *
     * @param position
     * @return
     */
    public User getItem(int position) {
        return users.get(position);
    }

    /**
     * 移除好友
     *
     * @param position
     */
    public void remove(int position) {
        users.remove(position);
        notifyDataSetChanged();
    }

    public void clear(){
        users.clear();
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(parent.getContext(), parent, onRVClickListener, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private OnRVClickListener onRVClickListener;

    public void setOnRVClickListener(OnRVClickListener onRVClickListener) {
        this.onRVClickListener = onRVClickListener;
    }
}
