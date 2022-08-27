package com.smartEleectronics.bletest.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.clj.fastble.data.BleDevice;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.databinding.FragmentBleDetailBinding;
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

        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        binding.setLifecycleOwner(this);

        initButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

}