package com.helpinghandsorg.helpinghands.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.Volunteer;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends Fragment {

    private DatabaseReference mDatabaseRef;
    private Volunteer volunteer = new Volunteer();
    private String uid;
    private CircleImageView mProfilePicture;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uid =getArguments().getString("uid");
        final TextView mTextViewEmail = view.findViewById(R.id.textViewProfileEmail);
        final TextView mTextViewName = view.findViewById(R.id.textViewProfileFullName);
        final TextView mTextViewGender = view.findViewById(R.id.textViewProfileGender);
        final TextView mTextViewTaskAlloted = view.findViewById(R.id.textViewProfileTask);
        final TextView mTextViewTaskFinsihed = view.findViewById(R.id.textViewProfileTaskFinished);
        final TextView mTextViewDesignation = view.findViewById(R.id.textViewDesignation);
        final TextView mTextViewJoiningDate = view.findViewById(R.id.textViewProfileJoiningDate);
        mProfilePicture = view.findViewById(R.id.imageViewAvatar);
        final Button logout = view.findViewById(R.id.buttonProfileLogout);
        logout.setVisibility(View.GONE);
        final ProgressBar mProgressBar = view.findViewById(R.id.progressBar3);
        Button backButoon = view.findViewById(R.id.button_Back);
        //backButoon.setVisibility(View.GONE);
        backButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ViewProfile.this).commit();
                }catch (Exception e){
                    NavHostFragment.findNavController(ViewProfile.this).navigate(R.id.action_viewProfile_to_allMembersList);
                }
            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Volunteer").child("Member")
                .child(uid);

        setUserProfile(new FirebaseCallBack() {
            @Override
            public void Callback(Volunteer data) {
                mTextViewEmail.setText(data.getEmail());
                mTextViewName.setText(data.getFullName());
                mTextViewGender.setText(data.getGender());
                mTextViewTaskAlloted.setText(String.valueOf(data.getTaskAlloted()));
                mTextViewTaskFinsihed.setText(String.valueOf(data.getTaskFinished()));
                mTextViewDesignation.setText(String.valueOf(data.getDesignation()));
                mTextViewJoiningDate.setText(String.valueOf(data.getJoiningDate()));
                if(data.getProfilePicUrl()!="null"){
                    Picasso.get().load(data.getProfilePicUrl())
                            .fit()
                            .into(mProfilePicture);
                }else {
                    Picasso.get().load(R.drawable.avatar_male)
                        .fit()
                        .into(mProfilePicture);

                }
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    //Fetches data from database
    public void setUserProfile(final FirebaseCallBack firebaseCallBack){
        DatabaseReference dbRef;
        dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference volunteerRef;
        volunteerRef = dbRef.child("Volunteer").child("Member");

        volunteerRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteer.setFullName((String) dataSnapshot.child("fullName").getValue());
                volunteer.setAdmin((String) dataSnapshot.child("admin").getValue());
                volunteer.setEmail((String) dataSnapshot.child("email").getValue());
                volunteer.setGender((String) dataSnapshot.child("gender").getValue());
                volunteer.setDesignation((String) dataSnapshot.child("designation").getValue());
                volunteer.setJoiningDate((String) dataSnapshot.child("joiningDate").getValue());
                volunteer.setTaskAlloted((Long) dataSnapshot.child("taskAlloted").getValue());
                volunteer.setTaskFinished((Long) dataSnapshot.child("taskFinished").getValue());
                volunteer.setRating((Long) dataSnapshot.child("rating").getValue());
                volunteer.setProfilePicUrl((String) dataSnapshot.child("profilePicUrl").getValue());
                firebaseCallBack.Callback(volunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoginActivity", "dberror: " + databaseError.getMessage());
            }
        });
    }

    public interface FirebaseCallBack {
        void Callback(Volunteer data);
    }
}
