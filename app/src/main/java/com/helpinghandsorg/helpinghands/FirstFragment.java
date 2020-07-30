package com.helpinghandsorg.helpinghands;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helpinghandsorg.helpinghands.adaptor.MyAdaptor;

import java.util.ArrayList;
import java.util.Collections;

public class FirstFragment extends Fragment implements MyAdaptor.OnTaskClickListner {
    private RecyclerView recyclerView;
    private TextView textview;
    private ArrayList<TaskModel> list;
    private MyAdaptor adaptor;
    private TaskModel taskModel = new TaskModel();
    private ProgressBar progressBar;
    private MyAdaptor.OnTaskClickListner onTaskClickListner;

    public FirstFragment() {
    }

    FirstFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewTaskLists);
        progressBar = view.findViewById(R.id.progressBar);
        textview = view.findViewById(R.id.textView7);
        onTaskClickListner= this;
        list = new ArrayList<>();
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTask.class));
                getActivity().finishAffinity();
            }
        });

        fetchTasks(new FirebaseCallBack() {
            @Override
            public void CallBack(final TaskModel taskModel) {
                list.add(taskModel);
                //Reversing list to show latest task at first
                Collections.reverse(list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adaptor = new MyAdaptor(getContext(), list, onTaskClickListner);
                //Refreshes the adaptor
                recyclerView.getRecycledViewPool().clear();
                recyclerView.setAdapter(adaptor);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchTasks(final FirebaseCallBack firebaseCallback){
        list.clear();
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("tasks");
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        taskModel = dataSnapshot1.getValue(TaskModel.class);
                        taskModel.setTaskID(dataSnapshot1.getKey());
                        firebaseCallback.CallBack(taskModel);
                }
                if(list.isEmpty()){
                    textview.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onTaskClick(int position) {
        Bundle bundle = new Bundle();
        Log.d("Crashfix", list.get(position).toString());
        bundle.putString("taskID", list.get(position).getTaskID());
        try {
            String taskID = list.get(position).getTaskID();
            Intent intent = new Intent(getContext(), TaskDetails.class).putExtra("taskID", taskID);
            startActivity(intent);
        }catch (IndexOutOfBoundsException e){
            String taskID = list.get(position+1).getTaskID();
            Intent intent = new Intent(getContext(), TaskDetails.class).putExtra("taskID", taskID);
            startActivity(intent);
        }

    }

    interface FirebaseCallBack{
        void CallBack(TaskModel taskModel);
    }
}
