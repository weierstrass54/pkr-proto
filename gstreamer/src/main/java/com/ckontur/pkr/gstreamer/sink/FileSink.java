package com.ckontur.pkr.gstreamer.sink;

import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class FileSink implements Sink {
    private final boolean append;
    private final BufferMode bufferMode;
    private final int bufferSize;
    private final String location;
    private final int maxTransientErrorTimeout;
    private final boolean oSync;

    public FileSink(String location) {
        this(false, BufferMode.DEFAULT, 65536, location, 0, false);
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("filesink location=\"%s\"", location));
        if (append) {
            sj.add("append=true");
        }
        sj.add(switch (bufferMode) {
            case FULL -> "buffer-mode=0";
            case LINE -> "buffer-mode=1";
            case UNBUFFERED -> "buffer-mode=2";
            case DEFAULT -> "";
        });
        if (bufferSize != 65536) {
            sj.add(String.format("buffer-size=%d", bufferSize));
        }
        if (maxTransientErrorTimeout > 0) {
            sj.add(String.format("max-transient-error-timeout=%d", maxTransientErrorTimeout));
        }
        if (oSync) {
            sj.add("o-sync=true");
        }
        return sj.toString();
    }

    public enum BufferMode {
        DEFAULT,
        FULL,
        LINE,
        UNBUFFERED
    }
}
