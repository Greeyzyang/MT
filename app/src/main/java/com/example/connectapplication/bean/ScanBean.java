package com.example.connectapplication.bean;

import java.io.Serializable;

public class ScanBean implements Serializable {

    private long data;//扫描时间

    private int  successCount;//扫描成功次数

    private int  failCount;//扫描失败次数

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }
}
