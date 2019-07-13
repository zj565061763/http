package com.sd.www.http.model;

public class WeatherModel
{
    public Weatherinfo weatherinfo;

    public static class Weatherinfo
    {
        public String city;
        public String cityid;
        public String temp1;
        public String temp2;
        public String weather;
    }
}
