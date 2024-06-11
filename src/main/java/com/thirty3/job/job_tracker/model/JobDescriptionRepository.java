package com.thirty3.job.job_tracker.model;

import com.thirty3.job.job_tracker.records.JobDescription;
import org.springframework.data.repository.CrudRepository;

public interface JobDescriptionRepository extends CrudRepository<JobDescription, Long> {

}