package com.example.cozycorner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cozycorner.R;
import com.example.cozycorner.model.Booking;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MyPostBookingsListAdapter extends ArrayAdapter<Booking> {
    private LayoutInflater inflater;
    private int layout;
    private List<Booking> items;
    private Context context;
    public MyPostBookingsListAdapter(Context context, int resource, List<Booking> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context=context;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        MyPostBookingsListAdapter.ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new MyPostBookingsListAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (MyPostBookingsListAdapter.ViewHolder) convertView.getTag();
        }
        Booking booking=items.get(position);
        viewHolder.address.setText(booking.getAddress());
        viewHolder.type.setText(booking.getType());
        viewHolder.user_name.setText(booking.getUserName());
        viewHolder.cost.setText(booking.getCost());
        Long start= booking.getStart();
        Long end = booking.getEnd();
        Date date_start= new Date(start);
        DateFormat dateFormat= DateFormat.getDateInstance();
        Date date_end= new Date(end);
        String text=dateFormat.format(date_start)+" - "+dateFormat.format(date_end);
        viewHolder.dates.setText(text);
        return convertView;
    }

    private class ViewHolder {
        TextView user_name,type,address,cost,dates;
        ViewHolder(View view){
            user_name=view.findViewById(R.id.my_post_booking_username);
            type=view.findViewById(R.id.my_post_booking_type);
            address=view.findViewById(R.id.my_post_booking_address);
            cost=view.findViewById(R.id.my_post_booking_cost);
            dates=view.findViewById(R.id.my_post_booking_dates);
        }
    }
}
