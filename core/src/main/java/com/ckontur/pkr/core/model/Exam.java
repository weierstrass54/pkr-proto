package com.ckontur.pkr.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class Exam {
    private final Long id;
    private final String qualification;
    private final String level;
    private final String name;

    /*
    Общее врем тестирования
    Кол-во вопросов
    Кол-во баллов за вопрос
    Макс. число баллов
    Кол-во успешных баллов
    Время на один вопрос
    Возможность пропускать вопросы
    Возможность возвращаться к ранее отвеченным вопросам
    Возможность досрочного завершения
     */

    public String getName() {
        return Optional.ofNullable(name)
            .orElse(String.format("%s %s", qualification, level));
    }
}
