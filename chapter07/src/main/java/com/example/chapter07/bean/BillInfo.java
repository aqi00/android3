package com.example.chapter07.bean;

public class BillInfo {
    public long rowid;
    public int xuhao;
    public String date;
    public int month;
    public int type;
    public double amount;
    public String desc;
    public String create_time;
    public String update_time;
    public String remark;

    public BillInfo() {
        rowid = 0L;
        xuhao = 0;
        date = "";
        month = 0;
        type = 0;
        amount = 0.0;
        desc = "";
        create_time = "";
        update_time = "";
        remark = "";
    }
}
