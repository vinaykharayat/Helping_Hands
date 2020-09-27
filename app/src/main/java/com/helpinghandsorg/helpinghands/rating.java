package com.helpinghandsorg.helpinghands;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.repositories.ChatModel;

public class rating extends Fragment {
    private RatingBar ratingBar;
    private String uid;
    private float currentRating, newRating;
    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText feedback = view.findViewById(R.id.editTextFeedback);
        uid = getArguments().getString("uid");
        ratingBar = view.findViewById(R.id.ratingBar);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
        Button submitFeedback = view.findViewById(R.id.buttonSubmitRating);
        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = builder.show();
                currentRating = ratingBar.getRating();
                getUsersRatingFromFirebase(new SubmissionList.FirebaseCallBackUser() {
                    @Override
                    public void Callback(Volunteer volunteer) {
                        if(!(volunteer.getTaskFinished() == 0)) {
                            newRating = (currentRating + volunteer.getRating()) / volunteer.getTaskFinished();
                            volunteer.setRating((long) newRating);
                            volunteer.setTaskFinished(volunteer.getTaskFinished()+1);
                            Log.d("rating_insideif", String.valueOf(volunteer.getRating()));
                        }else{
                            volunteer.setRating((long) currentRating);
                            volunteer.setTaskFinished(volunteer.getTaskFinished()+1);
                            Log.d("rating_else", String.valueOf(volunteer.getRating()));
                        }
                        updateRatingInDatabase(volunteer);
                    }
                });
                if(!feedback.getText().toString().isEmpty())
                    sendFeedbackToChat(feedback.getText().toString());
                dialog.dismiss();
                NavHostFragment.findNavController(rating.this).navigate(R.id.action_rating_to_submissionList);
            }
        });
    }

    private void sendFeedbackToChat(String feedback) {
        ChatModel chatModel = new ChatModel();
        chatModel.setMessage("Feedback for the task:\n"+ feedback);
        chatModel.setReciever(uid);
        chatModel.setSender("admin");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.push().setValue(chatModel);

    }

    private void updateRatingInDatabase(Volunteer volunteer) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(uid);
        dbRef.setValue(volunteer);
        try {
            dbRef = FirebaseDatabase.getInstance().getReference("Volunteer").child(volunteer.getDesignation()).child(uid);
            dbRef.setValue(volunteer);
        }catch (Exception ignored){

        }

    }

    private void getUsersRatingFromFirebase(final SubmissionList.FirebaseCallBackUser firebaseCallBackUser) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(uid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Volunteer volunteer = snapshot.getValue(Volunteer.class);
                Log.d("rating_getRating", volunteer.getId());
                firebaseCallBackUser.Callback(volunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
