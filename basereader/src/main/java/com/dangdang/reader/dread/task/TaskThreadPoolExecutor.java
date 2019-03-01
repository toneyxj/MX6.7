package com.dangdang.reader.dread.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dangdang.zframework.utils.deque.LinkedBlockingDeque;

public class TaskThreadPoolExecutor extends ThreadPoolExecutor {

	
	private final static int defaultCorePoolSize = 1;
	private final static int defaultMaximumPoolSize = 1;
	private final static long defaultKeepAliveTime = 60;
	private final static TimeUnit defaultUnit = TimeUnit.SECONDS;
	
	public TaskThreadPoolExecutor(){
		this(defaultCorePoolSize, defaultMaximumPoolSize, new LinkedBlockingDeque<Runnable>());
	}
	
	public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue){
		this(corePoolSize, maximumPoolSize, defaultKeepAliveTime, defaultUnit, workQueue);
	}
	
	public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	public static TaskThreadPoolExecutor getDefault(){
		return new TaskThreadPoolExecutor();
	}
	
	public void clearQueue(){
		getQueue().clear();
	}
	
}
