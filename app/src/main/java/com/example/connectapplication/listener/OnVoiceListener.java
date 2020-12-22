package com.example.connectapplication.listener;

import com.ryeex.ble.connector.callback.AsyncBleCallback;
import com.ryeex.ble.connector.error.BleError;

public interface OnVoiceListener {

    /**
     * 检查状态
     *
     * @param callback
     */
    void onCheck(AsyncBleCallback<Boolean, BleError> callback);

    /**
     * 语音会话开始
     *
     * @param sessionId
     */
    void onStart(int sessionId);

    /**
     * 传输语音流
     *
     * @param bytes
     */
    void onReceiveBytes(byte[] bytes);

    /**
     * 传输完成
     */
    void onReceiveBytesFinish(int sessionId);

    /**
     * 语音会话结束
     */
    void onStop();

}
