package hoefelb.csci412.wwu.lifesplit;

//Joe Gildner 05/27/2019
//Location search code from https://www.viralandroid.com/2016/04/google-maps-android-api-adding-search-bar-part-3.html
//Current location

import android.Manifest;
import android.app.Activity;
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
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import java.util.concurrent.TimeUnit;

public class mapsTimingScreen extends FragmentActivity implements OnMapReadyCallback {
    private int numOfSplits = 1;
    private final int PERMISSION_ACCESS_LOCATION = 4;
    private boolean locationEnabled = false;
    // private Fragment addNewTaskFragment;

    private GoogleMap mMap;
    private int splitObjectIndex;
    private SplitObject splitObject;

    private float stdZoom = 15.0f;
    private float DISTANCE_THRESHOLD = 10.0f; //IN METERS

    protected LocationRequest request;
    private FusedLocationProviderClient fusedLocationClient;

    protected Location mylocation;
    protected Context context;

    private LatLng startingLocation;

    private Button startButton;
    private Button pauseButton;

    private boolean aSet = false;
    private boolean bSet = false;

    private Marker aMarker;
    private MarkerOptions aMarkerOptions;
    private LatLng aLatLng;
    private Marker bMarker;
    private LatLng bLatLng;
    private Polyline directions;
    private String method;

    private MarkerOptions currMarkerOptions;
    private Marker currLocationMarker;


    private TextView timer;

