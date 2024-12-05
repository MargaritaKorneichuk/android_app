package com.example.cozycorner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.R;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button btn_login;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        preferenceManager=new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            //finish();
        }
        Button register_link=findViewById(R.id.register_link);
        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        btn_login=findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignInDetails()){
                    btn_login.setEnabled(false);
                    String url = Constants.KEY_DB_ADDRESS+"verify.php";
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

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
                                        btn_login.setEnabled(true);
                                        if(status.equals("success")){
                                            preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN,true);
                                            preferenceManager.putString(Constants.KEY_FIRST_NAME, responseJson.getString("user_fname"));
                                            preferenceManager.putString(Constants.KEY_LAST_NAME, responseJson.getString("user_lname"));
                                            preferenceManager.putString(Constants.KEY_PASSWORD,password.getText().toString());
                                            preferenceManager.putString(Constants.KEY_EMAIL,email.getText().toString());
                                            preferenceManager.putString(Constants.KEY_USER_ID,responseJson.getString("user_id"));
                                            preferenceManager.putString(Constants.KEY_IMAGE, responseJson.getString("image"));
                                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
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
                            params.put("email", email.getText().toString().trim());
                            params.put("pass", password.getText().toString().trim());
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
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
    private Boolean isValidSignInDetails(){
        if(email.getText().toString().trim().isEmpty()){
            showToast("Введите email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            showToast("Введите корректный email");
            return false;
        }else if(password.getText().toString().isEmpty()){
            showToast("Введите пароль");
            return false;
        }else {
            return true;
        }
    }
}