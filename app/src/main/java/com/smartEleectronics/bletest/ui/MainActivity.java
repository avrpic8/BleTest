package com.smartEleectronics.bletest.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.adapter.BleDeviceAdapter;
import com.smartEleectronics.bletest.databinding.ActivityMainBinding;
import com.smartEleectronics.bletest.viewModels.MainViewModel;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";

    private static final String[] permissions =
            {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,};

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 3;

    private Toolbar toolbar;
    private Menu menu;

    private ActivityMainBinding mainBinding;
    private MainViewModel mainViewModel;

    /// adapter list
    private BleDeviceAdapter deviceAdapter;


    /// Callback for enable GPS module
    ActivityResultLauncher<Intent> locationSetting = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(checkGPSIsOpen()){
                    showToast("Gps module is enabled");
                } else{
                    showToast("Please enable Gps module");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();
        iniToolbar();
        initRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(hasLocationPermission()) {
            if (!checkGPSIsOpen()) {
                enableGpsByUser();
                return true;
            }
            mainViewModel.startOrStopScanning();
        } else {
            requestPermissions();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            Toast.makeText(this, "permission accepted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionDenied(this, perms.get(0))) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    public void initViewModel() {
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getToastMessage().observe(this, s -> showToast(s));
        mainViewModel.getNewBleDevice().observe(this, newDevice -> {
            deviceAdapter.addDevice(newDevice);
            deviceAdapter.notifyDataSetChanged();

        });
        mainViewModel.getScanStopBtName().observe(this, this::updateBtScanSwitchName);
    }

    public void iniToolbar() {
        setSupportActionBar(mainBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_title);
    }

    public void initRecyclerView(){
        mainBinding.myDevicesList.setHasFixedSize(true);
        deviceAdapter = new BleDeviceAdapter(this);
        deviceAdapter.setOnDeviceClickListener(new BleDeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if(!BleManager.getInstance().isConnected(bleDevice)){
                    BleManager.getInstance().cancelScan();
                    mainViewModel.connectToDevice(bleDevice);
                }
            }
            @Override
            public void onDisConnect(BleDevice bleDevice) {

            }
            @Override
            public void onDetail(BleDevice bleDevice) {

            }
        });
        mainBinding.myDevicesList.setAdapter(deviceAdapter);
    }

    public boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(
                this,
                "test",
                REQUEST_CODE_PERMISSION_LOCATION,
                permissions
        );
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private void enableGpsByUser(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        locationSetting.launch(intent);
    }

    private void updateBtScanSwitchName(Boolean state) {
        MenuItem item = menu.findItem(R.id.btnScan);
        if (state) {
            item.setTitle("Stop Scanning");
        } else {
            item.setTitle("Start Scan");
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}