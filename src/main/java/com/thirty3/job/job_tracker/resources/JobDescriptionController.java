package com.thirty3.job.job_tracker.resources;

import com.thirty3.job.job_tracker.model.JobDescriptionRepository;
import com.thirty3.job.job_tracker.records.JobDescription;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class JobDescriptionController {

	@Autowired
	private JobDescriptionRepository repository;

	@GetMapping("/job-description/{id}")
	public Optional<JobDescription> get_job_description(@PathVariable("id") long id) {
		return repository.findById(id);
	}

	@PostMapping("/job-description")
	public JobDescription add_job_description(@Valid @RequestBody JobDescription jobDescription) {
		jobDescription.setStatus(JobDescription.Status.BOOKMARKED);
		return repository.save(jobDescription);
	}
}
