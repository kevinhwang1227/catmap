package com.example.user.catmap;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    static final LatLng KAIST = new LatLng(36.3702793,127.3627346);
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private LatLng mLocation;
    final ArrayList<HashMap<String,String>> catlist = new ArrayList<>();
    final ArrayList<String> catpath = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        FloatingActionButton takePhoto = (FloatingActionButton) findViewById(R.id.fabAdd);
        FloatingActionButton goToCatList = (FloatingActionButton) findViewById(R.id.fabList);
        FloatingActionButton goToGallery = (FloatingActionButton) findViewById(R.id.fabGallery);
        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.floatingMenu);
        HashMap<String,String> a = new HashMap<String,String>();
        HashMap<String,String> b = new HashMap<String,String>();
        HashMap<String,String> c = new HashMap<String,String>();
        a.put("name","냥냥이");
        b.put("name","아름이");
        c.put("name","메롱이");
        a.put("info","He is newly found black cat!!");
        b.put("info","아름이 lives in front of 아름관 and she even has her own house");
        c.put("info","SO CUTE!");
        catlist.add(a);
        catlist.add(b);
        catlist.add(c);
        String merongpath = "/storage/emulated/0/Download/21992772_695390700662959_6970364337640012304_o.jpg";
        String arumpath = "/storage/emulated/0/Download/20746128_678105939058102_111124808228914896_o.jpg";
        String nyangpath = "/storage/emulated/0/Download/22688011_705317359670293_7889289083326641980_n.jpg";
        catpath.add(nyangpath);
        catpath.add(arumpath);
        catpath.add(merongpath);

        GPSTracker gps = new GPSTracker(this);
        mLocation = new LatLng(gps.getLatitude(),gps.getLongitude());

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, AddCat.class);
                Bundle args = new Bundle();
                args.putSerializable("cats",(Serializable) catlist);
                intent.putExtra("bundle", args);
                Bundle path = new Bundle();
                path.putSerializable("path",(Serializable) catpath);
                intent.putExtra("path", path);
                intent.putExtra("from","photo");
                startActivityForResult(intent,Constants.SEND_ADD_CAT_TO_MAIN);
            }
        });


        goToCatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);

                Intent intent = new Intent(MainActivity.this, Diary.class);
                Bundle args = new Bundle();
                args.putSerializable("cats",(Serializable) catlist);
                intent.putExtra("bundle", args);
                Bundle path = new Bundle();
                path.putSerializable("path",(Serializable) catpath);
                intent.putExtra("path", path);
                startActivityForResult(intent,Constants.SEND_DIARY);
            }
        });

        goToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    fabMenu.close(true);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION_CODE);
                } else {
                    fabMenu.close(true);
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, Constants.PERMISSION_CODE);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                } else {
                    mPermissionDenied = true;
                }
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(KAIST));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        LatLng arum = new LatLng(36.3736679,127.3568132);
        LatLng merong = new LatLng(36.3697328,127.3581436);
        LatLng nyang = new LatLng(36.3742185,127.3582213);
        mMap.addMarker(new MarkerOptions().position(arum).title("아름이"));
        mMap.addMarker(new MarkerOptions().position(merong).title("메롱이"));
        mMap.addMarker(new MarkerOptions().position(nyang).title("냥냥이"));
        enableMyLocation();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMyLocationButtonClick() {
        GPSTracker gps = new GPSTracker(this);
        mLocation = new LatLng(gps.getLatitude(),gps.getLongitude());
        Toast.makeText(this, "현위치", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String message = String.format("Current location: Latitude: %1$s\nLongitude: %2$s", location.getLatitude(), location.getLongitude());
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == Constants.PERMISSION_CODE && data.getData() !=null) {
            Uri img = data.getData();
            Intent intent = new Intent(this,AddCat.class);
            Bundle args = new Bundle();
            args.putSerializable("cats",(Serializable) catlist);
            intent.putExtra("bundle", args);
            intent.putExtra("img",img.toString());
            intent.putExtra("from","gallery");
            Bundle path = new Bundle();
            path.putSerializable("path",(Serializable) catpath);
            intent.putExtra("path", path);
            startActivityForResult(intent,Constants.SEND_GALL_TO_MAIN);
        } else if (requestCode == Constants.SEND_ADD_CAT_TO_MAIN) {
             if (resultCode == RESULT_OK) {
                 String name = data.getStringExtra("name");
                 mMap.addMarker(new MarkerOptions().position(mLocation).title(name));
                 mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
                 Toast.makeText(this, "Cat Added", Toast.LENGTH_SHORT).show();
                 HashMap<String,String> d = new HashMap<>();
                 d.put("name",name);
                 catlist.add(d);
             }
         } else if (requestCode == Constants.SEND_GALL_TO_MAIN) {
             if (resultCode == RESULT_OK) {
                 String name = data.getStringExtra("name");
                 mMap.addMarker(new MarkerOptions().position(mLocation).title(name));
                 mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
                 Toast.makeText(this, "Cat Added", Toast.LENGTH_SHORT).show();
                 HashMap<String,String> d = new HashMap<>();
                 d.put("name",name);
                 catlist.add(d);
             }
         } else if (requestCode == Constants.SEND_DIARY) {
             String kevinpath = "/storage/emulated/0/Download/75552.ngsversion.1422285553360.adapt.1900.1.jpg";
             catpath.add(kevinpath);
         }
    }

}