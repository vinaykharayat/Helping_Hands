package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.adaptor.JobsAdaptor;

import java.util.ArrayList;

public class JobsFragment extends Fragment implements JobsAdaptor.OnTaskClickListner{

    private RecyclerView recyclerView;
    private ArrayList<JobDetails> list;
    private ProgressBar progressBar;
    private JobsAdaptor.OnTaskClickListner onTaskClickListner;
    private JobsAdaptor jobsAdaptor;
    private JobDetails jobDetails = new JobDetails();

    public JobsFragment() {
    }

    JobsFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewTaskLists);
        progressBar = view.findViewById(R.id.progressBar);
        onTaskClickListner= this;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<JobDetails>();
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddJobs.class));
                getActivity().finishAffinity();
                /*FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = new JobsAddFragment();
                ft.add(android.R.id.content,
                        fragment, "Chatsfragment").commit();*/
            }
        });

        fetchJobs(new FirebaseCallback() {
            @Override
            public void Callback(final JobDetails jobDetails) {
                list.add(jobDetails);
                if(!list.isEmpty()) {
                    jobsAdaptor = new JobsAdaptor(getContext(), list, onTaskClickListner);
                    recyclerView.setAdapter(jobsAdaptor);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchJobs(final FirebaseCallback firebaseCallback){
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("jobs");
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if(dataSnapshot.getChildrenCount() == 0){
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    JobDetails jobDetails= dataSnapshot1.getValue(JobDetails.class);
                    jobDetails.setJobId(dataSnapshot1.getKey());
                    firebaseCallback.Callback(jobDetails);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private interface FirebaseCallback{
        void Callback(JobDetails jobDetails);
    }


    @Override
    public void onTaskClick(int position) {
        String jobID = list.get(position).getJobId();
        Intent intent = new Intent(getContext(), JobsInfo.class).putExtra("jobID", jobID);
        startActivity(intent);
    }
}
