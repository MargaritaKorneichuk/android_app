package com.example.cozycorner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.R;
import com.example.cozycorner.adapters.UserAdapter;
import com.example.cozycorner.listeners.UserListener;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.model.User;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    private TextView errorMessage;
    private List<User> users;
    private RecyclerView users_list;
    private AppCompatImageView imageBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        progressBar=findViewById(R.id.progressBar);
        users_list=findViewById(R.id.usersRecyclerView);
        preferenceManager=new PreferenceManager(getApplicationContext());
        errorMessage=findViewById(R.id.errorMessage);
        imageBack=findViewById(R.id.imageBack);
        users=new ArrayList<>();
        imageBack.setOnClickListener(v->onBackPressed());
        getUsers();
    }
    private void getUsers(){
        loading(true);
        String url=Constants.KEY_DB_ADDRESS+"get_users.php?user_id=0";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            loading(false);
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            String user_id=preferenceManager.getString(Constants.KEY_USER_ID);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                if(user_id.equals(product.getString("id"))){
                                    continue;
                                }
                                User user=new User();
                                user.setEmail(product.getString("email"));
                                user.setFirstName(product.getString("firstName"));
                                user.setLastName(product.getString("lastName"));
                                user.setImage(product.getString("image"));
                                user.setId(product.getString("id"));
                                users.add(user);
                            }
                            if(users.size()>0){
                                UserAdapter userAdapter=new UserAdapter(getApplicationContext(),users,UsersActivity.this);
                                users_list.setAdapter(userAdapter);
                                users_list.setVisibility(View.VISIBLE);
                            }else{
                                showErrorMessage();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void showErrorMessage(){
        errorMessage.setText(String.format("%s","Нет доступных пользователей"));
        errorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent= new Intent(getApplicationContext(),PersonalChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}