package com.helpinghandsorg.helpinghands.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.Volunteer;

public class UserprofileRepositories {

    private Volunteer newVolunteer = new Volunteer();
    MutableLiveData<Volunteer> dataFetched = new MutableLiveData<>();

    public MutableLiveData<Volunteer> getUserprofile(){

        setUserProfile(new FirebaseCallBack() {
            @Override
            public void Callback(Volunteer data) {
                Log.d("Profile", " Insdie repo getUserProfile Called"+ newVolunteer.getFullName());
                dataFetched.setValue(newVolunteer);
                Log.d("Profile", " Insdie repo getUserProfile Called"+ dataFetched.getValue());
            }
        });
        Log.d("Profile", " Insdie repo getUserProfile Called"+ dataFetched.getValue());
        return dataFetched;
    }

    //Fetches data from database
    public void setUserProfile(final FirebaseCallBack firebaseCallBack){
        FirebaseAuth mAuth;
        DatabaseReference dbRef;
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        String uid;
        DatabaseReference volunteerRef;
        volunteerRef = dbRef.child("Volunteer");
        uid = mAuth.getCurrentUser().getUid();

        volunteerRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newVolunteer.setFullName((String) dataSnapshot.child("fullName").getValue());
                newVolunteer.setAdmin((String) dataSnapshot.child("admin").getValue());
                newVolunteer.setEmail((String) dataSnapshot.child("email").getValue());
                newVolunteer.setGender((String) dataSnapshot.child("gender").getValue());
                newVolunteer.setDesignation((String) dataSnapshot.child("designation").getValue());
                newVolunteer.setJoiningDate((String) dataSnapshot.child("joiningDate").getValue());
                newVolunteer.setTaskAlloted((Long) dataSnapshot.child("taskAlloted").getValue());
                newVolunteer.setTaskFinished((Long) dataSnapshot.child("taskFinished").getValue());
                newVolunteer.setRating((Long) dataSnapshot.child("rating").getValue());
                firebaseCallBack.Callback(newVolunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoginActivity", "dberror: " + databaseError.getMessage());
            }
        });
    }

    private interface FirebaseCallBack {
        void Callback(Volunteer data);
    }
}
