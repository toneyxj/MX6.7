package com.dangdang.reader.dread.task;

import java.util.concurrent.Callable;

import com.dangdang.reader.dread.task.ITaskCallback.BaseTaskKey;
import com.dangdang.reader.dread.task.ITaskCallback.BaseTaskResult;
import com.dangdang.zframework.log.LogM;

/**
 * 任务基类
 * @author luxu
 *
 * @param <T>
 */
public abstract class BaseTask<T> implements Callable<T> {
	
	/**
	 * 处理自己的任务
	 * @return
	 * @throws Exception
	 */
	public abstract T processTask() throws Exception;
	
	@Override
	public T call() throws Exception {
		
		final T t = processTask();
		
		return t;
	}
	
	public void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
	public abstract static class BaseTaskImpl<T> extends BaseTask<T> {
		
		public abstract BaseTaskKey getTaskKey();
		
		public abstract BaseTaskResult getTaskResult();
		
	}
	
}