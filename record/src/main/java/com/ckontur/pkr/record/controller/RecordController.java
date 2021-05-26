package com.ckontur.pkr.record.controller;

import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.record.Record;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"Заявки"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
@PreAuthorize("hasAnyAuthority('ADMIN', 'CRM', 'EXAMINEE')")
public class RecordController {
    private final RecordService recordService;

    @PostMapping("/")
    public Record create() {
        return null;
    }

    @DeleteMapping("/{id}")
    public Record delete(@PathVariable("id") Long id) {
        return recordService.deleteById(id)
            .orElseThrow(() -> new NotFoundException("Заявка " + id + "не найдена."));
    }

}
