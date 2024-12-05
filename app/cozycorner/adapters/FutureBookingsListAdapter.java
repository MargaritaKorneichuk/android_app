package com.example.cozycorner.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.R;
import com.example.cozycorner.activities.MyBookingsActivity;
import com.example.cozycorner.model.Booking;
import com.example.cozycorner.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FutureBookingsListAdapter extends ArrayAdapter<Booking> {
    private LayoutInflater inflater;
    private int layout;
    private List<Booking> items;
    private Context context;
    FutureBookingsListAdapter.ViewHolder viewHolder;
    Booking booking;
    public FutureBookingsListAdapter(Context context, int resource, List<Booking> items) {
        super(context, resource,items);
        this.items = items;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context=context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new FutureBookingsListAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (FutureBookingsListAdapter.ViewHolder) convertView.getTag();
        }
        booking=items.get(position);
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
        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Отмена брони");
                builder.setMessage("Отменить бронирование?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cancel();
                        Intent intent=new Intent(context, MyBookingsActivity.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                final AlertDialog alertDialog = builder.show();
                alertDialog.setCanceledOnTouchOutside(true);
            }
        });
        return convertView;
    }
    private class ViewHolder {
        TextView type,address,dates,cost;
        Button cancel;
        ViewHolder(View view){
            type=view.findViewById(R.id.my_future_booking_type);
            address=view.findViewById(R.id.my_future_booking_address);
            dates=view.findViewById(R.id.my_future_booking_dates);
            cost=view.findViewById(R.id.my_future_booking_cost);
            cancel=view.findViewById(R.id.my_future_booking_cancel_btn);
        }
    }
    private void cancel(){
        viewHolder.cancel.setEnabled(false);
        String url = Constants.KEY_DB_ADDRESS+"cancel_booking.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(FutureBookingsListAdapter.this.context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String status = responseJson.getString("status");
                            String message = responseJson.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            viewHolder.cancel.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(booking.getId()));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        // To prevent timeout error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
}
