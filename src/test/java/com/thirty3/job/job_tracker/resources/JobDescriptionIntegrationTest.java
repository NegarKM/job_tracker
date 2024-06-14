package com.thirty3.job.job_tracker.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirty3.job.job_tracker.model.JobDescriptionRepository;
import com.thirty3.job.job_tracker.records.JobDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobDescriptionIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JobDescriptionRepository repository;

    @BeforeEach
    public void initTests() {
        repository.deleteAll();
    }

    private JobDescription createJobDescription(String jobTitle) {
        JobDescription request = new JobDescription();
        request.setJobTitle(jobTitle);
        return this.restTemplate.postForEntity("http://localhost:" + port + "/job-description",
                request, JobDescription.class, (Object) null).getBody();
    }

    private JobDescription getJobDescription(long id) {
        return this.restTemplate.getForObject("http://localhost:" + port + "/job-description/" + id,
                JobDescription.class);
    }

    @Test
    void testPostJobDescriptionOK() throws Exception {
        String jobTitle = "jobTitle";
        JobDescription jobDescription = createJobDescription(jobTitle);
        assertThat(jobDescription).isNotNull();
        assertThat(jobDescription.getId()).isGreaterThan(0);
        assertThat(jobDescription.getStatus()).isEqualTo(JobDescription.Status.BOOKMARKED);
        assertThat(jobDescription.getJobTitle()).isEqualTo(jobTitle);
    }

    @Test
    void testPostJobDescription_when_missingJobDescription() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        assertThat(this.restTemplate.postForEntity("http://localhost:" + port + "/job-description",
                request, String.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testPostJobDescription_when_missingJobTitle() throws Exception {
        JobDescription request = new JobDescription();
        assertThat(this.restTemplate.postForEntity("http://localhost:" + port + "/job-description",
                request, String.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void tesGetJobDescriptionOK() throws Exception {
        String jobTitle = "jobTitle";
        long jobDescriptionId = createJobDescription(jobTitle).getId();
        JobDescription jobDescription = getJobDescription(jobDescriptionId);
        assertThat(jobDescription).isNotNull();
        assertThat(jobDescription.getId()).isGreaterThan(0);
    }

    @Test
    void tesGetJobDescription_when_notExist() throws Exception {
        JobDescription jobDescription = getJobDescription(123L);
        assertThat(jobDescription).isNull();
    }

    @Test
    void tesGetAllJobDescriptionOK() throws Exception {
        int n = 100;
        for (int i = 0; i < n; i++) {
            createJobDescription("jobTitle" + System.currentTimeMillis());
        }
        String jsonAsString = this.restTemplate.getForObject(
                "http://localhost:" + port + "/job-description", String.class, (Object) null);
        List<JobDescription> list = new ObjectMapper().readValue(jsonAsString, new TypeReference<>() {});
        assertThat(list.size()).isEqualTo(n);
    }

    @Test
    void tesGetAllJobDescription_when_notExist() throws Exception {
        String jsonAsString = this.restTemplate.getForObject(
                "http://localhost:" + port + "/job-description", String.class, (Object) null);
        List<JobDescription> list = new ObjectMapper().readValue(jsonAsString, new TypeReference<>() {});
        assertThat(list.size()).isEqualTo(0);
    }
}
