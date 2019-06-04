package hoefelb.csci412.wwu.lifesplit;

//Joe Gildner 05/27/2019
//Location search code from https://www.viralandroid.com/2016/04/google-maps-android-api-adding-search-bar-part-3.html
//Current location

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mapsTimingScreen extends FragmentActivity implements OnMapReadyCallback {
    private int numOfSplits = 1;
    private final int PERMISSION_ACCESS_LOCATION = 4;
    private boolean locationEnabled = false;
    // private Fragment addNewTaskFragment;

    private GoogleMap mMap;
    private int splitObjectIndex;

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

    private Button startButton;
    private Button pauseButton;
    private TextView currTime;
    private TextView avgTime;

    private boolean aSet = false;
    private boolean bSet = false;

    private Marker aMarker;
    private LatLng aLatLng;
    private Marker bMarker;
    private LatLng bLatLng;
    private Polyline directions;
    private String method;

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

        MarkerOptions markerOptions = new MarkerOptions().position(aLatLng);
        aMarker = mMap.addMarker(markerOptions);
        MarkerOptions bOptions = new MarkerOptions().position(bLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        bMarker = mMap.addMarker(bOptions);

        LatLngBounds mapBounds = new LatLngBounds(aLatLng, bLatLng);
        mMap.setLatLngBoundsForCameraTarget(mapBounds);

        if(aLatLng != null && bLatLng != null){
            String url = getDirectionsUrl(aLatLng, bLatLng);

            DownloadTask downloadTask = new DownloadTask();

            downloadTask.execute(url);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent context = getIntent();
        splitObjectIndex = context.getIntExtra("splitObjectIndex",-1);
        if (splitObjectIndex == -1){
            System.out.println("ERROR - ID not found");
        }
        final SplitObject  splitObject = TaskData.getTask(splitObjectIndex);
        Editable title = splitObject.getName();
        Editable[] splitNames = splitObject.getSplitNamesArray();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_timing_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTiming);
        mapFragment.getMapAsync(this);

        aLatLng = new LatLng(Double.parseDouble(splitNames[0].toString()),Double.parseDouble(splitNames[1].toString()));

        bLatLng = new LatLng(Double.parseDouble(splitNames[2].toString()),Double.parseDouble(splitNames[3].toString()));

        method = splitNames[4].toString();

        Toolbar toolbar = (Toolbar) findViewById(R.id.mapTitle);
        toolbar.setTitle(splitObject.getName());

        Button startButton = (Button) findViewById(R.id.mapSplit);
        Button pauseButton = (Button) findViewById(R.id.mapPause);
        TextView currTime = (TextView) findViewById(R.id.currTime);
        TextView avgTime = (TextView) findViewById(R.id.avgTime);

        startButton.setOnClickListener(startTimer);


    }

    private View.OnClickListener startTimer = new View.OnClickListener() {
        public void onClick(View v) {
            startButton.setTextColor(Color.LTGRAY);
            startButton.setOnClickListener(null);
        }
    };

    private View.OnClickListener pauseTimer = new View.OnClickListener() {
        public void onClick(View v) {
            pauseButton.setText("Reset");
            startButton.setText("Resume");
            startButton.setTextColor(Color.BLACK);
            pauseButton.setOnClickListener(resetTimer);
            startButton.setOnClickListener(startTimer);
        }
    };

    private View.OnClickListener resetTimer = new View.OnClickListener() {
        public void onClick(View v) {

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

    private class DownloadTask extends AsyncTask {

        @Override
        protected String doInBackground(Object[] url) {

            String data = "";

            try {
                data = downloadUrl((String)url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            System.out.println(data);
            return data;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute((String)result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(routes.toString());
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                System.out.println("REACHED");
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            directions = mMap.addPolyline(lineOptions);

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //&key=<YOUR_API_KEY>
        String api_key = "key=" + "AIzaSyDX-E1J2cTe3aQJxId6vh_HPsFXQ69OdfQ";

        // Sensor enabled
        String mode = "mode="+method;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + api_key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
