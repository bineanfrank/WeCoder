package com.harlan.jxust.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.harlan.jxust.bean.User;
import com.harlan.jxust.ui.adapter.listener.OnRVClickListener;
import com.harlan.jxust.ui.adapter.viewholder.BaseViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.ContactViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.FooterViewHolder;
import com.harlan.jxust.ui.adapter.viewholder.HeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :Harlan
 * @project:ContactAdapter
 * @date :2016-01-22-14:18
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

    private static final int VIEW_TYPE_HEADER = 0x1;
    private static final int VIEW_TYPE_CONTACT_WITH_TOPC = 0x2;
    private static final int VIEW_TYPE_CONTACT_NO_TOPC = 0x3;
    private static final int VIEW_TYPE_FOOTER = 0x4;

    private List<User> users = new ArrayList<>();
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private List<String> list;

    public ContactAdapter() {
        list = new ArrayList<>();
    }

    public void setDatas(List<User> list) {
        users.clear();
        if (null != list) {
            users.addAll(list);
        }
    }

    /**
     * 获取好友
     *
     * @param position
     * @return
     */
    public User getItem(int position) {
        return users.get(position - 1);
    }

    /**
     * 移除好友
     *
     * @param position
     */
    public void remove(int position) {
        users.remove(position - 1);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                holder = new HeaderViewHolder(parent.getContext(), parent, onRVClickListener);
                break;
            case VIEW_TYPE_FOOTER:
                holder = new FooterViewHolder(parent.getContext(), parent, onRVClickListener);
                break;
            default:
                holder = new ContactViewHolder(parent.getContext(), parent, onRVClickListener, viewType == VIEW_TYPE_CONTACT_NO_TOPC ? false : true);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {

        } else if (position == users.size() + 1) {
            ((BaseViewHolder) holder).bindData(users.size());
        } else {
            ((BaseViewHolder) holder).bindData(users.get(position - 1));
        }
    }

    /**
     * 头部，尾部以及TOPC是否显示四种布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (position == users.size() + 1) {
            System.out.println("Footer View, position = " + position);
            return VIEW_TYPE_FOOTER;
        } else {
            if (position == 1) {
                return VIEW_TYPE_CONTACT_WITH_TOPC;
            } else {
                String topC = users.get(position - 1).getTopc();
                if (topC != null && !topC.equals(getItem(position - 1).getTopc())) {
                    if ("".equals(topC)) return VIEW_TYPE_CONTACT_NO_TOPC;
                    else return VIEW_TYPE_CONTACT_WITH_TOPC;
                } else {
                    return VIEW_TYPE_CONTACT_NO_TOPC;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size() + 2;
    }

    private OnRVClickListener onRVClickListener;

    public void setOnRVClickListener(OnRVClickListener onRVClickListener) {
        this.onRVClickListener = onRVClickListener;
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getItemCount();
        list = new ArrayList<>();
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        list.add("*");
        for (int i = 1; i < count - 1; i++) {
            String letter = getItem(i).getTopc();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return positionOfSection.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }
}
