package com.ckontur.pkr.core.controller;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.core.model.Assessment;
import com.ckontur.pkr.core.repository.AssessmentRepository;
import com.ckontur.pkr.core.web.AssessmentRequests;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Api(tags = {"Центры оценки квалификаций"})
@RestController
@RequestMapping("/assessment")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','CRM')")
@Timed(value = "requests.assessment", percentiles = {0.75, 0.9, 0.95, 0.99})
public class AssessmentController {
    private final AssessmentRepository assessmentRepository;

    @GetMapping("/list")
    public List<Assessment> getAll(@RequestParam(value = "regionId", required = false) Long regionId) {
        return Optional.ofNullable(regionId)
            .map(assessmentRepository::getAllByRegionId)
            .orElseGet(assessmentRepository::getAll);
    }

    @PostMapping("/")
    public Assessment create(@Valid @RequestBody AssessmentRequests.CreateAssessment request) {
        return assessmentRepository.create(
            request.getName(), request.getRegionId(), request.getAddress(), request.getCapacity()
        ).orElseThrow(() -> new CreateEntityException("Не удалось создать новый ЦОК."));
    }

    @PutMapping("/{id}")
    public Assessment change(
        @PathVariable("id") Long id,
        @Valid @RequestBody AssessmentRequests.UpdateAssessment request
    ) {
        return assessmentRepository.updateById(
            id, request.getName(), request.getRegionId(), request.getAddress(), request.getCapacity()
        ).orElseThrow(() -> new NotFoundException("ЦОК " + id + " не найден."));
    }

    @DeleteMapping("/{id}")
    public Assessment delete(@PathVariable("id") Long id) {
        return assessmentRepository.deleteById(id)
            .orElseThrow(() -> new NotFoundException("ЦОК " + id + " не найден."));
    }

}
