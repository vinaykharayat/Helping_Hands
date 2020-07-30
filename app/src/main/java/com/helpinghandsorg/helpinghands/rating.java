package com.helpinghandsorg.helpinghands;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class rating extends Fragment {
    private RatingBar ratingBar;
    private String uid;
    private long totalTaskCompleted;
    private float currentRating, overallRating, newRating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uid = getArguments().getString("uid");
        ratingBar = view.findViewById(R.id.ratingBar);
        currentRating = ratingBar.getRating();
        getUsersRatingFromFirebase(new SubmissionList.FirebaseCallBackUser() {
            @Override
            public void Callback(Volunteer volunteer) {
                overallRating = volunteer.getRating();
                totalTaskCompleted = volunteer.getTaskFinished();
            }
        });
        newRating = (currentRating+overallRating)/totalTaskCompleted;
        Button submitFeedback = view.findViewById(R.id.buttonSubmitRating);
        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }

    private void getUsersRatingFromFirebase(final SubmissionList.FirebaseCallBackUser firebaseCallBackUser) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(uid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Volunteer volunteer = snapshot.getValue(Volunteer.class);
                firebaseCallBackUser.Callback(volunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
