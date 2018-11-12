package com.morladim.morganrss.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.morladim.morganrss.R;

/**
 * @author morladim
 * @date 2018/6/26
 */
public class MenuAdapter extends BaseExpandableListAdapter {

    public static final String[] GROUP_LIST = {"分組列表", "設置管理", "關於"};

    private Context context;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return GROUP_LIST.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 2;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 1000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_main_expandable_group, parent, false);
            groupHolder = new GroupHolder();
            groupHolder.textView = convertView.findViewById(R.id.text);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.textView.setText(GROUP_LIST[groupPosition]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_main_expandable_item, parent, false);
            childHolder = new ChildHolder();
            childHolder.textView = convertView.findViewById(R.id.text);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.textView.setText(groupPosition + " " + childPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    class GroupHolder {

        TextView textView;
    }

    class ChildHolder {
        TextView textView;
    }

}
