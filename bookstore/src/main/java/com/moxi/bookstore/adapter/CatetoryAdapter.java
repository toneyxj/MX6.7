package com.moxi.bookstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.CatetoryChanle;
import com.moxi.bookstore.bean.CatetoryChanleItem;
import com.moxi.bookstore.interfacess.ChanelInterf;
import com.moxi.bookstore.view.MyGridView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class CatetoryAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CatetoryChanle> catetoryList;
    private ChanelInterf listener;
    public CatetoryAdapter(Context cxt,List<CatetoryChanle> data,ChanelInterf listener){
        this.context=cxt;
        this.catetoryList=data;
        this.listener=listener;
    }

    @Override
    public int getGroupCount() {
        return catetoryList.size();
    }
    //child只要1个Item
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return catetoryList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return catetoryList.get(groupPosition).getCatetoryList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView=View.inflate(context, R.layout.item_catetory_group,null);
        TextView tv=(TextView)convertView.findViewById(R.id.tv);
        tv.setText(catetoryList.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View v, ViewGroup parent) {
        v=View.inflate(context,R.layout.item_catetory_child_gv,null);
        MyGridView myGridView=(MyGridView)v.findViewById(R.id.my_child_gv);
        final String groupname=catetoryList.get(groupPosition).getName();
        final List<CatetoryChanleItem> list=catetoryList.get(groupPosition).getCatetoryList();
        if (null!=list&&0!=list.size()){
            myGridView.setAdapter(new GridTextAdapter(context,list));
            myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listener.goChanelPage(groupname,list.get(position));
                }
            });

        }
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
