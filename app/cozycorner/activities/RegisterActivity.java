package com.example.cozycorner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.cozycorner.MyTextWatcher;
import com.example.cozycorner.R;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity{
    EditText email;
    EditText firstName;
    EditText lastName;
    EditText password;
    Button register;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        preferenceManager=new PreferenceManager(getApplicationContext());
        register=findViewById(R.id.register);
        email=findViewById(R.id.email_r);
        email.setTag("email");
        email.addTextChangedListener(new MyTextWatcher(email,register));
        firstName=findViewById(R.id.firstName);
        firstName.setTag("fname");
        firstName.addTextChangedListener(new MyTextWatcher(firstName,register));
        lastName=findViewById(R.id.lastName);
        lastName.setTag("lname");
        lastName.addTextChangedListener(new MyTextWatcher(lastName,register));
        password=findViewById(R.id.password_r);
        password.setTag("pass");
        password.addTextChangedListener(new MyTextWatcher(password,register));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmail(email)){
                    email.setError("Адрес не корректен");
                    return;
                }
                singUp();
            }
        });
    }
    private void singUp(){
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        if (!emailText.isEmpty() && !passwordText.isEmpty() && !firstNameText.isEmpty()&&!lastNameText.isEmpty()) {
            register.setEnabled(false);
            String url = Constants.KEY_DB_ADDRESS+"insert.php";
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
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
                                register.setEnabled(true);
                                if(status.equals("success")){
                                    preferenceManager.putString(Constants.KEY_FIRST_NAME,firstName.getText().toString());
                                    preferenceManager.putString(Constants.KEY_LAST_NAME,lastName.getText().toString());
                                    preferenceManager.putString(Constants.KEY_EMAIL,email.getText().toString());
                                    preferenceManager.putString(Constants.KEY_PASSWORD,password.getText().toString());
                                    preferenceManager.putString(Constants.KEY_IMAGE,"");
                                    preferenceManager.putString(Constants.KEY_USER_ID,responseJson.getString("user_id"));
                                    Intent intent=new Intent(RegisterActivity.this, HomeActivity.class);
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
                    params.put("fname", firstName.getText().toString().trim());
                    params.put("lname", lastName.getText().toString().trim());
                    params.put("email", email.getText().toString().trim());
                    params.put("pass", password.getText().toString().trim());
                    params.put("image","");
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

        } else {
            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_LONG).show();
        }
    }
    boolean isEmail(EditText text){
        CharSequence email=text.getText().toString();
        return (!TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}