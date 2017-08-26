package com.cpop.orasulmeu.com.cpop.orasulmeu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.cpop.orasulmeu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cristi on 24-Apr-16.
 */


public class CustomListViewAdapter extends BaseAdapter implements Filterable {
    Context mContext;
    LayoutInflater inflater;
    List<Items> listItems;
    CustomFilter filter;
    List<Items> filteredlistItems;
    public static final String TAG = CustomListViewAdapter.class.getSimpleName();


    public CustomListViewAdapter(Activity context, List<Items> listItems) {
        mContext = context;
        this.listItems = listItems;
        this.filteredlistItems = listItems;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int location) {
        return listItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_layout, null);

        TextView title_item = (TextView) convertView.findViewById(R.id.title_item);
        TextView address_item = (TextView) convertView.findViewById(R.id.address_item);


// getting billionaires data for the row
//        Items m = listItems.get(position);
//
//// name
//        title_item.setText(m.getName());
//        address_item.setText(m.getAddress());

        CustomViewHolder holder = new CustomViewHolder(convertView);
        holder.name.setText(listItems.get(position).getName());
        holder.address.setText(listItems.get(position).getAddress());

        // Listen for ListView Item Click
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Items item = (new ArrayList<Items>(locationsMap.values())).get(pos);
//
        //Log.d(TAG, "SELECTED POSITION:" + String.valueOf(pos));
        //Log.d(TAG,"String:"+ item);
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, LocationDetails.class);
        bundle.putString("name", (listItems.get(position).getName()));
        bundle.putString("description", (listItems.get(position).getDescription()));
        bundle.putString("address", (listItems.get(position).getAddress()));
        bundle.putString("rating", (listItems.get(position).getRating()));
        bundle.putString("open_time", (listItems.get(position).getOpen_time()));
        bundle.putString("close_time", (listItems.get(position).getClose_time()));
        bundle.putDouble("latitude", (listItems.get(position).getLatitude()));
        bundle.putDouble("longitude", (listItems.get(position).getLongitude()));

        intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


    @Override
    public Filter getFilter() {

        if(filter == null){
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint){

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0){
                //CONSTRAINT TO UPPER
                constraint = constraint.toString().toUpperCase();
                Log.d(TAG,"Contraint"+ constraint.toString());
                List<Items> filters = new ArrayList<Items>();

                for(int i=0;i<filteredlistItems.size();i++){
                    if(filteredlistItems.get(i).getName().toUpperCase().contains(constraint)){
                        Items item = new Items(filteredlistItems.get(i).getName(),filteredlistItems.get(i).getAddress());
                        filters.add(filteredlistItems.get(i));
                    }
                }

                results.count = filters.size();
                results.values = filters;
                Log.d(TAG, "Filtered results:" + results.values.toString());
            }else{
                results.count = filteredlistItems.size();
                results.values = filteredlistItems;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            listItems = (List<Items>) results.values;
            Log.d(TAG, "Filtered results:" + results.values.toString());
            notifyDataSetChanged();
        }

    }
}