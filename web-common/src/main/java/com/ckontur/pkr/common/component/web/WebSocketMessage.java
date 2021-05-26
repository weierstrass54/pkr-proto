package com.ckontur.pkr.common.component.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class WebSocketMessage<K, V> {
    private K key;
    private V payload;

    public abstract String getType();

    @JsonIgnore
    public abstract String getChannel();
}
