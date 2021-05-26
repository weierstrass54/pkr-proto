package com.ckontur.pkr.gstreamer.depayload;

import com.ckontur.pkr.gstreamer.pipeline.MediaPipelineElement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Depayload implements MediaPipelineElement {
    RTP_H264_DEPAY("capsfilter caps=\"application/x-rtp,media=video\" @ rtph264depay"),
    RTP_PCMA_DEPAY("capsfilter caps=\"application/x-rtp,media=audio\" ! rtppcmadepay");

    private final String gstDescription;

    @Override
    public String gstDescription() {
        return gstDescription;
    }
}
