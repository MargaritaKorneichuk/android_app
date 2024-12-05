package com.example.cozycorner.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.model.Booking;
import com.example.cozycorner.adapters.MyPostBookingsListAdapter;
import com.example.cozycorner.R;
import com.example.cozycorner.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPostBookingsActivity extends AppCompatActivity {
    int post_id;
    List<Booking> list_bookings;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_bookings);
        Intent intent=getIntent();
        post_id=intent.getIntExtra("post_id",0);
        list_bookings=new ArrayList<>();
        list=findViewById(R.id.my_post_bookings);
        loadBookings();
    }
    private void loadBookings(){
        String url= Constants.KEY_DB_ADDRESS+"get_my_post_bookings.php?post_id="+post_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                Booking booking= new Booking();
                                booking.setId(product.getInt("id"));
                                booking.setAddress(product.getString("address"));
                                booking.setPost_id(product.getInt("post_id"));
                                String userName=product.getString("lastName")+" "+product.getString("firstName");
                                booking.setUserName(userName);
                                booking.setStart(product.getLong("start"));
                                booking.setEnd(product.getLong("end"));
                                booking.setCost(product.getString("cost"));
                                booking.setType(product.getString("type"));
                                list_bookings.add(booking);
                            }
                            //creating adapter object and setting it to recyclerview
                            MyPostBookingsListAdapter adapter = new MyPostBookingsListAdapter(MyPostBookingsActivity.this,R.layout.my_post_bookings_list_item, list_bookings);
                            list.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}