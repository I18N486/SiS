package com.iflytek.zst.dictationlibrary.online.transtask;

import android.support.v4.util.Pools;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by stzhang on 2018/12/20.
 * 备注:多任务队列并行执行或单任务队列列顺序执行,根据{@link #TaskQueue(int)} size设置任务队列数,
 * 无参的构造方法是单任务队列(单线程)顺序执行
 * size>1时多任务队列(多线程)并行执行(不能保证任务队列的执行顺序)
 */
public class TaskQueue<T extends Runnable> {
    public static final int DEFAULT_EXECUTOR_SIZE = 1;
    private BlockingDeque<T> mTaskQueue;
    private TaskExecutor[] mTaskExecutors;
    private Pools.SynchronizedPool<T> mPool;

    public TaskQueue() {
        this(DEFAULT_EXECUTOR_SIZE);
    }

    // 在开发者new队列的时候，要指定窗口数量。
    public TaskQueue(int size) {
        mTaskQueue = new LinkedBlockingDeque<>();
        mTaskExecutors = new TaskExecutor[size];
        mPool=new Pools.SynchronizedPool<>(size*3);
    }

    public void start(){
        stop();
        for (int i = 0; i < mTaskExecutors.length; i++) {
            mTaskExecutors[i]=new TaskExecutor(mTaskQueue,mPool);
            mTaskExecutors[i].start();
        }
    }

    public void stop(){
        if (mTaskExecutors != null) {
            for (TaskExecutor  taskExecutor:mTaskExecutors ) {
                if (taskExecutor != null) {
                    taskExecutor.quit();
                }
            }
        }
    }

    public void addTask(T runnable){
        mTaskQueue.add(runnable);
    }

    public T obtainTask() {
        return mPool.acquire();
    }

    public int getTaskSize(){
        return mTaskQueue.size();
    }

    public boolean isEmpty(){
        return mTaskQueue.isEmpty();
    }

    public BlockingDeque<T> getTaskQueue() {
        return mTaskQueue;
    }
}
