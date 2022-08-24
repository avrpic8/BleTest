package com.smartEleectronics.bletest.bindingAdapter;

import android.widget.TextView;

public class BindingBleDeviceLayoutAdapter {

    @androidx.databinding.BindingAdapter("android:intToString")
    public static void toString(TextView txt, int value) {

        txt.setText(String.valueOf(value));
    }
}
