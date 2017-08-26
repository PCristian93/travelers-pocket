package com.cpop.orasulmeu.com.cpop.orasulmeu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cpop.orasulmeu.R;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ProgressDialog pDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean checkFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final CheckBox cbRemember = (CheckBox) findViewById(R.id.cbRemember);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegister);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        cbRemember.setOnCheckedChangeListener(this);

        if(isNetworkAvailable()){
// Verificam daca e bifata casuta
            checkFlag = cbRemember.isChecked();
//            Log.d("FlagCheck", "checkFlag: " + checkFlag);
// Stocam credentialele pentru logarea automata
            pref = getSharedPreferences("login.conf", Context.MODE_PRIVATE);
            editor = pref.edit();
            String email =  pref.getString("email", "");
            String password =  pref.getString("password", "");
            String name = pref.getString("name","");

            HashMap data = new HashMap();
            data.put("txtEmail", email);
            data.put("txtPassword", password);

            if(!(email.equals("") && password.equals(""))){
                pDialog = new ProgressDialog(this);
// Showing progress dialog before making http request
                pDialog.setMessage("Logare...");
                pDialog.show();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                pDialog.hide();
                                Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                                    buildAlertMessageNoGps();
                                }else{
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }

            registerLink.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    LoginActivity.this.startActivity(registerIntent);
                }
            });

            bLogin.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();
                    HashMap data = new HashMap();
                    data.put("txtEmail", etEmail.getText().toString());
                    data.put("txtPassword",etPassword.getText().toString());
                    pDialog = new ProgressDialog(LoginActivity.this);
// Showing progress dialog before making http request
                    pDialog.setMessage("Logare...");
                    pDialog.show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    String name = jsonResponse.getString("name");
                                    String email = jsonResponse.getString("email");

                                    if(checkFlag){
                                        editor.putString("email",etEmail.getText().toString());
                                        editor.putString("password", etPassword.getText().toString());
                                        editor.putString("name",name);
                                        editor.apply();
                                    }

                                    Log.d("LoginPREF", pref.getString("email", ""));
                                    Log.d("LoginPREF", pref.getString("password", ""));
                                    Log.d("LoginPREF", pref.getString("name", ""));

                                    Intent intent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("email", email);
                                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                                        buildAlertMessageNoGps();
                                    }else {
                                        LoginActivity.this.startActivity(intent);
                                        finish();
                                    }


                                } else {
                                    pDialog.hide();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Email sau parola gresita")
                                            .setNegativeButton("Reincercare", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            });
        }else{
            buildAlertMessageNoInternetCon();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkFlag = isChecked;
        Log.d("FlagCheck", "checkFlag: " + checkFlag);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void buildAlertMessageNoInternetCon() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Nu exista conexiune la internet. Doriti sa va conectati la internet?")
                .setCancelable(false)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS-ul este dezactivat, doriti sa il activati?")
                .setCancelable(false)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
