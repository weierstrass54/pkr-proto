package com.ckontur.pkr.exam.service;

import com.ckontur.pkr.common.exception.NotFoundException;
import com.ckontur.pkr.exam.model.question.*;
import com.ckontur.pkr.exam.repository.DetailedExamRepository;
import com.ckontur.pkr.exam.web.PassRequests;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final DetailedExamRepository detailedExamRepository;

    public boolean check(PassRequests.ExamList examList) {
        return detailedExamRepository.findById(examList.getExamId()).map(exam ->
            exam.getQuestions().map(question -> {
                List<Answer> examineeAnswers = examList.getAnswers().getOrElse(question.getId(), List.empty());
                return switch (question.getType()) {
                    case SINGLE, MULTIPLE -> isSingleOrMultipleCorrect(
                        ((AnsweredChoiceQuestion)question).getAnswers(), examineeAnswers
                    );
                    case SEQUENCE -> isSequenceCorrect(
                        ((AnsweredChoiceQuestion) question).getAnswers(), examineeAnswers
                    );
                    case MATCHING -> isMatchCorrect(
                        ((AnsweredMatchQuestion)question).getAnswers(), examineeAnswers
                    );
                };
            })
            .filter(Option::isDefined)
            .map(__ -> exam.getPointsPerCorrect())
            .fold(0, Integer::sum) >= exam.getPassedPoints()
        ).getOrElseThrow(() -> new NotFoundException("Экзамен " + examList.getExamId() + " не найден."));
    }

    private Option<Boolean> isSingleOrMultipleCorrect(List<Long> correctAnswers, List<Answer> examineeAnswers) {
        return Option.of(
            examineeAnswers.map(a -> (ChoiceAnswer) a).map(ChoiceAnswer::getId).containsAll(correctAnswers)
        ).filter(o -> o);
    }

    private Option<Boolean> isSequenceCorrect(List<Long> correctAnswers, List<Answer> examineeAnswers) {
        return Option.of(
            examineeAnswers.map(a -> (ChoiceAnswer) a).map(ChoiceAnswer::getId).equals(correctAnswers)
        ).filter(o -> o);
    }

    private Option<Boolean> isMatchCorrect(Map<Long, Long> correctAnswers, List<Answer> examineeAnswers) {
        return Option.of(
            examineeAnswers.map(a -> (MatchAnswer) a).map(a ->
                correctAnswers.get(a.getLeftId()).map(correctId -> a.getRightId().equals(correctId)).getOrElse(false)
            ).fold(true, (a, b) -> a && b)
        ).filter(o -> o);
    }
}
