package com.smartEleectronics.bletest.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.databinding.BluetoothDeviceBinding;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceHolder> {

    private LayoutInflater inflater;
    private List<BleDevice> bleDeviceList = new ArrayList<>();
    private OnDeviceClickListener mListener;


    @NonNull
    @Override
    public BleDeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }
        BluetoothDeviceBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.bluetooth_device,
                parent,
                false
        );
        return new BleDeviceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceHolder holder, int position) {
        BleDevice currentDevice = bleDeviceList.get(position);
        holder.bind(currentDevice);
    }

    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }

    public class BleDeviceHolder extends RecyclerView.ViewHolder{

        BluetoothDeviceBinding layoutBinding;

        public BleDeviceHolder(BluetoothDeviceBinding binding) {
            super(binding.getRoot());
            this.layoutBinding = binding;
        }

        public void bind(BleDevice device){
            layoutBinding.txtBtName.setText(device.getName());
            layoutBinding.txtBtMac.setText(device.getMac());
            layoutBinding.btSignalDB.setText(String.valueOf(device.getRssi()));

            layoutBinding.container.setOnClickListener(view -> {
                if(mListener != null) mListener.onConnect(device);
            });
        }
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }
}
