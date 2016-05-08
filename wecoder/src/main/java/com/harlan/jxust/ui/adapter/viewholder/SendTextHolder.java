package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.CircleImageView;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.utils.EmojiHelper;
import com.harlan.jxust.wecoder.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Harlan on 2016/4/11.
 */
public class SendTextHolder extends BaseViewHolder {

    private BmobIMConversation c;

    @Bind(R.id.tv_timestamp)
    TextView tv_timestamp;

    @Bind(R.id.siv_avatar)
    SuperImageView siv_avatar;

    @Bind(R.id.iv_msg_failed_resend)
    ImageView iv_msg_failed_resend;

    @Bind(R.id.tv_chat_send_text)
    TextView tv_chat_send_text;

    @Bind(R.id.pb_sending)
    ProgressBar progressBar;

    public SendTextHolder(Context context, ViewGroup root, BmobIMConversation c, OnRVClickListener listener) {
        super(context, root, R.layout.item_chat_send_text, listener);
        this.c = c;
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
        tv_chat_send_text.setText(EmojiHelper.replace(context, content));

        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            iv_msg_failed_resend.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            iv_msg_failed_resend.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            iv_msg_failed_resend.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //重发
        iv_msg_failed_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        progressBar.setVisibility(View.VISIBLE);
                        iv_msg_failed_resend.setVisibility(View.GONE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            iv_msg_failed_resend.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            iv_msg_failed_resend.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void showTime(boolean isShow) {
        tv_timestamp.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
