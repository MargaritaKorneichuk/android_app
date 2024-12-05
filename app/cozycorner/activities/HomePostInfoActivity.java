package com.example.cozycorner.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.adapters.UserAdapter;
import com.example.cozycorner.adapters.ViewPagerAdapter;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.R;
import com.example.cozycorner.model.User;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class HomePostInfoActivity extends AppCompatActivity {
    private Post post;
    private Button chat,book,reviews;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_post_info);
        Intent intent=getIntent();
        post=(Post)intent.getSerializableExtra("post");
        user=new User();
        TextView type,address,square,level,cost,beds,places,rent_type,description,requirements,rating;
        ViewPager2 viewPager2=findViewById(R.id.viewPager);
        String[] images_str=post.getImages().split(";");
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getApplicationContext(), Arrays.asList(images_str.clone()));
        viewPager2.setAdapter(viewPagerAdapter);
        RatingBar ratingBar=findViewById(R.id.ratingBar3);
        type=findViewById(R.id.home_post_info_type);
        address=findViewById(R.id.home_post_info_address);
        square=findViewById(R.id.home_post_info_square);
        level=findViewById(R.id.home_post_info_level);
        cost=findViewById(R.id.home_post_info_price);
        beds=findViewById(R.id.home_post_info_beds);
        places=findViewById(R.id.home_post_info_places);
        rent_type=findViewById(R.id.home_post_info_rent_type);
        description=findViewById(R.id.home_post_info_description);
        requirements=findViewById(R.id.home_post_info_requirements);
        rating=findViewById(R.id.home_post_info_rating);
        book=findViewById(R.id.home_post_info_book_btn);
        chat=findViewById(R.id.home_post_info_chat_btn);
        reviews=findViewById(R.id.home_post_see_reviews);
        String str=post.getType();
        String[] mas=str.split(", ");
        type.setText(mas[0]);
        address.setText(post.getAddress());
        square.setText(String.valueOf(post.getMetres()));
        level.setText(mas[1]);
        cost.setText(post.getCost());
        beds.setText(String.valueOf(post.getBeds()));
        places.setText(String.valueOf(post.getPlaces()));
        rent_type.setText(post.getRent_type());
        description.setText(post.getDescription());
        requirements.setText(post.getRequirements());
        ratingBar.setRating(post.getRating());
        rating.setText(String.valueOf(post.getRating()));
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePostInfoActivity.this, BookingActivity.class);
                intent.putExtra("post",post);
                startActivity(intent);
                //finish();
            }
        });
        if(!post.getRent_type().equals("Посуточно")){
            book.setVisibility(View.GONE);
        }
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePostInfoActivity.this, SeeReviewsActivity.class);
                intent.putExtra("post_id",post.getId());
                startActivity(intent);
            }
        });
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Intent intent= new Intent(HomePostInfoActivity.this, HomeActivity.class);
                //startActivity(intent);
                finish();
            }
        });
        PreferenceManager preferenceManager=new PreferenceManager(getApplicationContext());
        if(String.valueOf(post.getHost_id()).equals(preferenceManager.getString(Constants.KEY_USER_ID))){
            chat.setEnabled(false);
        }
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostInfo();
            }
        });
    }
    private void getHostInfo(){
        String url=Constants.KEY_DB_ADDRESS+"get_users.php?user_id="+post.getHost_id();
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
                                user.setFirstName(product.getString("firstName"));
                                user.setLastName(product.getString("lastName"));
                                user.setImage(product.getString("image"));
                                user.setId(product.getString("id"));

                            }
                            Intent intent=new Intent(getApplicationContext(),PersonalChatActivity.class);
                            intent.putExtra(Constants.KEY_USER,user);
                            startActivity(intent);
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