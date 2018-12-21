package com.iflytek.zst.dictationlibrary.utils;

/**
 * Created by jiwang on 2018/4/2.
 * 备注:
 */
public class ByteRingBuffer {
    private byte[] m_buffer;
    private int m_head;
    private int m_tail;

    public ByteRingBuffer() {
        this(262144);
    }

    public ByteRingBuffer(int bufSiz) {
        this.m_buffer = null;
        this.m_head = 0;
        this.m_tail = 0;
        this.m_buffer = new byte[bufSiz + 1];
        this.m_head = 0;
        this.m_tail = 0;
    }

    public boolean isFull() {
        return (this.m_tail + 1) % this.m_buffer.length == this.m_head;
    }

    public boolean isEmpty() {
        return this.m_tail == this.m_head;
    }

    public void clear() {
        this.m_head = this.m_tail = 0;
    }

    public int getCapaSize() {
        return this.m_buffer.length - 1;
    }

    public int getBusySize() {
        return this.m_head <= this.m_tail ? this.m_tail - this.m_head : this.m_buffer.length - (this.m_head - this.m_tail);
    }

    public int getFreeSize() {
        return this.m_buffer.length - this.getBusySize() - 1;
    }

    public boolean put(byte[] buff, int offset, int size) {
        if (buff != null && offset + size <= buff.length) {
            if (this.getFreeSize() < size) {
                return false;
            } else {
                int num_cpy = size;
                if (this.m_tail + size > this.m_buffer.length) {
                    num_cpy = this.m_buffer.length - this.m_tail;
                }

                System.arraycopy(buff, offset, this.m_buffer, this.m_tail, num_cpy);
                if (size - num_cpy > 0) {
                    System.arraycopy(buff, offset + num_cpy, this.m_buffer, 0, size - num_cpy);
                }

                this.m_tail = (this.m_tail + size) % this.m_buffer.length;
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean get(byte[] buff, int offset, int size) {
        if (buff != null && offset + size <= buff.length) {
            if (this.getBusySize() < size) {
                return false;
            } else {
                int num_cpy = size;
                if (this.m_head + size > this.m_buffer.length) {
                    num_cpy = this.m_buffer.length - this.m_head;
                }

                System.arraycopy(this.m_buffer, this.m_head, buff, offset, num_cpy);
                if (size - num_cpy > 0) {
                    System.arraycopy(this.m_buffer, 0, buff, offset + num_cpy, size - num_cpy);
                }

                this.m_head = (this.m_head + size) % this.m_buffer.length;
                return true;
            }
        } else {
            return false;
        }
    }

}
