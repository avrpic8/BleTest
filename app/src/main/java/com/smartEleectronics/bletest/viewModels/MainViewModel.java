package com.smartEleectronics.bletest.viewModels;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.smartEleectronics.bletest.R;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private Application application;

    /// Live data variables
    private MutableLiveData<String> messagesLive;
    private MutableLiveData<BleDevice> bleDeviceLive;

    public MainViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        initBle();
    }

    public MutableLiveData<String> getToastMessage(){
        if(messagesLive == null){
            messagesLive = new MutableLiveData<String>();
        }
        return messagesLive;
    }
    public MutableLiveData<BleDevice> getNewBleDevice(){
        if(bleDeviceLive == null){
            bleDeviceLive = new MutableLiveData<BleDevice>();
        }
        return bleDeviceLive;
    }


    public void initBle(){
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true)
                .setReConnectCount(1, 500)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    public void startScan(){
        if(BleManager.getInstance().isSupportBle()) {
            if(BleManager.getInstance().isBlueEnable()) {
                BleManager.getInstance().scan(new BleScanCallback() {
                    @Override
                    public void onScanFinished(List<BleDevice> scanResultList) {
                        messagesLive.setValue(getApplication().getString(R.string.scan_finished));
                    }

                    @Override
                    public void onScanStarted(boolean success) {
                        messagesLive.setValue(getApplication().getString(R.string.scan_started));
                    }

                    @Override
                    public void onScanning(BleDevice bleDevice) {
                        bleDeviceLive.setValue(bleDevice);
                    }
                });
            }else{
                BleManager.getInstance().enableBluetooth();
            }
        }else{
            messagesLive.setValue(getApplication().getString(R.string.ble_not_support));
        }
    }

    public void connectToDevice(BleDevice bleDevice){
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                messagesLive.setValue(getApplication().getString(R.string.connecting));
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                messagesLive.setValue(getApplication().getString(R.string.connect_fail));
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                messagesLive.setValue(getApplication().getString(R.string.connected));
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "onCleared: Ble destroyed");
        BleManager.getInstance().destroy();
    }
}
