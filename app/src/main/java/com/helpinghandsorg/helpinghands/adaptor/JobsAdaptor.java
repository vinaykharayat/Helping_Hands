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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helpinghandsorg.helpinghands.JobDetails;
import com.helpinghandsorg.helpinghands.R;

import java.util.ArrayList;

public class JobsAdaptor extends RecyclerView.Adapter<JobsAdaptor.myViewHolder> {

    private Context context;
    private ArrayList<JobDetails> jobLists;
    private int counter = 0;
    private Boolean success = false;
    private String jobid;
    private OnTaskClickListner mTaskListner;
    private int newPosition;

    public JobsAdaptor(Context c, ArrayList<JobDetails> t, OnTaskClickListner onTaskClickListner) {
        context = c;
        jobLists = t;
        this.mTaskListner = onTaskClickListner;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(context).inflate(R.layout.task_preview, parent, false), mTaskListner);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        newPosition = position;
        holder.title.setText(jobLists.get(newPosition).getPost());
        holder.dueDate.setText(jobLists.get(newPosition).getLastdate());
        holder.description.setText(jobLists.get(newPosition).getEligibility());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobid = jobLists.get(newPosition).getJobId();
                new AlertDialog.Builder(context)
                        .setTitle(R.string.confirm_text)
                        .setMessage(R.string.confirm_text_description)
                        .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance()
                                        .getReference("jobs")
                                        .child(jobid)
                                        .removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                //taskLists.remove(position);
                                                jobLists.remove(newPosition);
                                                notifyItemRemoved(newPosition);
                                                notifyItemRangeChanged(newPosition, jobLists.size());
                                                Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
                                                success = true;
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
    }

    @Override
    public int getItemCount() {
        return jobLists.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, dueDate, description;
        ImageButton deleteButton;
        OnTaskClickListner onTaskClickListner;

        public myViewHolder(@NonNull View itemView, OnTaskClickListner onTaskClickListner) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.taskDueDate);
            description = itemView.findViewById(R.id.taskDescription);
            deleteButton = itemView.findViewById(R.id.taskDeleteButton);
            this.onTaskClickListner = onTaskClickListner;

            itemView.setOnClickListener(this);

        }

        public ImageButton getDeleteButton() {
            return deleteButton;
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
