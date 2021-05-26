package com.ckontur.pkr.gstreamer.pipeline;

import com.ckontur.pkr.gstreamer.encoder.Encoder;
import com.ckontur.pkr.gstreamer.mux.Mp4Mux;
import com.ckontur.pkr.gstreamer.sink.FileSink;
import com.ckontur.pkr.gstreamer.source.RtspSource;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class Rtsp2FileMediaPipeline extends MediaPipeline {
    public Rtsp2FileMediaPipeline(String source, String location, Consumer<String> onError, Consumer<String> onMessage, Consumer<String> onWarning) {
        super(
            MediaPipelineBuilder
                .builder()
                .source(new RtspSource("rtp_source", source))
                .video(Encoder.RTP_H264_VIDEO)
                .audio(Encoder.RTP_PCMA_AAC_AUDIO)
                .mux(new Mp4Mux("mux", 15000))
                .sink(new FileSink(location + ".mp4"))
                .build(),
            onError, onWarning, onMessage
        );
    }

    public Rtsp2FileMediaPipeline(String source, String location, Consumer<String> onError) {
        this(source, location, onError, log::warn, log::info);
    }

    public Rtsp2FileMediaPipeline(String source, String location) {
        this(source, location, log::error, log::warn, log::info);
    }

}
