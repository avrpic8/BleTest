package com.smartEleectronics.bletest.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.util.Constants;

public class DetailViewModel extends AndroidViewModel {

    /// Live data variables
    private MutableLiveData<String> liveMessages = new MutableLiveData<>();
    
    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getToastMessage(){
        return liveMessages;
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
        }else{
            liveMessages.setValue(getApplication().getString(R.string.first_connect));
        }
    }

    public void receiveDataFromBleDevice(BleDevice device){
        BleManager.getInstance().read(device,
                Constants.SERVICE_UUID,
                Constants.CHARACTERISTIC_UUID,
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {

                    }

                    @Override
                    public void onReadFailure(BleException exception) {

                    }
                });
    }
}
