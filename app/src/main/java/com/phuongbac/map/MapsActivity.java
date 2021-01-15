package com.phuongbac.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MapsActivity extends Activity {

    private MapView                 map;
    private TextView                textView, tvInfo, tvTitle;
    private IMapController          mapController;
    private MyLocationNewOverlay    myLocationNewOverlay;
    private final int               REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private Button                  btnMyLocation;
    private GpsTracker              gpsTracker;
    private Marker                  marker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_maps);

        btnMyLocation = (Button) findViewById(R.id.btnMyLocation);
        textView = findViewById(R.id.searchbox);
        tvTitle = findViewById(R.id.tv_title);
        tvInfo = findViewById(R.id.tv_info);
        gpsTracker = new GpsTracker(ctx);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        mapController.setZoom(18.0);

        myLocationNewOverlay= new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationNewOverlay);

//        Handler handler= new Handler();
//        Runnable runnableCode = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    textView.setText(gpsTracker.getLocation().toString());
////                  Log.e("MAP", vrsCurrentLatitude+" "+vrsCurrentLongitude);
//                    handler.postDelayed(this, 1000);
//                }catch (Exception e) {
//                    textView.setText(e.getMessage());
//                }
//            }
//        };
//        handler.post(runnableCode);

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Location location = gpsTracker.getLocation();
                    if (location==null)
                    {
                        throw new Exception("gpsTracker return null value");
                    }
                    GeoPoint currentPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController.setCenter(currentPoint);
                    mapController.animateTo(currentPoint);
                }
                catch (Exception e) {
                    Log.d("GPS", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        final MapEventsReceiver mReceive = new MapEventsReceiver(){
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                map.getOverlays().remove(marker);
                map.invalidate();
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                map.getOverlays().remove(marker);
                marker = new Marker(map);
                marker.setPosition(p);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setSnippet("Lat/Lng: " + p);
                map.getOverlays().add(marker);
                tvTitle.setText(marker.getTitle() + " " + marker.getSnippet());
                tvInfo.setText(marker.getSnippet());
                map.invalidate();
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(mReceive));
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
