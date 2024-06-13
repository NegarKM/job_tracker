package com.thirty3.job.job_tracker.records;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    private Status status;

    public enum Status {
        BOOKMARKED, APPLYING, APPLIED, REJECTED, ARCHIVED
    }
}
