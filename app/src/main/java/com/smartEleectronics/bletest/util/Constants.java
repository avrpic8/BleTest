package com.smartEleectronics.bletest.util;

public class Constants {

    /// For Esp32 module  (Server)
    //public static final String SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    //public static final String CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8";


    /// For Esp32 module (Uart module)
    public static final String  SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    public static final String  CHARACTERISTIC_UUID_RX  = "beb5483e-36e1-4688-b7f5-ea07361b26a8"; // write
    public static final String  CHARACTERISTIC_UUID_TX =  "beb5483e-36e1-4688-b7f5-ea07361b26a5"; // notify

    /// For Sky-Lab module
    //public static final String SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    //public static final String CHARACTERISTIC_UUID_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    //public static final String CHARACTERISTIC_UUID_NOTIFY = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
}
