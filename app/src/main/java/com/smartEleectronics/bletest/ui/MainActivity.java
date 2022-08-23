package com.smartEleectronics.bletest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.databinding.ActivityMainBinding;
import com.smartEleectronics.bletest.viewModels.MainViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityMainBinding mainBinding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();
        iniToolbar();
        initButton();

    }

    public void initViewModel(){
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    public void iniToolbar(){
        setSupportActionBar(mainBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_title);
    }

    public void initButton(){
        mainBinding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.startScan();
            }
        });
    }
}