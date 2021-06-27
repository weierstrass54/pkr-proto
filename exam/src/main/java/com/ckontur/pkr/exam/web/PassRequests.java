package com.ckontur.pkr.exam.web;

import com.ckontur.pkr.exam.component.ExamListDeserializer;
import com.ckontur.pkr.exam.model.question.Answer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PassRequests {

    @Getter
    @RequiredArgsConstructor
    @JsonDeserialize(using = ExamListDeserializer.class)
    public static class ExamList {
        private final Long recordId;
        private final Long examId;
        private final Map<Long, List<Answer>> answers;
    }
}