    private long totalTime=0;
    long milSecs;
    long startTime;
    long timeBuff;
    long upTime = 0L;
    Handler handler;
    long hours;
    long secs;
    long mins;


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(request, locationCallback,null);
    }


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
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        startingLocation = new LatLng(39, -97);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(locationEnabled) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, locationSuccess);
        }
        else{
            System.out.println("LOCATION IS DISABLED");
        }

        aMarkerOptions = new MarkerOptions().position(aLatLng).title("Start");
        aMarker = mMap.addMarker(aMarkerOptions);
        MarkerOptions bOptions = new MarkerOptions().position(bLatLng).title("End").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        bMarker = mMap.addMarker(bOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(aLatLng).include(bLatLng);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 100);
        mMap.moveCamera(cameraUpdate);

        if(aLatLng != null && bLatLng != null){
            String url = getDirectionsUrl(aLatLng, bLatLng);

            DownloadTask downloadTask = new DownloadTask();

            downloadTask.execute(url);
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_maps_indicator_current_position);
        currMarkerOptions = new MarkerOptions().icon(icon).anchor(0.5f,0.5f);

        startLocationUpdates();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent context = getIntent();
        splitObjectIndex = context.getIntExtra("splitObjectIndex",-1);
        if (splitObjectIndex == -1){
            System.out.println("ERROR - ID not found");
        }

        splitObject = TaskData.getTask(splitObjectIndex);
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

        startButton = (Button) findViewById(R.id.mapSplit);
        pauseButton = (Button) findViewById(R.id.mapPause);
        timer = (TextView) findViewById(R.id.currTime);
        TextView avgTime = (TextView) findViewById(R.id.avgTime);
        avgTime.setText(toTimeFormat((long)(splitObject.getAvg())));
        System.out.println(splitObject.getAvg());
        System.out.println(splitObject.getCount());

        startButton.setOnClickListener(startTimer);
        pauseButton.setOnClickListener(null);

        handler = new Handler();

    }

    private View.OnClickListener startTimer = new View.OnClickListener() {
        public void onClick(View v) {
            float[] distance = new float[2];
            Location.distanceBetween(mylocation.getLatitude(), mylocation.getLongitude(), aLatLng.latitude, aLatLng.longitude,distance);

            if(distance[0] > (DISTANCE_THRESHOLD + mylocation.getAccuracy())){
                String toastText = String.format("Too far from the start point, no cheating! %.1fm away", distance[0]);
                Toast toast=Toast.makeText(getBaseContext(),toastText,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0,300);
                toast.show();
            }else {
                startButton.setTextColor(Color.LTGRAY);
                startButton.setOnClickListener(null);
                startButton.setText("In Progress...");
                pauseButton.setText("Pause");
                pauseButton.setOnClickListener(pauseTimer);
                aMarker.remove();

                startTime = SystemClock.uptimeMillis();
                timeBuff = 0;
                handler.postDelayed(runnable, 0);
            }
        }
    };

    private View.OnClickListener resumeTimer = new View.OnClickListener() {
        public void onClick(View v) {
            startButton.setTextColor(Color.LTGRAY);
            startButton.setOnClickListener(null);
            startButton.setText("In Progress...");
            pauseButton.setText("Pause");
            pauseButton.setOnClickListener(pauseTimer);

            startTime = SystemClock.uptimeMillis()-timeBuff;
            timeBuff=0;
            handler.postDelayed(runnable, 0);
        }
    };

    private View.OnClickListener pauseTimer = new View.OnClickListener() {
        public void onClick(View v) {
            pauseButton.setText("Reset");
            startButton.setText("Resume");
            startButton.setTextColor(Color.BLACK);
            pauseButton.setOnClickListener(resetTimer);
            startButton.setOnClickListener(resumeTimer);

            timeBuff += milSecs;

            handler.removeCallbacks(runnable);
        }
    };

    private View.OnClickListener resetTimer = new View.OnClickListener() {
        public void onClick(View v) {
            startButton.setBackgroundColor(Color.parseColor("#45c15c"));
            startButton.setText("Start");
            pauseButton.setText("Pause");
            startButton.setTextColor(Color.BLACK);
            pauseButton.setOnClickListener(null);
            startButton.setOnClickListener(startTimer);
            aMarker = mMap.addMarker(aMarkerOptions);

            timeBuff = 0;
            totalTime = 0;
            timer.setText("00:00:00");


        }
    };

    public void endTimer() {
        startButton.setText("Save");
        startButton.setBackgroundColor(Color.parseColor("#00BFFF"));
        pauseButton.setText("Reset");
        startButton.setTextColor(Color.BLACK);
        pauseButton.setOnClickListener(resetTimer);

        handler.removeCallbacks(runnable);

        startButton.setOnClickListener(save);

    }

    private View.OnClickListener save = new View.OnClickListener() {
        public void onClick(View v) {
            milSecs = SystemClock.uptimeMillis() - startTime;
            totalTime = milSecs;

            splitObject.runSplit();
            splitObject.calcAvg(totalTime);

            Intent returnIntent = getIntent();
            returnIntent.putExtra("splitObjectID",splitObject.getID());
            returnIntent.putExtra("totalTimeLong",totalTime);
            returnIntent.putExtra("totalTimesRun",splitObject.getCount());
            setResult(Activity.RESULT_OK, returnIntent);
            mapsTimingScreen.this.finish();


        }
    };



    private OnSuccessListener<Location> locationSuccess = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                mylocation = location;
                startingLocation = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng myLocation = startingLocation;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation,stdZoom));

                currMarkerOptions.position(startingLocation);

                currLocationMarker = mMap.addMarker(currMarkerOptions);
                mMap.setOnMarkerClickListener(null);

            } else {
                System.out.println("LOCATION IS NULL, BIG OOF");
            }
        }
    };

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                System.out.println("YOU'VE FAILED");
                return;
            }
                mylocation = locationResult.getLastLocation();
                LatLng myLocationLatLng = new LatLng(mylocation.getLatitude(),mylocation.getLongitude());
                currMarkerOptions.position(myLocationLatLng);
                currLocationMarker.remove();
                currLocationMarker = mMap.addMarker(currMarkerOptions);

                float[] distance = new float[2];
                Location.distanceBetween(mylocation.getLatitude(), mylocation.getLongitude(), bLatLng.latitude, bLatLng.longitude,distance);

                if(distance[0] < DISTANCE_THRESHOLD + mylocation.getAccuracy()){
                    endTimer();
                }

        };
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

    //Overloaded method for when miliseconds is known, to format as --:--:--
    String toTimeFormat(long time){
        upTime = timeBuff + milSecs;
        hours = TimeUnit.MILLISECONDS.toHours(time);
        mins = TimeUnit.MILLISECONDS.toMinutes(time)-hours*60;
        secs = TimeUnit.MILLISECONDS.toSeconds(time)-3600*hours-60*mins;

        String timerText = String.format("%02d:%02d:%02d", hours, mins,secs);

        return timerText;
    }

    // ----- BELOW CODE FOR DIRECTIONS API, SOURCE: https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
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

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milSecs = SystemClock.uptimeMillis() - startTime;
            upTime = timeBuff + milSecs;

            hours = TimeUnit.MILLISECONDS.toHours(upTime);
            mins = TimeUnit.MILLISECONDS.toMinutes(upTime)-hours*60;
            secs = TimeUnit.MILLISECONDS.toSeconds(upTime)-3600*hours-60*mins;

            String timerText = String.format("%02d:%02d:%02d", hours, mins,secs);

            timer.setText(timerText);

            handler.postDelayed(this, 0);
        }
    };

}
