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
import com.example.cozycorner.R;
import com.example.cozycorner.model.Review;
import com.example.cozycorner.adapters.ReviewsListAdapter;
import com.example.cozycorner.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SeeReviewsActivity extends AppCompatActivity {
    private List<Review> reviews;
    private ListView list;
    private int post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_reviews);
        Intent intent=getIntent();
        post_id=intent.getIntExtra("post_id",0);
        reviews=new ArrayList<>();
        list=findViewById(R.id.list_reviews);
        load_reviews();
    }
    private void load_reviews(){
        String url= Constants.KEY_DB_ADDRESS+"get_reviews.php?post_id="+post_id;
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
                                Review review=new Review();
                                review.setId(product.getInt("id"));
                                review.setReview_text(product.getString("text"));
                                review.setRating(Float.parseFloat(product.getString("mark")));
                                String userName= product.getString("firstName")+" "+product.getString("lastName");
                                review.setUser_name(userName);
                                review.setUser_id(product.getInt("user_id"));
                                review.setPost_id(product.getInt("post_id"));
                                reviews.add(review);
                            }
                            //creating adapter object and setting it to recyclerview
                            ReviewsListAdapter adapter = new ReviewsListAdapter(SeeReviewsActivity.this,R.layout.list_reviews_item, reviews);
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