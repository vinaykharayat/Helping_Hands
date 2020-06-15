package com.helpinghandsorg.helpinghands.adaptor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helpinghandsorg.helpinghands.AddTask;
import com.helpinghandsorg.helpinghands.R;
import com.helpinghandsorg.helpinghands.TaskModel;

import java.util.ArrayList;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.myViewHolder> {

    private Context context;
    private ArrayList<TaskModel> taskLists;
    private String taskid;
    private OnTaskClickListner mTaskListner;

    public MyAdaptor(Context c, ArrayList<TaskModel> t, OnTaskClickListner onTaskClickListner) {
        context = c;
        taskLists = t;
        this.mTaskListner = onTaskClickListner;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.task_preview, parent, false);
        myViewHolder viewHolder = new myViewHolder(view, mTaskListner);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        holder.title.setText(taskLists.get(position).getTaskTitle());
        holder.dueDate.setText(taskLists.get(position).getDueDate());
        holder.description.setText(taskLists.get(position).getTaskDescription());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskid = taskLists.get(holder.getAdapterPosition()).getTaskID();
                new AlertDialog.Builder(context)
                        .setTitle(R.string.confirm_text)
                        .setMessage(R.string.confirm_text_description)
                        .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                taskLists.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance()
                                        .getReference("tasks")
                                        .child(taskid)
                                        .removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(R.string.negative_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        holder.editTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskid = taskLists.get(holder.getAdapterPosition()).getTaskID();
                new AlertDialog.Builder(context)
                        .setTitle(R.string.confirm_text)
                        .setMessage("Do you really want to edit this task?")
                        .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, AddTask.class).putExtra("taskID", taskid);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.negative_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskLists.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, dueDate, description;
        ImageButton deleteButton;
        ImageView editTaskButton;
        OnTaskClickListner onTaskClickListner;

        public myViewHolder(@NonNull View itemView, OnTaskClickListner onTaskClickListner) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.taskDueDate);
            description = itemView.findViewById(R.id.taskDescription);
            deleteButton = itemView.findViewById(R.id.taskDeleteButton);
            editTaskButton = itemView.findViewById(R.id.imageViewEditButton);
            this.onTaskClickListner = onTaskClickListner;

            itemView.setOnClickListener(this);

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
