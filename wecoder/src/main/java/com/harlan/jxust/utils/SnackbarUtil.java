package com.harlan.jxust.utils;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.androidadvance.topsnackbar.TSnackbar;
import com.harlan.jxust.wecoder.R;

/**
 * Created by Harlan on 2016/1/18.
 */
public class SnackbarUtil {

    public static final int DURATION = TSnackbar.LENGTH_LONG / 2;

    private static int getColor(View view) {
        TypedArray array = view.getContext().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorPrimaryDark
        });
        int backgroundColorDark = array.getColor(0, 0xFF00FF);
        array.recycle();
        return backgroundColorDark;
    }

    /**
     * just show with a message
     *
     * @param view    parent view of TSnackbar
     * @param message
     */
    public static void show(View view, int message) {
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_SHORT);
        View snackbgView = snackbar.getView();
        snackbgView.setBackgroundColor(getColor(view));
        snackbar.show();
    }

    /**
     * @param view
     * @param message
     */
    public static void show(View view, String message) {
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_SHORT);
        View snackbgView = snackbar.getView();
        snackbgView.setBackgroundColor(getColor(view));
        snackbar.show();
    }

    public static void show(View view, String message, boolean length) {
        TSnackbar snackbar = TSnackbar.make(view, message, length ? TSnackbar.LENGTH_LONG : TSnackbar.LENGTH_SHORT);
        View snackbgView = snackbar.getView();
        snackbgView.setBackgroundColor(getColor(view));
        snackbar.show();
    }

    /**
     * just show with a message
     *
     * @param activity use this activity can get a view as the parent view of TSnackbar
     * @param message
     */
    public static void show(Activity activity, int message) {
        View view = activity.getWindow().getDecorView();
        show(view, message);
    }

    /**
     * just show with a message
     *
     * @param activity use this activity can get a view as the parent view of TSnackbar
     * @param message
     */
    public static void show(Activity activity, String message) {
        View view = activity.getWindow().getDecorView();
        show(view, message, false);
    }

    /**
     * show with a message and a pending action
     *
     * @param view     parent view of TSnackbar
     * @param message  show message
     * @param action   a action to do
     * @param listener interface of the action
     */
    public static void showAction(View view, int message, int action, View.OnClickListener listener) {
        TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_SHORT)
                .setAction(action, listener);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getColor(view));
        snackbar.show();
    }

    /**
     * show with a message and a pending action
     *
     * @param activity use this activity can get a view as the parent view of TSnackbar
     * @param message  show message
     * @param action   a action to do
     * @param listener interface of the action
     */
    public static void showAction(Activity activity, int message, int action, View.OnClickListener listener) {
        View view = activity.getWindow().getDecorView();
        showAction(view, message, action, listener);
    }
}
