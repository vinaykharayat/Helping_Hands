package com.helpinghandsorg.helpinghands.repositories;

public class TaskConfirmationSender
{
    private String taskDueDate, buttonStatus, userEmail, userName, submissionDate, id;

    public TaskConfirmationSender() {
    }

    public TaskConfirmationSender(String taskDueDate, String buttonStatus, String userEmail, String userName, String submissionDate, String id) {
        this.taskDueDate = taskDueDate;
        this.buttonStatus = buttonStatus;
        this.userEmail = userEmail;
        this.userName = userName;
        this.submissionDate = submissionDate;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(String taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public String getButtonStatus() {
        return buttonStatus;
    }

    public void setButtonStatus(String buttonStatus) {
        this.buttonStatus = buttonStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
