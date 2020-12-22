package com.example.connectapplication.bean;

import java.io.Serializable;

public class ConnectBean implements Serializable {

    private long data;//连接时间

    private int  successCount;//连接成功次数

    private int  failCount;//连接失败次数

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
