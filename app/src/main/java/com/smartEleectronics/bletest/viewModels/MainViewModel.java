package com.smartEleectronics.bletest.viewModels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.smartEleectronics.bletest.R;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private Application application;

    public MainViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        initBle();
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
                        Toast.makeText(application, "Scan finished", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onScanStarted(boolean success) {
                        Toast.makeText(application, "Scan started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onScanning(BleDevice bleDevice) {

                    }
                });
            }else{
                Toast.makeText(application, R.string.turn_on_ble, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(application, R.string.ble_not_support, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "onCleared: Ble destroyed");
        BleManager.getInstance().destroy();
    }
}
