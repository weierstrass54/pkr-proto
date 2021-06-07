package com.ckontur.pkr.exam.model;

import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Getter
public class DetailedExam extends Exam {
    private final List<Question> questions;

    public DetailedExam(long id, String qualification, String level, Duration duration, int pointsPerCorrect,
                        int percentPassed, boolean skippable, boolean previousable, boolean isPublished, List<Question> questions) {
        super(id, qualification, level, duration, pointsPerCorrect, percentPassed, skippable, previousable, isPublished);
        this.questions = questions;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public int getPassedPoints() {
        int passedCount = getPercentPassed() * getQuestionCount() / 100;
        if (getPercentPassed() * getQuestionCount() % 100 != 0) {
            passedCount += 1;
        }
        return passedCount * getPointsPerCorrect();
    }
}
