package com.moxi.bookstore.adapter;

import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.bean.CatetoryChanleItem;

/**
 * channel适配器
 * 
 * @author admin
 * 
 */
public class GridTextAdapter extends BaseAdapter {

	private List<CatetoryChanleItem> data;
	private Context mContext;

	public GridTextAdapter(Context context, List<CatetoryChanleItem> data) {
		this.mContext = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		if (null==data||data.size()==0)
			return 0;
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler viewHodler = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_gridview_txt, null);
			viewHodler = new ViewHodler();
			viewHodler.mTvType = (TextView) convertView
					.findViewById(R.id.tv_item_goods_type);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHodler) convertView.getTag();
		}


		viewHodler.mTvType.setText(data.get(position).getName());
		//viewHodler.mTvType.setTag(data.get(position));
		return convertView;
	}

	private class ViewHodler {
		private TextView mTvType;
	}

}
