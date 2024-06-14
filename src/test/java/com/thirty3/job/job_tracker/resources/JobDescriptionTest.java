package com.thirty3.job.job_tracker.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirty3.job.job_tracker.model.JobDescriptionRepository;
import com.thirty3.job.job_tracker.records.JobDescription;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class JobDescriptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobDescriptionRepository repository;

    @Test
    void testPostJobDescriptionOK() throws Exception {
        JobDescription jd = new JobDescription(1L,
                "sample description",
                "software developer",
                "http://jt.com/careers",
                "jt",
                "Remote",
                null);
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(jd);

        jd.setStatus(JobDescription.Status.BOOKMARKED);
        when(repository.save(any())).thenReturn(jd);
        String expectedResponseBody = mapper.writeValueAsString(jd);

        this.mockMvc.perform(post("/job-description").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseBody));
    }

    @Test
    void testPostJobDescriptionBadRequest() throws Exception {
        JobDescription jd = new JobDescription(1L,
                "sample description",
                null,
                "http://jt.com/careers",
                "jt",
                "Remote",
                null);
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(jd);
        this.mockMvc.perform(post("/job-description").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetJobDescriptionOK() throws Exception {
        long id = 1L;
        JobDescription jd = new JobDescription();
        jd.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(jd));
        this.mockMvc.perform(get(MessageFormat.format("/job-description/{0}", id))).
                andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetAllJobDescriptionOK() throws Exception {
        this.mockMvc.perform(get("/job-description")).
                andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(content().string("[]"));

        ArrayList<JobDescription> jds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            jds.add(new JobDescription((long) i, "description", "jobTitle", "url", "companyName", "location", null));
        }
        when(repository.findAll()).thenReturn(jds);
        this.mockMvc.perform(get("/job-description")).
                andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(20)))
                .andExpect(jsonPath("$[*].id", everyItem(allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(19)))))
                .andExpect(jsonPath("$[*].jobTitle", everyItem(is("jobTitle"))));
    }
}
