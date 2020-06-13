package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobsAddFragment extends Fragment {
    private EditText post,eligibility,ageLimit,qualification,fees ,lastDate,totalPost, process, benefits, salary, company;
    private Button submitButton;
    JobDetails jobDetails;

    public JobsAddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        post = view.findViewById(R.id.editTextPost);
        eligibility = view.findViewById(R.id.editTextEligibilty);
        ageLimit = view.findViewById(R.id.editTextAgeLimit);
        qualification = view.findViewById(R.id.editTextQualification);
        fees = view.findViewById(R.id.editTextFees);
        lastDate = view.findViewById(R.id.editTextLastDate);
        totalPost = view.findViewById(R.id.editTextTotalpost);
        submitButton= view.findViewById(R.id.buttonSubmit);
        process = view.findViewById(R.id.editTextProcess);
        benefits = view.findViewById(R.id.editTextBenefits);
        salary = view.findViewById(R.id.editTextSalary);
        company = view.findViewById(R.id.editTextCompany);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendJobToDatabase();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jobs_add, container, false);
    }

    private void sendJobToDatabase() {
        jobDetails= new JobDetails(
                post.getText().toString(),
                eligibility.getText().toString(),
                ageLimit.getText().toString(),
                qualification.getText().toString(),
                fees.getText().toString(),
                lastDate.getText().toString(),
                totalPost.getText().toString(),
                process.getText().toString(),
                benefits.getText().toString(),
                salary.getText().toString(),
                company.getText().toString()
                );

        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs");
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
