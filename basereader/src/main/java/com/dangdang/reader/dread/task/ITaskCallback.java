package com.dangdang.reader.dread.task;

import com.dangdang.reader.dread.format.BaseBookManager.IAsynListener;

/**
 * 
 * @author luxu
 *
 */
public interface ITaskCallback {
	
	public void onTask(BaseTaskKey taskKey, BaseTaskResult result);
	
	public static class BaseTaskKey {
		
		private IAsynListener asynListener;

		public IAsynListener getAsynListener() {
			return asynListener;
		}

		public void setAsynListener(IAsynListener asynListener) {
			this.asynListener = asynListener;
		}

	}
	
	public static class BaseTaskResult {
		
	}
	
}