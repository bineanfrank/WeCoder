package com.harlan.jxust.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harlan.jxust.ui.activity.LocationActivity;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.view.imageview.SuperImageView;
import com.harlan.jxust.wecoder.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * 接收到的位置类型
 */
public class ReceiveLocationHolder extends BaseViewHolder {

    @Bind(R.id.siv_avatar)
    protected SuperImageView siv_avatar;

    @Bind(R.id.timestamp)
    protected TextView tv_time;

    @Bind(R.id.tv_location)
    protected TextView tv_location;

    public ReceiveLocationHolder(Context context, ViewGroup root, OnRVClickListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_received_location, onRecyclerViewListener);
    }

    @Override
    public void bindData(Object o) {
        BmobIMMessage msg = (BmobIMMessage) o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        System.out.println("info = " + info);
        siv_avatar.loadImage(info.getAvatar(), false, R.drawable.default_avatar);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(msg.getCreateTime());
        tv_time.setText(time);
        final BmobIMLocationMessage message = BmobIMLocationMessage.buildFromDB(msg);
        tv_location.setText(message.getAddress());
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

        siv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}