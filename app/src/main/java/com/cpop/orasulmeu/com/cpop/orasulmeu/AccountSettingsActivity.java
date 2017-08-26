package com.cpop.orasulmeu.com.cpop.orasulmeu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cpop.orasulmeu.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountSettingsActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static final String TAG = AccountSettingsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etOldPw = (EditText) findViewById(R.id.etPassword);
        final EditText etNewPw  = (EditText) findViewById(R.id.etNewPassword);
        final EditText etConfirmPw = (EditText) findViewById(R.id.etConfirmNewPassword);
        final TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        Button bSaveData  = (Button) findViewById(R.id.bSaveData);
        Button bLogOut = (Button) findViewById(R.id.bLogout);
        ProgressDialog PD;

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        etName.setText(name);
        tvEmail.setText(email);


        pref = getSharedPreferences("login.conf", Context.MODE_PRIVATE);

        Log.d(TAG, pref.getString("username", ""));
        Log.d(TAG, pref.getString("password", ""));
        PD = new ProgressDialog(this);
        PD.setMessage("Asteptati...");
        PD.setCancelable(false);

        bSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = tvEmail.getText().toString();
                final String name = etName.getText().toString();
                final String oldpassword = etOldPw.getText().toString();
                final String newpassword = etNewPw.getText().toString();
                final String confirmpassword = etConfirmPw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(),
                                        "Modificari Salvate",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Parola Veche Incorecta", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if (etNewPw != null && etNewPw.length() >= 7 && etNewPw.length() <= 16) {
                    if (newpassword.equals(confirmpassword)) {
                        Log.d(TAG, "New PW:" + newpassword);
                        AccountSettingsRequest updateRequest = new AccountSettingsRequest(email, name, oldpassword, newpassword, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(AccountSettingsActivity.this);
                        queue.add(updateRequest);
                    } else {
                        Log.d(TAG, "New PW:" + etNewPw.getText().toString());
                        Log.d(TAG, "Conf PW:" + etConfirmPw.getText().toString());
                        etNewPw.setError("Noua parola introdusa nu este aceeasi cu parola confirmata");
                        etNewPw.requestFocus();
                    }
                } else {
                    etNewPw.setError("Parola trebuie sa contina intre 7 si 16 caractere");
                    etNewPw.requestFocus();
                }

            }
        });

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = pref.edit();
                editor.clear();
                editor.commit();
                Intent in = new Intent(AccountSettingsActivity.this, LoginActivity.class);
                startActivity(in);
                finish();
            }
        });

    }
}
