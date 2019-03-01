package com.dangdang.reader.dread.core.base;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;
import com.dangdang.reader.dread.holder.GalleryIndex;

public interface IPageAdapter {

	
	
	public View getView(DPageIndex pageIndex, View convertView, BaseReaderWidget parent);
	
	public void refreshView(DPageIndex pageIndex, BasePageView pageView, BaseReaderWidget parent);
	
	public void refreshView(DPageIndex pageIndex, GalleryIndex galleryIndex, BasePageView pageView, BaseReaderWidget parent);
	
	public static interface DataSetObserver {

		public void onChanged();

	}

	public static abstract class BasePageAdapter implements IPageAdapter {

		private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();

		public void registerObserver(DataSetObserver observer) {
			if (observer == null) {
				throw new IllegalArgumentException("The observer is null.");
			}
			synchronized (observers) {
				if (observers.contains(observer)) {
					throw new IllegalStateException("Observer " + observer
							+ " is already registered.");
				}
				observers.add(observer);
			}
		}

		public void unregisterObserver(DataSetObserver observer) {
			if (observer == null) {
				throw new IllegalArgumentException("The observer is null.");
			}
			synchronized (observers) {
				int index = observers.indexOf(observer);
				if (index == -1) {
					throw new IllegalStateException("Observer " + observer
							+ " was not registered.");
				}
				observers.remove(index);
			}
		}

		/**
		 * Remove all registered observer
		 */
		public void unregisterAll() {
			synchronized (observers) {
				observers.clear();
			}
		}

		public void notifyDataSetChanged() {
			synchronized (observers) {
				for (DataSetObserver observer : observers) {
					observer.onChanged();
				}
			}
		}

	}

}
