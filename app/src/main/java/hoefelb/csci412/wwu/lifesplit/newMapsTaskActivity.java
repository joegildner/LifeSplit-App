package hoefelb.csci412.wwu.lifesplit;

//Joe Gildner 05/27/2019
//Location search code from https://www.viralandroid.com/2016/04/google-maps-android-api-adding-search-bar-part-3.html
//Current location

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class newMapsTaskActivity extends FragmentActivity implements OnMapReadyCallback {
    private int numOfSplits = 1;
    private final int PERMISSION_ACCESS_LOCATION = 4;
    private boolean locationEnabled = false;
    // private Fragment addNewTaskFragment;

    private GoogleMap mMap;

    private float stdZoom = 15.0f;

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    protected LocationRequest request;
    private FusedLocationProviderClient fusedLocationClient;

    protected Location mylocation;
    protected Context context;

    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    private LatLng startingLocation;

    private Button aButton;
    private Button bButton;

    private boolean aSet = false;
    private boolean bSet = false;
    private Marker bMarker;
    private Marker aMarker;
    private TextView infoText;
    private String defText;


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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            locationEnabled = true;
        }

        request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(100);

        startingLocation = new LatLng(39, -97);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(locationEnabled) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, locationSuccess);
        }
        else{
            System.out.println("LOCATION IS DISABLED");
        }

        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startingLocation));
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
        aButton = (Button) findViewById(R.id.setA);
        bButton = (Button) findViewById(R.id.setB);
        infoText = (TextView)findViewById(R.id.infoText);
        defText = (String)infoText.getText();


        search.setOnClickListener(mapSearch);
        aButton.setOnClickListener(setA);
        aButton.setBackgroundColor(Color.RED);
        bButton.setOnClickListener(setB);
        bButton.setBackgroundColor(Color.parseColor("#007fff"));


    }

    private View.OnClickListener setA = new View.OnClickListener() {
        public void onClick(View v) {
            mMap.setOnMapClickListener(setAMarker);
            bButton.setOnClickListener(null);
            aButton.setOnClickListener(null);
            infoText.setText("Tap map to set marker.");

        }
    };

    private GoogleMap.OnMapClickListener setAMarker = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {
            MarkerOptions markerOptions = new MarkerOptions().position(point);
            aMarker = mMap.addMarker(markerOptions);

            aButton.setText("Reset A");
            aButton.setOnClickListener(resetA);
            mMap.setOnMapClickListener(null);
            aSet = true;
            infoText.setText(defText);

            if(bSet) bButton.setOnClickListener(resetB);
            else bButton.setOnClickListener(setB);
        }
    };

    private View.OnClickListener resetA = new View.OnClickListener() {
        public void onClick(View v) {
            aButton.setText("Set A");
            aButton.setOnClickListener(setA);
            aMarker.remove();
            aMarker = null;
            aSet = false;

        }
    };

    private View.OnClickListener setB = new View.OnClickListener() {
        public void onClick(View v) {
            mMap.setOnMapClickListener(setBMarker);
            bButton.setOnClickListener(null);
            aButton.setOnClickListener(null);
            infoText.setText("Tap map to set marker.");
        }
    };

    private GoogleMap.OnMapClickListener setBMarker = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {
            MarkerOptions markerOptions = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            bMarker = mMap.addMarker(markerOptions);


            bButton.setText("Reset B");
            bButton.setOnClickListener(resetB);
            mMap.setOnMapClickListener(null);
            bSet = true;
            infoText.setText(defText);

            if(aSet) aButton.setOnClickListener(resetA);
            else aButton.setOnClickListener(setA);
        }
    };

    private View.OnClickListener resetB = new View.OnClickListener() {
        public void onClick(View v) {
            bButton.setText("Set B");
            bButton.setOnClickListener(setB);
            bMarker.remove();
            bMarker = null;

            bSet = false;

        }
    };

    private OnSuccessListener<Location> locationSuccess = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                mylocation = location;
                startingLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation,stdZoom));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_maps_indicator_current_position);

                MarkerOptions markerOptions = new MarkerOptions().position(startingLocation)
                        .icon(icon).anchor(0.5f,0.5f);;

                mMap.addMarker(markerOptions);
            } else {
                System.out.println("LOCATION IS NULL, BIG OOF");
            }
        }
    };

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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, stdZoom));

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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));

            }

        }
    };

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationEnabled = true;
                }

                else {
                    locationEnabled = false;
                }

                return;
            }
        }
    }


}
