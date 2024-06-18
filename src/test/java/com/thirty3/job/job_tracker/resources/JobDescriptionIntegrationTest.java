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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

        // For execution of PATCH requests using RestTemplate
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private JobDescription createJobDescription(String jobTitle) {
        JobDescription request = new JobDescription();
        request.setJobTitle(jobTitle);
        return createJobDescription(request);
    }

    private JobDescription createJobDescription(JobDescription jobDescription) {
        return this.restTemplate.postForEntity("http://localhost:" + port + "/job-description",
                jobDescription, JobDescription.class, (Object) null).getBody();
    }

    private JobDescription getJobDescription(long id) {
        return this.restTemplate.getForObject("http://localhost:" + port + "/job-description/" + id,
                JobDescription.class);
    }

    private void updateJobDescription(long id, JobDescription jobDescription) {
        this.restTemplate.patchForObject("http://localhost:" + port + "/job-description/" + id,
                jobDescription, String.class);
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
    void testGetJobDescriptionOK() throws Exception {
        String jobTitle = "jobTitle";
        long jobDescriptionId = createJobDescription(jobTitle).getId();
        JobDescription jobDescription = getJobDescription(jobDescriptionId);
        assertThat(jobDescription).isNotNull();
        assertThat(jobDescription.getId()).isGreaterThan(0);
    }

    @Test
    void testGetJobDescription_when_notExist() throws Exception {
        assertThat(this.restTemplate.getForEntity("http://localhost:" + port + "/job-description/123",
                String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllJobDescriptionOK() throws Exception {
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
    void testGetAllJobDescription_when_notExist() throws Exception {
        String jsonAsString = this.restTemplate.getForObject(
                "http://localhost:" + port + "/job-description", String.class, (Object) null);
        List<JobDescription> list = new ObjectMapper().readValue(jsonAsString, new TypeReference<>() {});
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    void testDeleteJobDescriptionOK() throws Exception {
        long jobDescriptionId = createJobDescription("jobTitle").getId();
        assertThat(getJobDescription(jobDescriptionId)).isNotNull();
        this.restTemplate.delete("http://localhost:" + port + "/job-description/" + jobDescriptionId);
        assertThat(this.restTemplate.getForEntity("http://localhost:" + port + "/job-description/123",
                String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteJobDescription_when_notExist() throws Exception {
        assertThat(this.restTemplate.exchange("http://localhost:" + port + "/job-description/1", HttpMethod.DELETE,
                null, String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateJobDescriptionOK() throws Exception {
        JobDescription jobDescription = new JobDescription(
                0L,
                "oldDescription",
                "oldJobTitle",
                "oldURL",
                "oldCompanyName",
                "oldLocation",
                JobDescription.Status.BOOKMARKED);
        JobDescription oldJobDescription = getJobDescription(createJobDescription(jobDescription).getId());
        assertThat(oldJobDescription.getId()).isGreaterThan(0);
        assertThat(oldJobDescription.getDescription()).isEqualTo("oldDescription");
        assertThat(oldJobDescription.getJobTitle()).isEqualTo("oldJobTitle");
        assertThat(oldJobDescription.getUrl()).isEqualTo("oldURL");
        assertThat(oldJobDescription.getCompanyName()).isEqualTo("oldCompanyName");
        assertThat(oldJobDescription.getLocation()).isEqualTo("oldLocation");
        assertThat(oldJobDescription.getStatus().toString()).isEqualTo(JobDescription.Status.BOOKMARKED.toString());

        JobDescription request = new JobDescription(
                0L,
                "newDescription",
                "newJobTitle",
                "newURL",
                "newCompanyName",
                "newLocation",
                JobDescription.Status.REJECTED);
        long id = oldJobDescription.getId();
        updateJobDescription(id, request);
        JobDescription newJobDescription = getJobDescription(id);
        assertThat(newJobDescription.getId()).isEqualTo(id);
        assertThat(newJobDescription.getDescription()).isEqualTo("newDescription");
        assertThat(newJobDescription.getJobTitle()).isEqualTo("newJobTitle");
        assertThat(newJobDescription.getUrl()).isEqualTo("newURL");
        assertThat(newJobDescription.getCompanyName()).isEqualTo("newCompanyName");
        assertThat(newJobDescription.getLocation()).isEqualTo("newLocation");
        assertThat(newJobDescription.getStatus().toString()).isEqualTo(JobDescription.Status.BOOKMARKED.toString());
    }

    @Test
    void testUpdateJobDescription_when_updateToNull() throws Exception {
        JobDescription jobDescription = new JobDescription(
                0L,
                "oldDescription",
                "oldJobTitle",
                "oldURL",
                "oldCompanyName",
                "oldLocation",
                JobDescription.Status.BOOKMARKED);
        JobDescription oldJobDescription = getJobDescription(createJobDescription(jobDescription).getId());
        assertThat(oldJobDescription.getId()).isGreaterThan(0);

        JobDescription request = new JobDescription(
                0L,
                null,
                "newJobTitle",
                null,
                null,
                null,
                JobDescription.Status.REJECTED);
        long id = oldJobDescription.getId();
        updateJobDescription(id, request);
        JobDescription newJobDescription = getJobDescription(id);
        assertThat(newJobDescription.getId()).isEqualTo(id);
        assertThat(newJobDescription.getDescription()).isNull();
        assertThat(newJobDescription.getJobTitle()).isEqualTo("newJobTitle");
        assertThat(newJobDescription.getUrl()).isNull();
        assertThat(newJobDescription.getCompanyName()).isNull();
        assertThat(newJobDescription.getLocation()).isNull();
        assertThat(newJobDescription.getStatus().toString()).isEqualTo(JobDescription.Status.BOOKMARKED.toString());
    }

    @Test
    void testUpdateJobDescription_when_notExist() throws Exception {
        JobDescription request = new JobDescription(
                0L,
                "newDescription",
                "newJobTitle",
                "newURL",
                "newCompanyName",
                "newLocation",
                JobDescription.Status.REJECTED);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(request);
        assertThat(this.restTemplate.exchange("http://localhost:" + port + "/job-description/1", HttpMethod.PATCH,
                httpEntity, String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateJobDescriptionStatusOK() throws Exception {
        JobDescription jobDescription = new JobDescription();
        jobDescription.setStatus(JobDescription.Status.BOOKMARKED);
        jobDescription.setJobTitle("jobTitle");
        JobDescription oldJobDescription = createJobDescription(jobDescription);

        long id = oldJobDescription.getId();
        this.restTemplate.patchForObject(
                "http://localhost:" + port + "/job-description/" + id + "/status?status=" + JobDescription.Status.APPLIED,
                null, String.class);
        JobDescription newJobDescription = getJobDescription(id);
        assertThat(newJobDescription.getStatus().toString()).isEqualTo(JobDescription.Status.APPLIED.toString());
    }

    @Test
    void testUpdateJobDescriptionStatus_when_validStatus() throws Exception {
        JobDescription jobDescription = new JobDescription();
        jobDescription.setStatus(JobDescription.Status.BOOKMARKED);
        jobDescription.setJobTitle("jobTitle");
        JobDescription oldJobDescription = createJobDescription(jobDescription);

        long id = oldJobDescription.getId();
        this.restTemplate.patchForObject(
                "http://localhost:" + port + "/job-description/" + id + "/status?status=rejected",
                null, String.class);
        JobDescription newJobDescription = getJobDescription(id);
        assertThat(newJobDescription.getStatus().toString()).isEqualTo(JobDescription.Status.REJECTED.toString());
    }

    @Test
    void testUpdateJobDescriptionStatus_when_invalidStatus() throws Exception {
        JobDescription jobDescription = new JobDescription();
        jobDescription.setStatus(JobDescription.Status.BOOKMARKED);
        jobDescription.setJobTitle("jobTitle");
        JobDescription oldJobDescription = createJobDescription(jobDescription);
        long id = oldJobDescription.getId();

        assertThat(this.restTemplate.exchange(
                "http://localhost:" + port + "/job-description/" + id + "/status?status=INVALID",
                HttpMethod.PATCH, null, String.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateJobDescriptionStatus_when_notExist() throws Exception {
        assertThat(this.restTemplate.exchange(
                "http://localhost:" + port + "/job-description/1/status?status=" + JobDescription.Status.APPLIED,
                HttpMethod.PATCH, null, String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
