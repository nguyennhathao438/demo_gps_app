package com.example.gps_demo_app;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleton;
    private List<Location> myLocations = new ArrayList<>();
    public MyApplication getInstance() {
        return singleton;
    }
    public void onCreate() {
        super.onCreate();
        singleton = this ;
    }
    public void setMyLocations(List<Location> myLocations){
        this.myLocations = myLocations;
    }
    public List<Location> getMyLocations(){
        return myLocations;
    }
}
