package com.dangdang.reader.dread.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.deque.BlockingDeque;
import com.dangdang.zframework.utils.deque.LinkedBlockingDeque;

public abstract class BaseTaskManager implements ITaskManager, Runnable {
	
	protected final static int MAX_TASKQUEUE_SIZE = 3;
	
	protected BlockingDeque<BaseTask<?>> taskQueue = new LinkedBlockingDeque<BaseTask<?>>();
	protected ExecutorService extService;
	protected BaseTask<?> tasking = null;
	
	private boolean run = false;
	private Thread thread;
	
	public BaseTaskManager(){
		thread = new Thread(this);
		extService = Executors.newFixedThreadPool(1);
	}
	
	public void putTask(BaseTask<?> e){
		if(!checkQueueValid()){
			return;
		}
		try {
			//taskQueue.put(e);
			taskQueue.putFirst(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void putTaskToFirst(BaseTask<?> e){
		if(!checkQueueValid()){
			return;
		}
		try {
			final boolean isTasking = theSameTasking(e);
			printLog(" putTaskToFirst e = " + e + ", " + getTaskSize() + ", " + isTasking);
			if(isTasking){
				return;
			}
			taskQueue.remove(e);
			taskQueue.putFirst(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 检查列表大小，如果大于 MAX_TASKQUEUE_SIZE 那么删除最早增加的任务
	 */
	public void checkQueueSize(){
		final int taskSize = getTaskSize();
		if(getTaskSize() > getMaxTaskSize()){
			taskQueue.removeLast();
			printLog(" checkQueueSize taskSize = " + taskSize);
		}
	}
	
	protected int getMaxTaskSize(){
		return MAX_TASKQUEUE_SIZE;
	}
	
	public int getTaskSize(){
		int size = 0;
		if(taskQueue != null){
			size = taskQueue.size();
		} else {
			printLogE(" taskQueue == null 1 ");
		}
		return size;
	}

	protected boolean theSameTasking(BaseTask<?> e) {
		return e.equals(tasking);
	}
	
	public boolean isTasking(){
		return tasking != null;
	}
	
	public void setTasking(BaseTask<?> task){
		tasking = task;
	}
	
	public void resetTasking(){
		tasking = null;
	}
	
	public void removeTask(BaseTask<?> e){
		//printLog(" removeTask contains = " + taskQueue.contains(e));
		if(!checkQueueValid()){
			return;
		}
		taskQueue.remove(e);
	}
	
	public void startTask(){
		run = true;
		thread.start();
	}
	
	public void stopTask(){
		try {
			run = false;
			clearTask();
			thread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRun(){
		return run;
	}
	
	public void clearTask(){
		if(!checkQueueValid()){
			return;
		}
		taskQueue.clear();
	}
	
	protected boolean checkQueueValid(){
		boolean valid = true;
		if(taskQueue == null){
			valid = false;
			printLogE(" taskQ == null 0 ");
		}
		return valid;
	}
	
	protected void printLog(String log){
		//LogM.i(getClass().getSimpleName(), log);
	}	
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
}