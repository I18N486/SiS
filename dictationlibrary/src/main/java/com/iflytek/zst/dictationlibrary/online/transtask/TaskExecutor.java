package com.iflytek.zst.dictationlibrary.online.transtask;

import android.support.v4.util.Pools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskExecutor extends Thread {
    private BlockingQueue<Runnable> mRunnables;
    private Pools.SynchronizedPool<Runnable> mPool;
    private boolean isRunning=true;
    private final ReentrantLock mLock = new ReentrantLock();
    private final Condition mCondition = mLock.newCondition();

    public TaskExecutor(BlockingQueue blockingQueue,Pools.SynchronizedPool pool) {
        this.mRunnables = blockingQueue;
        this.mPool = pool;
    }

    public void addTask(Runnable runnable) {
        mRunnables.add(runnable);
    }

    @Override
    public synchronized void start() {
        isRunning = true;
        super.start();
    }

    public void quit() {
        isRunning = false;
        interrupt();
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            try {
                Runnable runnable = mRunnables.take();
                if (runnable instanceof AbsTask) {
                    final AbsTask absTask = (AbsTask) runnable;
                    if (absTask.getDelay() != 0) {
                        Thread.sleep(absTask.getDelay());
                    }

                    while (absTask.isConditionLockBeforeRunning()) {
                        mLock.lock();
                        try {
                            absTask.setOnLockReleaseBeforeRunningListener(new AbsTask.OnLockReleaseListener() {
                                @Override
                                public void onLockRelease() {
                                    mLock.lock();
                                    try {
                                        mCondition.signal();
                                    }finally {
                                        mLock.unlock();
                                    }
                                    absTask.setOnLockReleaseBeforeRunningListener(null);
                                }
                            });
                            if (absTask.isConditionLockBeforeRunning()) {
                                if (absTask.getConditionLockBeforeRunningTime() == 0) {
                                    mCondition.await();
                                }else {
                                    mCondition.await(absTask.getConditionLockBeforeRunningTime(), TimeUnit.MILLISECONDS);
                                    absTask.setOnLockReleaseBeforeRunningListener(null);
                                    absTask.setConditionLockBeforeRunning(false);
                                }
                            }
                        }finally {
                            mLock.unlock();
                        }
                    }
                    //执行方法
                    runnable.run();

                    while (absTask.isConditionLockAfterRun()) {
                        mLock.lock();
                        try {
                            absTask.setOnLockReleaseAfterRunningListener(new AbsTask.OnLockReleaseListener() {
                                @Override
                                public void onLockRelease() {
                                    mLock.lock();
                                    try {
                                        mCondition.signal();
                                    }finally {
                                        mLock.unlock();
                                    }
                                    absTask.setOnLockReleaseAfterRunningListener(null);
                                }
                            });
                            if (absTask.isConditionLockAfterRun()) {
                                if (absTask.getConditionLockAfterRunTime() == 0) {
                                    mCondition.await();
                                } else {
                                    mCondition.await(absTask.getConditionLockAfterRunTime(), TimeUnit.MILLISECONDS);
                                    absTask.setOnLockReleaseAfterRunningListener(null);
                                    absTask.setConditionLockAfterRun(false);
                                }
                            }
                        }finally {
                            mLock.unlock();
                        }
                    }

                }else{
                    runnable.run();
                }
                mPool.release(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
