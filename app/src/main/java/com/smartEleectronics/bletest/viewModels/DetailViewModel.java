package com.smartEleectronics.bletest.viewModels;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.util.Constants;

import java.nio.ByteBuffer;

public class DetailViewModel extends AndroidViewModel {

    /// Live data variables
    private MutableLiveData<String> liveMessages = new MutableLiveData<>();
    private MutableLiveData<byte[]> liveReceivedData = new MutableLiveData<>();
    private MutableLiveData<Boolean> sending = new MutableLiveData<>();
    private MutableLiveData<Boolean> reading = new MutableLiveData<>();

    private char counterText = 0;

    /// Blink led animation
    private Animation anim = new AlphaAnimation(0.0f, 1.0f);

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getToastMessage(){
        return liveMessages;
    }
    public MutableLiveData<byte[]> getLiveReceivedData(){
        return liveReceivedData;
    }
    public MutableLiveData<Boolean> getSendingStatus(){ return sending;}
    public MutableLiveData<Boolean> getReadingStatus(){ return reading;}

    public void sendDataToBleDevice(BleDevice device, String data) {
        if(BleManager.getInstance().isConnected(device)){
            sending.setValue(true);
            BleManager.getInstance().write(device,
                    Constants.SERVICE_UUID,
                    Constants.CHARACTERISTIC_UUID_RX,
                    data.getBytes(),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            sending.setValue(false);
                            Log.i("write", "write success, current: " + current
                                    + " total: " + total
                                    + " justWrite: " + new String(justWrite));
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            sending.setValue(false);
                            Log.i("write", "onWriteFailure: " + exception.toString());
                        }
                    });
        }else{
            liveMessages.setValue(getApplication().getString(R.string.first_connect));
        }
    }

    public void receiveDataFromBleDevice(BleDevice device){
        BleManager.getInstance().notify(
                device,
                Constants.SERVICE_UUID,
                Constants.CHARACTERISTIC_UUID_TX,
                false,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.i("read", "onNotifySuccess: ");
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        reading.setValue(false);
                        liveMessages.setValue(exception.toString());

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        reading.setValue(true);
                        liveReceivedData.setValue(data);
                        Log.i("read", "onRead: " + new String(data));
                    }
                });
    }

    public void blinkLED(View view, int duration, int offset) {
        anim.setDuration(duration);
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }
    public void stopBlinkLED(){
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {
                anim.cancel();
            }
        });
    }

    public void addText(EditText text, String content) {
        counterText ++;
        if(counterText == 70){
            counterText = 0;
            text.setText("");
        }
        text.append(content);
        text.append("\n");
        int offset = text.getLineCount() * text.getLineHeight();
        if (offset > text.getHeight()) {
            text.scrollTo(0, offset - text.getHeight());
        }
    }
}
