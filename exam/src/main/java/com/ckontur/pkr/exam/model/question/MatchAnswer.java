package com.ckontur.pkr.exam.model.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MatchAnswer implements Answer {
    private final Long leftId;
    private final Long rightId;
}
