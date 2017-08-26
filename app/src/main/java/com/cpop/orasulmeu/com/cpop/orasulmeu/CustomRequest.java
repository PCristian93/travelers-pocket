package com.cpop.orasulmeu.com.cpop.orasulmeu;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Cristi on 21-May-16.
 */
public class CustomRequest extends Request<JSONObject> {
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String,String> params,
                         Response.Listener<JSONObject> responseListener,Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(JSONObject response) {

    }
}
