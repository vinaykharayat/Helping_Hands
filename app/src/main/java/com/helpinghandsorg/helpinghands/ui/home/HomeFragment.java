package com.helpinghandsorg.helpinghands.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.JobDetails;
import com.helpinghandsorg.helpinghands.JobsInfo;
import com.helpinghandsorg.helpinghands.LoginActivity;
import com.helpinghandsorg.helpinghands.TaskDetails;
import com.helpinghandsorg.helpinghands.Volunteer;
import com.helpinghandsorg.helpinghands.adaptor.JobsAdaptor;
import com.helpinghandsorg.helpinghands.adaptor.JobsAdaptorUser;
import com.helpinghandsorg.helpinghands.adaptor.MyAdaptorUser;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.TaskModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MyAdaptorUser.OnTaskClickListner, JobsAdaptorUser.OnTaskClickListner {

    private HomeViewModel homeViewModel;
    private boolean stateIntern;
    private RecyclerView recyclerView;
    private ArrayList<TaskModel> list;
    private ArrayList<JobDetails> list1;
    private MyAdaptorUser adaptor;
    private JobsAdaptorUser jobsAdaptor;
    private TaskModel taskModel;
    private  AlertDialog dialog;
    private MyAdaptorUser.OnTaskClickListner onTaskClickListner;
    private JobsAdaptorUser.OnTaskClickListner onTaskClickListner1;
    private String designation;
    private JobDetails jobDetails;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //checking if current user is logged in
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        onTaskClickListner= this;
        onTaskClickListner1 = this;

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        ImageView mNavAvatar = headerView.findViewById(R.id.imageViewProfilePicture);

        mNavAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigateUp();
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_profile);
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //If current user is null go to login activity
        if (currentUser == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            try {
                getActivity().finishAffinity();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        readUserDetails(new FirebaseCallBack() {
            @Override
            public void callback(String designation) {
                HomeFragment.this.designation = designation;
                if(!designation.equals("Member")){
                    internUI(designation);
                }else{
                    generalUI();
                }
            }
        });
        //Creating progress bar
    }

    private void generalUI() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
        dialog = builder.show();
        stateIntern = false;
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("jobs");
        recyclerView = getView().findViewById(R.id.recyclerViewTaskListsUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list1 = new ArrayList<>();
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1.clear();
                //Getting tasks from database and setting Task Details to textviews.
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    jobDetails = new JobDetails();
                    jobDetails = dataSnapshot1.getValue(JobDetails.class);
                    jobDetails.setJobId(dataSnapshot1.getKey());
                    list1.add(jobDetails);
                }

                if(list1.isEmpty()){
                    TextView textview = getView().findViewById(R.id.textViewNoJobs);
                    textview.setText("No Jobs Available");
                    textview.setVisibility(View.VISIBLE);
                }
                jobsAdaptor = new JobsAdaptorUser(getContext(), list1, onTaskClickListner1);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(jobsAdaptor);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void internUI(final String designation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
        dialog = builder.show();
        stateIntern = true;
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("tasks");
        recyclerView = getView().findViewById(R.id.recyclerViewTaskListsUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<TaskModel>();

        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                //Getting tasks from database and setting Task Details to textviews.
                taskModel = new TaskModel();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    taskModel = dataSnapshot1.getValue(TaskModel.class);
                    taskModel.setTaskID(dataSnapshot.getKey());
                }
                if(designation.equals(taskModel.getDestination())) {
                    list.add(taskModel);
                }
                if(list.isEmpty()){
                    TextView textview = getView().findViewById(R.id.textViewNoJobs);
                    textview.setText("No tasks available");
                    textview.setVisibility(View.VISIBLE);
                }
                adaptor = new MyAdaptorUser(getContext(), list, onTaskClickListner);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adaptor);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUserDetails(final FirebaseCallBack firebaseCallBack) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseCallBack.callback(dataSnapshot.child("designation").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private interface FirebaseCallBack{
        void callback(String designation);
    }

    @Override
    public void onTaskClick(int position) {
        Bundle bundle = new Bundle();
        if(stateIntern){
            Log.d("Crashfix", list.get(position).toString());
            bundle.putString("taskID", list.get(position).getTaskID());
            try {
                String taskID = list.get(position).getTaskID();
                Intent intent = new Intent(getContext(), TaskDetails.class).putExtra("taskID", taskID);
                startActivity(intent);
            } catch (IndexOutOfBoundsException e) {
                String taskID = list.get(position + 1).getTaskID();
                Intent intent = new Intent(getContext(), TaskDetails.class).putExtra("taskID", taskID);
                startActivity(intent);
            }
        }
        else{
            Log.d("Crashfix", list1.get(position).toString());
            bundle.putString("taskID", list1.get(position).getJobId());
            try {
                String taskID = list1.get(position).getJobId();
                Intent intent = new Intent(getContext(), JobsInfo.class).putExtra("jobID", taskID);
                startActivity(intent);
            } catch (IndexOutOfBoundsException e1) {
                String taskID = list1.get(position + 1).getJobId();
                Intent intent = new Intent(getContext(), JobsInfo.class).putExtra("jobID", taskID);
                startActivity(intent);
            }
        }
    }
}
