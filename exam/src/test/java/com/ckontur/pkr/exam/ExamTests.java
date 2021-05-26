package com.ckontur.pkr.exam;

import com.ckontur.pkr.testcontainers.SpringBootPostgreSQLContainerTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class ExamTests extends SpringBootPostgreSQLContainerTests {
    @Autowired
    private MockMvc mockMvc;


}
