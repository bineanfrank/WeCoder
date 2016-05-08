package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.wecoder.R;

import butterknife.Bind;

/**
 * Created by Harlan on 2016/4/8.
 */
public class FooterViewHolder extends BaseViewHolder {

    @Bind(R.id.tv_total)
    TextView tv_total;

    public FooterViewHolder(Context context, ViewGroup root, OnRVClickListener listener) {
        super(context, root, R.layout.item_contact_list_footer, listener);
    }

    @Override
    public void bindData(Object o) {
        tv_total.setText((int) o + "位联系人");
    }
}
