package com.smartEleectronics.bletest.ui;

import android.os.Bundle;

import com.clj.fastble.data.BleDevice;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_detail);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initToolbar(){
        BleDevice device = getIntent().getParcelableExtra(getString(R.string.ble_device_data_key));
        String deviceName = device.getName();
        if(deviceName == null) deviceName = "?";
        binding.toolbar.setTitle("Ble Detail  " + "(" + deviceName + ")");
        setSupportActionBar(binding.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_detail);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}