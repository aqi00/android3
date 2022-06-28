package com.example.chapter18.bean;

public class CityInfo {
    public String provice_name; // 省份名称
    public String city_name; // 地市名称
    public String county_name; // 县区名称
    public String address; // 详细地址
    public String city_code; // 城市编码
    public WeatherInfo weather_info; // 天气信息

    public CityInfo(String provice_name, String city_name, String county_name, String address) {
        this.provice_name = provice_name;
        this.city_name = city_name;
        this.county_name = county_name;
        this.address = address;
    }

}
