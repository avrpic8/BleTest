package com.smartEleectronics.bletest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.clj.fastble.BleManager;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniToolbar();
    }

    public void initBle(){
        BleManager.getInstance().init(getApplication());
    }
    public void iniToolbar(){
        toolbar = findViewById(R.id.toolbar);
    }
}