package com.thirty3.job.job_tracker.records;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String description;

    @NotNull
    private String jobTitle;

    private String url;

    private String companyName;

    private String location;

    private String status = "Bookmarked";

    public JobDescription() {}

    public JobDescription(long id, String description, String jobTitle, String url, String companyName, String location, String status) {
        this.id = id;
        this.description = description;
        this.jobTitle = jobTitle;
        this.url = url;
        this.companyName = companyName;
        this.location = location;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
