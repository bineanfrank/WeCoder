package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.utils.EmojiHelper;
import com.harlan.jxust.wecoder.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * Created by Harlan on 2016/4/11.
 */
public class ReceiveTextHolder extends BaseViewHolder {

    @Bind(R.id.tv_timestamp)
    TextView tv_timestamp;

    @Bind(R.id.siv_avatar)
    SuperImageView siv_avatar;

    @Bind(R.id.tv_chat_received_text)
    TextView tv_chat_received_text;


    public ReceiveTextHolder(Context context, ViewGroup root, OnRVClickListener listener) {
        super(context, root, R.layout.item_chat_received_text, listener);
    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        tv_timestamp.setText(time);
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        siv_avatar.loadImage(info.getAvatar(), false, R.drawable.default_avatar);
        String content = message.getContent();
        tv_chat_received_text.setText(EmojiHelper.replace(context, content));
        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SnackbarUtil.show(itemView, "点击" + info.getName() + "的头像");
            }
        });
    }

    public void showTime(boolean isShow) {
        tv_timestamp.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
