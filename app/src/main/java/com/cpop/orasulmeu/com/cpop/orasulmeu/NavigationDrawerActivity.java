package com.cpop.orasulmeu.com.cpop.orasulmeu;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cpop.orasulmeu.R;
import com.cpop.orasulmeu.fragments.ListContent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    SupportMapFragment sMapFragment;
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
    public String url;
    public String name, email;
    public static GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    static LatLng latLng;
    public LatLng markerLocation;
    public GoogleMap mGoogleMap;
    public Marker currLocationMarker;
    SharedPreferences pref;
    public static final String TAG = NavigationDrawerActivity.class.getSimpleName();

    public String getUrl() {
        return url;
    }

    public static LatLng getPos() {

        return latLng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView tvName = (TextView) header.findViewById(R.id.tvName);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);
        pref = getSharedPreferences("login.conf", Context.MODE_PRIVATE);
//        Folosite pentru Debug
//        Log.d("LoginPREF", pref.getString("email", ""));
//        Log.d("LoginPREF", pref.getString("password", ""));
//        Log.d("LoginPREF", pref.getString("name", ""));
        latLng = new LatLng(46.771210, 23.623635);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        if (pref.getString("email", "") == "") {
//            Log.d("LOGIN", "nume intent: "+ name);
//            Log.d("LOGIN", "email intent: "+ email);
            tvName.setText(name);
            tvEmail.setText(email);
        } else {
//            Log.d("LOGIN", "nume pref: "+ pref.getString("name", ""));
//            Log.d("LOGIN", "email pref: "+ pref.getString("email", ""));
            tvName.setText(pref.getString("name", ""));
            tvEmail.setText(pref.getString("email", ""));
        }
        sMapFragment.getMapAsync(this);
        sFm.beginTransaction().add(R.id.map_frame, sMapFragment).commit();


    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

//Meniu optiuni - secundar - nu este nevoie momentan de el
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();

        if (!sMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.map_frame, sMapFragment).commit();
        else
            sFm.beginTransaction().show(sMapFragment);

        if (id == R.id.nav_food) {
            this.url = "https://travelerspocket.000webhostapp.com/Restaurants.php";
            ListContent lstFragment = (ListContent) getSupportFragmentManager().findFragmentByTag("lstfragment");
            if (lstFragment == null) {
                lstFragment = new ListContent();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, lstFragment, "lstfragment").addToBackStack(null).commit();
            }

        } else if (id == R.id.nav_drinks) {
            this.url = "https://travelerspocket.000webhostapp.com/Bars.php";
            ListContent lstFragment = (ListContent) getSupportFragmentManager().findFragmentByTag("lstfragment");
            if (lstFragment == null) {
                lstFragment = new ListContent();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, lstFragment, "lstfragment").addToBackStack(null).commit();
            }

        } else if (id == R.id.nav_hotel) {
            this.url = "https://travelerspocket.000webhostapp.com/Hotels.php";
            ListContent lstFragment = (ListContent) getSupportFragmentManager().findFragmentByTag("lstfragment");
            if (lstFragment == null) {
                lstFragment = new ListContent();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, lstFragment, "lstfragment").addToBackStack(null).commit();
            }
        } else if (id == R.id.nav_visit) {
            this.url = "https://travelerspocket.000webhostapp.com/Visit.php";
            ListContent lstFragment = (ListContent) getSupportFragmentManager().findFragmentByTag("lstfragment");
            if (lstFragment == null) {
                lstFragment = new ListContent();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, lstFragment, "lstfragment").addToBackStack(null).commit();
            }
        } else if (id == R.id.nav_transport) {
            this.url = "https://travelerspocket.000webhostapp.com/Transport.php";
            ListContent lstFragment = (ListContent) getSupportFragmentManager().findFragmentByTag("lstfragment");
            if (lstFragment == null) {
                lstFragment = new ListContent();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(android.R.id.content, lstFragment, "lstfragment").addToBackStack(null).commit();
            }
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(NavigationDrawerActivity.this, AccountSettingsActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            NavigationDrawerActivity.this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        JsonArrayRequest addLocationsMarkers = new JsonArrayRequest("https://travelerspocket.000webhostapp.com/Markers.php",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
// Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                String locationName = (obj.getString("name"));
                                double locationLat = (obj.getDouble("latitude"));
                                double locationLng = (obj.getDouble("longitude"));
                                int categoryId = (obj.getInt("category_id"));
// adding Billionaire to worldsBillionaires array

                                markerLocation = new LatLng(locationLat, locationLng);

                                MarkerOptions options = new MarkerOptions();
                                options.position(markerLocation);
                                options.title(locationName);
                                if (categoryId == 2) {
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                } else {
                                    if (categoryId == 3) {
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                    } else {
                                        if (categoryId == 4) {
                                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                        } else if (categoryId == 5) {
                                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                        }
                                    }
                                }
                                mGoogleMap.addMarker(options);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
// notifying list adapter about data changes
// so that it shows updated data in ListView
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
//                hidePDialog();
            }
        });

// Adding request to request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(addLocationsMarkers);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
//        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            //place marker at current position
            this.latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

//        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

}
