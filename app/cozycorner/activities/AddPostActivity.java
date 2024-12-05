package com.example.cozycorner.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.fragments.AddPostDescriptionFragment;
import com.example.cozycorner.fragments.AddPostPriceFragment;
import com.example.cozycorner.fragments.AddPostTypeFragment;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.R;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    Button next;
    FrameLayout frame;
    Post post;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post2);
        preferenceManager=new PreferenceManager(getApplicationContext());
        post=new Post();
        List<Fragment> list=new ArrayList<>();
        list.add(AddPostTypeFragment.newInstance(post));
        list.add(AddPostPriceFragment.newInstance(post));
        list.add(AddPostDescriptionFragment.newInstance(post));
        frame=findViewById(R.id.add_frame);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.add_frame,list.get(0));
        ft.addToBackStack(null);
        ft.commit();
        final int[] i = {0};
        next=findViewById(R.id.confirm_btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i[0]++;
                if(i[0]==3){
                    postRequest();
                } else{
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.add_frame,list.get(i[0]));
                    ft.addToBackStack(null);
                    ft.commit();
                    if(i[0]==2){
                        next.setText("Сохранить");
                    }
                }
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void postRequest(){
        int user_id=Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));
        post.setHost_id(user_id);
        post.setVisible(true);
        next.setEnabled(false);
        String url = Constants.KEY_DB_ADDRESS+"insert_post.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(AddPostActivity.this);
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
                            next.setEnabled(true);
                            if(status.equals("success")){
                                //Intent intent=new Intent(AddPostActivity.this, MyPostsActivity.class);
                                //startActivity(intent);
                                finish();
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
                params.put("type", post.getType());
                params.put("address", post.getAddress());
                params.put("cost", post.getCost());
                params.put("desc", post.getDescription());
                params.put("req", post.getRequirements());
                params.put("rent_type", post.getRent_type());
                params.put("beds", String.valueOf(post.getBeds()));
                params.put("places", String.valueOf(post.getPlaces()));
                params.put("host_id", String.valueOf(post.getHost_id()));
                params.put("vis", String.valueOf(post.isVisible()));
                params.put("metres",String.valueOf(post.getMetres()));
                params.put("images",post.getImages());
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