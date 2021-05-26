package com.ckontur.pkr.exam.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Exam {
    private final String qualification;
    private final String level;
    private final Duration duration;
    private final int pointsPerCorrect;
    private final int percentPassed;
    private final boolean skippable;
    private final boolean previousable;
    private final List<Question> questions;

    public int getQuestionCount() {
        return questions.size();
    }

    public int getPassedPoints() {
        int passedCount = percentPassed * getQuestionCount() / 100;
        if (percentPassed * getQuestionCount() % 100 != 0) {
            passedCount += 1;
        }
        return passedCount * pointsPerCorrect;
    }
}
