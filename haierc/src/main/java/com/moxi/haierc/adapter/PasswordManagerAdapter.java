package com.moxi.haierc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.model.PasswordClickModel;
import com.moxi.haierc.ports.OnCheckChange;

import java.util.List;

/**
 * Created by King on 2017/12/20.
 */

public class PasswordManagerAdapter extends BaseAdapter {

    private Context context;
    private List<PasswordClickModel> modelList;
    private OnCheckChange onCheckChange;

    public PasswordManagerAdapter(Context context, List<PasswordClickModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    public void setOnCheckChange(OnCheckChange onCheckChange) {
        this.onCheckChange = onCheckChange;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public PasswordClickModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PassViewHolder holder = null;
        if (convertView == null) {
            holder = new PassViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_password_manager, null);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_choose);
            holder.frameLayout = (FrameLayout) convertView.findViewById(R.id.frame_layout_check);
            holder.tvPassItem = (TextView) convertView.findViewById(R.id.tv_password_item);
            convertView.setTag(holder);
        } else {
            holder = (PassViewHolder) convertView.getTag();
        }
        if (modelList.get(position).isShowCheckBox()) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (modelList.get(position).isSelected()) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        holder.tvPassItem.setText(modelList.get(position).getItemName());
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCheckChange != null) {
                    onCheckChange.onChanged(position);
                }
            }
        });
        return convertView;
    }

    class PassViewHolder {
        FrameLayout frameLayout;
        CheckBox checkBox;
        TextView tvPassItem;
    }
}
