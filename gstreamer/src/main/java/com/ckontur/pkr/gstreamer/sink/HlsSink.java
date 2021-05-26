package com.ckontur.pkr.gstreamer.sink;

import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class HlsSink implements Sink {
    private final String location;
    private final int maxFiles;
    private final int playlistLength;
    private final String playlistLocation;
    private final String playlistRoot;
    private final int targetDuration;

    public HlsSink(String location, String playlistLocation) {
        this(location, 10, 5, playlistLocation, null, 15);
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("hlssink location=\"%s\"", location));
        if (maxFiles != 10) {
            sj.add(String.format("max-files=%d", maxFiles));
        }
        if (playlistLength != 5) {
            sj.add(String.format("playlist-length=%d", playlistLength));
        }
        if (!playlistLocation.equals("playlist.m3u8")) {
            sj.add(String.format("playlist-location=\"%s\"", playlistLocation));
        }
        if (playlistRoot != null) {
            sj.add(String.format("playlist-root=\"%s\"", playlistRoot));
        }
        if (targetDuration != 15) {
            sj.add(String.format("target-duration=%d", targetDuration));
        }
        return sj.toString();
    }
}
