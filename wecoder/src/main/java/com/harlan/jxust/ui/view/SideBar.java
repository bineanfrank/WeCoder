package com.harlan.jxust.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.harlan.jxust.ui.adapter.ContactAdapter;
import com.harlan.jxust.utils.ScreenUtil;
import com.harlan.jxust.wecoder.R;

/**
 * Created by Harlan on 2016/4/8.
 */
public class SideBar extends View {
    private Paint paint;
    private TextView floatview;
    private float height;
    private RecyclerView mRVList;
    private Context context;

    public void setRecyclerView(RecyclerView mRVList) {
        this.mRVList = mRVList;
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private String[] sections = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "☆"};

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(ScreenUtil.sp2px(context, 12));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndscroll(MotionEvent event) {
        if (mRVList == null) {
            //check the mListView to avoid NPE. but the mListView shouldn't be null
            //need to check the call stack later
            return;
        }
        String floatChar = sections[sectionForPoint(event.getY())];
        floatview.setText(floatChar);

        ContactAdapter adapter = (ContactAdapter) mRVList.getAdapter();
        String[] adapterSections = (String[]) adapter.getSections();
        try {
            for (int i = adapterSections.length - 1; i > -1; i--) {
                if (adapterSections[i].equals(floatChar)) {
                    mRVList.smoothScrollToPosition(adapter.getPositionForSection(i));
                    break;
                }
            }
        } catch (Exception e) {
            // Log.e("setHeaderTextAndscroll", e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (floatview == null) {
                    floatview = (TextView) ((View) getParent()).findViewById(R.id.float_view);
                }
                setHeaderTextAndscroll(event);
                floatview.setVisibility(View.VISIBLE);
                setBackgroundResource(R.drawable.sidebar_background_pressed);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                setHeaderTextAndscroll(event);
                return true;
            }
            case MotionEvent.ACTION_UP:
                floatview.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                floatview.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
        }
        return super.onTouchEvent(event);
    }
}
