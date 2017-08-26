package com.cpop.orasulmeu.com.cpop.orasulmeu;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cpop.orasulmeu.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationDetails extends AppCompatActivity {

    TextView title;
    TextView address;
    TextView description;
    TextView time;
    TextView rate;
    RatingBar rating;
    Button bGenerate;
    double latitude;
    double longitude;
    boolean mShowMap;
    GoogleMap mMap;
    private LatLng LocationPosition;
    Marker locMarker,locMarkerDes;
    public static final String TAG = LocationDetails.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        final ViewGroup LocDetailsLayout = (ViewGroup) findViewById(R.id.LocDetailsLayout);
        final LatLng UserPosition = NavigationDrawerActivity.getPos();
        title = (TextView) findViewById(R.id.tvName);
        address = (TextView) findViewById(R.id.tvAdress);
        description = (TextView) findViewById(R.id.tvDescription);
        time =(TextView) findViewById(R.id.tvOpen_Time);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rate = (TextView) findViewById(R.id.tvRating);
        bGenerate = (Button) findViewById(R.id.bGenerateRoute);

        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("name");
        String desc = bundle.getString("description");
        String ad = bundle.getString("address");
        String rat = bundle.getString("rating");
        String open_time = bundle.getString("open_time");
        String close_time = bundle.getString("close_time");
        double latitute = bundle.getDouble("latitude");
        double longitude = bundle.getDouble("longitude");



        title.setText(name);
        address.setText("Adresa: " + ad);
        description.setText("Telefon: " + desc);
        time.setText("Program: " + open_time.substring(0, 5) + " - " + close_time.substring(0, 5));
        rate.setText("Rating: ");
        rating.setRating(Float.parseFloat(rat));
        //Log.d(TAG, "latitudine:" + latitute + " si long:" + longitude);
        mShowMap = initMap();
        LocationPosition = new LatLng(latitute,longitude);

        if (mShowMap){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(LocationPosition);
            markerOptions.title(name);
            locMarker = mMap.addMarker(markerOptions);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LocationPosition, 16);
            mMap.moveCamera(update);
        }

        bGenerate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                View map = findViewById(R.id.map);
                ViewGroup.LayoutParams sizeRules = map.getLayoutParams();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    TransitionManager.beginDelayedTransition(LocDetailsLayout);
//                }
                map.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));
                //imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

                title.setVisibility(View.GONE);
                address.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                rate.setVisibility(View.GONE);
                rating.setVisibility(View.GONE);
                bGenerate.setVisibility(View.GONE);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserPosition, 15));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(UserPosition);
                locMarkerDes = mMap.addMarker(markerOptions);

                String url = getMapsApiDirectionsUrl();
                Log.d(TAG,"BOSS:" + url);
                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(url);
            }


        });


    }

    private String getMapsApiDirectionsUrl() {
        // String waypoints = "waypoints="
        // + UserLocation.latitude + "," + UserLocation.longitude
        // + "|" + "|" + SamsaraLocation.latitude + ","
        // + SamsaraLocation.longitude;
        final LatLng UserPosition = NavigationDrawerActivity.getPos();
        String origin = String.valueOf(UserPosition.latitude)+","+String.valueOf(UserPosition.longitude);
        String destination = String.valueOf(LocationPosition.latitude)+","+String.valueOf(LocationPosition.longitude);

        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&sensor=false&mode=%22WALKING%22";

        return url;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);
               Log.d(TAG,"BOSS :" + i + " "+ routes.get(i));
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }

    private boolean initMap(){
        if (mMap == null){
            MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mMap = mapFrag.getMap();
        }
        return (mMap!=null);
    }

}
