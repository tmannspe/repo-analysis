package com.company.repoanalysis.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMetricsFromSbsUIBackend() throws Exception {
        String requestBody = "https://seu16.gdc-leinf01.t-systems.com/bitbucket/scm/sbs/sbs-ui-backend.git";
        this.mockMvc.perform(get("/metrics?before=2022-02-09&after=2022-01-09")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(jsonPath("number-of-authors", is(5)))
                .andExpect(jsonPath("number-of-entities", is(13)))
                .andExpect(jsonPath("number-of-entities-changed", is(25)))
                .andExpect(jsonPath("number-of-commits", is(19)));
    }

    @Test
    public void testHotspotsFromSbsUIBackend() throws Exception {
        String requestBody = "https://seu16.gdc-leinf01.t-systems.com/bitbucket/scm/sbs/sbs-ui-backend.git";
        this.mockMvc.perform(get("/metrics/hotspots?before=2022-02-09&after=2021-01-09")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
        .andExpect(jsonPath("[*].module", containsInAnyOrder("src\\main\\java\\com\\tsystems\\sbs\\sbsui\\ticket\\service\\JiraTicketService.java",
                "src\\main\\java\\com\\tsystems\\sbs\\sbsui\\project\\controller\\ProjectResource.java",
                "src\\main\\java\\com\\tsystems\\sbs\\sbsui\\project\\service\\ProjectService.java")));
    }

    @Test
    public void testHotspot() throws Exception {
        String requestBody = "https://seu16.gdc-leinf01.t-systems.com/bitbucket/scm/sbspub/jenkins-api.git";
        this.mockMvc.perform(get("/metrics/hotspots?before=2022-02-09&after=2021-01-09")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print());
    }
}
