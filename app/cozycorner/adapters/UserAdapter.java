package com.example.cozycorner.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cozycorner.R;
import com.example.cozycorner.listeners.UserListener;
import com.example.cozycorner.model.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.zip.Inflater;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private final List<User> users;
    private final LayoutInflater inflater;
    private final UserListener userListener;

    public UserAdapter(Context context,List<User> users,UserListener userListener) {
        this.users = users;
        this.inflater=LayoutInflater.from(context);
        this.userListener=userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_container_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name,email;
        RoundedImageView image;

        public UserViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textName);
            email=itemView.findViewById(R.id.textEmail);
            image=itemView.findViewById(R.id.imageProfile);
        }
        void setUserData(User user){
            this.email.setText(user.getEmail());
            String str= user.getFirstName()+" "+user.getLastName();
            this.name.setText(str);
            this.image.setImageBitmap(getUserImage(user.getImage()));
            itemView.setOnClickListener(v->userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
