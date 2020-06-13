package com.helpinghandsorg.helpinghands.adaptor;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.TaskModel;

import java.util.ArrayList;

public class MyAdaptorUser extends RecyclerView.Adapter<MyAdaptorUser.myViewHolder> {
    private Context context;
    private ArrayList<TaskModel> taskLists;
    private OnTaskClickListner mTaskListner;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;


    public MyAdaptorUser(Context c, ArrayList<TaskModel> t, OnTaskClickListner onTaskClickListner) {
        context = c;
        taskLists = t;
        this.mTaskListner = onTaskClickListner;
    }

    @NonNull
    @Override
    public MyAdaptorUser.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        builder = new AlertDialog.Builder(parent.getContext());
        builder.setTitle("Please Wait").setView(R.layout.my_progress_view).setCancelable(false);
        dialog = builder.create();
        return new MyAdaptorUser.myViewHolder(LayoutInflater.from(context).inflate(R.layout.task_preview, parent, false), mTaskListner);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdaptorUser.myViewHolder holder, final int position) {
        //Set title and description to task preview textviews
        holder.title.setText(taskLists.get(position).getTaskTitle());
        holder.dueDate.setText(taskLists.get(position).getDueDate());
        holder.description.setText(taskLists.get(position).getTaskDescription());
        //Sets the path of database to taskAwatingConfirmation/task_title/UserEmail
    }





    @Override
    public int getItemCount() {
        return taskLists.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, dueDate, description;
        OnTaskClickListner onTaskClickListner;

        public myViewHolder(@NonNull View itemView, OnTaskClickListner onTaskClickListner) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.taskDueDate);
            description = itemView.findViewById(R.id.taskDescription);
            this.onTaskClickListner = onTaskClickListner;

            ConstraintLayout taskBar = itemView.findViewById(R.id.linearLayoutTaskBar);

            itemView.setOnClickListener(this);
            //hides delete task button
            taskBar.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            onTaskClickListner.onTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskClickListner {
        void onTaskClick(int position);
    }
}
