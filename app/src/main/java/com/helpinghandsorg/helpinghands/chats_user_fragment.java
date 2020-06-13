package com.helpinghandsorg.helpinghands;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.adaptor.MessageAdaptorUser;
import com.helpinghandsorg.helpinghands.repositories.ChatModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class chats_user_fragment extends Fragment {

    private EditText message;

    private MessageAdaptorUser messageAdaptor;
    private List<ChatModel> mChat;
    private RecyclerView recyclerView;


    public chats_user_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton sendMessageButton = view.findViewById(R.id.button_send);
        message = view.findViewById(R.id.EditText_Message);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!message.getText().toString().trim().equals("")) {
                    sendMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),"admin", message.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Please type something first", Toast.LENGTH_LONG).show();
                }
                message.setText("");
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readMessage(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    private void sendMessage(String sender, String receiver, String text) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", text);
        hashMap.put("receiver", receiver);
        dbRef.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid){
        mChat = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    ChatModel chat = dataSnapshot1.getValue(ChatModel.class);
                    if(( (chat.getReceiver().equals("admin") && chat.getSender().equals(myid))|| (chat.getSender().equals("admin") && chat.getReceiver().equals(myid)) )){
                        mChat.add(chat);
                    }
                    //Log.d("Chaterror", String.valueOf(messageAdaptor.getItemCount()));
                    messageAdaptor = new MessageAdaptorUser(getContext(),mChat);
                    recyclerView.setAdapter(messageAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chats_fragment, container, false);
    }
}
