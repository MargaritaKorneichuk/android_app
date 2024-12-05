package com.example.cozycorner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import com.example.cozycorner.R;
import com.example.cozycorner.adapters.ChatAdapter;
import com.example.cozycorner.adapters.MyPostsListAdapter;
import com.example.cozycorner.model.ChatMessage;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.model.User;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PersonalChatActivity extends AppCompatActivity {
    private User receiverUser;
    TextView name;
    AppCompatImageView back;
    private List<ChatMessage> chatMessageList;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    RecyclerView recyclerView;
    EditText messageText;
    FrameLayout layout;
    ProgressBar progressBar;
    private final Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);
        name=findViewById(R.id.textName);
        back=findViewById(R.id.imageBack);
        messageText=findViewById(R.id.messageText);
        layout=findViewById(R.id.layoutSend);
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.chatRecyclerView);
        loadReceiverDetails();
        init();
        back.setOnClickListener(v->onBackPressed());
        layout.setOnClickListener(v->sendMessage());
        loadChat();
        handler.removeCallbacks(task);
        handler.postDelayed(task, 7000);
    }
    private void init(){
        preferenceManager=new PreferenceManager(getApplicationContext());
        chatMessageList=new ArrayList<>();
        chatAdapter=new ChatAdapter(getApplicationContext(),
                chatMessageList,
                preferenceManager.getString(Constants.KEY_USER_ID),
                getBitmapFromEncodedString(receiverUser.getImage()));
        recyclerView.setAdapter(chatAdapter);
    }
    private final Runnable task = new Runnable() {
        public void run() {
            loadChat();
            handler.postDelayed(this, 5000);
        }
    };
    private void sendMessage(){
        String url = Constants.KEY_DB_ADDRESS+"insert_chat_message.php";
        Date date=new Date();
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
                            if(status.equals("error")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            ChatMessage mess=new ChatMessage();
                            mess.setMessage(messageText.getText().toString());
                            mess.setSenderId(preferenceManager.getString(Constants.KEY_USER_ID));
                            mess.setDateObject(date);
                            mess.setReceiverId(receiverUser.getId());
                            mess.setDateTime(getReadableDateTime(date));
                            chatMessageList.add(mess);
                            chatAdapter.notifyItemRangeInserted(chatMessageList.size(),chatMessageList.size());
                            recyclerView.smoothScrollToPosition(chatMessageList.size()-1);
                            messageText.setText(null);
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
                params.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
                params.put(Constants.KEY_RECEIVER_ID,receiverUser.getId());
                params.put(Constants.KEY_MESSAGE,messageText.getText().toString());
                params.put(Constants.KEY_TIMESTAMP,String.valueOf(date.getTime()));
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
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    private void loadReceiverDetails(){
        receiverUser=(User)getIntent().getSerializableExtra(Constants.KEY_USER);
        String str=receiverUser.getFirstName()+" "+receiverUser.getLastName();
        name.setText(str);
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    private void loadChat(){
        String url=Constants.KEY_DB_ADDRESS+"get_chat.php?user_id="+preferenceManager.getString(Constants.KEY_USER_ID)
                +"&receiver_id="+receiverUser.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            chatMessageList.clear();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                ChatMessage message=new ChatMessage();
                                message.setSenderId(product.getString(Constants.KEY_SENDER_ID));
                                message.setReceiverId(product.getString(Constants.KEY_RECEIVER_ID));
                                message.setMessage(product.getString(Constants.KEY_MESSAGE));
                                message.setDateTime(getReadableDateTime(new Date(product.getLong(Constants.KEY_TIMESTAMP))));
                                message.setDateObject(new Date(product.getLong(Constants.KEY_TIMESTAMP)));
                                chatMessageList.add(message);
                            }
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(chatMessageList.size()-1);
                            recyclerView.setVisibility(View.VISIBLE);
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
}