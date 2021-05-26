package com.ckontur.pkr.auth;

import com.ckontur.pkr.auth.web.AuthenticateRequest;
import com.ckontur.pkr.testcontainers.SpringBootPostgreSQLContainerTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class AuthenticationTests extends SpringBootPostgreSQLContainerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testInvalidLoginAndPassword() throws Exception {
        mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthenticateRequest("any", "any")))
        ).andExpect(status().isForbidden());
    }

    @Test
    void testInvalidLogin() throws Exception {
        mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthenticateRequest("any", "test-admin")))
        ).andExpect(status().isForbidden());
    }

    @Test
    void testInvalidPassword() throws Exception {
        mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthenticateRequest("test-admin", "any")))
        ).andExpect(status().isForbidden());
    }

    @Test
    void testSuccessfullyAuthenticate() throws Exception {
        mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthenticateRequest("test-admin", "test-admin")))
        ).andExpect(status().isOk());
    }

    @Test
    void testSuccessfullyAuthenticateAndTokenIsPassed() throws Exception {
        MvcResult response = mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthenticateRequest("test-admin", "test-admin")))
        ).andExpect(status().isOk()).andReturn();

        String token = response.getResponse().getContentAsString();
        mockMvc.perform(
            get("/user/1")
            .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk());
    }

}
