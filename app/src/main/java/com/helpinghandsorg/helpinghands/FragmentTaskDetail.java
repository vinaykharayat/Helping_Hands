package com.helpinghandsorg.helpinghands;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.repositories.TaskConfirmationSender;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTaskDetail extends Fragment {

    private TaskModel taskModel;
    private DatabaseReference taskRef;
    private TextView taskTitle, taskDescription;
    private String taskID, designation;
    private Button buttonSubmissionList, buttonAddSubmission;
    private AlertDialog dialog1;
    private AlertDialog.Builder builder;
    private String taskTitleData;
    private TaskConfirmationSender taskConfirmationSender = new TaskConfirmationSender();

    public FragmentTaskDetail() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskID = getActivity().getIntent().getExtras().getString("taskID");
        Log.d("taskerror_FTaskDetails", taskID);
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
        dialog1 = builder.create();
        taskTitle = view.findViewById(R.id.textViewTaskTitleDetail);
        taskDescription = view.findViewById(R.id.textViewTaskTitleDescription);
        buttonSubmissionList = view.findViewById(R.id.buttonViewSubmission);
        buttonAddSubmission = view.findViewById(R.id.buttonAddSubmission);
        taskRef = FirebaseDatabase.getInstance().getReference().child("tasks").child(taskID);
        readData(new FirebaseCallbackUser() {
            @Override
            public void Callback(Volunteer volunteer) {
                if (volunteer.isAdmin().equals("true")) {
                    buttonSubmissionList.setVisibility(View.VISIBLE);
                } else {
                    Log.d("404", "I am here");
                    buttonAddSubmission.setVisibility(View.VISIBLE);
                }
            }
        });
        fetchTaskDetails(new FireBaseCallBack() {
            @Override
            public void CallBack(TaskModel task) {
                taskTitleData = task.getTaskTitle();
                taskTitle.setText(taskTitleData);
                taskDescription.setText(task.getTaskDescription());
            }
        });

        buttonAddSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                email = removeSpecialCharacter(email);
                final DatabaseReference taskConfirmationRef = FirebaseDatabase.getInstance().getReference()
                        .child("taskAwatingConfirmation")
                        .child(taskTitleData)
                        .child(email);
                taskConfirmationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String buttonStatus = (String) dataSnapshot.child("buttonStatus").getValue();
                        if (buttonStatus != null) {
                            Log.d("taskerror", buttonStatus);
                            if (buttonStatus.equals("true")) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Submission Already exists")
                                        .setMessage("Would you like to submit again?")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog1 = builder.show();
                                                sendConfirmationToAdmin(new FirebaseCallBackTask() {
                                                    @Override
                                                    public void Callback(TaskConfirmationSender taskConfirmationSender) {
                                                        taskConfirmationRef.setValue(taskConfirmationSender).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog1.dismiss();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        } else {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("No submission exists")
                                    .setMessage("Would you like to submit?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog1 = builder.show();
                                            sendConfirmationToAdmin(new FirebaseCallBackTask() {
                                                @Override
                                                public void Callback(TaskConfirmationSender taskConfirmationSender) {
                                                    taskConfirmationRef.setValue(taskConfirmationSender).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            dialog1.dismiss();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        buttonSubmissionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("taskID", taskID);
                NavHostFragment.findNavController(FragmentTaskDetail.this)
                        .navigate(R.id.action_fragmentTaskDetail_to_submissionList, bundle);
            }
        });
    }

    private void readData(final FirebaseCallbackUser firebaseCallback) {
        DatabaseReference dbRed = FirebaseDatabase.getInstance().getReference("Volunteer")
                .child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbRed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Volunteer volunteer = dataSnapshot.getValue(Volunteer.class);
                firebaseCallback.Callback(volunteer);
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
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    //Fetching data from /tasks/taskID from database
    private void fetchTaskDetails(final FireBaseCallBack fireBaseCallBack) {
        taskModel = new TaskModel();
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskModel.setTaskTitle((String) dataSnapshot.child("taskTitle").getValue());
                taskModel.setTaskDescription((String) dataSnapshot.child("taskDescription").getValue());
                taskModel.setDueDate((String) dataSnapshot.child("dueDate").getValue());
                taskConfirmationSender.setTaskDueDate((String) dataSnapshot.child("dueDate").getValue());
                fireBaseCallBack.CallBack(taskModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String removeSpecialCharacter(String email) {
        StringBuffer sbf = new StringBuffer(email);
        email = String.valueOf(sbf.reverse());
        int length = email.length();
        email = email.substring(4, length);
        StringBuffer stringBuffer = new StringBuffer(email);
        email = String.valueOf(stringBuffer.reverse());
        return email.replace("@", "_");
    }

    private void sendConfirmationToAdmin(final FirebaseCallBackTask firebaseCallBack) {
        DatabaseReference volunteerRef = FirebaseDatabase.getInstance().getReference()
                .child("Volunteer").child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Fetching details of users (full name, email) from database and setting their value to taskConfirmation Object
        volunteerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("fullName").getValue().toString();
                String userEmail = dataSnapshot.child("email").getValue().toString();
                String userId = dataSnapshot.getKey();

                //TODO: Fetch UID of user and set it to taskConfirmation OBject
                taskConfirmationSender.setUserEmail(userEmail);
                taskConfirmationSender.setUserName(userName);
                taskConfirmationSender.setId(userId);
                taskConfirmationSender.setButtonStatus("true");

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String submissionDate = day + "/" + month + "/" + year;
                taskConfirmationSender.setSubmissionDate(submissionDate);

                firebaseCallBack.Callback(taskConfirmationSender);
                /**/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallBackTask {
        void Callback(TaskConfirmationSender taskConfirmationSender);
    }

    private interface FirebaseCallbackUser {
        void Callback(Volunteer volunteer);
    }

    private interface FireBaseCallBack {
        void CallBack(TaskModel task);
    }
}
