package com.ckontur.pkr.auth;

import com.ckontur.pkr.auth.web.AuthenticateRequest;
import com.ckontur.pkr.auth.web.ChangeUserRequest;
import com.ckontur.pkr.auth.web.CreateUserRequest;
import com.ckontur.pkr.common.model.Authority;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.testcontainers.SpringBootPostgreSQLContainerTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
public class UserTests extends SpringBootPostgreSQLContainerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String authToken;

    @BeforeClass
    void beforeClass() throws Exception {
        MvcResult response = mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthenticateRequest("test-admin", "test-admin")))
        ).andExpect(status().isOk()).andReturn();
        authToken = response.getResponse().getContentAsString();
    }

    @AfterMethod
    void afterEachMethod() {
        jdbcTemplate.update("DELETE FROM users WHERE id <> 1");
    }

    @Test
    void testGetUserByIdNotAuthenticated() throws Exception {
        mockMvc.perform(get("/user/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(
            get("/user/2000")
                .header("Authorization", "Bearer " + authToken)
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetUserByIdAuthenticated() throws Exception {
        mockMvc.perform(
            get("/user/1")
                .header("Authorization", "Bearer " + authToken)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(
            new User(1L, "test-admin", "$2a$10$lf0VM9MZqeJG7yA6LNDuPOVc9n2YdLZCiB7tZCeiHZY1p58wnEUbe", Set.of(Authority.ADMIN)))
        ));
    }

    @Test
    void testCreateUserNotAuthenticated() throws Exception {
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("any", "any", Set.of(Authority.CRM))))
        ).andExpect(status().isForbidden());
    }

    @Test
    void testCreateUserBadRequest() throws Exception {
        mockMvc.perform(
            post("/user/")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("any", "any", Set.of(Authority.CRM))))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserSuccessfully() throws Exception {
        mockMvc.perform(
            post("/user/")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("test-user", "test-user", Set.of(Authority.CRM))))
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
            post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthenticateRequest("test-user", "test-user")))
        ).andExpect(status().isOk());
    }

    @Test
    void testChangeUserForbidden() throws Exception {
        mockMvc.perform(
            put("/user/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChangeUserRequest("test-user-changed", "test-user-changed", Set.of(Authority.CRM))))
        ).andExpect(status().isForbidden());
    }

    @Test
    void testChangeUserBadRequest() throws Exception {
        mockMvc.perform(
            put("/user/2")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChangeUserRequest("", "", Collections.emptySet())))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void testChangeUserSuccessfully() throws Exception {
        MvcResult response =  mockMvc.perform(
            post("/user/")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("test-user", "test-user", Set.of(Authority.CRM))))
        ).andExpect(status().isOk()).andReturn();

        User user = objectMapper.readValue(response.getResponse().getContentAsString(), User.class);
        mockMvc.perform(
            put("/user/" + user.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChangeUserRequest("test-user-changed", null, null)))
        ).andExpect(status().isOk()).andExpect(jsonPath("$.login", is("test-user-changed")));
    }

    @Test
    void testDeleteUserForbidden() throws Exception {
        mockMvc.perform(
            delete("/user/1")
        ).andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(
            delete("/user/2000")
                .header("Authorization", "Bearer " + authToken)
        ).andExpect(status().isNotFound());
    }

    @Test
    void testDeleteSuccessfully() throws Exception {
        MvcResult response =  mockMvc.perform(
            post("/user/")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("test-user", "test-user", Set.of(Authority.CRM))))
        ).andExpect(status().isOk()).andReturn();

        User user = objectMapper.readValue(response.getResponse().getContentAsString(), User.class);
        mockMvc.perform(
            delete("/user/" + user.getId())
                .header("Authorization", "Bearer " + authToken)
        ).andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void testUserHasNoAuthority() throws Exception {
        mockMvc.perform(
            post("/user/")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateUserRequest("test-examinee", "test-examinee", Set.of(Authority.EXAMINEE))))
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));

        String token = mockMvc.perform(
            post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthenticateRequest("test-examinee", "test-examinee")))
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        mockMvc.perform(
            get("/user/1")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isForbidden());
    }
}
