package com.cpop.orasulmeu.com.cpop.orasulmeu;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cristi on 21-May-16.
 */
public class AccountSettingsRequest extends StringRequest{
    private static final String ACCOUNT_SETTINGS_REQUEST_URL = "https://travelerspocket.000webhostapp.com/Update.php";
    private Map<String, String> params;

    public AccountSettingsRequest (String email, String name, String oldpassword, String newpassword,  Response.Listener<String> listener){
        super(Method.POST, ACCOUNT_SETTINGS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("name", name);
        params.put("oldpassword", oldpassword);
        params.put("newpassword", newpassword);
    }



    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
