package com.example.gps_demo_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "gpsdemo.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE User (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password_hash TEXT," +
                "full_name TEXT" +
                ");";

        String createLocationTable = "CREATE TABLE Locations (" +
                "location_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "latitude REAL," +
                "longitude REAL," +
                "altitude REAL," +
                "timestamp INTEGER," +
                "is_current INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES User(user_id)" +
                ");";

        db.execSQL(createUserTable);
        db.execSQL(createLocationTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nâng cấp DB khi cần thiết
        db.execSQL("DROP TABLE IF EXISTS Locations");
        db.execSQL("DROP TABLE IF EXISTS User");
        onCreate(db);
    }
    public long addUser(String username, String passwordHash, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password_hash", passwordHash);
        values.put("full_name", fullName);
        return db.insert("User", null, values);
    }
    public boolean checkLogin(String username, String passwordHash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE username = ? AND password_hash = ?", new String[]{username, passwordHash});
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM User WHERE username = ?", new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }
    public void addLocation(int userId, double lat, double lon, double alt, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Đặt is_current=0 cho vị trí hiện tại cũ
            ContentValues resetValues = new ContentValues();
            resetValues.put("is_current", 0);
            db.update("Locations", resetValues, "user_id = ? AND is_current = 1", new String[]{String.valueOf(userId)});

            // Thêm vị trí mới với is_current=1
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("latitude", lat);
            values.put("longitude", lon);
            values.put("altitude", alt);
            values.put("timestamp", timestamp);
            values.put("is_current", 1);
            db.insert("Locations", null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public List<UserLocation> getCurrentLocationsWithUser() {
        List<UserLocation> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT User.user_id, User.username, User.full_name, " +
                "Locations.latitude, Locations.longitude, Locations.altitude, Locations.timestamp " +
                "FROM Locations INNER JOIN User ON Locations.user_id = User.user_id " +
                "WHERE Locations.is_current = 1";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int userId = cursor.getInt(0);
            String username = cursor.getString(1);
            String fullName = cursor.getString(2);
            double latitude = cursor.getDouble(3);
            double longitude = cursor.getDouble(4);
            double altitude = cursor.getDouble(5);
            long timestamp = cursor.getLong(6);

            UserLocation ul = new UserLocation(userId, username, fullName, latitude, longitude, altitude, timestamp);
            result.add(ul);
        }
        cursor.close();
        return result;
    }

}
