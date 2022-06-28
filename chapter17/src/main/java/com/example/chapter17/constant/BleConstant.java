package com.example.chapter17.constant;

import java.util.UUID;

public class BleConstant {
    //服务uuid
    public static UUID UUID_SERVER = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    //读的特征值¸
    public static UUID UUID_CHAR_READ = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");
    //写的特征值
    public static UUID UUID_CHAR_WRITE = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
}
