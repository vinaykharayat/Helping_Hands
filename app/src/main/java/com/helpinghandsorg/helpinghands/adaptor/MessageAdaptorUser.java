package com.helpinghandsorg.helpinghands.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.repositories.ChatModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdaptorUser extends RecyclerView.Adapter<MessageAdaptorUser.myViewHolder> {

    public static final int MESSAGE_TYPE_LEFT =0;
    public static final int MESSAGE_TYPE_RIGHT =1;
    private Context mContext;
    private List<ChatModel> mChat;
    private String profilePicUrl;
    FirebaseUser firebaseUser;

    public MessageAdaptorUser(Context mContext, List<ChatModel> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    public MessageAdaptorUser(Context mContext, List<ChatModel> mChat, String profilePicUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.profilePicUrl = profilePicUrl;
    }

    public MessageAdaptorUser() {
    }

    @NonNull
    @Override
    public MessageAdaptorUser.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MESSAGE_TYPE_RIGHT) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdaptorUser.myViewHolder(v);
        } else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdaptorUser.myViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdaptorUser.myViewHolder holder, int position) {
        ChatModel chats = mChat.get(position);
        holder.showMessage.setText(chats.getMessage());
        try{
            if(profilePicUrl.equals("null")){
                holder.imageViewProfilePic.setImageResource(R.drawable.avatar_male);
            }else {
                Picasso.get().load(profilePicUrl).fit().into(holder.imageViewProfilePic);
            }}catch (NullPointerException e){
            Picasso.get().load(R.drawable.avatar_male).fit().into(holder.imageViewProfilePic);
        }
        try{
            holder.time.setText(chats.getTime());
        }catch (Exception ignored){

        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        private TextView showMessage;
        private ImageView imageViewProfilePic;
        private TextView time;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            imageViewProfilePic = itemView.findViewById(R.id.profile_image);
            time = itemView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals("admin")|| mChat.get(position).equals(firebaseUser.getUid())){
            return MESSAGE_TYPE_LEFT;
        }else{
            return MESSAGE_TYPE_RIGHT;
        }
    }
}
