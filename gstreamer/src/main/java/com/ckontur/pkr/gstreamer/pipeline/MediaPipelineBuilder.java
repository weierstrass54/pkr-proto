package com.ckontur.pkr.gstreamer.pipeline;

import com.ckontur.pkr.gstreamer.encoder.Encoder;
import com.ckontur.pkr.gstreamer.exception.InvalidMediaPipelineException;
import com.ckontur.pkr.gstreamer.mux.Mux;
import com.ckontur.pkr.gstreamer.sink.Sink;
import com.ckontur.pkr.gstreamer.source.Source;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;

import java.util.StringJoiner;

@Slf4j
public class MediaPipelineBuilder {
    private String id = "_";
    private Source source;
    private Encoder videoEncoder;
    private Encoder audioEncoder;
    private Mux mux;
    private Sink sink;

    private MediaPipelineBuilder() {}

    public static MediaPipelineBuilder builder() {
        return new MediaPipelineBuilder();
    }

    public MediaPipelineBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public MediaPipelineBuilder source(Source source) {
        this.source = source;
        return this;
    }

    public MediaPipelineBuilder video(Encoder videoEncoder) {
        this.videoEncoder = videoEncoder;
        return this;
    }

    public MediaPipelineBuilder audio(Encoder audioEncoder) {
        this.audioEncoder = audioEncoder;
        return this;
    }

    public MediaPipelineBuilder mux(Mux mux) {
        this.mux = mux;
        return this;
    }

    public MediaPipelineBuilder sink(Sink sink) {
        this.sink = sink;
        return this;
    }

    public Pipeline build() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(source.gstDescription());
        if (videoEncoder == null) {
            throw new InvalidMediaPipelineException("Video parameter is required.");
        }
        sj.add(String.format("%s. ! queue ! %s", source.getName(), videoEncoder.gstDescription()));
        if (mux == null) {
            throw new InvalidMediaPipelineException("Mux parameter is required.");
        }
        sj.add(String.format("! %s", mux.gstDescription()));
        if (sink == null) {
            throw new InvalidMediaPipelineException("Sink parameter is required.");
        }
        sj.add(String.format("! %s", sink.gstDescription()));
        if (audioEncoder != null) {
            sj.add(String.format("%s. ! queue ! %s ! %s.", source.getName(), audioEncoder.gstDescription(), mux.getName()));
        }
        Pipeline pipeline = (Pipeline) Gst.parseLaunch(sj.toString());
        log.info(sj.toString());
        return pipeline;
    }
}
