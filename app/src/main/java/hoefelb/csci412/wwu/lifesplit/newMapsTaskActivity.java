package hoefelb.csci412.wwu.lifesplit;

//Joe Gildner 05/27/2019
//Location search code from https://www.viralandroid.com/2016/04/google-maps-android-api-adding-search-bar-part-3.html
//Current location

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class newMapsTaskActivity extends FragmentActivity implements OnMapReadyCallback {
    private int numOfSplits = 1;
    // private Fragment addNewTaskFragment;

    private GoogleMap mMap;

    private float stdZoom = 15.0f;

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_maps_task);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button search = (Button) findViewById(R.id.searchMap);
        search.setOnClickListener(mapSearch);

        //addNewTaskFragment = new AddNewTask();
       // final FragmentManager fragmentManager = getFragmentManager();
       // FragmentTransaction transaction =  fragmentManager.beginTransaction();
       // transaction.replace(R.id.fragmentConstraintLayout,addNewTaskFragment);
       // transaction.commit();
      //  Bundle arguments = new Bundle();

    }

    private View.OnClickListener mapSearch = new View.OnClickListener() {
        public void onClick(View v) {
            EditText searchText = (EditText) findViewById(R.id.mapSearchText);
            String location = searchText.getText().toString();

            List<Address> addressList = null;

            if (location != null && !location.equals("")) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,stdZoom));

            }

        }
    };

    private View.OnClickListener mapSearch2 = new View.OnClickListener() {
        public void onClick(View v) {
            EditText searchText = (EditText) findViewById(R.id.mapSearchText);
            String location = searchText.getText().toString();

            List<Address> addressList = null;

            if (location != null && !location.equals("")) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address search = addressList.get(0);
                LatLng latLng = new LatLng(search.getLatitude(), search.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20.0f));

            }

        }
    };


}
