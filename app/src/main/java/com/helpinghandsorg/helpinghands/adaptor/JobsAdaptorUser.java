package com.helpinghandsorg.helpinghands.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.helpinghandsorg.helpinghands.JobDetails;
import com.helpinghandsorg.helpinghands.R;

import java.util.ArrayList;

public class JobsAdaptorUser extends RecyclerView.Adapter<JobsAdaptorUser.myViewHolder> {

    private Context context;
    private ArrayList<JobDetails> jobLists;
    private OnTaskClickListner mTaskListner;

    public JobsAdaptorUser(Context c, ArrayList<JobDetails> t, OnTaskClickListner onTaskClickListner) {
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
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {
        holder.title.setText(jobLists.get(position).getPost().toUpperCase());
        holder.dueDate.setText(jobLists.get(position).getLastdate());
        //holder.description.setText(jobLists.get(position).getEligibility());
        holder.salary.setText(jobLists.get(position).getSalary());
        holder.companyName.setText(jobLists.get(position).getCompany().toUpperCase());
        holder.totalPost.setText(jobLists.get(position).getTotalpost());
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
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
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
