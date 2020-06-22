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
import com.helpinghandsorg.helpinghands.AddJobs;
import com.helpinghandsorg.helpinghands.JobDetails;
import com.helpinghandsorg.helpinghands.R;

import java.util.ArrayList;

public class JobsAdaptor extends RecyclerView.Adapter<JobsAdaptor.myViewHolder> {

    private Context context;
    private ArrayList<JobDetails> jobLists;
    private String jobid;
    private OnTaskClickListner mTaskListner;

    public JobsAdaptor(Context c, ArrayList<JobDetails> t, OnTaskClickListner onTaskClickListner) {
        context = c;
        jobLists = t;
        this.mTaskListner = onTaskClickListner;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(context).inflate(R.layout.job_preview, parent, false), mTaskListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {
        holder.title.setText(jobLists.get(position).getPost().toUpperCase());
        holder.dueDate.setText(jobLists.get(position).getLastdate());
        //holder.description.setText(jobLists.get(position).getEligibility());
        holder.salary.setText(jobLists.get(position).getSalary());
        holder.companyName.setText(jobLists.get(position).getCompany().toUpperCase());
        holder.totalPost.setText(jobLists.get(position).getTotalpost());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobid = jobLists.get(position).getJobId();
                new AlertDialog.Builder(context)
                        .setTitle(R.string.confirm_text)
                        .setMessage(R.string.confirm_text_description)
                        .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                jobLists.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                FirebaseDatabase.getInstance()
                                        .getReference("jobs")
                                        .child(jobid)
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
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobid = jobLists.get(holder.getAdapterPosition()).getJobId();
                new AlertDialog.Builder(context)
                        .setTitle(R.string.confirm_text)
                        .setMessage("Do you really want to edit this job?")
                        .setPositiveButton(R.string.positive_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, AddJobs.class).putExtra("jobID", jobid);
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
        return jobLists.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, dueDate, description, salary, totalPost, companyName;
        ImageButton deleteButton;
        ImageView editButton;
        OnTaskClickListner onTaskClickListner;

        public myViewHolder(@NonNull View itemView, OnTaskClickListner onTaskClickListner) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.taskDueDate);
            description = itemView.findViewById(R.id.taskDescription);
            deleteButton = itemView.findViewById(R.id.taskDeleteButton);
            editButton = itemView.findViewById(R.id.imageViewEditButton);
            salary = itemView.findViewById(R.id.textViewSalary);
            totalPost = itemView.findViewById(R.id.textViewTotalPost);
            companyName = itemView.findViewById(R.id.companyName);
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
