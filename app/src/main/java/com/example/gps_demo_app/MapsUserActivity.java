package com.example.gps_demo_app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsUserActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dbHelper = new DBHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<UserLocation> userLocations = dbHelper.getCurrentLocationsWithUser();

        if (userLocations.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu vị trí hiện tại của user nào", Toast.LENGTH_SHORT).show();
            return;
        }

        for (UserLocation ul : userLocations) {
            LatLng latLng = new LatLng(ul.latitude, ul.longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(ul.username)
                    .snippet("Full name: " + ul.fullName + "\nAltitude: " + ul.altitude));
        }

        // Zoom camera vào vị trí đầu tiên
        UserLocation firstUser = userLocations.get(0);
        LatLng firstLatLng = new LatLng(firstUser.latitude, firstUser.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 14f));
    }
}
