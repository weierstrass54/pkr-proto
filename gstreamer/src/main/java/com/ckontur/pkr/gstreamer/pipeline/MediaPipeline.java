package com.ckontur.pkr.gstreamer.pipeline;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.event.EOSEvent;
import org.freedesktop.gstreamer.message.Message;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MediaPipeline {
    static {
        Gst.init();
    }

    private final Pipeline pipeline;
    private final Consumer<String> onError;
    private final Consumer<String> onWarning;
    private final Consumer<String> onMessage;
    private final CompletableFuture<MediaState> startFuture = new CompletableFuture<>();
    private final CompletableFuture<MediaState> stopFuture = new CompletableFuture<>();

    public static MediaState allOf(Collection<CompletableFuture<MediaState>> pipelines) {
        CompletableFuture.allOf(pipelines.toArray(new CompletableFuture[0]));
        return pipelines.stream().map(p -> {
            try {
                return p.get();
            }
            catch (InterruptedException | ExecutionException e) {
                log.error("MediaPipeline execution error.", e);
                return MediaState.error(e.getMessage());
            }
        }).reduce(MediaState.ok(), (s1, s2) -> {
            if (!s1.isOk() && !s2.isOk()) {
                return MediaState.error(s1.getDescription() + ", " + s2.getDescription());
            }
            return !s2.isOk() ? s2 : s1;
        });
    }

    public final CompletableFuture<MediaState> start() {
        if (pipeline.isPlaying()) {
            return CompletableFuture.completedFuture(MediaState.ok());
        }
        pipeline.getBus().connect(this::onStateChange);
        pipeline.getBus().connect(this::onMessage);
        pipeline.getBus().connect((Bus.WARNING) this::onWarning);
        pipeline.getBus().connect((Bus.ERROR) this::onError);
        pipeline.getBus().connect((Bus.EOS) this::onEos);
        pipeline.play();
        return startFuture;
    }

    public final CompletableFuture<MediaState> stop() {
        if (!pipeline.isPlaying()) {
            return CompletableFuture.completedFuture(MediaState.ok());
        }
        pipeline.sendEvent(new EOSEvent());
        return stopFuture;
    }

    protected void onStateChange(GstObject source, State old, State current, State pending) {
        if (source.getName().startsWith("pipeline") && current.name().equals("PLAYING")) {
            startFuture.complete(MediaState.ok());
        }
    }

    protected void onWarning(GstObject source, int code, String message) {
        onWarning.accept(String.format("WARNING #%d: %s", code, message));
    }

    protected void onMessage(Bus bus, Message message) {
        if (message.getStructure() != null && message.getStructure().hasField("text")) {
            onMessage.accept(String.format("%s: %s", message.getSource().getName(), message.getStructure().getString("text")));
        }
    }

    protected void onError(GstObject source, int code, String message) {
        startFuture.complete(MediaState.error(message));
        onError.accept(String.format("ERROR #%d: %s", code, message));
    }

    protected void onEos(GstObject source) {
        log.info("Got EOS at source.");
        stopFuture.complete(MediaState.ok());
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MediaState {
        private final String value;
        private final String description;

        public boolean isOk() {
            return value.equals("Ok.");
        }

        protected static MediaState ok() {
            return new MediaState("Ok.", "");
        }

        protected static MediaState error(String reason) {
            return new MediaState("Error.", reason);
        }
    }
}
