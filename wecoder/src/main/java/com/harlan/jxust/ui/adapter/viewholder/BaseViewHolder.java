package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;

import butterknife.ButterKnife;

/**
 * Created by Harlan on 2016/4/6.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    OnRVClickListener onRVClickListener;
    protected Context context;

    public BaseViewHolder(Context context, ViewGroup root, int layoutRes, OnRVClickListener listener) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        this.context = context;
        ButterKnife.bind(this, itemView);
        this.onRVClickListener = listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(T t);

    @Override
    public void onClick(View v) {
        if (onRVClickListener != null) {
            onRVClickListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRVClickListener != null) {
            onRVClickListener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }
}
