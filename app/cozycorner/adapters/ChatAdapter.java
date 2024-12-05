package com.example.cozycorner.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cozycorner.R;
import com.example.cozycorner.model.ChatMessage;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessageList;
    private final String senderId;
    private final Bitmap receiverProfileImage;
    private final LayoutInflater inflater;

    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVED=2;

    public ChatAdapter(Context context,List<ChatMessage> chatMessageList, String senderId, Bitmap receiverProfileImage) {
        this.chatMessageList = chatMessageList;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
        this.inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType ==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(inflater.inflate(R.layout.item_container_sent_message,parent,false));
        }else{
            return new ReceivedMessageViewHolder(inflater.inflate(R.layout.item_container_received_message,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessageList.get(position));
        }else{
            ((ReceivedMessageViewHolder)holder).setData(chatMessageList.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).getSenderId().equals(senderId)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage,textDateTime;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage=itemView.findViewById(R.id.textMessage);
            textDateTime=itemView.findViewById(R.id.textDateTime);
        }
        void setData(ChatMessage chatMessage){
            textMessage.setText(chatMessage.getMessage());
            textDateTime.setText(chatMessage.getDateTime());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView image;
        TextView textMessage, textDateTime;
        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.imageProfile);
            textMessage=itemView.findViewById(R.id.textMessage);
            textDateTime=itemView.findViewById(R.id.textDateTime);
        }
        void setData(ChatMessage chatMessage,Bitmap bitmap){
            textMessage.setText(chatMessage.getMessage());
            textDateTime.setText(chatMessage.getDateTime());
            image.setImageBitmap(bitmap);
        }
    }
}
