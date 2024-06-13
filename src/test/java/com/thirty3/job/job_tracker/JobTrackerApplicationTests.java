package com.thirty3.job.job_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirty3.job.job_tracker.resources.JobDescriptionController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class JobTrackerApplicationTests {

	@Autowired
	private JobDescriptionController controller;

	@Test
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

}
