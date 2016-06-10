package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harlan.jxust.ui.activity.MyInfoActivity;
import com.harlan.jxust.ui.adapter.listener.AudioPlayListener;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.wecoder.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * 发送的语音类型
 */
public class SendVoiceHolder extends BaseViewHolder {

    @Bind(R.id.siv_avatar)
    SuperImageView siv_avatar;
    @Bind(R.id.iv_msg_failed_resend)
    ImageView iv_fail_resend;
    @Bind(R.id.timestamp)
    TextView tv_time;
    @Bind(R.id.tv_length)
    TextView tv_length;
    @Bind(R.id.iv_voice)
    ImageView iv_voice;

    @Bind(R.id.pb_sending)
    ProgressBar progress_load;

    BmobIMConversation c;

    public SendVoiceHolder(Context context, ViewGroup root, BmobIMConversation c, OnRVClickListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_send_voice, onRecyclerViewListener);
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
        //使用buildFromDB方法转化成指定类型的消息
        final BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(true, msg);
        tv_length.setText(message.getDuration() + "\''");
        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus() || status == BmobIMSendStatus.UPLOADAILED.getStatus()) {//发送失败/上传失败
            iv_fail_resend.setVisibility(View.VISIBLE);
            progress_load.setVisibility(View.GONE);
            tv_length.setVisibility(View.INVISIBLE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
            tv_length.setVisibility(View.INVISIBLE);
        } else {//发送成功
            iv_fail_resend.setVisibility(View.GONE);
            progress_load.setVisibility(View.GONE);
            tv_length.setVisibility(View.VISIBLE);
        }

        iv_voice.setOnClickListener(new AudioPlayListener(getContext(), message, iv_voice));

        iv_voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (onRecyclerViewListener != null) {
//                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
//                }
                return true;
            }
        });

        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyInfoActivity.class);
                intent.putExtra("from", MyInfoActivity.FROM_SELF);
                context.startActivity(intent);
            }
        });
        //重发
        iv_fail_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        progress_load.setVisibility(View.VISIBLE);
                        iv_fail_resend.setVisibility(View.GONE);
                        tv_length.setVisibility(View.GONE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            tv_length.setVisibility(View.GONE);
                            iv_fail_resend.setVisibility(View.GONE);
                            progress_load.setVisibility(View.GONE);
                        } else {
                            iv_fail_resend.setVisibility(View.VISIBLE);
                            progress_load.setVisibility(View.GONE);
                            tv_length.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
