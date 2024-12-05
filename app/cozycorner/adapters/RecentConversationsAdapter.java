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
import com.example.cozycorner.listeners.ConversationListener;
import com.example.cozycorner.model.ChatMessage;
import com.example.cozycorner.model.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{
    private final List<ChatMessage> chatMessageList;
    private final LayoutInflater inflater;
    private final ConversationListener conversationListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessageList, Context context, ConversationListener conversationListener) {
        this.chatMessageList = chatMessageList;
        this.inflater = LayoutInflater.from(context);
        this.conversationListener=conversationListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(inflater.inflate(R.layout.item_container_recent_conversion,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessageList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{
        TextView textName, textRecentMessage;
        RoundedImageView imageView;

        public ConversionViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecentMessage=itemView.findViewById(R.id.textRecentMessage);
            textName=itemView.findViewById(R.id.textName);
            imageView=itemView.findViewById(R.id.imageProfile);
        }

        void setData(ChatMessage message){
            textName.setText(message.getConversionName());
            textRecentMessage.setText(message.getMessage());
            imageView.setImageBitmap(getConversationImage(message.getConversionImage()));
            itemView.setOnClickListener(v->{
                User user=new User();
                user.setId(message.getConversionId());
                user.setImage(message.getConversionImage());
                String[] mas=message.getConversionName().split(" ");
                user.setFirstName(mas[0]);
                user.setLastName(mas[1]);
                conversationListener.onConversationClicked(user);
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    conversationListener.onLongClickListener(message.getConversionId());
                    return false;
                }
            });
        }
    }
    private Bitmap getConversationImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
