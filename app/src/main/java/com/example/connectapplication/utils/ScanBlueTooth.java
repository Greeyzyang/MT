package com.example.connectapplication.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.connectapplication.constans.CommonValue;
import com.example.connectapplication.event.BindEvent;
import com.example.connectapplication.event.UpdateEvent;
import com.ryeex.ble.common.device.IResultCallback;
import com.ryeex.ble.common.device.OnBindListener;
import com.ryeex.ble.common.device.OnDataSyncListener;
import com.ryeex.ble.common.device.OnUnbindListener;
import com.ryeex.ble.common.model.entity.FirmwareUpdateInfo;
import com.ryeex.ble.common.model.entity.Height;
import com.ryeex.ble.common.model.entity.RyeexDeviceBindInfo;
import com.ryeex.ble.common.model.entity.Weight;
import com.ryeex.ble.connector.BleEngine;
import com.ryeex.ble.connector.callback.AsyncBleCallback;
import com.ryeex.ble.connector.error.BleError;
import com.ryeex.ble.connector.scan.BleScanner;
import com.ryeex.ble.connector.scan.ScannedDevice;
import com.ryeex.watch.adapter.device.WatchDevice;
import com.ryeex.watch.adapter.model.entity.DeviceDataSet;
import com.ryeex.watch.protocol.callback.AsyncProtocolCallback;
import com.ryeex.watch.protocol.pb.entity.PBDevice;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScanBlueTooth {

    public static void startScan(Context context) {

        AndPermission.with(context)
                .runtime()
                .permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)
                .onGranted(permissions -> {
                    BleScanner.getInstance().start();
                })
                .onDenied(permissions -> {

                })
                .start();
    }


    /**
     * 开始绑定
     *
     * @param scannedDevice
     */
    public static void startBind(ScannedDevice scannedDevice, WatchDevice bindingDevice) {
        bindingDevice.setPid(scannedDevice.getRyeexProductId());
        bindingDevice.setMac(scannedDevice.getMac());
        bindingDevice.bind(new OnBindListener() {

            @Override
            public void onConnecting() {
                Log.d("yj", "bindingDevice---connecting---");
            }

            @Override
            public void onConfirming() {
                Log.d("yj", "bindingDevice---confirming---");
            }

            @Override
            public void onBinding() {
                Log.d("yj", "bindingDevice---binding---");
            }

            @Override
            public void onServerBind(RyeexDeviceBindInfo ryeexDeviceBindInfo, AsyncBleCallback<Void, BleError> asyncBleCallback) {

                if (asyncBleCallback != null) {
                    asyncBleCallback.sendSuccessMessage(null);
                    Log.d("yj", "bindingDevice---serverbind---");
                }
            }

            @Override
            public void onSuccess() {
                Log.d("yj", "bindingDevice---success---");
                EventBus.getDefault().post(new BindEvent(CommonValue.BIND_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.d("yj", "bindingDevice---failure---bleError--" + bleError.toString());
                EventBus.getDefault().post(new BindEvent(CommonValue.BIND_ERROR, bleError.toString()));
            }
        });

    }

    /**
     * 解绑
     */
    public static void endBind(WatchDevice bindingDevice) {
        bindingDevice.unbind(new OnUnbindListener() {
            @Override
            public void onServerUnbind(AsyncBleCallback<Void, BleError> asyncBleCallback) {
                if (asyncBleCallback != null) {
                    asyncBleCallback.sendSuccessMessage(null);
                    Log.d("yj", "serverUnbind-----------");
                }
            }

            @Override
            public void onSuccess() {
                Log.d("yj", "Unbind------success-----");
                EventBus.getDefault().post(new BindEvent(CommonValue.UNBIND_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.e("yj", "unbind----error--bleError--" + bleError.toString());
                EventBus.getDefault().post(new BindEvent(CommonValue.UNBIND_ERROR, bleError.toString()));
            }
        });
//        DeviceManager.getInstance().unbind(new AsyncBleCallback<Void, BleError>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("yj","Unbind--------success---");
//                EventBus.getDefault().post(new BindEvent(CommonValue.UNBIND_SUCCESS));
//            }
//
//            @Override
//            public void onFailure(BleError bleError) {
//                Log.d("yj","Unbind-------failure----");
//                EventBus.getDefault().post(new BindEvent(CommonValue.UNBIND_ERROR));
//            }
//        });
    }

    /**
     * login
     */
    public static void startLogin(WatchDevice device) {
        device.login(new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("yj", "login----success----");
                EventBus.getDefault().post(new BindEvent(CommonValue.LOGIN_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError error) {
                Log.d("yj", "login----fail--bleerror--" + error.toString());
                EventBus.getDefault().post(new BindEvent(CommonValue.LOGIN_ERROR, error.toString()));
            }
        });
//        DeviceManager.getInstance().login(new AsyncBleCallback<Void, BleError>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("yj","login----success----");
//                EventBus.getDefault().post(new BindEvent(CommonValue.LOGIN_SUCCESS));
//            }
//
//            @Override
//            public void onFailure(BleError bleError) {
//                Log.d("yj","login----fail----"+bleError.getMessage().toString());
//                EventBus.getDefault().post(new BindEvent(CommonValue.LOGIN_ERROR));
//            }
//        });
    }

    /**
     * logout
     */
    public static void endlogout(WatchDevice device) {
        device.logout(new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("yj", "logout----success----");
                EventBus.getDefault().post(new BindEvent(CommonValue.LOGOUT_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.e("yj", "logout----error--bleError--" + bleError.toString());
                EventBus.getDefault().post(new BindEvent(CommonValue.LOGOUT_ERROR, bleError.toString()));
            }
        });
//         DeviceManager.getInstance().logout(new AsyncBleCallback<Void, BleError>() {
//             @Override
//             public void onSuccess(Void aVoid) {
//                 Log.d("yj","logout----success----");
//                EventBus.getDefault().post(new BindEvent(CommonValue.LOGOUT_SUCCESS));
//             }
//
//             @Override
//             public void onFailure(BleError bleError) {
//                 Log.d("yj","logout----error----");
//                 EventBus.getDefault().post(new BindEvent(CommonValue.LOGOUT_ERROR));
//             }
//         });
    }

    /**
     * disconnect
     */
    public static void CancelBind(WatchDevice watchDevice, BindEvent bindEvent) {
        watchDevice.cancelBind(new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("yj", "cancelbind----success----");
                EventBus.getDefault().post(bindEvent);
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.d("yj", "cancelbind----error----");
                EventBus.getDefault().post(bindEvent);
            }
        });
    }

    public static void SycDataToWatch(WatchDevice watchDevice, String heights, String weights) {
        Height height = new Height();
        height.setHeight(Float.valueOf(heights));
        height.setUnit(Height.Unit.CM);
        Weight weight = new Weight();
        weight.setWeight(Float.valueOf(weights));
        weight.setUnit(Weight.Unit.KG);
        watchDevice.setUserHeight(height, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                watchDevice.setUserWeight(weight, new AsyncBleCallback<Void, BleError>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonValue.SYNC_DATA_TO_WATCH_SUCCESS++;

                    }

                    @Override
                    public void onFailure(BleError bleError) {
                        CommonValue.SYNC_DATA_TO_WATCH_FAIL++;
                        Log.e("yj", "sync_data_to_watch------bleError---" + bleError.toString());
                    }
                });
            }

            @Override
            public void onFailure(BleError bleError) {
                CommonValue.SYNC_DATA_TO_WATCH_FAIL++;
            }
        });
    }

    public static void SyncDataTime(WatchDevice watchDevice) {
        watchDevice.syncTime(false, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonValue.SYNC_DATA_TIME_SUCCESS++;
            }

            @Override
            public void onFailure(BleError bleError) {
                CommonValue.SYNC_DATA_TIME_FAIL++;
                Log.e("yj", "sync_device_time------bleError---" + bleError.toString());
            }
        });
    }

    public static void SyncDataHealth(WatchDevice watchDevice) {
        watchDevice.syncDeviceData(new OnDataSyncListener<List<DeviceDataSet>>() {
            @Override
            public void onStart(int i) {

            }

            @Override
            public void onProgress(float v, int i, int i1) {

            }

            @Override
            public void onSuccess(List<DeviceDataSet> deviceDataSets, IResultCallback iResultCallback) {
                if (iResultCallback != null) {
                    iResultCallback.onResult(true);
                }
                CommonValue.SYNC_DATA_HEALTH_SUCCESS++;
                EventBus.getDefault().post(new BindEvent("syncDataSuccess", null));
            }

            @Override
            public void onFailure(BleError bleError) {
                EventBus.getDefault().post(new BindEvent("syncDataError", bleError.toString()));
                Log.e("yj", "sync_device_data------bleError---" + bleError.toString());
                CommonValue.SYNC_DATA_HEALTH_FAIL++;
            }
        });
    }

    public static void syncDataall(WatchDevice watchDevice, String heights, String weights) {
        Height height = new Height();
        height.setHeight(Float.valueOf(heights));
        height.setUnit(Height.Unit.CM);
        Weight weight = new Weight();
        weight.setWeight(Float.valueOf(weights));
        weight.setUnit(Weight.Unit.KG);
        watchDevice.setUserHeight(height, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                watchDevice.setUserWeight(weight, new AsyncBleCallback<Void, BleError>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonValue.SYNC_DATA_TO_WATCH_SUCCESS++;
                        SyncDataTimeall(watchDevice);
                    }

                    @Override
                    public void onFailure(BleError bleError) {
                        Log.e("yj", "setUserWeightall------bleError---" + bleError.toString());
                        EventBus.getDefault().post(new BindEvent("syncWeightError", bleError.toString()));
                        CommonValue.SYNC_DATA_TO_WATCH_FAIL++;
                    }
                });
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.e("yj", "setUserHieghtall------bleError---" + bleError.toString());
                CommonValue.SYNC_DATA_TO_WATCH_FAIL++;
                EventBus.getDefault().post(new BindEvent("syncHeightError", bleError.toString()));
            }
        });
    }


    public static void SyncDataTimeall(WatchDevice watchDevice) {
        watchDevice.syncTime(false, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonValue.SYNC_DATA_TIME_SUCCESS++;
                SyncDataHealth(watchDevice);
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.e("yj", "sync_time_all------bleError---" + bleError.toString());
                CommonValue.SYNC_DATA_TIME_FAIL++;
                EventBus.getDefault().post(new BindEvent("syncTimeError", bleError.toString()));
            }
        });
    }

    public static void writeDeviceLog(WatchDevice watchDevice, Context conext, String filename) {
        watchDevice.getDeviceLogFile(getFilePath(conext) + "/DeviceLogs/" + filename + "/", new AsyncBleCallback<String, BleError>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(conext, "日志已写入" + getFilePath(conext) + "/DeviceLogs/" + "目录下", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.e("yj", "writeDeviceError---------" + bleError.toString());
                Toast.makeText(conext, "日志已写入失败", Toast.LENGTH_LONG).show();
            }
        });
    }



    /**
     * 获得文件存储路径
     *
     * @return
     */
    private static String getFilePath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {//如果外部储存可用
            return context.getExternalFilesDir(null).getPath();//获得外部存储路径,默认路径为 /storage/emulated/0/Android/data/com.waka.workspace.logtofile/files/Logs/log_2016-03-14_16-15-09.log
        } else {
            return context.getFilesDir().getPath();//直接存在/data/data里，非root手机是看不到的
        }
    }

    public static void writeBleError(WatchDevice watchDevice, Context context, String filename) {
        watchDevice.getDeviceLogFile(getFilePath(context) + "/bleErrorLogs/" + filename + "/", new AsyncBleCallback<String, BleError>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(context, "日志已写入" + getFilePath(context) + "/DeviceLogs/" + "目录下", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(BleError bleError) {
                Log.e("yj", "writeBleError---------" + bleError.toString());
                Toast.makeText(context, "日志已写入失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void ConnectedBluetooth(WatchDevice watchDevice) {
        watchDevice.startConnect(new AsyncProtocolCallback<PBDevice.DeviceStatus, BleError>() {
            @Override
            public void onSuccess(PBDevice.DeviceStatus deviceStatus) {
                Log.e("yj", "connect-------success---");
                EventBus.getDefault().post(new BindEvent(CommonValue.BLUE_CONNECT_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError bleError) {
                EventBus.getDefault().post(new BindEvent(CommonValue.BLUE_CONNECT_FAILURE, bleError.toString()));
                Log.e("yj", "connect-------bleerror---" + bleError.toString());
            }
        });
    }

    public static void DisConnectedBlueTooth(WatchDevice watchDevice) {
        watchDevice.disconnect(new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("yj", "disconnect-------success---");
                EventBus.getDefault().post(new BindEvent(CommonValue.BLUE_DISCONNECT_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError bleError) {
                EventBus.getDefault().post(new BindEvent(CommonValue.BLUE_DISCONNECT_FAILURE, bleError.toString()));
                Log.e("yj", "disconnect-------bleerror---" + bleError.toString());
            }
        });
    }

    static float finishProgress = 0;

    public static void UpdateVersion(WatchDevice watchDevice, String version, String fileDir, String filename) {
        //TODO demo是用assets资源，实际是要从云端下载

        FirmwareUpdateInfo firmwareUpdateInfo = new FirmwareUpdateInfo();
        //是否强制升级
        firmwareUpdateInfo.setForce(false);
        firmwareUpdateInfo.setVersion(version);
        FirmwareUpdateInfo.UpdateItem updateItem = new FirmwareUpdateInfo.UpdateItem();
        //0资源包 1固件包 注意有些版本是两个包有都的，demo这两个版本是没有资源包的
        updateItem.setId(1);
        updateItem.setLocalPath(fileDir + filename + ".bin");
        File files = new File(fileDir + filename + ".bin");
        String md5str = Util.md5ForFile(files);
        Log.d("yj", "md5str------" + md5str);
        updateItem.setMd5(md5str);
        File file = new File(updateItem.getLocalPath());
        updateItem.setLength((int) file.length());
        List<FirmwareUpdateInfo.UpdateItem> items = new ArrayList<>();
        items.add(updateItem);
        firmwareUpdateInfo.setUrlList(items);

        Log.i("yj", "updateFirmware firmwareUpdateInfo:" + GSON.toJSONString(firmwareUpdateInfo));

        watchDevice.updateFirmware(firmwareUpdateInfo, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onUpdate(Bundle bundle) {
                if (!watchDevice.isLogin())
                    startLogin(watchDevice);
                float totalLength = bundle.getFloat(BleEngine.KEY_LENGTH);
                float speed = bundle.getFloat(BleEngine.KEY_SPEED);
                int leftSeconds = (int) ((totalLength * (1 - finishProgress / 100)) / speed);
                Log.e("yj", "updateFirmware onUpdate length=" + totalLength + " speed=" + speed + " time=" + leftSeconds);
                EventBus.getDefault().post(new UpdateEvent(CommonValue.UPDATE_ON_UPDATE, leftSeconds));
            }

            @Override
            public void onProgress(float progress) {
                Log.e("yj", "updateFirmware onProgress:" + progress);
                finishProgress = progress;
                EventBus.getDefault().post(new BindEvent(CommonValue.UPDATE_PREGRESS, String.valueOf(finishProgress)));
            }

            @Override
            public void onSuccess(Void result) {
                Log.e("yj", "updateFirmware onSuccess");
                watchDevice.disconnect(null);
                EventBus.getDefault().post(new BindEvent(CommonValue.UPDATE_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError error) {
                Log.e("yj", "updateFirmware onFailure:" + error.toString());
                EventBus.getDefault().post(new BindEvent(CommonValue.UPDATE_FAIL, error.toString()));
            }
        });
    }



    public static void UpdateVersion1(WatchDevice watchDevice, String version, String fileDir, String filename, String filename1) {
        //TODO demo是用assets资源，实际是要从云端下载

        FirmwareUpdateInfo firmwareUpdateInfo = new FirmwareUpdateInfo();
        List<FirmwareUpdateInfo.UpdateItem> items = new ArrayList<>();
        //是否强制升级
        firmwareUpdateInfo.setForce(false);
        firmwareUpdateInfo.setVersion(version);
        
        FirmwareUpdateInfo.UpdateItem updateItem = new FirmwareUpdateInfo.UpdateItem();
        //0资源包 1固件包 注意有些版本是两个包有都的，demo这两个版本是没有资源包的
        updateItem.setId(1);
        updateItem.setLocalPath(fileDir + filename + ".bin");
        File files = new File(fileDir + filename + ".bin");
        String md5str = Util.md5ForFile(files);
        Log.d("yj", "md5str------" + md5str);
        updateItem.setMd5(md5str);
        File file = new File(updateItem.getLocalPath());
        updateItem.setLength((int) file.length());
        items.add(updateItem);

        FirmwareUpdateInfo.UpdateItem updateItem1 = new FirmwareUpdateInfo.UpdateItem();
        updateItem1.setId(0);
        updateItem1.setLocalPath(fileDir + filename1 + ".bin");
        File files1 = new File(fileDir + filename1 + ".bin");
        String md5str1 = Util.md5ForFile(files1);
        Log.d("yj", "md5str1------" + md5str1);
        updateItem1.setMd5(md5str1);
        File file1 = new File(updateItem1.getLocalPath());
        updateItem1.setLength((int) file1.length());
        items.add(updateItem1);

        firmwareUpdateInfo.setUrlList(items);
        firmwareUpdateInfo.setResFull(false);

        Log.i("yj", "updateFirmware firmwareUpdateInfo:" + GSON.toJSONString(firmwareUpdateInfo));

        watchDevice.updateFirmware(firmwareUpdateInfo, new AsyncBleCallback<Void, BleError>() {
            @Override
            public void onUpdate(Bundle bundle) {
                if (!watchDevice.isLogin())
                    startLogin(watchDevice);
                float totalLength = bundle.getFloat(BleEngine.KEY_LENGTH);
                float speed = bundle.getFloat(BleEngine.KEY_SPEED);
                int leftSeconds = (int) ((totalLength * (1 - finishProgress / 100)) / speed);
                Log.d("yj", "updateFirmware onUpdate length=" + totalLength + " speed=" + speed + " time=" + leftSeconds);
                EventBus.getDefault().post(new UpdateEvent(CommonValue.UPDATE_ON_UPDATE, leftSeconds));
            }

            @Override
            public void onProgress(float progress) {
                Log.d("yj", "updateFirmware onProgress:" + progress);
                finishProgress = progress;
                EventBus.getDefault().post(new BindEvent(CommonValue.UPDATE_PREGRESS, String.valueOf(finishProgress)));
            }

            @Override
            public void onSuccess(Void result) {
                Log.d("yj", "updateFirmware onSuccess");
                watchDevice.disconnect(null);
                EventBus.getDefault().post(new BindEvent(CommonValue.UPDATE_SUCCESS, null));
            }

            @Override
            public void onFailure(BleError error) {
                Log.e("yj", "updateFirmware onFailure:" + error.toString());
                watchDevice.disconnect(null);                   //更新成功或者失败断开连接
                EventBus.getDefault().post(new BindEvent(CommonValue.UPDATE_FAIL, error.toString()));
            }
        });
    }
}
