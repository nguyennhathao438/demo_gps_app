package com.example.gps_demo_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView tv_lat, tv_lon , tv_altitude ,tv_accuracy , tv_speed, tv_sensor , tv_updates, tv_address , tv_wayPointCount;
    Switch sw_locationupdates , sw_gps;
    Button btn_newWaypoint,btn_showWaypointList,btn_showMap;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    Location currentLocation;
    List<Location> savedLocations;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_wayPointCount = findViewById(R.id.tv_wayPointCount);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        btn_newWaypoint = findViewById(R.id.btn_newWayPoint);
        btn_showWaypointList = findViewById(R.id.btn_showWayPointList);
        btn_showMap =findViewById(R.id.btn_showMap);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@androidx.annotation.NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location latestLocation = locationResult.getLocations()
                        .get(locationResult.getLocations().size() - 1);
                updateUIValues(latestLocation);
            }
        };
        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                5000L //Cập nhật mỗi 5s
        ).setMinUpdateIntervalMillis(2000L)// Tối thểu 2s giữa các lần
                .setMaxUpdateDelayMillis(10000L) //tối đa 10s
                .build();


        sw_gps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int priority;
                if (sw_gps.isChecked()) {
                    priority = Priority.PRIORITY_HIGH_ACCURACY;
                    tv_sensor.setText("GPS mode: High Accuracy");
                } else {
                    priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
                    tv_sensor.setText("GPS mode: Balanced");
                }

                // Tạo lại LocationRequest với priority mới
                locationRequest = new LocationRequest.Builder(priority, 5000L)
                        .setMinUpdateIntervalMillis(2000L)
                        .setMaxUpdateDelayMillis(10000L)
                        .build();

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_locationupdates.isChecked()){
                    startLocationUpdates();
                }else{
                    stopLocationUpdates();
                }
            }
        });
        startLocationUpdates();
        btn_newWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    MyApplication myApplication = (MyApplication) getApplicationContext();
                    myApplication.getMyLocations().add(currentLocation); // ✅ Thêm trực tiếp vào danh sách toàn cục

                    // Cập nhật số waypoint
                    tv_wayPointCount.setText(String.valueOf(myApplication.getMyLocations().size()));

                    Toast.makeText(MainActivity.this, "Đã lưu vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Chưa có vị trí hiện tại để lưu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_showWaypointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ShowLocationList.class);
                startActivity(i);
            }
        });
        btn_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }//end oncreate
    private void startLocationUpdates(){
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
        tv_updates.setText("Đã bật định vị");
    }
    private void stopLocationUpdates() {
        tv_updates.setText("Đã ngừng định vị");
        tv_lat.setText("----");
        tv_lon.setText("----");
        tv_speed.setText("----");
        tv_accuracy.setText("----");
        tv_altitude.setText("----");
        tv_address.setText("----");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            updateUIValues(location);
                            currentLocation = location;
                        } else {
                            Toast.makeText(MainActivity.this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Lỗi lấy vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {
            // Nếu chưa có quyền thì yêu cầu quyền
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền vị trí để hoạt động.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateUIValues(Location location){
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }else{
            tv_altitude.setText("Không tìm thấy");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }else{
            tv_speed.setText("Không tìm thấy");
        }
        Geocoder geocoder = new Geocoder(MainActivity.this, new Locale("vi", "VN"));
            try{
                List<Address> addresses =geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                tv_address.setText(addresses.get(0).getAddressLine(0));
            }catch(Exception e){
                tv_address.setText("Không thể lâý địa chỉ");
            }


            MyApplication myApplication = (MyApplication)getApplicationContext();
            savedLocations = myApplication.getMyLocations();
            tv_wayPointCount.setText(Integer.toString(savedLocations.size()));
    }
}