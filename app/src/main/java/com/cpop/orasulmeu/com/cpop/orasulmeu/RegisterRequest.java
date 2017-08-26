package com.cpop.orasulmeu.com.cpop.orasulmeu;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by Cristi on 10-Apr-16.
 */

public class RegisterRequest extends StringRequest{
    private static final String REGISTER_REQUEST_URL = "https://travelerspocket.000webhostapp.com/Register.php";
    private Map<String, String> params;

    public RegisterRequest (String name, String email, String password, Response.Listener<String> listener){
       super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
