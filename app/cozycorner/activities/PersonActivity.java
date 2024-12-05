package com.example.cozycorner.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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
import com.example.cozycorner.adapters.ListAdapter;
import com.example.cozycorner.utilities.Constants;
import com.example.cozycorner.utilities.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PersonActivity extends AppCompatActivity {
    ListView profile_list;
    ImageButton chats;
    ImageButton home;
    TextView username;
    ImageView profile_image;
    PreferenceManager preferenceManager;
    String encode_image;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        preferenceManager=new PreferenceManager(getApplicationContext());
        textView=findViewById(R.id.textChooseImage);
        profile_list=findViewById(R.id.profile_list);
        String[] list=getResources().getStringArray(R.array.profile_list);
        ListAdapter adapter=new ListAdapter(this, R.layout.profile_list_item, Arrays.asList(list.clone()));
        profile_list.setAdapter(adapter);
        chats=findViewById(R.id.chats);
        home=findViewById(R.id.lent);
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonActivity.this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
            }
        });
        username=findViewById(R.id.username);
        String username_s=preferenceManager.getString(Constants.KEY_FIRST_NAME)+" "+preferenceManager.getString(Constants.KEY_LAST_NAME);
        username.setText(username_s);
        profile_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv=view.findViewById(R.id.item_name);
                switch (tv.getText().toString()){
                    case "Выйти из профиля":
                        AlertDialog.Builder builder = new AlertDialog.Builder(PersonActivity.this);
                        builder.setTitle("Выход из профиля");
                        builder.setMessage("Выйти из профиля?");
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                preferenceManager.clear();
                                Intent intent=new Intent(PersonActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                //finish();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                        final AlertDialog alertDialog = builder.show();
                        alertDialog.setCanceledOnTouchOutside(true);
                        break;
                    case "Удалить профиль":
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(PersonActivity.this);
                        builder1.setTitle("Удаление профиля");
                        builder1.setMessage("Удалить профиль?");
                        builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                preferenceManager.clear();
                                deleteProfile();
                            }
                        });
                        builder1.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                        final AlertDialog alertDialog1 = builder1.show();
                        alertDialog1.setCanceledOnTouchOutside(true);
                        break;
                    case "Личные данные":
                        Intent in=new Intent(PersonActivity.this, PersonalDataActivity.class);
                        startActivity(in);
                        break;
                    case "Мои объявления":
                        Intent inten=new Intent(PersonActivity.this, MyPostsActivity.class);
                        startActivity(inten);
                        break;
                    case "Мои бронирования":
                        Intent intent=new Intent(PersonActivity.this, MyBookingsActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
        profile_image=findViewById(R.id.profile_image);
        String image=preferenceManager.getString(Constants.KEY_IMAGE);
        if(!image.equals("")){
            Bitmap bitmap=getUserImage(image);
            profile_image.setImageBitmap(bitmap);
            textView.setVisibility(View.GONE);
        }
        profile_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
                return false;
            }
        });
    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    private void deleteProfile(){
        String url = Constants.KEY_DB_ADDRESS+"delete_profile.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(PersonActivity.this);

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
                                Intent intent=new Intent(PersonActivity.this,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                params.put("user_id", preferenceManager.getString(Constants.KEY_USER_ID));
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
    private String encodeImage(Bitmap bitmap){
        int previewWidth=150;
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri=result.getData().getData();
                        try{
                            InputStream inputStream=getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
                            profile_image.setImageBitmap(bitmap);
                            encode_image=encodeImage(bitmap);
                            preferenceManager.putString(Constants.KEY_IMAGE,encode_image);
                            updateImageOnServer();
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void updateImageOnServer(){
        String url = Constants.KEY_DB_ADDRESS+"update_image.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(PersonActivity.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String status = responseJson.getString("status");
                            String message = responseJson.getString("message");
                            textView.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(error.getLocalizedMessage()!=null){
                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", encode_image);
                params.put("user_id",preferenceManager.getString(Constants.KEY_USER_ID));
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