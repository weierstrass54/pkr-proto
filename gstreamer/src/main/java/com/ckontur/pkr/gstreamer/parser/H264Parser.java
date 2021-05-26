package com.ckontur.pkr.gstreamer.parser;

import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class H264Parser implements Parser {
    private final int configInterval;
    private final boolean updateTimecode;

    public H264Parser() {
        this(0, false);
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add("h264parse");
        if (configInterval > 0) {
            sj.add(String.format("config-interval=%d", configInterval));
        }
        if (updateTimecode) {
            sj.add("update-timecode=true");
        }
        return sj.toString();
    }
}
