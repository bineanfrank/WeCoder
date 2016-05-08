
package com.harlan.jxust.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.config.Constants;
import com.harlan.jxust.utils.PinyinUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

@SuppressLint("DefaultLocale")
public class UserDao {

    public static final String TABLE_NAME = "tb_user";
    public static final String COLUMN_NAME_OBJECTID = "objectid";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_USERNAME = "username";
    public static final String COLUMN_NAME_EMAIL = "email";
    public static final String COLUMN_NAME_PHONE = "phone";
    public static final String COLUMN_NAME_TOPC = "topc";
    public static final String COLUMN_NAME_SEX = "sex";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    private DBHelper dbHelper;
    private Context context;

    public UserDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
        this.context = context;
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<User> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE_NAME, null, null);
            for (User user : contactList) {
                ContentValues values = new ContentValues();
                setValues(user, values);
                db.replace(TABLE_NAME, null, values);
            }
        }
    }

    private void setValues(User user, ContentValues values) {
        values.put(COLUMN_NAME_OBJECTID, user.getObjectId());
        values.put(COLUMN_NAME_SEX, user.getSex());
        if (user.getNick() != null) {
            values.put(COLUMN_NAME_NICK, user.getNick());
        }
        if (user.getAvatar() != null) {
            values.put(COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (user.getMobilePhoneNumber() != null) {
            values.put(COLUMN_NAME_PHONE, user.getMobilePhoneNumber());
        }
        if (user.getEmail() != null) {
            values.put(COLUMN_NAME_EMAIL, user.getEmail());
        }
        if (user.getUsername() != null) {
            values.put(COLUMN_NAME_USERNAME, user.getUsername());
        }

        values.put(COLUMN_NAME_TOPC, user.getTopc());
    }

    /**
     * 获取好友list
     *
     * @return
     */
    @SuppressLint("DefaultLocale")
    public List<User> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> users = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String objectId = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_OBJECTID));
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_USERNAME));
                String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));
                String email = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EMAIL));
                int sex = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_SEX));
                String topC = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TOPC));
                User user = new User();
                user.setObjectId(objectId);
                user.setUsername(username);
                user.setNick(nick);
                user.setSex(sex);
                user.setAvatar(avatar);
                user.setEmail(email);
                user.setMobilePhoneNumber(phone);
                user.setTopc(topC);
                users.add(user);
            }
            cursor.close();
        }
        Collections.sort(users, new PinyinComparator());
        return users;
    }

    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            // TODO Auto-generated method stub
            char py1 = o1.getTopc().charAt(0);
            char py2 = o2.getTopc().charAt(0);
            if (py1 == py2) return 0;
            return py1 < py2 ? -1 : 1;
        }
    }

    /**
     * 删除一个联系人
     *
     * @param username
     */
    public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE_NAME, COLUMN_NAME_OBJECTID + " = ?", new String[]{username});
        }
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    public void saveContact(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        setValues(user, values);
        if (db.isOpen()) {
            db.replace(TABLE_NAME, null, values);
        }
    }
}
