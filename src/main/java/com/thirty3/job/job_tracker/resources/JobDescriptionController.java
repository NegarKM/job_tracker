package com.thirty3.job.job_tracker.resources;

import com.thirty3.job.job_tracker.exceptions.InvalidInputException;
import com.thirty3.job.job_tracker.model.JobDescriptionRepository;
import com.thirty3.job.job_tracker.records.JobDescription;
import com.thirty3.job.job_tracker.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
public class JobDescriptionController {

	@Autowired
	private JobDescriptionRepository repository;

	@GetMapping("/job-description")
	public Iterable<JobDescription> get_all_job_descriptions() {
		return repository.findAll();
	}

	@GetMapping("/job-description/{id}")
	public Optional<JobDescription> get_job_description(@PathVariable("id") long id) {
		return Optional.ofNullable(repository.findById(id).orElseThrow(ResourceNotFoundException::new));
	}

	@PostMapping("/job-description")
	public JobDescription create_job_description(@Valid @RequestBody JobDescription jobDescription) {
		jobDescription.setStatus(JobDescription.Status.BOOKMARKED);
		return repository.save(jobDescription);
	}

	@DeleteMapping("/job-description/{id}")
	public void delete_job_description(@PathVariable("id") long id) {
		JobDescription jobDescription = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
		repository.delete(jobDescription);
	}

	@PatchMapping("/job-description/{id}")
	public void update_job_description(@PathVariable("id") long id, @RequestBody JobDescription newJobDescription) {
		JobDescription jobDescription = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
		jobDescription.setJobTitle(newJobDescription.getJobTitle());
		jobDescription.setDescription(newJobDescription.getDescription());
		jobDescription.setUrl(newJobDescription.getUrl());
		jobDescription.setLocation(newJobDescription.getLocation());
		jobDescription.setCompanyName(newJobDescription.getCompanyName());
		repository.save(jobDescription);
	}

	@PatchMapping("/job-description/{id}/status")
	public void update_job_description_status(@PathVariable("id") long id, @RequestParam(value = "status") String status) {
		JobDescription jobDescription = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
		try {
			jobDescription.setStatus(JobDescription.Status.valueOf(status.toUpperCase()));
		} catch(IllegalArgumentException e) {
			throw new InvalidInputException();
		}
		repository.save(jobDescription);
	}
}
