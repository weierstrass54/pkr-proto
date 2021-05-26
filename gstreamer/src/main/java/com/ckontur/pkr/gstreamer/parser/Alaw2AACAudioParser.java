package com.ckontur.pkr.gstreamer.parser;

public class Alaw2AACAudioParser implements Parser {
    @Override
    public String gstDescription() {
        return "alawdec ! voaacenc";
    }
}
