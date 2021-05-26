package com.ckontur.pkr.gstreamer.mux;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class Mp4Mux implements Mux {
    @Getter
    private final String name;
    private final Integer fragmentDuration;

    public Mp4Mux(String name) {
        this(name, null);
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("mp4mux name=%s", name));
        if (fragmentDuration != null) {
            sj.add(String.format("fragment-duration=%d", fragmentDuration));
        }
        return sj.toString();
    }
}
