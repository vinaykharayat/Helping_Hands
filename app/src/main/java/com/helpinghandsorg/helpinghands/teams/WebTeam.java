package com.helpinghandsorg.helpinghands.teams;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.Admin_chat_activity;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.Volunteer;
import com.helpinghandsorg.helpinghands.adaptor.UsersListAdaptor;

import java.util.ArrayList;

public class WebTeam extends Fragment implements UsersListAdaptor.OnUserListClickListner{


    private RecyclerView recyclerView;
    private UsersListAdaptor usersListAdaptor;
    private ArrayList<Volunteer> volunteerArrayList= new ArrayList<>();
    private UsersListAdaptor.OnUserListClickListner onUserListClickListner;
    Volunteer volunteer=new Volunteer();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUserListClickListner = this;
        recyclerView = view.findViewById(R.id.recycler_view_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DatabaseReference appTeamRef = FirebaseDatabase.getInstance().getReference("Volunteer").child("WebTeam");
        appTeamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteerArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    volunteer = dataSnapshot1.getValue(Volunteer.class);
                    volunteerArrayList.add(volunteer);
                }
                usersListAdaptor = new UsersListAdaptor(getContext(), volunteerArrayList, onUserListClickListner);
                recyclerView.setAdapter(usersListAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_list, container, false);
    }
    private void deleteUserProfile(int position) {
        DatabaseReference fuser = FirebaseDatabase.getInstance().getReference()
                .child("Volunteer")
                .child(volunteerArrayList.get(position).getDesignation()).child(volunteerArrayList.get(position).getId());
        fuser.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(getContext(), "User Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editUserProfile(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", volunteerArrayList.get(position).getId());
        NavHostFragment.findNavController(WebTeam.this)
                .navigate(R.id.action_allMembersList_to_editUser, bundle);
    }

    private void openUserProfile(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", volunteerArrayList.get(position).getId());
        NavHostFragment.findNavController(WebTeam.this)
                .navigate(R.id.action_allMembersList_to_viewProfile, bundle);
    }

    private void sendMessage(int position) {
        Intent intent = new Intent(getContext(), Admin_chat_activity.class);
        intent.putExtra("uid",volunteerArrayList.get(position).getId());
        startActivity(intent);

    }

    @Override
    public void onUserListClick(final int position) {
        String[] chooseUserOptionList = getActivity().getResources().getStringArray(R.array.user_profile_select_option);
        new AlertDialog.Builder(getContext()).setTitle("Choose Action")
                .setItems(chooseUserOptionList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openUserProfile(position);
                                break;
                            case 1:
                                sendMessage(position);
                                break;
                            case 2:
                                editUserProfile(position);
                                break;
                            case 3:
                                deleteUserProfile(position);
                                break;
                        }
                    }
                }).show();

    }

    @Override
    public void onMessageListClick(String uid) {

    }
}
