package com.example.cozycorner.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cozycorner.R;
import com.example.cozycorner.activities.ReviewActivity;
import com.example.cozycorner.model.Booking;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class CompletedBookingsListAdapter extends ArrayAdapter<Booking> {
    private LayoutInflater inflater;
    private int layout;
    private List<Booking> items;
    private Context context;
    public CompletedBookingsListAdapter(Context context, int resource, List<Booking> items) {
        super(context, resource,items);
        this.items = items;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context=context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        CompletedBookingsListAdapter.ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new CompletedBookingsListAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (CompletedBookingsListAdapter.ViewHolder) convertView.getTag();
        }
        Booking booking=items.get(position);
        viewHolder.type.setText(booking.getType());
        viewHolder.address.setText(booking.getAddress());
        Long start= booking.getStart();
        Long end = booking.getEnd();
        Date date_start= new Date(start);
        DateFormat dateFormat= DateFormat.getDateInstance();
        Date date_end= new Date(end);
        String text=dateFormat.format(date_start)+" - "+dateFormat.format(date_end);
        viewHolder.dates.setText(text);
        viewHolder.cost.setText(booking.getCost());
        if(booking.isHasReview()){
            viewHolder.review.setEnabled(false);
        }
        viewHolder.review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ReviewActivity.class);
                intent.putExtra("booking",booking);
                context.startActivity(intent);
                //((Activity)context).finish();
            }
        });
        return convertView;
    }
    private class ViewHolder {
        TextView type,address,dates,cost;
        Button review;
        ViewHolder(View view){
            type=view.findViewById(R.id.my_completed_booking_type);
            address=view.findViewById(R.id.my_completed_booking_address);
            dates=view.findViewById(R.id.my_completed_booking_dates);
            cost=view.findViewById(R.id.my_completed_booking_cost);
            review=view.findViewById(R.id.my_booking_review_btn);
        }
    }
}
