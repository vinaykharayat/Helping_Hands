package com.helpinghandsorg.helpinghands;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.richeditor.RichEditor;

public class JobsInfo extends AppCompatActivity {
    private String jobID;
    private TextView post,eligibility,agelimit,qualifiction,fees,lastdate,totalpost, process, benefits, salary, company;
    private RichEditor editor;
    private JobDetails jobDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_info);
        jobID = getIntent().getExtras().getString("jobID");
        post = findViewById(R.id.textViewPostName);
        eligibility = findViewById(R.id.textViewEligibility);
        agelimit = findViewById(R.id.textViewAgeLimit);
        qualifiction = findViewById(R.id.textViewQualification);
        fees = findViewById(R.id.textViewFees);
        lastdate = findViewById(R.id.textViewLastDate);
        totalpost = findViewById(R.id.textViewTotalPost);
        //process = findViewById(R.id.textViewProcess);
        benefits = findViewById(R.id.textViewBenefits);
        salary = findViewById(R.id.textViewSalary);
        company = findViewById(R.id.textViewCompany);
        editor = findViewById(R.id.textViewProcess);
        editor.setEditorHeight(400);
        //editor.setPadding(10,10,10,10);

        getJobData(new FirebaseCallback() {
            @Override
            public void Callback(JobDetails jobDetails) {
                post.setText(jobDetails.getPost());
                eligibility.setText(jobDetails.getEligibility());
                agelimit.setText(jobDetails.getAgelimit());
                qualifiction.setText(jobDetails.getQualifiction());
                fees.setText(jobDetails.getFees());
                lastdate.setText(jobDetails.getLastdate());
                totalpost.setText(jobDetails.getTotalpost());
                //process.setText(jobDetails.getProcess());
                benefits.setText(jobDetails.getBenefits());
                salary.setText(jobDetails.getSalary());
                company.setText(jobDetails.getCompany());
                editor.setHtml(jobDetails.getProcess());
                editor.setInputEnabled(false);
            }
        });



    }

    private interface FirebaseCallback {
        void Callback(JobDetails jobDetails);
    }

    private void getJobData(final FirebaseCallback firebaseCallback) {
        jobDetails = new JobDetails();
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobRef.child(jobID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    jobDetails = dataSnapshot.getValue(JobDetails.class);
                    firebaseCallback.Callback(jobDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
