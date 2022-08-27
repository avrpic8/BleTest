package com.smartEleectronics.bletest.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.smartEleectronics.bletest.util.Constants;

public class DetailViewModel extends AndroidViewModel {
    public DetailViewModel(@NonNull Application application) {
        super(application);

        initBle();
    }

    private void initBle(){
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true)
                .setReConnectCount(1, 500)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    public void sendDataToBleDevice(BleDevice device, String data) {
        if(BleManager.getInstance().isConnected(device)){
            BleManager.getInstance().write(device,
                    Constants.SERVICE_UUID,
                    Constants.CHARACTERISTIC_UUID,
                    data.getBytes(),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Log.i("write", "write success, current: " + current
                                    + " total: " + total
                                    + " justWrite: " + new String(justWrite));
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            Log.i("write", "onWriteFailure: " + exception.toString());
                        }
                    });
        }
    }
}
