package com.smartEleectronics.bletest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.clj.fastble.BleManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBle();
        iniToolbar();
    }

    public void initBle(){
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true)
                .setReConnectCount(1, 500)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
    public void iniToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_title);
    }
}