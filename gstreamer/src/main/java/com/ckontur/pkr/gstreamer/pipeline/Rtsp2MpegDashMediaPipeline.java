package com.ckontur.pkr.gstreamer.pipeline;

import com.ckontur.pkr.gstreamer.encoder.Encoder;
import com.ckontur.pkr.gstreamer.mux.MpegTsMux;
import com.ckontur.pkr.gstreamer.sink.MpegDashSink;
import com.ckontur.pkr.gstreamer.source.RtspSource;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class Rtsp2MpegDashMediaPipeline extends MediaPipeline {

    public Rtsp2MpegDashMediaPipeline(String source, String location, Consumer<String> onError, Consumer<String> onWarning, Consumer<String> onMessage) {
        super(
            MediaPipelineBuilder.builder()
                .source(new RtspSource("rtp_source", source))
                .video(Encoder.RTP_H264_VIDEO)
                .audio(Encoder.RTP_PCMA_AAC_AUDIO)
                .mux(new MpegTsMux("mux"))
                .sink(new MpegDashSink(location))
                .build(),
            onError, onWarning, onMessage
        );
    }

    public Rtsp2MpegDashMediaPipeline(String source, String location, Consumer<String> onError) {
        this(source, location, onError, log::warn, log::info);
    }

    public Rtsp2MpegDashMediaPipeline(String source, String location) {
        this(source, location, log::error, log::warn, log::info);
    }
}
