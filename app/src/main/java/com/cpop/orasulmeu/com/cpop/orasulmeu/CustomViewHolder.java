package com.cpop.orasulmeu.com.cpop.orasulmeu;

import android.view.View;
import android.widget.TextView;

import com.cpop.orasulmeu.R;

/**
 * Created by Cristi on 17-May-16.
 */
public class CustomViewHolder {
    TextView name;
    TextView address;

    public CustomViewHolder(View v){
        name = (TextView) v.findViewById(R.id.title_item);
        address = (TextView) v.findViewById(R.id.address_item);
    }
}
