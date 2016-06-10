package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harlan.jxust.db.NewFriendManager;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.wecoder.R;

import butterknife.Bind;

/**
 * Created by Harlan on 2016/4/8.
 */
public class HeaderViewHolder extends BaseViewHolder {

    @Bind(R.id.tv_unread)
    TextView tv_unread;

    public HeaderViewHolder(Context context, ViewGroup root, OnRVClickListener listener) {
        super(context, root, R.layout.item_contact_list_header, listener);
    }

    @Override
    public void bindData(Object o) {
        //是否有好友添加的请求
        if (NewFriendManager.getInstance(getContext()).hasNewFriendInvitation()) {
            int size = NewFriendManager.getInstance(getContext()).getAllNewFriend().size();
            tv_unread.setVisibility(View.VISIBLE);
            tv_unread.setText(size + "");
        } else {
            tv_unread.setVisibility(View.GONE);
        }
    }
}
