package com.moxi.calendar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.moxi.calendar.adapter.CalendarAdapter;
import com.moxi.calendar.model.DateInfo;

import java.util.List;


public class OnItemClickListenerImpl implements OnItemClickListener {
	
	private CalendarAdapter adapter = null;
	private MainActivity activity = null;
	public OnItemClickListenerImpl(CalendarAdapter adapter, MainActivity activity) {
		this.adapter = adapter;
		this.activity = activity;
	}
	
	public void onItemClick(AdapterView<?> gridView, View view, int position, long id) {
		List<DateInfo> list =  activity.adapter.getList();
		if (!list.get(position).isThisMonth) {
			return;
		}
		adapter.setSelectedPosition(list.get(position).date);
		adapter.notifyDataSetInvalidated(); 
		activity.getclickEvents();
	}
	
}
