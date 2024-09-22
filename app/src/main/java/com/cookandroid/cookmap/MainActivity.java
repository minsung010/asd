package com.cookandroid.cookmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment mapFrag;
    private Button btnPrev, btnNext;
    private List<String> lines = new ArrayList<>();
    private int placeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("경기도 으뜸 맛집");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        mapFrag = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFrag == null) {
            mapFrag = SupportMapFragment.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.map, mapFrag);
            ft.commit();
        }

        mapFrag.getMapAsync(this);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        readCSV();

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String line = lines.get(placeCount--);
                if (placeCount < 0)
                    placeCount = lines.size() - 1;
                String[] tokens = line.split(",");
                double lat = Double.parseDouble(tokens[0]);
                double lon = Double.parseDouble(tokens[1]);
                String restName = tokens[2];

                LatLng point = new LatLng(lat, lon);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
                GroundOverlayOptions placeMark = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.food))
                        .position(point, 500f, 500f);
                gMap.addGroundOverlay(placeMark);
                Toast.makeText(getApplicationContext(), restName, Toast.LENGTH_LONG).show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String line = lines.get(placeCount++);
                if (placeCount > lines.size() - 1)
                    placeCount = 0;
                String[] tokens = line.split(",");
                double lat = Double.parseDouble(tokens[0]);
                double lon = Double.parseDouble(tokens[1]);
                String restName = tokens[2];

                LatLng point = new LatLng(lat, lon);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
                GroundOverlayOptions placeMark = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.food))
                        .position(point, 500f, 500f);
                gMap.addGroundOverlay(placeMark);
                Toast.makeText(getApplicationContext(), restName, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "위성 지도");
        menu.add(0, 2, 0, "일반 지도");
        menu.add(0, 3, 0, "월드컵경기장 바로가기");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gMap == null) return false;

        switch (item.getItemId()) {
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case 3:
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.568256, 126.897240), 13));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.568256, 126.897240), 13));
        gMap.getUiSettings().setZoomControlsEnabled(true);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                GroundOverlayOptions videoMark = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.food))
                        .position(point, 400f, 400f);
                gMap.addGroundOverlay(videoMark);
            }
        });
    }

    private void readCSV() {
        InputStream inputStream = getResources().openRawResource(R.raw.good_place);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
