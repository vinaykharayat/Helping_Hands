package com.helpinghandsorg.helpinghands;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.helpinghandsorg.helpinghands.adaptor.UsersListAdaptor;
import com.helpinghandsorg.helpinghands.repositories.TaskConfirmationSender;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubmissionList extends Fragment implements UsersListAdaptor.OnUserListClickListner{
    private RecyclerView recyclerView;
    private UsersListAdaptor usersListAdaptor;
    private Volunteer volunteer;
    private TaskConfirmationSender taskConfirmationSender;
    private ArrayList<Volunteer> volunteerArrayList;
    private UsersListAdaptor.OnUserListClickListner onUserListClickListner;
    private String taskID, taskTitle, userId;
    private DatabaseReference reference;

    public SubmissionList() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUserListClickListner = this;
        taskID = getActivity().getIntent().getExtras().getString("taskID");
        recyclerView = view.findViewById(R.id.recycler_view_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getTaskFromFirebase(new FirebaseCallBackTask() {

            @Override
            public void Callback(TaskModel taskModel) {
                //Returns data from /tasks/taskID
                taskTitle = taskModel.getTaskTitle();
                taskCompletedUserList(new FirebaseCallBack() {
                    @Override
                    public void Callback(TaskConfirmationSender taskConfirmationSender) {
                        // Returns data from taskConfirmAwating/taskTitle
                        userId = taskConfirmationSender.getId();
                        Log.d("Tasksproblem",userId);
                        getUser(new FirebaseCallBackUser() {
                            @Override
                            public void Callback(Volunteer volunteer) {
                                volunteerArrayList.add(volunteer);
                                usersListAdaptor = new UsersListAdaptor(getContext(), volunteerArrayList, onUserListClickListner);
                                recyclerView.setAdapter(usersListAdaptor);
                            }
                        });
                    }
                });
            }
        });
    }



    private void getUser(final FirebaseCallBackUser firebaseCallBackUser) {
        volunteer = new Volunteer();
        volunteerArrayList = new ArrayList<>();
        usersListAdaptor = new UsersListAdaptor();
        reference =FirebaseDatabase.getInstance().getReference("Volunteer").child("Member").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteer = dataSnapshot.getValue(Volunteer.class);

                firebaseCallBackUser.Callback(volunteer);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void taskCompletedUserList(final FirebaseCallBack firebaseCallBack) {
        reference = FirebaseDatabase.getInstance().getReference("taskAwatingConfirmation").child(taskTitle);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    taskConfirmationSender = dataSnapshot1.getValue(TaskConfirmationSender.class);
                    firebaseCallBack.Callback(taskConfirmationSender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTaskFromFirebase(final FirebaseCallBackTask firebaseCallBack) {
        reference = FirebaseDatabase.getInstance().getReference("tasks").child(taskID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                firebaseCallBack.Callback(taskModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submission_list, container, false);
    }

    @Override
    public void onUserListClick(final int position) {
        String[] chooseUserOptionList = getActivity().getResources().getStringArray(R.array.task_submitted_option);
        new AlertDialog.Builder(getContext()).setTitle("Choose Action")
                .setItems(chooseUserOptionList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                viewWork(position);
                                break;
                            case 1:
                                rateUser(position);
                                break;
                            case 2:
                                confirmTaskFinshed(position);
                                break;
                        }
                    }
                }).show();
    }

    @Override
    public void onMessageListClick(String uid) {

    }

    private void confirmTaskFinshed(int position) {
        String uid =volunteerArrayList.get(position).getId();
        reference = FirebaseDatabase.getInstance().getReference("Volunteer").child("Member");
        reference.child(uid).child("taskFinished").setValue(+1);

    }

    private void rateUser(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", volunteerArrayList.get(position).getId());
        NavHostFragment.findNavController(SubmissionList.this)
                .navigate(R.id.action_submissionList_to_rating, bundle);
    }

    private void viewWork(int position) {
    }


    public interface FirebaseCallBackTask {
        void Callback(TaskModel taskModel);
    }

    public interface FirebaseCallBack {
        void Callback(TaskConfirmationSender taskConfirmationSender);
    }

    public interface FirebaseCallBackUser{
        void Callback(Volunteer volunteer);
    }

}
