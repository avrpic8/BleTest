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
    private MutableLiveData<String> liveMessages;
    private MutableLiveData<BleDevice> liveBleDevice;
    private MutableLiveData<Boolean> liveScanStopBtName;

    public MainViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        initBle();
    }

    public MutableLiveData<String> getToastMessage(){
        if(liveMessages == null){
            liveMessages = new MutableLiveData<String>();
        }
        return liveMessages;
    }
    public MutableLiveData<BleDevice> getNewBleDevice(){
        if(liveBleDevice == null){
            liveBleDevice = new MutableLiveData<BleDevice>();
        }
        return liveBleDevice;
    }
    public MutableLiveData<Boolean> getScanStopBtName(){
        if(liveScanStopBtName == null){
            liveScanStopBtName = new MutableLiveData<Boolean>();
        }
        return liveScanStopBtName;
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
                        liveMessages.setValue(getApplication().getString(R.string.scan_finished));
                        liveScanStopBtName.setValue(false);
                    }

                    @Override
                    public void onScanStarted(boolean success) {
                        liveMessages.setValue(getApplication().getString(R.string.scan_started));
                    }

                    @Override
                    public void onScanning(BleDevice bleDevice) {
                        liveBleDevice.setValue(bleDevice);
                    }
                });
            }else{
                BleManager.getInstance().enableBluetooth();
            }
        }else{
            liveMessages.setValue(getApplication().getString(R.string.ble_not_support));
        }
    }

    public void startOrStopScanning(){
        if(!Boolean.TRUE.equals(liveScanStopBtName.getValue())){
            startScan();
            liveScanStopBtName.setValue(true);
        }

        else {
            BleManager.getInstance().cancelScan();
            liveScanStopBtName.setValue(false);
        }
    }

    public void connectToDevice(BleDevice bleDevice){
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                liveMessages.setValue(getApplication().getString(R.string.connecting));
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                liveMessages.setValue(getApplication().getString(R.string.connect_fail));
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                liveMessages.setValue(getApplication().getString(R.string.connected));
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
