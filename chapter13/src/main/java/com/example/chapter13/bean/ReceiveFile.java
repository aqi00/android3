package com.example.chapter13.bean;

public class ReceiveFile {
    public String lastFile; // 上次的文件名
    public int receiveCount; // 接收包的数量
    public byte[] receiveData; // 收到的字节数组

    public ReceiveFile() {
    }

    public ReceiveFile(String lastFile, int receiveCount, int partLength) {
        this.lastFile = lastFile;
        this.receiveCount = receiveCount;
        this.receiveData = new byte[partLength];
    }
}
