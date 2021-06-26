package com.ckontur.pkr.exam.component;

import com.ckontur.pkr.common.exception.InvalidArgumentException;
import com.ckontur.pkr.exam.model.question.Answer;
import com.ckontur.pkr.exam.model.question.ChoiceAnswer;
import com.ckontur.pkr.exam.model.question.MatchAnswer;
import com.ckontur.pkr.exam.model.question.Question;
import com.ckontur.pkr.exam.web.PassRequests;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.stream.StreamSupport;

import static io.vavr.API.*;
import static io.vavr.Predicates.*;

public class ExamListDeserializer extends JsonDeserializer<PassRequests.ExamList> {
    @Override
    public PassRequests.ExamList deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode jn = Try.of(() -> (JsonNode) p.getCodec().readTree(p))
            .getOrElseThrow(t -> new InvalidArgumentException("Не удалось прочитать json в теле запроса.", t));

        Long recordId = Option.of(jn.get("recordId"))
            .filter(JsonNode::isLong)
            .map(JsonNode::asLong)
            .getOrElseThrow(() -> new InvalidArgumentException("Поле recordId невалидно."));

        Map<Long, List<Answer>> answers = Stream.of(jn.get("answers"))
            .filter(JsonNode::isArray)
            .flatMap(node -> Stream.ofAll(StreamSupport.stream(node.spliterator(), false)))
            .map(this::deserializeAnswer)
            .collect(HashMap.collector());

        return new PassRequests.ExamList(recordId, answers);
    }

    private Tuple2<Long, List<Answer>> deserializeAnswer(JsonNode node) {
        Long questionId = Option.of(node.get("questionId"))
            .filter(JsonNode::isLong)
            .map(JsonNode::asLong)
            .getOrElseThrow(() -> new InvalidArgumentException("Поле questionId невалидно."));

        Question.Type type = Option.of(node.get("type"))
            .map(JsonNode::asText)
            .toTry()
            .flatMap(t -> Try.of(() -> Question.Type.of(t)))
            .getOrElseThrow(t -> new InvalidArgumentException("Поле type невалидно.", t));

        List<String> answers = Stream.of(node.get("answers"))
            .filter(JsonNode::isArray)
            .flatMap(answerNode -> Stream.ofAll(StreamSupport.stream(answerNode.spliterator(), false)))
            .map(JsonNode::asText)
            .collect(List.collector());

        return Try.of(() -> {
            switch (type) {
                case SINGLE, MULTIPLE, SEQUENCE -> {
                    List<Answer> choices = answers.map(Long::valueOf).map(ChoiceAnswer::new);
                    return new Tuple2<>(questionId, choices);
                }
                case MATCHING -> {
                    List<Answer> matches = answers.map(v -> {
                        String[] pieces = v.split(":");
                        return new MatchAnswer(Long.valueOf(pieces[0]), Long.valueOf(pieces[1]));
                    });
                    return new Tuple2<>(questionId, matches);
                }
                default -> throw new InvalidArgumentException("Указан неверный тип вопроса.");
            }
        }).getOrElseThrow(t -> Match(t).of(
            Case($(instanceOf(InvalidArgumentException.class)), e -> e),
            Case($(), new InvalidArgumentException("Список ответов невалиден.", t))
        ));
    }
}
