package com.iflytek.zst.taoqi.bean;

import android.support.annotation.NonNull;

/**
 * Created by DELL-5490 on 2018/10/15.
 */

public class MarkBean implements Comparable<MarkBean> {
    public int markStart;
    public int markEnd;

    public int getMarkStart() {
        return markStart;
    }

    public void setMarkStart(int markStart) {
        this.markStart = markStart;
    }

    public int getMarkEnd() {
        return markEnd;
    }

    public void setMarkEnd(int markEnd) {
        this.markEnd = markEnd;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MarkBean)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MarkBean bean = (MarkBean) (obj);
        if (this.markStart == bean.markStart && this.markEnd == bean.markEnd) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + markStart;
        result = 31 * result + markEnd;
        return result;
    }

    @Override
    public int compareTo(@NonNull MarkBean o) {
        return markStart - o.markStart;
    }
}
