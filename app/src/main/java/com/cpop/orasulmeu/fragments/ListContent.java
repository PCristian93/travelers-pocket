package com.cpop.orasulmeu.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cpop.orasulmeu.R;
import com.cpop.orasulmeu.com.cpop.orasulmeu.CustomListViewAdapter;
import com.cpop.orasulmeu.com.cpop.orasulmeu.Items;
import com.cpop.orasulmeu.com.cpop.orasulmeu.NavigationDrawerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristi on 20-Apr-16.
 */
public class ListContent extends ListFragment {
    // Log tag
    private static final String TAG = ListContent.class.getSimpleName();
    // Billionaires json ur

    private ProgressDialog pDialog;
//    Map<String, Items> locationsMap;
    private List<Items> itemList = new ArrayList<Items>();
    private ListView listView;
    private CustomListViewAdapter adapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_list, container, false);
//        locationsMap = new LinkedHashMap<String,Items>();
        NavigationDrawerActivity nvg = (NavigationDrawerActivity) getActivity();
        String url = nvg.getUrl();
        searchView = (SearchView) view.findViewById(R.id.searchView);
        adapter = new CustomListViewAdapter(getActivity(), itemList);
        setListAdapter(adapter);


        pDialog = new ProgressDialog(getActivity());
// Showing progress dialog before making http request
        pDialog.setMessage("Incarcare Lista...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest listRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
// Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Items items = new Items();
//                                items.setId(obj.getInt("location_id"));
                                items.setName(obj.getString("name"));
                                items.setAddress(obj.getString("address"));
                                items.setDescription(obj.getString("description"));
                                items.setRating(obj.getString("rating"));
                                items.setOpen_time(obj.getString("open_time"));
                                items.setClose_time(obj.getString("close_time"));
                                items.setLatitude(obj.getDouble("latitude"));
                                items.setLongitude(obj.getDouble("longitude"));
// adding Billionaire to worldsBillionaires array
                                itemList.add(items);
//                                locationsMap.put(items.getName(), items);
//                                Log.d(TAG,"LOCATIONS MAP:" + locationsMap.values().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
// notifying list adapter about data changes
// so that it shows updated data in ListView
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                hidePDialog();
            }
        });

// Adding request to request queue
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(listRequest);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        return view;



    }

//    @Override
//    public void onListItemClick(ListView l, View v, int pos, long id) {
//        super.onListItemClick(l, v, pos, id);
//        //Items item = locationsMap.get(pos);
//        Items item = (new ArrayList<Items>(locationsMap.values())).get(pos);
//
//        //Log.d(TAG, "SELECTED POSITION:" + String.valueOf(pos));
//        //Log.d(TAG,"String:"+ item);
//        Bundle bundle = new Bundle();
//        Intent intent = new Intent(v.getContext(), LocationDetails.class);
//        bundle.putInt("location_id",item.getId());
//        bundle.putString("name", item.getName());
//        bundle.putString("description", item.getDescription());
//        bundle.putString("address", item.getAddress());
//        bundle.putString("rating", item.getRating());
//        bundle.putString("open_time", item.getOpen_time());
//        bundle.putString("close_time", item.getClose_time());
//        bundle.putDouble("latitude", item.getLatitude());
//        bundle.putDouble("longitude", item.getLongitude());
//
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }


    }

}

