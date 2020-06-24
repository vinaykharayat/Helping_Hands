package com.helpinghandsorg.helpinghands;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;


public class SecondFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private FirebaseDatabase db;
    private TextInputEditText taskTitle;
    private TextInputEditText taskDescription;
    private TextView TextViewDueDate;
    private String dueDateString;
    private Spinner spinner;
    private DatePickerDialog.OnDateSetListener mDateSetListner;
    private String designation;
    private AlertDialog dialog;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("tasks");
        spinner = view.findViewById(R.id.spinnner);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.designation_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        TextViewDueDate = view.findViewById(R.id.TextDueDate);
        Button buttonSelectDate = view.findViewById(R.id.buttonSelectDate);
        taskTitle = view.findViewById(R.id.TextInputTaskTitle);
        taskDescription = view.findViewById(R.id.TextInputTaskDescription);

        try {
            if (!(getActivity().getIntent().getExtras().getString("taskID") == null)) {
                Button button = view.findViewById(R.id.buttonSubmitTask);
                button.setVisibility(View.GONE);
                editTask();
                button = view.findViewById(R.id.buttonUpdateTask);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
                        dialog = builder.show();
                        updateTask();
                    }
                });
            }
        }catch (NullPointerException ignored){

        }


        buttonSelectDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += month;
                dueDateString = dayOfMonth + "-" + month + "-" + year;
                TextViewDueDate.setText(dueDateString);
            }
        };

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                designation = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.findViewById(R.id.buttonSubmitTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTask();
            }
        });
    }

    private void editTask() {
        String taskID = getActivity().getIntent().getExtras().getString("taskID");
        getTaskData(taskID, new FirebaseCallBack() {
            @Override
            public void Callback(final TaskModel taskModel) {
                taskTitle.setText(taskModel.getTaskTitle());
                taskDescription.setText(taskModel.getTaskDescription());
                TextViewDueDate.setText(taskModel.getDueDate());
                for (int i = 0; i < spinner.getCount(); i++) {
                    if (spinner.getItemAtPosition(i) == taskModel.getDestination()) {
                        spinner.setSelection(i);
                    }
                }
            }
        });

    }

    private void updateTask() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("dueDate", TextViewDueDate.getText().toString());
        map.put("taskDescription", taskDescription.getText().toString());
        map.put("destination", spinner.getSelectedItem().toString());
        map.put("taskTitle", taskTitle.getText().toString());
        dbRef.child(getActivity().getIntent().getExtras().getString("taskID")).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getContext(), Main2Activity.class));
                getActivity().finishAffinity();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private interface FirebaseCallBack {
        void Callback(TaskModel taskModel);
    }

    private void getTaskData(String taskID, final FirebaseCallBack firebaseCallBack) {
        dbRef.child(taskID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                Log.d("edittask", dataSnapshot.getValue().toString());
                firebaseCallBack.Callback(taskModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openDatePickerDialog() {
        //Sets current date to dialog
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Creates date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Material_Light_Dialog,
                mDateSetListner, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        datePickerDialog.show();

    }


    private void submitTask() {
        String title = taskTitle.getText().toString();
        String des = taskDescription.getText().toString();
        String uid = mAuth.getCurrentUser().getUid();


        TaskModel newModel = new TaskModel(title, dueDateString, des);
        newModel.setDestination(designation);
        dbRef.push().setValue(newModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getContext(), Main2Activity.class));
                getActivity().finishAffinity();
            }
        });

    }
}
