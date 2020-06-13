package com.helpinghandsorg.helpinghands;

public class JobDetails {
    private String post,eligibility,agelimit,qualifiction,fees,lastdate,totalpost, process, benefits, salary, company, jobId;

    public JobDetails(String post,
                      String eligibility,
                      String agelimit,
                      String qualifiction,
                      String fees,
                      String lastdate,
                      String totalpost,
                      String process,
                      String benefits,
                      String salary,
                      String company) {
        this.post = post;
        this.eligibility = eligibility;
        this.agelimit = agelimit;
        this.qualifiction = qualifiction;
        this.fees = fees;
        this.lastdate = lastdate;
        this.totalpost = totalpost;
        this.process = process;
        this.benefits = benefits;
        this.salary = salary;
        this.company = company;
    }

    public JobDetails(String post, String eligibility, String agelimit, String qualifiction, String fees, String lastdate, String totalpost, String jobId) {
        this.post = post;
        this.eligibility = eligibility;
        this.agelimit = agelimit;
        this.qualifiction = qualifiction;
        this.fees = fees;
        this.lastdate = lastdate;
        this.totalpost = totalpost;
        this.jobId = jobId;
    }

    public JobDetails() {
    }

    public String getProcess() {
        return process;
    }

    public String getBenefits() {
        return benefits;
    }

    public String getSalary() {
        return salary;
    }

    public String getCompany() {
        return company;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getAgelimit() {
        return agelimit;
    }

    public void setAgelimit(String agelimit) {
        this.agelimit = agelimit;
    }

    public String getQualifiction() {
        return qualifiction;
    }

    public void setQualifiction(String qualifiction) {
        this.qualifiction = qualifiction;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getLastdate() {
        return lastdate;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public String getTotalpost() {
        return totalpost;
    }

    public void setTotalpost(String totalpost) {
        this.totalpost = totalpost;
    }
}
