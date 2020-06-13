package com.helpinghandsorg.helpinghands;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditUser extends Fragment {
    private String uid;
    private EditText name, email;
    private Volunteer volunteer;
    private Spinner spinner;
    private String newDesignation;
    private String designation;
    private Volunteer volunteer1 = new Volunteer();

    public EditUser() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uid = getArguments().getString("uid");
        name = view.findViewById(R.id.editTextName);
        email = view.findViewById(R.id.editTextEmail);
        spinner = view.findViewById(R.id.spinner);
        Button saveChanges = view.findViewById(R.id.buttonSaveChanges);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.designation_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        readData(new FirebaseCallBack() {
            @Override
            public void CallBack(Volunteer volunteer) {
                name.setText(volunteer.getFullName());
                email.setText(volunteer.getEmail());
                designation = volunteer.getDesignation();
                spinner.setSelection(adapter.getPosition(designation));
                volunteer1 = volunteer;
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newDesignation.equals("Member")) {
                    volunteer1.setDesignation(newDesignation);
                    DatabaseReference appTeamRef = FirebaseDatabase.getInstance().getReference("Volunteer").child(newDesignation).child(uid);
                    appTeamRef.setValue(volunteer1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(uid).child("designation").setValue(newDesignation);
                            Snackbar.make(view, "User details updated successfully!", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("EditUser", e.getMessage());
                            Snackbar.make(getView(), "Something went wrong!", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    });
                }
                NavHostFragment.findNavController(EditUser.this)
                        .navigate(R.id.action_editUser_to_allMembersList);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newDesignation = (String) parent.getItemAtPosition(position);
                Log.d("Crashfix", newDesignation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void readData(final FirebaseCallBack firebaseCallback) {
        volunteer = new Volunteer();
        DatabaseReference appTeamRef = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(uid);
        appTeamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    volunteer = dataSnapshot.getValue(Volunteer.class);
                    firebaseCallback.CallBack(volunteer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallBack {
        void CallBack(Volunteer volunteer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user, container, false);
    }
}
