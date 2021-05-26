package com.ckontur.pkr.gstreamer.parser;

import com.ckontur.pkr.gstreamer.pipeline.MediaPipelineElement;

public interface Parser extends MediaPipelineElement {
    H264Parser H264_DEFAULT = new H264Parser();
    Alaw2AACAudioParser ALAW_2_AAC_AUDIO = new Alaw2AACAudioParser();
}
