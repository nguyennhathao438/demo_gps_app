package com.example.gps_demo_app;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gps_demo_app.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlinx.coroutines.internal.Symbol;

//Lấy apikey trên google cloud
//Vào library bật Maps SDK for Android
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    List<Location> locationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyApplication myApplication = (MyApplication)getApplicationContext();
        locationList = myApplication.getMyLocations();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationList == null || locationList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu vị trí", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo danh sách LatLng từ locationList
        int lastIndex = locationList.size() - 1;
        List<LatLng> pathPoints = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            Location l = locationList.get(i);
            LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
            pathPoints.add(latLng);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Lat: " + l.getLatitude() + " | Lon: " + l.getLongitude());

            if (i == lastIndex) {
                // Vị trí hiện tại (mới nhất) - marker đỏ
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                // Các vị trí khác - marker mặc định (màu xanh dương)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }

            mMap.addMarker(markerOptions);
        }

        // Vẽ đường đi
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(pathPoints)
                .width(10)
                .color(Color.BLUE)
                .geodesic(true);

        Polyline polyline = mMap.addPolyline(polylineOptions);

        polyline.setPattern(Arrays.asList(
                new Dash(30), // khoảng cách giữa các nét
                new Gap(20)   // khoảng cách trống
        ));

        // Zoom đến điểm cuối
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                pathPoints.get(pathPoints.size() - 1), 14f
        ));
    }

}