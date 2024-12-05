package com.example.cozycorner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;


import com.example.cozycorner.R;
import com.example.cozycorner.model.Review;

import java.util.List;

public class ReviewsListAdapter extends ArrayAdapter<Review> {
    private LayoutInflater inflater;
    private int layout;
    private List<Review> items;
    private Context context;
    public ReviewsListAdapter(Context context, int resource, List<Review> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.context=context;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        ReviewsListAdapter.ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ReviewsListAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ReviewsListAdapter.ViewHolder) convertView.getTag();
        }
        Review review=items.get(position);
        viewHolder.user_name.setText(review.getUser_name());
        viewHolder.review_text.setText(review.getReview_text());
        viewHolder.rating.setRating(review.getRating());
        return convertView;
    }

    private class ViewHolder {
        TextView user_name,review_text;
        RatingBar rating;
        ViewHolder(View view){
            user_name=view.findViewById(R.id.review_user_name);
            review_text=view.findViewById(R.id.review_text);
            rating=view.findViewById(R.id.ratingBar);
        }
    }
}
