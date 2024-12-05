package com.example.cozycorner.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.model.Booking;
import com.example.cozycorner.fragments.CompletedBookingsFragment;
import com.example.cozycorner.fragments.FutureBookingsFragment;
import com.example.cozycorner.R;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MyBookingsActivity extends AppCompatActivity {
    private List<Booking> bookings_posts;
    private List<Booking> future_list,completed_list;
    RadioButton future,completed;
    RadioGroup radioGroup;
    PreferenceManager preferenceManager;
    FutureBookingsFragment futureBookingsFragment;
    CompletedBookingsFragment completedBookingsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        preferenceManager=new PreferenceManager(getApplicationContext());
        future=findViewById(R.id.future_radio_btn);
        completed=findViewById(R.id.completed_radio_btn);
        radioGroup=findViewById(R.id.radio_group_bookings);
        bookings_posts=new ArrayList<>();
        future_list=new ArrayList<>();
        completed_list=new ArrayList<>();
        futureBookingsFragment=FutureBookingsFragment.newInstance(future_list);
        completedBookingsFragment=CompletedBookingsFragment.newInstance(completed_list);
        load_bookings();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case -1:
                    case R.id.future_radio_btn:
                        setNewFragment(futureBookingsFragment);
                        break;
                    case R.id.completed_radio_btn:
                        setNewFragment(completedBookingsFragment);
                    default:
                        break;
                }
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Intent intent=new Intent(MyBookingsActivity.this, PersonActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_bookings();
    }

    private void load_bookings(){
        int user_id=Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));
        String url=Constants.KEY_DB_ADDRESS+"get_my_bookings.php?user_id="+user_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            bookings_posts.clear();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                Booking booking=new Booking();
                                booking.setId(product.getInt("id"));
                                booking.setStart(product.getLong("start"));
                                booking.setEnd(product.getLong("end"));
                                booking.setCost(product.getString("cost"));
                                booking.setUser_id(product.getInt("user_id"));
                                booking.setPost_id(product.getInt("post_id"));
                                booking.setType(product.getString("type"));
                                booking.setAddress(product.getString("address"));
                                int value=product.getInt("hasReview");
                                booking.setHasReview(value != 0);
                                bookings_posts.add(booking);
                            }
                            sepList();
                            //creating adapter object and setting it to recyclerview
                            //HomePostListAdapter adapter = new HomePostListAdapter(HomeActivity.this,R.layout.home_post_list_item, home_posts);
                            //posts.setAdapter(adapter);
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
    private void setNewFragment(Fragment frag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.my_bookings_frame,frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void sepList(){
        Calendar today=Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long now=today.getTimeInMillis();
        completed_list.clear();
        future_list.clear();
        for (Booking booking:bookings_posts) {
            if(now>booking.getEnd()){
                completed_list.add(booking);
            }else{
                future_list.add(booking);
            }
        }
        if(futureBookingsFragment.adapter!=null){
            futureBookingsFragment.adapter.notifyDataSetChanged();
        }else{
            setNewFragment(futureBookingsFragment);
        }
        if(completedBookingsFragment.adapter!=null){
            completedBookingsFragment.adapter.notifyDataSetChanged();
        }
    }
}