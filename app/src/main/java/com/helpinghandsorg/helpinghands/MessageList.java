package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.adaptor.UsersListAdaptor;
import com.helpinghandsorg.helpinghands.repositories.ChatModel;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.Set;


public class MessageList extends Fragment implements UsersListAdaptor.OnUserListClickListner{

    private RecyclerView recyclerView;
    private UsersListAdaptor usersListAdaptor;
    private ArrayList<String> chatArrayList = new ArrayList<>();
    private ArrayList<Volunteer> mUsers;
    private ChatModel chatModel = new ChatModel();
    private UsersListAdaptor.OnUserListClickListner onUserListClickListner;
    private String fuser;
    private DatabaseReference Ref;
    public MessageList() {
        // Required empty public constructor
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUserListClickListner = this;
        recyclerView = view.findViewById(R.id.recycler_view_user_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        fuser = "admin";

        Ref = FirebaseDatabase.getInstance().getReference("Chats");
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //Fetching messages from firebase
                    chatModel = dataSnapshot1.getValue(ChatModel.class);

                    //if message reciever is admin
                    if(chatModel.getSender().equals("admin") && !chatModel.getReceiver().equals("admin")){
                        //Stores user id inside arraylist if I am receiver
                        chatArrayList.add(chatModel.getReceiver());
                        chatArrayList = removeDublicateValues(chatArrayList);
                    }
                    //if message reciever is user
                    if(!chatModel.getSender().equals("admin") && chatModel.getReceiver().equals("admin")){
                        chatArrayList.add(chatModel.getSender());
                    }

                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_list, container, false);
    }

    private ArrayList<String> removeDublicateValues(ArrayList<String> chatArrayList) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(chatArrayList);
        chatArrayList.clear();
        chatArrayList.addAll(set);
        return chatArrayList;
    }

    private void readChats() {
        mUsers = new ArrayList<>();
        Ref = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member");
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    try {
                        //fetching users (members) from database
                        Volunteer volunteer = dataSnapshot1.getValue(Volunteer.class);
                        //iterates over All ids(chatarraylist) and put inside "String id"
                        for (String id : chatArrayList) {
                            if (volunteer.getId().equals(id)) {
                                if (mUsers.size() != 0) {
                                    //iterating over all users and placing them inside seperate objects
                                    for (Volunteer volunteer1 : mUsers) {
                                        //if user (uid)
                                        if (!volunteer.getId().equals(volunteer1.getId())) {
                                            mUsers.add(volunteer);
                                        }
                                    }
                                } else {
                                    mUsers.add(volunteer);
                                }
                            }
                        }
                    }catch (ConcurrentModificationException ignored){

                    }
                }
                Log.d("chaterror_msglist", chatArrayList.toString());
                usersListAdaptor = new UsersListAdaptor(getContext(), mUsers, onUserListClickListner);
                recyclerView.setAdapter(usersListAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onUserListClick(int position) {

    }

    @Override
    public void onMessageListClick(String uid) {
        Intent intent = new Intent(getContext(), Admin_chat_activity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("from", "messageList");
        startActivity(intent);
        getActivity().finish();

    }
}
