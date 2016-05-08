package com.harlan.jxust.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Harlan on 2016/4/5.
 */
public class PreferencesUtil {

    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_SP_NAME = "wecoder_preference_name";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static PreferencesUtil sInstance;

    private PreferencesUtil(Context context) {
        sp = context.getSharedPreferences(KEY_SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static PreferencesUtil getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PreferencesUtil.class) {
                if (sInstance == null) {
                    sInstance = new PreferencesUtil(context);
                }
            }
        }
        return sInstance;
    }

    private void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    private int getInt(String key, int value) {
        return sp.getInt(key, 0);
    }

    private boolean getBoolean(String key, boolean value) {
        return sp.getBoolean(key, false);
    }

    private void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    private static final String FIRST_USE = "first_use";

    public boolean isFirstUse() {
        return getBoolean(FIRST_USE, false);
    }

    public void setFirstUse(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    private static final String KEY_THEME = "key_theme";

    public int getCurTheme() {
        return getInt(KEY_THEME, 0);
    }

    public void setCurTheme(String key, int value) {
        editor.putInt(key, value).commit();
    }

    private static final String KEY_PER_CAMERA = "key_per_camera";

    public boolean getPerCamera() {
        return getBoolean(KEY_PER_CAMERA, false);
    }

    public void setPerCamera(boolean value) {
        editor.putBoolean(KEY_PER_CAMERA, value).commit();
    }

    private static final String KEY_PER_AUDIO = "key_per_audio";

    public boolean getPerAudio() {
        return getBoolean(KEY_PER_AUDIO, false);
    }

    public void setPerAudio(boolean value) {
        editor.putBoolean(KEY_PER_AUDIO, value).commit();
    }

    private static final String KEY_PER_LOC = "key_per_loc";

    public boolean getPerLoc() {
        return getBoolean(KEY_PER_LOC, false);
    }

    public void setPerLoc(boolean value) {
        editor.putBoolean(KEY_PER_LOC, value).commit();
    }
}
