package com.smartEleectronics.bletest.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.databinding.FragmentBleDetailBinding;
import com.smartEleectronics.bletest.util.Constants;
import com.smartEleectronics.bletest.viewModels.DetailViewModel;


public class BleDetailFragment extends Fragment {

    private FragmentBleDetailBinding binding;

    private DetailViewModel detailViewModel;
    private BleDevice bleDevice;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bleDevice = getBleDevice();
        binding.setBleDeviceData(bleDevice);

        initViewModel();
        initButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        BleManager.getInstance().stopNotify(
                bleDevice,
                Constants.SERVICE_UUID,
                Constants.CHARACTERISTIC_UUID_NOTIFY);
    }

    private void initViewModel(){
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        binding.setLifecycleOwner(this);

        detailViewModel.getToastMessage().observe(getViewLifecycleOwner(), s -> {
            showToast(s);
        });

        detailViewModel.receiveDataFromBleDevice(bleDevice);
        detailViewModel.getLiveReceivedData().observe(getViewLifecycleOwner(), data -> {
            Log.i("read", "initViewModel: " + new String(data));
            getActivity().runOnUiThread(() -> {
                detailViewModel.addText(binding.edtResponse, new String(data));
            });
        });


        detailViewModel.getSendingStatus().observe(getViewLifecycleOwner(), sendingStatus -> {
            if(sendingStatus){
                detailViewModel.blinkLED(binding.activeSend, 100, 100);
            }else{
                detailViewModel.stopBlinkLED();
            }
        });
        detailViewModel.getReadingStatus().observe(getViewLifecycleOwner(), readinStatus->{
            detailViewModel.blinkLED(binding.activeReceive, 100, 100);
            detailViewModel.stopBlinkLED();
        });
    }

    private void initButtons(){
        binding.btnSendData.setOnClickListener(view -> {
            String data = binding.edtWriteCommand.getText().toString();
            detailViewModel.sendDataToBleDevice(bleDevice, data);
        });
    }

    private BleDevice getBleDevice(){
        BleDevice device = this.getActivity()
                .getIntent()
                .getParcelableExtra(getString(R.string.ble_device_data_key));
        return device;
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}