package com.dangdang.reader.dread.task;

public interface ITaskManager {

	/**
	 * 开始执行任务，不能重复开启
	 */
	public void startTask();
	
	/**
	 * 结束执行任务
	 */
	public void stopTask();
	
	/**
	 * 任务管理是否在运行
	 * @return
	 */
	public boolean isRun();
	
	public void putTask(BaseTask<?> e);
	
	public void putTaskToFirst(BaseTask<?> e);
	
	public void removeTask(BaseTask<?> e);
	
	public void clearTask();
	
}
