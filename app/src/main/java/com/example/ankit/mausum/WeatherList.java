package com.example.ankit.mausum;

public class WeatherList {
    int temp;
    String main;
    String icon;
    String time;
int Degree;

    public void setDegree(int degree) {
        Degree = degree;
    }

    public int getDegree() {

        return Degree;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {

        return time;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getIcon() {
        return icon;
    }

    public int getTemp() {
        return temp;
    }

    public String getMain() {
        return main;
    }

    public WeatherList(int temp, String main, String icon) {
        this.temp = temp;
        this.main = main;
        this.icon = icon;
    }
}
