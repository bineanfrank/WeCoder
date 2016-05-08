package com.harlan.jxust;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.model.i.UpdateCacheListener;
import com.harlan.jxust.ui.activity.MainActivity;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Harlan on 2016/4/6.
 */
public class WeCoderMessageHandler extends BmobIMMessageHandler {

    private Context context;

    public WeCoderMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
        //检测更新下用户的信息
        UserModel.getInstance().updateUserInfo(event, new UpdateCacheListener() {
            @Override
            public void done(BmobException e) {
                BmobIMMessage msg = event.getMessage();
                if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {//用户自定义的消息类型，其类型值均为0
                    //自行处理自定义消息类型
                    Logger.i(msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra());
                    Toast.makeText(context, msg.getMsgType() + "," + msg.getContent(), Toast.LENGTH_SHORT).show();
                } else {//SDK内部内部支持的消息类型
                    if (BmobNotificationManager.getInstance(context).isShowNotification()) {//如果需要显示通知栏，SDK提供以下两种显示方式：
                        Intent pendingIntent = new Intent(context, MainActivity.class);
                        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //1、多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
                        BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);
                    } else {//直接发送消息事件
                        Logger.i("当前处于应用内，发送event");
                        EventBus.getDefault().post(event);
                    }
                }
            }
        });
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        Map<String, List<MessageEvent>> map = event.getEventMap();
        Logger.i("离线消息属于" + map.size() + "个用户");
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            //挨个检测离线用户信息是否需要更新
            UserModel.getInstance().updateUserInfo(list.get(0), new UpdateCacheListener() {
                @Override
                public void done(BmobException e) {
                    EventBus.getDefault().post(event);
                }
            });
        }
    }
}
