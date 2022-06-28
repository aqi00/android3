package com.example.chapter18.bean;

public class WeatherInfo {
    public String weather; // 天气现象
    public String temperature; // 实时气温，单位：摄氏度
    public String winddirection; // 风向描述
    public String windpower; // 风力级别，单位：级
    public String humidity; // 空气湿度

    public WeatherInfo(String weather, String temperature, String winddirection, String windpower, String humidity) {
        this.weather = weather;
        this.temperature = temperature;
        this.winddirection = winddirection;
        this.windpower = windpower;
        this.humidity = humidity;
    }

}
