package com.example.gps_demo_app;

public class UserLocation {
    public int userId;
    public String username;
    public String fullName;
    public double latitude;
    public double longitude;
    public double altitude;
    public long timestamp;

    public UserLocation(int userId, String username, String fullName, double latitude, double longitude, double altitude, long timestamp) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }
}