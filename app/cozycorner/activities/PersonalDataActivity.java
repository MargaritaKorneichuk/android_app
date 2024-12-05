package com.example.cozycorner.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class PersonalDataActivity extends AppCompatActivity {
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText currentPassword;
    EditText newPassword;
    Button saveChanges;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        preferenceManager=new PreferenceManager(getApplicationContext());
        saveChanges=findViewById(R.id.buttonSave);
        firstName=findViewById(R.id.firstNameData);
        firstName.setTag("fname");
        firstName.addTextChangedListener(new MyTextWatcher(firstName,saveChanges));
        lastName=findViewById(R.id.lastNameData);
        lastName.setTag("lname");
        lastName.addTextChangedListener(new MyTextWatcher(lastName,saveChanges));
        email=findViewById(R.id.emailData);
        email.setTag("email");
        email.addTextChangedListener(new MyTextWatcher(email,saveChanges));
        currentPassword=findViewById(R.id.currentPassword);
        currentPassword.setTag("password");
        currentPassword.addTextChangedListener(new MyTextWatcher(currentPassword,saveChanges));
        newPassword=findViewById(R.id.newPassword);
        firstName.setText(preferenceManager.getString(Constants.KEY_FIRST_NAME));
        lastName.setText(preferenceManager.getString(Constants.KEY_LAST_NAME));
        email.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPassword.getText().toString().equals("")){
                    currentPassword.setError("Поле не должно быть пустым");
                    return;
                }
                String emailText = email.getText().toString().trim();
                String passwordText = currentPassword.getText().toString().trim();
                String new_pass=newPassword.getText().toString().trim();
                String firstNameText=firstName.getText().toString().trim();
                String lastNameText=lastName.getText().toString().trim();
                if(!passwordText.equals(preferenceManager.getString(Constants.KEY_PASSWORD))){
                    currentPassword.setError("Неверный пароль");
                    return;
                }
                saveChanges.setEnabled(false);
                String url = Constants.KEY_DB_ADDRESS+"update.php";
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(PersonalDataActivity.this);

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
                                    saveChanges.setEnabled(true);
                                    if(status.equals("success")){
                                        preferenceManager.putString(Constants.KEY_LAST_NAME,lastNameText);
                                        preferenceManager.putString(Constants.KEY_FIRST_NAME,firstNameText);
                                        if(new_pass.equals("")){
                                            preferenceManager.putString(Constants.KEY_PASSWORD,passwordText);
                                        }else{
                                            preferenceManager.putString(Constants.KEY_PASSWORD,new_pass);
                                        }
                                        preferenceManager.putString(Constants.KEY_EMAIL,emailText);
                                        currentPassword.setText("");
                                        newPassword.setText("");
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
                        params.put("email", emailText);
                        params.put("new_pass", new_pass);
                        params.put("user_id",preferenceManager.getString(Constants.KEY_USER_ID));
                        params.put("fname",firstNameText);
                        params.put("lname",lastNameText);
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
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Intent intent=new Intent(PersonalDataActivity.this, PersonActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }
}