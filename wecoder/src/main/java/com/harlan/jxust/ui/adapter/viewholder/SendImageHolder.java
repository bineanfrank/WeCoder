package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harlan.jxust.ui.activity.MyInfoActivity;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.wecoder.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * 发送的文本类型
 */
public class SendImageHolder extends BaseViewHolder {

    @Bind(R.id.siv_avatar)
    SuperImageView siv_avatar;

    @Bind(R.id.iv_msg_failed_resend)
    ImageView iv_fail_resend;

    @Bind(R.id.timestamp)
    TextView tv_time;

    @Bind(R.id.siv_picture)
    SuperImageView siv_picture;

    @Bind(R.id.tv_percent)
    TextView tv_percent;

    BmobIMConversation c;

    public SendImageHolder(Context context, ViewGroup root, BmobIMConversation c, OnRVClickListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_send_picture, onRecyclerViewListener);
        this.c = c;
    }

    @Override
    public void bindData(Object o) {
        BmobIMMessage msg = (BmobIMMessage) o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        siv_avatar.loadImage(info.getAvatar(), false, R.drawable.default_avatar);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(true, msg);
        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus() || status == BmobIMSendStatus.UPLOADAILED.getStatus()) {
            iv_fail_resend.setVisibility(View.VISIBLE);
            tv_percent.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            iv_fail_resend.setVisibility(View.GONE);
            tv_percent.setVisibility(View.VISIBLE);
        } else {
            tv_percent.setVisibility(View.GONE);
            iv_fail_resend.setVisibility(View.GONE);
        }

        siv_picture.loadImageFixedSize(message.getLocalPath(), R.drawable.default_image, 320, 450);

        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyInfoActivity.class);
                intent.putExtra("from", MyInfoActivity.FROM_SELF);
                context.startActivity(intent);
            }
        });
        siv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toast("点击图片:" + (TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath() : message.getRemoteUrl()) + "");
//                if (onRecyclerViewListener != null) {
//                    onRecyclerViewListener.onItemClick(getAdapterPosition());
//                }
            }
        });

        siv_picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (onRecyclerViewListener != null) {
//                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
//                }
                return true;
            }
        });

        //重发
        iv_fail_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        iv_fail_resend.setVisibility(View.GONE);
                        tv_percent.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int i) {
                        tv_percent.setText(i + "%");
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            iv_fail_resend.setVisibility(View.GONE);
                            tv_percent.setVisibility(View.GONE);
                        } else {
                            iv_fail_resend.setVisibility(View.VISIBLE);
                            tv_percent.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void onProgress(int value) {
        tv_percent.setText(value + "%");
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
