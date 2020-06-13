package com.helpinghandsorg.helpinghands;


public class TaskModel {
    private String taskTitle;
    private String dueDate;
    private String taskDescription;
    private String taskID;
    private String submissionDate;
    private String destination;

    public TaskModel(String taskTitle, String dueDate, String taskDescription) {
        this.taskTitle = taskTitle;
        this.dueDate = dueDate;
        this.taskDescription = taskDescription;
    }

    public TaskModel(String taskTitle, String dueDate, String taskDescription, String taskID) {
        this.taskTitle = taskTitle;
        this.dueDate = dueDate;
        this.taskDescription = taskDescription;
        this.taskID = taskID;
    }

    public TaskModel() {

    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

}

