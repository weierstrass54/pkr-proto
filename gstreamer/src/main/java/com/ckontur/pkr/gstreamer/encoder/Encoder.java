package com.ckontur.pkr.gstreamer.encoder;

import com.ckontur.pkr.gstreamer.depayload.Depayload;
import com.ckontur.pkr.gstreamer.parser.Parser;
import com.ckontur.pkr.gstreamer.pipeline.MediaPipelineElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Encoder implements MediaPipelineElement {
    public static final Encoder RTP_H264_VIDEO = new Encoder(Depayload.RTP_H264_DEPAY, Parser.H264_DEFAULT);
    public static final Encoder RTP_PCMA_AAC_AUDIO = new Encoder(Depayload.RTP_PCMA_DEPAY, Parser.ALAW_2_AAC_AUDIO);

    private final Depayload depayload;
    private final Parser parser;

    @Override
    public String gstDescription() {
        return Stream.of(depayload, parser)
            .map(MediaPipelineElement::gstDescription)
            .collect(Collectors.joining(" ! "));
    }
}
