package com.dangdang.reader.dread.format;


import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.IBookParserListener;
import com.dangdang.reader.dread.core.base.IReaderApplication.IAbortParserListener;
import com.dangdang.reader.dread.task.ITaskCallback.BaseTaskKey;
import com.dangdang.reader.dread.task.ITaskCallback.BaseTaskResult;


public interface IBookManager {

	
	public void startRead(BaseReadInfo readInfo);
	
	public void reStartRead(BaseReadInfo readInfo);
	
	/**
	 * 异步获取
	 * @param param
	 */
	public void drawPage(BaseDrawPageParam param);
	
	/**
	 * 同步
	 * @param param
	 */
	public BaseDrawPageResult drawPageSync(BaseDrawPageParam param);
	
	public void destroy();
	
	public void registerComposingListener(IBookParserListener l);
	
	public boolean isCanExit();
	
	public void requestAbortComposing(IAbortParserListener l);
	
	public int getPageCount();
	
	public static class BaseDrawPageParam extends BaseTaskKey{
		
		/**
		 * 是否同步获取, true同步，false异步
		 */
		private boolean isSync = true;
		
		/**
		 * 画此图的样式
		 */
		private DrawPageStyle drawStyle;
		
		/**
		 * 是否同步获取
		 * @return
		 */
		public boolean isSync() {
			return isSync;
		}

		/**
		 * 是否同步获取, true同步，false异步
		 */
		public void setSync(boolean isSync) {
			this.isSync = isSync;
		}

		public DrawPageStyle getDrawStyle() {
			return drawStyle;
		}

		public void setDrawStyle(DrawPageStyle drawStyle) {
			this.drawStyle = drawStyle;
		}
		
	}
	
	public static class BaseDrawPageResult extends BaseTaskResult {
		
		
	}
	
	
	public static class DrawPageStyle {
		
		private int bgType;

		public int getBgType() {
			return bgType;
		}

		public void setBgType(int bgType) {
			this.bgType = bgType;
		}
	}
	
}
