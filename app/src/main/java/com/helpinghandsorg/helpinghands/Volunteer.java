package com.helpinghandsorg.helpinghands;


public class Volunteer {
    private String gender, admin, fullName, email, joiningDate, profilePicUrl, designation, id;
    private Long taskAlloted, taskFinished;
    private Long rating;

    public Volunteer() {

    }

    Volunteer(String fullName,
              String email,
              String gender,
              String admin,
              String joiningDate,
              String profilePicUrl,
              String designation,
              String id,
              Long rating,
              Long taskAlloted,
              Long taskFinished) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.admin = admin;
        this.joiningDate = joiningDate;
        this.profilePicUrl = profilePicUrl;
        this.designation = designation;
        this.rating = rating;
        this.id = id;
        this.taskAlloted = taskAlloted;
        this.taskFinished = taskFinished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String isAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Long getTaskAlloted() {
        return taskAlloted;
    }

    public void setTaskAlloted(Long taskAlloted) {
        this.taskAlloted = taskAlloted;
    }

    public Long getTaskFinished() {
        return taskFinished;
    }

    public void setTaskFinished(Long taskFinished) {
        this.taskFinished = taskFinished;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }
}
