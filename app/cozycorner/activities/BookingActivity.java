package com.example.cozycorner.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.model.Booking;
import com.example.cozycorner.DateValidatorBlockAll;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.R;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {
    Post post;
    TextView type,address,cost,places,dates;
    Button confirm,choose;
    Long startDate=0L,endDate=0L;
    List<Calendar> disabled=new ArrayList<>();
    public static List<Long>  long_disabled=new ArrayList<>();
    List<Booking> bookings;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        preferenceManager=new PreferenceManager(getApplicationContext());
        Intent intent=getIntent();
        post= (Post) intent.getSerializableExtra("post");
        type=findViewById(R.id.booking_type);
        address=findViewById(R.id.booking_address);
        dates=findViewById(R.id.booking_dates);
        cost=findViewById(R.id.booking_price);
        places=findViewById(R.id.booking_places);
        confirm=findViewById(R.id.booking_save_btn);
        choose=findViewById(R.id.booking_choose_date_btn);
        type.setText(post.getType());
        address.setText(post.getAddress());
        bookings=new ArrayList<>();
        loadBookings();
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("Выберите дату");
        CalendarConstraints.Builder calenderConstraintBuilder = new CalendarConstraints.Builder();
        calenderConstraintBuilder.setValidator(new DateValidatorBlockAll());
        materialDateBuilder.setCalendarConstraints(calenderConstraintBuilder.build());
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        choose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Pair<Long,Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        dates.setText(materialDatePicker.getHeaderText());
                        startDate=selection.first;
                        endDate=selection.second;
                        int days= (int) ((endDate-startDate)/86400000)+1;
                        String price=post.getCost();
                        String[] mas=price.split(" ");
                        int price1=Integer.parseInt(mas[0]);
                        int cost1=days*price1;
                        String final_cost=cost1+" руб.";
                        cost.setText(final_cost);
                        List<Long> list=new ArrayList<>();
                        for (long i = startDate; i <= endDate; i+=86400000) {
                            list.add(i);
                        }
                        TextView textView=BookingActivity.this.findViewById(R.id.warning_textView);
                        boolean shouldBeTrue = !Collections.disjoint(list, long_disabled);
                        if(shouldBeTrue){
                            confirm.setEnabled(false);
                            textView.setVisibility(View.VISIBLE);
                        }else{
                            confirm.setEnabled(true);
                            textView.setVisibility(View.GONE);
                        }
                    }
                });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_booking();
            }
        });
        places.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView textView=BookingActivity.this.findViewById(R.id.warning_textView2);
                if(!s.toString().equals("")){
                    if(Integer.parseInt(s.toString())>post.getPlaces()){
                        textView.setVisibility(View.VISIBLE);
                        confirm.setEnabled(false);
                    }else{
                        textView.setVisibility(View.GONE);
                        confirm.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Intent intent= new Intent(BookingActivity.this, HomePostInfoActivity.class);
                //intent.putExtra("post",post);
                //startActivity(intent);
                finish();
            }
        });
    }
    private void insert_booking(){
        int user_id=Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));
        String url = Constants.KEY_DB_ADDRESS+"insert_booking.php";
        confirm.setEnabled(false);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(BookingActivity.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String status = responseJson.getString("status");
                            String message = responseJson.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);
                            if(status.equals("success")){
                                Intent intent=new Intent(BookingActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                //finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(post.getId()));
                params.put("start", String.valueOf(startDate));
                params.put("end", String.valueOf(endDate));
                params.put("user_id", String.valueOf(user_id));
                params.put("cost", cost.getText().toString());
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
    private void loadBookings(){
        String url=Constants.KEY_DB_ADDRESS+"get_bookings.php?post_id="+post.getId();
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
                                Booking booking=new Booking();
                                booking.setId(product.getInt("id"));
                                booking.setUser_id(product.getInt("user_id"));
                                booking.setCost(product.getString("cost"));
                                booking.setPost_id(product.getInt("post_id"));
                                booking.setStart(product.getLong("start"));
                                booking.setEnd(product.getLong("end"));
                                //adding the product to product list
                                bookings.add(booking);
                                setConstrains();
                            }
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
    private void setConstrains(){
        for (Booking booking:bookings) {
            Calendar b_start=Calendar.getInstance();
            b_start.setTimeInMillis(booking.getStart());
            Calendar b_end=Calendar.getInstance();
            b_end.setTimeInMillis(booking.getEnd());
            List<Calendar> dis=getAllDaysBetween(b_start,b_end);
            disabled.addAll(dis);
        }
        calendar_to_long();
    }
    private static List<Calendar> getAllDaysBetween(Calendar start, Calendar end) {
        List<Calendar> result = new ArrayList<>();
        Calendar startCalendar = createNewCalendarWithoutTime(start);
        Calendar endCalendar = createNewCalendarWithoutTime(end);
        while(startCalendar.before(endCalendar)) {
            result.add(createNewCalendarWithoutTime(startCalendar));
            startCalendar.add(Calendar.DATE, 1);
        }
        result.add(createNewCalendarWithoutTime(startCalendar));
        return result;
    }
    private static Calendar createNewCalendarWithoutTime(Calendar calendar) {
        Calendar result = Calendar.getInstance();
        result.setTime(calendar.getTime());
        result.set(Calendar.MILLISECOND, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.HOUR_OF_DAY, 0);
        return  result;
    }
    private void calendar_to_long(){
        for (Calendar cal: disabled) {
            Long l=cal.getTimeInMillis();
            long_disabled.add(l);
        }
    }

}