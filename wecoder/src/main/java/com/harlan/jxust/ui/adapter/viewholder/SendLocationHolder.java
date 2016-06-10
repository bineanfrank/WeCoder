package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harlan.jxust.ui.activity.LocationActivity;
import com.harlan.jxust.ui.activity.MyInfoActivity;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.wecoder.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * 发送的语音类型
 */
public class SendLocationHolder extends BaseViewHolder {

    @Bind(R.id.siv_avatar)
    SuperImageView siv_avatar;

    @Bind(R.id.timestamp)
    TextView tv_time;

    @Bind(R.id.tv_location)
    TextView tv_location;

    @Bind(R.id.msg_status)
    ImageView iv_send_status;

    @Bind(R.id.pb_sending)
    ProgressBar pb_sending;

    BmobIMConversation c;

    public SendLocationHolder(Context context, ViewGroup root, BmobIMConversation c, OnRVClickListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_send_location, onRecyclerViewListener);
        this.c = c;
    }

    @Override
    public void bindData(Object o) {
        BmobIMMessage msg = (BmobIMMessage) o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        siv_avatar.loadImage(info.getAvatar(), false, R.drawable.default_avatar);

        final BmobIMLocationMessage message = BmobIMLocationMessage.buildFromDB(msg);
        tv_location.setText(message.getAddress());
        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SENDFAILED.getStatus()) {
            iv_send_status.setVisibility(View.VISIBLE);
            pb_sending.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            iv_send_status.setVisibility(View.GONE);
            pb_sending.setVisibility(View.VISIBLE);
        } else {
            iv_send_status.setVisibility(View.GONE);
            pb_sending.setVisibility(View.GONE);
        }
        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyInfoActivity.class);
                intent.putExtra("from", MyInfoActivity.FROM_SELF);
                context.startActivity(intent);
            }
        });

        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationActivity.startToSeeLocationDetail(context, message.getLatitude(), message.getLongitude());
            }
        });
        tv_location.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (onRecyclerViewListener != null) {
//                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
//                }
                return true;
            }
        });
        //重发
        iv_send_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        pb_sending.setVisibility(View.VISIBLE);
                        iv_send_status.setVisibility(View.GONE);
                        pb_sending.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            iv_send_status.setVisibility(View.GONE);
                            pb_sending.setVisibility(View.GONE);
                        } else {
                            iv_send_status.setVisibility(View.VISIBLE);
                            pb_sending.setVisibility(View.GONE);
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
