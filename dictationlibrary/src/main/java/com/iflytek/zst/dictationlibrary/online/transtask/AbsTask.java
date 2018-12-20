package com.iflytek.zst.dictationlibrary.online.transtask;

/**
 * Created by DELL-5490 on 2018/12/20.
 */

public abstract class AbsTask implements Runnable{
    private String taskName;
    /**
     * 延迟多久执行
     */
    private int delay;
    /**
     * 是否有执行的条件锁,true 会先锁住不执行run方法,当设置false时才执行
     */
    private boolean conditionLockBeforeRunning;
    /**
     * 设置条件锁执行等待最长时间,如果在该时间内未执行完,则释放锁
     */
    private long conditionLockBeforeRunningTime;

    private OnLockReleaseListener mOnLockReleaseBeforeRunningListener;

    /**
     * 是否有执行的条件锁,true 会在执行run方法后锁住,当设置false时才执行
     */
    private boolean conditionLockAfterRun;
    private OnLockReleaseListener mOnLockReleaseAfterRunningListener;
    /**
     * 设置条件锁执行等待最长时间,如果在该时间内未执行完,则释放锁
     */
    private long conditionLockAfterRunTime;

    public AbsTask() {
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public AbsTask(int delay) {
        this.delay = delay;
    }

    public AbsTask(int delay, boolean conditionLockBeforeRunning,boolean conditionLockAfterRun) {
        this.delay = delay;
        this.conditionLockBeforeRunning = conditionLockBeforeRunning;
        this.conditionLockAfterRun = conditionLockAfterRun;
    }

    public AbsTask(int delay, boolean conditionLockBeforeRunning, long conditionLockBeforeRunningTime, boolean conditionLockAfterRun, long conditionLockAfterRunTime) {
        this.delay = delay;
        this.conditionLockBeforeRunning = conditionLockBeforeRunning;
        this.conditionLockBeforeRunningTime = conditionLockBeforeRunningTime;
        this.conditionLockAfterRun = conditionLockAfterRun;
        this.conditionLockAfterRunTime = conditionLockAfterRunTime;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * 设置run方法之前的条件,当条件为true时会锁定任务栈,
     * 可以通过{@link #setConditionLockAfterRunTime(long)} 设置最长锁定时间,当再次调用设置false时,才会执行run方法
     *
     * @param conditionLockBeforeRunning
     */
    public void setConditionLockBeforeRunning(boolean conditionLockBeforeRunning) {
        this.conditionLockBeforeRunning = conditionLockBeforeRunning;
        if (!conditionLockBeforeRunning) {
            if (this.mOnLockReleaseBeforeRunningListener != null) {
                this.mOnLockReleaseBeforeRunningListener.onLockRelease();
            }
        }
    }

    public boolean isConditionLockBeforeRunning() {
        return conditionLockBeforeRunning;
    }

    public void setConditionLockBeforeRunningTime(long conditionLockBeforeRunningTime) {
        this.conditionLockBeforeRunningTime = conditionLockBeforeRunningTime;
    }

    public long getConditionLockBeforeRunningTime() {
        return conditionLockBeforeRunningTime;
    }

    void setOnLockReleaseBeforeRunningListener(OnLockReleaseListener onLockReleaseBeforeRunningListener) {
        mOnLockReleaseBeforeRunningListener = onLockReleaseBeforeRunningListener;
    }

    /**
     * 设置run方法之后的条件,当条件为true时会锁定任务栈,可以通过{@link #setConditionLockAfterRunTime(long)} 设置最长锁定时间,当再次调用设置false时,继续下一个任务
     *
     * @param conditionLockAfterRun
     */
    public void setConditionLockAfterRun(boolean conditionLockAfterRun) {
        this.conditionLockAfterRun = conditionLockAfterRun;
        if (!conditionLockAfterRun) {
            if (this.mOnLockReleaseAfterRunningListener != null) {
                this.mOnLockReleaseAfterRunningListener.onLockRelease();
            }
        }
    }

    public boolean isConditionLockAfterRun() {
        return conditionLockAfterRun;
    }

    public void setConditionLockAfterRunTime(long conditionLockAfterRunTime) {
        this.conditionLockAfterRunTime = conditionLockAfterRunTime;
    }

    public long getConditionLockAfterRunTime() {
        return conditionLockAfterRunTime;
    }

    void setOnLockReleaseAfterRunningListener(OnLockReleaseListener onLockReleaseAfterRunningListener) {
        mOnLockReleaseAfterRunningListener = onLockReleaseAfterRunningListener;
    }

    public interface OnLockReleaseListener{
        void onLockRelease();
    }

}
