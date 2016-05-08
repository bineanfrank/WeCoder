package com.harlan.jxust.model;

import android.content.Context;

import com.harlan.jxust.WeCoderApplication;

/**
 * @author :smile
 * @project:BaseModel
 * @date :2016-01-23-10:37
 */
public abstract class BaseModel {

    public int CODE_NULL = 1000;
    public static int CODE_NOT_EQUAL = 1001;
    public static int CODE_NOT_LEGAL = 1002;
    public static final int DEFAULT_LIMIT = 50;

    public Context getContext() {
        return WeCoderApplication.getInstance();
    }
}
