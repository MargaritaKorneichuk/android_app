package com.example.cozycorner.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cozycorner.adapters.ViewPagerAdapter;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.R;

import java.util.Arrays;

public class MyPostInfoActivity extends AppCompatActivity {
    private Post post;
    private Button edit,see_reviews,see_bookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_info);
        Intent intent=getIntent();
        post=(Post)intent.getSerializableExtra("post");
        TextView type,address,square,level,cost,beds,places,rent_type,description,requirements;
        ViewPager2 viewPager2=findViewById(R.id.viewPager);
        String[] images_str=post.getImages().split(";");
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getApplicationContext(), Arrays.asList(images_str.clone()));
        viewPager2.setAdapter(viewPagerAdapter);
        type=findViewById(R.id.my_post_info_type);
        address=findViewById(R.id.my_post_info_address);
        square=findViewById(R.id.my_post_info_square);
        level=findViewById(R.id.my_post_info_level);
        cost=findViewById(R.id.my_post_info_price);
        beds=findViewById(R.id.my_post_info_beds);
        places=findViewById(R.id.my_post_info_places);
        rent_type=findViewById(R.id.my_post_info_rent_type);
        description=findViewById(R.id.my_post_info_description);
        requirements=findViewById(R.id.my_post_info_requirements);
        edit=findViewById(R.id.my_post_info_edit);
        see_reviews=findViewById(R.id.my_post_info_see_rewiews);
        see_bookings=findViewById(R.id.my_post_info_see_bookings);
        String str=post.getType();
        String[] mas=str.split(", ");
        type.setText(mas[0]);
        address.setText(post.getAddress());
        square.setText(String.valueOf(post.getMetres()));
        level.setText(mas[1]);
        cost.setText(post.getCost());
        beds.setText(String.valueOf(post.getBeds()));
        places.setText(String.valueOf(post.getPlaces()));
        rent_type.setText(post.getRent_type());
        description.setText(post.getDescription());
        requirements.setText(post.getRequirements());
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Intent intent= new Intent(MyPostInfoActivity.this, MyPostsActivity.class);
                //startActivity(intent);
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyPostInfoActivity.this, EditPostActivity.class);
                intent.putExtra("post",post);
                startActivity(intent);
            }
        });
        see_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyPostInfoActivity.this, SeeReviewsActivity.class);
                intent.putExtra("post_id",post.getId());
                startActivity(intent);
            }
        });
        see_bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyPostInfoActivity.this, MyPostBookingsActivity.class);
                intent.putExtra("post_id",post.getId());
                startActivity(intent);
            }
        });
    }
}