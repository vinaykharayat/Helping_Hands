package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.ui.profile.ViewProfile;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_chat_activity extends AppCompatActivity {
    CircleImageView profilePic, profilePicChat;
    TextView name;
    ImageButton backButton;
    Volunteer volunteer = new Volunteer();
    DatabaseReference Ref;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat_activity);

        name = findViewById(R.id.admin_chat_name);
        backButton = findViewById(R.id.admin_chat_back_button);
        profilePic = findViewById(R.id.admin_chat_profile_pic);
        profilePicChat = this.getLayoutInflater().inflate(R.layout.chat_item_left,null).findViewById(R.id.profile_image);
        Bundle extras = getIntent().getExtras();
        uid = extras.getString("uid");

        //Log.d("Crashfix", "Inside onCreate" + uid);
        readData(new FirebaseCallBack() {
            @Override
            public void Callback(String userName, String imageUrl) {
                name.setText(userName);
                Picasso.get().load(imageUrl).fit().into(profilePic);
                Picasso.get().load(imageUrl).fit().into(profilePicChat);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin_chat_activity.this, Main2Activity.class));
                finish();
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                FragmentManager fm= getSupportFragmentManager();
                Fragment profileFrag = new ViewProfile();
                profileFrag.setArguments(bundle);

                fm.beginTransaction().add(android.R.id.content,
                        profileFrag).commit();
            }
        });
    }

    private void readData(final FirebaseCallBack firebaseCallBack) {
        Ref = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(uid);
        //Log.d("Crashfix", "Inside readData" + uid);
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteer = dataSnapshot.getValue(Volunteer.class);
                firebaseCallBack.Callback(volunteer.getFullName(), volunteer.getProfilePicUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private interface FirebaseCallBack {
        void Callback(String userName, String imageUrl);
    }
}
