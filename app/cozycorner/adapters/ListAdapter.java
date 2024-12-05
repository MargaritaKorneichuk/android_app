package com.example.cozycorner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cozycorner.R;

import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;
    private int layout;
    private List<String> items;
    public ListAdapter(Context context, int resource, List<String> items) {
        super(context, resource,items);
        this.items = items;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String str=items.get(position);
        viewHolder.nameView.setText(str);
        return convertView;
    }
    private class ViewHolder {
        final ImageView imageView;
        final TextView nameView;
        ViewHolder(View view){
            imageView = view.findViewById(R.id.imageView);
            nameView = view.findViewById(R.id.item_name);
        }
    }
}
