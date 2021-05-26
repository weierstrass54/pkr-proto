package com.ckontur.pkr.record.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    private RecordUser user;
    private long regionId;
    private long qualificationId;
    private long levelId;
    private long assessmentId;
    private long scheduleId;
}
