package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import jp.wasabeef.richeditor.RichEditor;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobsAddFragment extends Fragment {
    private EditText post, eligibility, ageLimit, qualification, fees, lastDate, totalPost, benefits, salary, company;
    private RichEditor editor;
    private JobDetails jobDetails;
    private DatabaseReference jobRef;
    private String jobID;
    private AlertDialog dialog;

    public JobsAddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            jobID = getActivity().getIntent().getExtras().getString("jobID");
        }catch (NullPointerException ignored){

        }
        jobRef = FirebaseDatabase.getInstance().getReference("jobs");
        post = view.findViewById(R.id.editTextPost);
        eligibility = view.findViewById(R.id.editTextEligibilty);
        ageLimit = view.findViewById(R.id.editTextAgeLimit);
        qualification = view.findViewById(R.id.editTextQualification);
        fees = view.findViewById(R.id.editTextFees);
        lastDate = view.findViewById(R.id.editTextLastDate);
        totalPost = view.findViewById(R.id.editTextTotalpost);
        Button submitButton = view.findViewById(R.id.buttonSubmit);
        Button buttonUpdateJob = view.findViewById(R.id.buttonUpdateJob);
        benefits = view.findViewById(R.id.editTextBenefits);
        salary = view.findViewById(R.id.editTextSalary);
        company = view.findViewById(R.id.editTextCompany);

        //Implementing Rich Text Editor
        editor = view.findViewById(R.id.editTextProcess);
        editor.setPlaceholder("Process to apply");
        editor.setEditorHeight(200);
        editor.setPadding(10, 10, 10, 10);
        view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setBullets();
            }
        });

        view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setNumbers();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendJobToDatabase();
            }
        });
        if (!(jobID == null)) {
            buttonUpdateJob.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
            getJobDetails(new FirebaseCallback() {
                @Override
                public void callback(JobDetails jobDetails) {
                    post.setText(jobDetails.getPost());
                    eligibility.setText(jobDetails.getEligibility());
                    ageLimit.setText(jobDetails.getAgelimit());
                    qualification.setText(jobDetails.getQualifiction());
                    fees.setText(jobDetails.getFees());
                    lastDate.setText(jobDetails.getLastdate());
                    totalPost.setText(jobDetails.getTotalpost());
                    editor.setHtml(jobDetails.getProcess());
                    benefits.setText(jobDetails.getBenefits());
                    salary.setText(jobDetails.getSalary());
                    company.setText(jobDetails.getCompany());
                }
            });

            buttonUpdateJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
                    dialog = builder.show();
                    updateJob();
                }
            });
        }
    }

    private void updateJob() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("agelimit", ageLimit.getText().toString());
        map.put("benefits", benefits.getText().toString());
        map.put("company", company.getText().toString());
        map.put("eligibility", eligibility.getText().toString());
        map.put("fees", fees.getText().toString());
        map.put("lastdate", lastDate.getText().toString());
        map.put("post", post.getText().toString());
        map.put("process", editor.getHtml());
        map.put("qualifiction", qualification.getText().toString());
        map.put("salary", salary.getText().toString());
        map.put("totalpost", totalPost.getText().toString());
        jobRef.child(jobID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                startActivity(new Intent(getContext(), Main2Activity.class));
                getActivity().finishAffinity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getJobDetails(final FirebaseCallback firebaseCallback) {
        jobRef.child(jobID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                JobDetails jobDetails = dataSnapshot.getValue(JobDetails.class);
                firebaseCallback.callback(jobDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallback {
        void callback(JobDetails jobDetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jobs_add, container, false);
    }

    private void sendJobToDatabase() {
        jobDetails = new JobDetails(
                post.getText().toString(),
                eligibility.getText().toString(),
                ageLimit.getText().toString(),
                qualification.getText().toString(),
                fees.getText().toString(),
                lastDate.getText().toString(),
                totalPost.getText().toString(),
                editor.getHtml(),
                benefits.getText().toString(),
                salary.getText().toString(),
                company.getText().toString()
        );

        jobRef.push().setValue(jobDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(getContext(), Main2Activity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
    }
}
