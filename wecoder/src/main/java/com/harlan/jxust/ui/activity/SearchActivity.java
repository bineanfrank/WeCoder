package com.harlan.jxust.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.model.UserModel;
import com.harlan.jxust.ui.adapter.SearchContacAdapter;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.wecoder.R;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Harlan on 2016/4/29.
 */
public class SearchActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.et_search_box)
    EditText mEditText;
    @Bind(R.id.rv_search_contacts)
    RecyclerView rv_search_contacts;
    @Bind(R.id.btn_search)
    Button btn_search;

    private SearchContacAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar();
        initView();
    }

    private void setupToolbar() {
        mToolbar.setBackgroundColor(getColorPrimary());
        mToolbar.setTitle("搜索");
        mToolbar.setTitleTextAppearance(this, android.R.style.TextAppearance_Medium);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.collapseActionView();
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mAdapter = new SearchContacAdapter();
        rv_search_contacts.setAdapter(mAdapter);
        rv_search_contacts.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnRVClickListener(new OnRVClickListener() {
            @Override
            public void onItemClick(int position) {
                User user = mAdapter.getItem(position);
                Intent intent = new Intent(SearchActivity.this, MyInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent, null);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    mAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.btn_search)
    public void onSearchClick(View view) {

        String prefix = mEditText.getText().toString();
        if (TextUtils.isEmpty(prefix)) return;

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在搜索...");
        mDialog.show();

        //有字符变化时，动态获取搜索结果
        UserModel.getInstance().queryUsers(prefix, new FindListener() {
            @Override
            public void onSuccess(List list) {
                System.out.println(list);
                if (list != null && list.size() > 0) {
                    mAdapter.setDatas(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                }
                mDialog.dismiss();
            }

            @Override
            public void onError(int i, String s) {
                mDialog.dismiss();
            }
        });
    }
}
