package com.harlan.jxust.model;

import android.text.TextUtils;

import com.harlan.jxust.bean.Friend;
import com.harlan.jxust.bean.User;
import com.harlan.jxust.model.i.QueryUserListener;
import com.harlan.jxust.model.i.UpdateCacheListener;
import com.harlan.jxust.utils.PinyinUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * @author :smile
 * @project:UserModel
 * @date :2016-01-22-18:09
 */
public class UserModel extends BaseModel {

    private static UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param listener
     */
    public void login(String username, String password, final LogInListener listener) {

        if (TextUtils.isEmpty(username)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写密码"));
            return;
        }

        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                listener.done(getCurrentUser(), null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.done(user, new BmobException(i, s));
            }
        });
    }

    /**
     * 退出登录
     */
    public void logout() {
        BmobUser.logOut(getContext());
    }

    public User getCurrentUser() {
        return BmobUser.getCurrentUser(getContext(), User.class);
    }

    /**
     * 注册
     *
     * @param avatar
     * @param username
     * @param password
     * @param listener
     */
    public void register(String avatar, final String username, final String password, final LogInListener listener) {

        Logger.d("register begins.");

        final User user = new User();

        final BmobFile file = new BmobFile(new File(avatar));
        file.upload(getContext(), new UploadFileListener() {
            @Override
            public void onSuccess() {
                user.setAvatar(file.getFileUrl(getContext()));
                user.setUsername(username);
                user.setPassword(password);
                user.setTopc(PinyinUtil.getTopC(username) + "");
                user.setSex(0);
                user.signUp(getContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Logger.d("register success.");
                        listener.done(null, null);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Logger.d("register failed.");
                        listener.done(null, new BmobException(i, s));
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                user.setAvatar("");
                user.setUsername(username);
                user.setPassword(password);
                user.setTopc(PinyinUtil.getTopC(username) + "");
                user.setSex(0);
                user.signUp(getContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Logger.d("register success.");
                        listener.done(null, null);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Logger.d("register failed.");
                        listener.done(null, new BmobException(i, s));
                    }
                });
            }
        });
    }

    /**
     * 查询用户信息
     *
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    listener.internalDone(list.get(0), null);
                } else {
                    listener.internalDone(new BmobException(000, "查无此人"));
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.internalDone(new BmobException(i, s));
            }
        });
    }


    /**
     * 查询所有用户
     *
     * @param listener
     */
    public void queryUsers(String prefix, final FindListener<User> listener) {
        BmobQuery<User> query = new BmobQuery<>();
        //去掉当前用户
        try {
            BmobUser user = BmobUser.getCurrentUser(getContext());
            query.addWhereNotEqualTo("username", user.getUsername());
            query.addWhereContains("username", prefix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        query.setLimit(DEFAULT_LIMIT);
        query.order("-createdAt");
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(CODE_NULL, "没有联系人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    private static List<Friend> contacts = new ArrayList<>();

    public void clearContacts() {
        this.contacts.clear();
    }

    public static void setContacts(List<Friend> contacts) {
        UserModel.contacts = contacts;
    }

    public List<Friend> getContacts() {
        return contacts;
    }

    /**
     * 更新用户资料和会话资料
     *
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        String username = info.getName();
        String title = conversation.getConversationTitle();
        Logger.i("" + username + "," + title);
        //sdk内部，将新会话的会话标题用objectId表示，因此需要比对用户名和会话标题--单聊，后续会根据会话类型进行判断
        if (!username.equals(title)) {
            UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        String avatar = s.getAvatar();
                        Logger.i("query success：" + name + "," + avatar);
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //更新用户资料
                        BmobIM.getInstance().updateUserInfo(info);
                        //更新会话资料
                        BmobIM.getInstance().updateConversation(conversation);
                    } else {
                        Logger.e(e);
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.internalDone(null);
        }
    }

    /**
     * 同意添加好友：1、发送同意添加的请求，2、添加对方到自己的好友列表中
     */
    public void agreeAddFriend(User friend, SaveListener listener) {
        Friend f = new Friend();
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        f.setUser(user);
        f.setFriendUser(friend);
        f.save(getContext(), listener);
    }

    /**
     * 查询好友
     *
     * @param listener
     */
    public void queryFriends(final FindListener<Friend> listener) {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(getContext(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list != null && list.size() > 0) {
                    setContacts(new ArrayList<>(list));
                    listener.onSuccess(list);
                } else {
                    listener.onError(0, "暂无联系人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 删除好友
     *
     * @param f
     * @param listener
     */
    public void deleteFriend(Friend f, DeleteListener listener) {
        Friend friend = new Friend();
        friend.delete(getContext(), f.getObjectId(), listener);
    }
}
