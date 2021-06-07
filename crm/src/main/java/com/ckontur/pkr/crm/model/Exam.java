package com.ckontur.pkr.crm.model;

import lombok.Data;

import java.time.Duration;

@Data
public class Exam {
    private long id;
    private String qualification;
    private String level;
    private Duration duration;
    private int pointsPerCorrect;
    private int percentPassed;
    private boolean skippable;
    private boolean previousable;
    private boolean isPublished;
}
