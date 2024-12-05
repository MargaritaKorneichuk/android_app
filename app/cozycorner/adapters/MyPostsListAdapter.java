package com.example.cozycorner.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.R;
import com.example.cozycorner.activities.MyPostInfoActivity;
import com.example.cozycorner.activities.MyPostsActivity;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPostsListAdapter extends ArrayAdapter<Post>{
    private LayoutInflater inflater;
    private int layout;
    private List<Post> items;
    private Context context;
    MyPostsListAdapter.ViewHolder viewHolder;
    public MyPostsListAdapter(Context context, int resource,List<Post> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context=context;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        //MyPostsListAdapter.ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new MyPostsListAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (MyPostsListAdapter.ViewHolder) convertView.getTag();
        }
        Post post=items.get(position);
        //int i=post.getType().length()+String.valueOf(post.getMetres()).length()+4;
        int sp_16=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getContext().getResources().getDisplayMetrics());
        String s = post.getType()+", "+ post.getMetres() +" м";
        SpannableString string = new SpannableString(s);
        String s2="2";
        SpannableString string2= new SpannableString(s2);
        string2.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string2.setSpan(new AbsoluteSizeSpan(sp_16), 0, s2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence ch= TextUtils.concat(string,string2);
        viewHolder.type.setText(ch);
        if(post.isVisible()){
            viewHolder.changeVisibility.setText("Скрыть объявление из ленты");
        }else{
            viewHolder.changeVisibility.setText("Показывать объявление в ленте");
        }
        viewHolder.cost.setText(post.getCost());
        viewHolder.address.setText(post.getAddress());
        viewHolder.changeVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.isVisible()){
                    updateVisibility(0,post);
                }else{
                    updateVisibility(1,post);
                }
                Intent intent=new Intent(context, MyPostsActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
                //((MyPostsActivity)context).load_posts();
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Удаление объявления");
                builder.setMessage("Удалить объявление?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delete(post);
                        Intent intent=new Intent(context,MyPostsActivity.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        //((MyPostsActivity)context).load_posts();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                final AlertDialog alertDialog = builder.show();
                alertDialog.setCanceledOnTouchOutside(true);
            }
        });
        viewHolder.type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MyPostInfoActivity.class);
                intent.putExtra("post",items.get(position));
                context.startActivity(intent);
            }
        });
        viewHolder.cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MyPostInfoActivity.class);
                intent.putExtra("post",items.get(position));
                context.startActivity(intent);
            }
        });
        viewHolder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MyPostInfoActivity.class);
                intent.putExtra("post",items.get(position));
                context.startActivity(intent);
            }
        });
        String[] images_str=post.getImages().split(";");
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(context, Arrays.asList(images_str.clone()));
        viewHolder.viewPager.setAdapter(viewPagerAdapter);
        return convertView;
    }

    private class ViewHolder {
        TextView type,cost,address;
        ImageButton delete;
        Button changeVisibility;
        ViewPager2 viewPager;
        ViewHolder(View view){
            type=view.findViewById(R.id.post_type);
            cost=view.findViewById(R.id.post_cost);
            address=view.findViewById(R.id.post_address);
            delete=view.findViewById(R.id.post_delete_btn);
            changeVisibility=view.findViewById(R.id.post_visibility_btn);
            viewPager=view.findViewById(R.id.viewPager);
        }
    }
    private void delete(Post post_del){
        viewHolder.delete.setEnabled(false);
        String url = Constants.KEY_DB_ADDRESS+"delete_post.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MyPostsListAdapter.this.context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String status = responseJson.getString("status");
                            String message = responseJson.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            viewHolder.delete.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(post_del.getId()));
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
    private void updateVisibility(int visibility,Post post_v){
        viewHolder.changeVisibility.setEnabled(false);
        String url = Constants.KEY_DB_ADDRESS+"update_visibility_post.php";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MyPostsListAdapter.this.context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String status = responseJson.getString("status");
                            String message = responseJson.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            viewHolder.changeVisibility.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(post_v.getId()));
                params.put("vis",String.valueOf(visibility));
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
