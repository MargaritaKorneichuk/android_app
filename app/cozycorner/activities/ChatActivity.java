package com.example.cozycorner.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.R;
import com.example.cozycorner.adapters.RecentConversationsAdapter;
import com.example.cozycorner.listeners.ConversationListener;
import com.example.cozycorner.model.ChatMessage;
import com.example.cozycorner.model.User;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements ConversationListener {
    private PreferenceManager preferenceManager;
    private RoundedImageView image;
    private TextView userName;
    private FloatingActionButton fab;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter recentConversationsAdapter;
    private RecyclerView conversationsList;
    private ProgressBar progressBar;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        preferenceManager=new PreferenceManager(getApplicationContext());
        image=findViewById(R.id.imageProfile);
        userName=findViewById(R.id.textChatUserName);
        conversationsList=findViewById(R.id.conversationsRecycleView);
        fab=findViewById(R.id.fabNewChat);
        conversations=new ArrayList<>();
        recentConversationsAdapter=new RecentConversationsAdapter(conversations,getApplicationContext(),this);
        conversationsList.setAdapter(recentConversationsAdapter);
        progressBar=findViewById(R.id.progressBar);
        loadConversations();
        loadUserDetails();
        ImageButton home=findViewById(R.id.lent);
        ImageButton profile=findViewById(R.id.personal_info);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, PersonActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UsersActivity.class));
            }
        });
        handler.removeCallbacks(task);
        handler.postDelayed(task, 7000);
    }
    private final Runnable task = new Runnable() {
        public void run() {
            loadConversations();
            handler.postDelayed(this, 5000);
        }
    };
    private void loadUserDetails(){
        String str=preferenceManager.getString(Constants.KEY_FIRST_NAME)+" "+preferenceManager.getString(Constants.KEY_LAST_NAME);
        userName.setText(str);
        if(!preferenceManager.getString(Constants.KEY_IMAGE).equals("")) {
            byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            image.setImageBitmap(bitmap);
        }
    }
    private void loadConversations(){
        String url=Constants.KEY_DB_ADDRESS+"get_user_chats.php?user_id="+preferenceManager.getString(Constants.KEY_USER_ID);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            conversations.clear();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                ChatMessage message=new ChatMessage();
                                message.setMessage(product.getString(Constants.KEY_MESSAGE));
                                message.setConversionImage(product.getString(Constants.KEY_IMAGE));
                                String name=product.getString(Constants.KEY_FIRST_NAME)+" "+product.getString(Constants.KEY_LAST_NAME);
                                message.setConversionName(name);
                                message.setConversionId(product.getString("u_id"));
                                conversations.add(message);
                            }
                            recentConversationsAdapter.notifyDataSetChanged();
                            conversationsList.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

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
        handler.postDelayed(task, 0);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(task);
        super.onPause();
    }

    @Override
    public void onConversationClicked(User user) {
        Intent intent=new Intent(getApplicationContext(),PersonalChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }

    @Override
    public void onLongClickListener(String receiverId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Удаление беседы");
        builder.setMessage("Удалить беседу?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteChat(receiverId);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog1 = builder.show();
        alertDialog1.setCanceledOnTouchOutside(true);
    }
    private void deleteChat(String receiverId){
        String url = Constants.KEY_DB_ADDRESS+"delete_chat.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

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
                            if(status.equals("success")){
                                ChatActivity.this.onResume();
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
                params.put("user_id", preferenceManager.getString(Constants.KEY_USER_ID));
                params.put("receiver_id",receiverId);
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