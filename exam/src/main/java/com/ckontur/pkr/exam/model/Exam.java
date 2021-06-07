package com.ckontur.pkr.exam.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class Exam {
    private final long id;
    private final String qualification;
    private final String level;
    private final Duration duration;
    private final int pointsPerCorrect;
    private final int percentPassed;
    private final boolean skippable;
    private final boolean previousable;
    private final boolean isPublished;
}
