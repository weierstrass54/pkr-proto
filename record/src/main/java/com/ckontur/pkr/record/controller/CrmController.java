package com.ckontur.pkr.record.controller;

import com.ckontur.pkr.record.model.*;
import com.ckontur.pkr.record.repository.CrmRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"Справочные данные CRM"})
@RestController
@RequestMapping(value = "/crm", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNAL')")
public class CrmController {
    private final CrmRepository autocompleteRepository;

    @GetMapping(value = "/regions")
    public List<Region> getRegions() {
        return autocompleteRepository.getRegions();
    }

    @GetMapping("/qualifications")
    public List<Qualification> getQualifications() {
        return autocompleteRepository.getQualifications();
    }

    @GetMapping("/levels")
    public List<Level> getLevels() {
        return autocompleteRepository.getLevels();
    }

    @GetMapping("/assessments")
    public List<Assessment> getAssessments() {
        return autocompleteRepository.getAssessments();
    }

    @GetMapping("/schedule/{id}")
    public List<Schedule> getScheduleByAssessment(@PathVariable("id") Long assessmentId) {
        return autocompleteRepository.getScheduleByAssessment(assessmentId);
    }

}
