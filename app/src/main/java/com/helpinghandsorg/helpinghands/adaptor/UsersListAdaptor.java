package com.helpinghandsorg.helpinghands.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.Volunteer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersListAdaptor extends RecyclerView.Adapter<UsersListAdaptor.myViewHolder> {
    private Context mContext;
    private ArrayList<Volunteer> mUserList = new ArrayList<>();
    private OnUserListClickListner onUserListClickListner;
    private int position;

    public UsersListAdaptor(Context mContext, ArrayList<Volunteer> mUserList, OnUserListClickListner onUserListClickListner) {
        this.mContext = mContext;
        this.mUserList = mUserList;
        this.onUserListClickListner = onUserListClickListner;
    }


    public UsersListAdaptor(Context mContext, ArrayList<Volunteer> mUserList) {
        this.mContext = mContext;
        this.mUserList = mUserList;

    }

    public UsersListAdaptor() {
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.user_list_preview, parent, false);

        return new UsersListAdaptor.myViewHolder(v, onUserListClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.mUsername.setText(mUserList.get(position).getFullName());
        holder.mUserDesignation.setText(mUserList.get(position).getDesignation());
        try{
        if(mUserList.get(position).getProfilePicUrl().equals("null")) {
            holder.mProfileImage.setImageResource(R.drawable.avatar_male);
        }else{
            Picasso.get().load(mUserList.get(position).getProfilePicUrl()).fit().into(holder.mProfileImage);
        }
        }catch (NullPointerException e){
            holder.mProfileImage.setImageResource(R.drawable.avatar_male);
        }


    }
    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mUsername, mUserDesignation;
        ImageView mProfileImage;
        OnUserListClickListner onUserListClickListner;

        public myViewHolder(@NonNull View itemView, OnUserListClickListner onUserListClickListner) {
            super(itemView);

            mUsername = itemView.findViewById(R.id.textViewName);
            mProfileImage = itemView.findViewById(R.id.profile_image);
            mUserDesignation = itemView.findViewById(R.id.textViewDesignation);
            this.onUserListClickListner = onUserListClickListner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserListClickListner.onMessageListClick(mUserList.get(getAdapterPosition()).getId());
            onUserListClickListner.onUserListClick(getAdapterPosition());
        }
    }

    public interface OnUserListClickListner{
        //void onUserListClick(int position);
        void onMessageListClick(String uid);

        void onUserListClick(int adapterPosition);
    }

}
