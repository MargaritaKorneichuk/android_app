package com.example.cozycorner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.R;
import com.example.cozycorner.adapters.MyPostsListAdapter;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    FloatingActionButton add_post;
    ListView posts;
    List<Post> my_posts;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        preferenceManager=new PreferenceManager(getApplicationContext());
        add_post=findViewById(R.id.add_post);
        posts=findViewById(R.id.posts_list);
        my_posts=new ArrayList<>();
        load_posts();
        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyPostsActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });
    }
    void load_posts(){
        int user_id=Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));
        String url=Constants.KEY_DB_ADDRESS+"get_my_posts.php?host_id="+user_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            my_posts.clear();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                Post post=new Post();
                                post.setId(product.getInt("id"));
                                post.setType(product.getString("type"));
                                post.setAddress(product.getString("address"));
                                post.setMetres(product.getInt("metres"));
                                post.setCost(product.getString("price"));
                                post.setDescription(product.getString("description"));
                                post.setRequirements(product.getString("requirements"));
                                post.setBeds(product.getInt("beds"));
                                post.setPlaces(product.getInt("places"));
                                post.setRent_type(product.getString("rent_type"));
                                post.setVisible(product.getInt("visibility")!=0);
                                post.setHost_id(product.getInt("host_id"));
                                post.setImages(product.getString("images"));
                                //adding the product to product list
                                my_posts.add(post);
                            }
                            //creating adapter object and setting it to recyclerview
                            MyPostsListAdapter adapter = new MyPostsListAdapter(MyPostsActivity.this,R.layout.my_posts_list_item, my_posts);
                            posts.setAdapter(adapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        load_posts();
    }
}