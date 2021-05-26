package com.ckontur.pkr.auth;

import com.ckontur.pkr.testcontainers.SpringBootPostgreSQLContainerTests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class SwaggerTests extends SpringBootPostgreSQLContainerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testSwaggerUI() throws Exception {
        mockMvc.perform(get("/swagger-ui/"))
            .andExpect(status().isOk());
    }
}
