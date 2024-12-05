package com.example.cozycorner.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.example.cozycorner.R;
import com.example.cozycorner.activities.HomePostInfoActivity;
import com.example.cozycorner.model.Post;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HomePostListAdapter extends ArrayAdapter<Post>{
    private LayoutInflater inflater;
    private int layout;
    private List<Post> items;
    private Context context;
    private Post post;
    public HomePostListAdapter(Context context, int resource, List<Post> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context=context;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        HomePostListAdapter.ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new HomePostListAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (HomePostListAdapter.ViewHolder) convertView.getTag();
        }
        post=items.get(position);
        int sp_14=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getContext().getResources().getDisplayMetrics());
        String s = post.getType()+", "+ post.getMetres() +" м";
        SpannableString string = new SpannableString(s);
        String s2="2";
        SpannableString string2= new SpannableString(s2);
        string2.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string2.setSpan(new AbsoluteSizeSpan(sp_14), 0, s2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence ch= TextUtils.concat(string,string2);
        viewHolder.type.setText(ch);
        viewHolder.address.setText(post.getAddress());
        viewHolder.cost.setText(post.getCost());
        String guests=getResourcesByLocale().getQuantityString(R.plurals.guests, post.getPlaces(), post.getPlaces());
        String beds=getResourcesByLocale().getQuantityString(R.plurals.beds, post.getBeds(), post.getBeds());
        String str=guests+", "+beds;
        viewHolder.places.setText(str);
        viewHolder.ratingBar.setRating(post.getRating());
        viewHolder.rating.setText(String.valueOf(post.getRating()));
        viewHolder.type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, HomePostInfoActivity.class);
                intent.putExtra("post",items.get(position));
                context.startActivity(intent);
            }
        });
        viewHolder.cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,HomePostInfoActivity.class);
                intent.putExtra("post",items.get(position));
                context.startActivity(intent);
            }
        });
        viewHolder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,HomePostInfoActivity.class);
                intent.putExtra("post",items.get(position));
                context.startActivity(intent);
            }
        });
        viewHolder.places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,HomePostInfoActivity.class);
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
        TextView type,cost,address,places,rating;
        RatingBar ratingBar;
        ViewPager2 viewPager;
        ViewHolder(View view){
            type=view.findViewById(R.id.home_post_type);
            cost=view.findViewById(R.id.home_post_price);
            address=view.findViewById(R.id.home_post_address);
            places=view.findViewById(R.id.home_post_places);
            rating=view.findViewById(R.id.home_post_rating);
            ratingBar=view.findViewById(R.id.ratingBar2);
            viewPager=view.findViewById(R.id.viewPager);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Resources getResourcesByLocale() {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale("ru"));
        return context.createConfigurationContext(configuration).getResources();
    }
}
