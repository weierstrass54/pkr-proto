package com.ckontur.pkr.gstreamer.mux;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class MpegTsMux implements Mux {
    @Getter
    private final String name;
    private final boolean m2tsMode;

    public MpegTsMux(String name) {
        this(name, false);
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("mpegtsmux name=%s", name));
        if (m2tsMode) {
            sj.add("m2ts-mode=true");
        }
        return sj.toString();
    }
}
