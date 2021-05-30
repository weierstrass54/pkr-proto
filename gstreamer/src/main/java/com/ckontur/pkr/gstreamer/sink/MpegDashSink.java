package com.ckontur.pkr.gstreamer.sink;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.StringJoiner;

@RequiredArgsConstructor
public class MpegDashSink implements Sink {
    private final boolean isDynamic;
    private final int minBufferTime;
    private final int minimumUpdatePeriod;
    private final String mpdBaseUrl;
    private final String mpdFileName;
    private final String mpdRootPath;
    private final Muxer muxer;
    private final BigInteger periodDuration;
    private final boolean sendKeyframeRequests;
    private final int targetDuration;
    private final boolean useSegmentList;

    public MpegDashSink(String location) {
        this(
            false, 2000, 0, null, "dash.mpd", location, Muxer.TS,
            new BigInteger("18446744073709551615"), true, 15, false
        );
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("dashsink mpd-root-path=\"%s\"", mpdRootPath));
        if (isDynamic) {
            sj.add("dynamic=true");
        }
        if (minBufferTime != 2000) {
            sj.add(String.format("min-buffer-time=%d", minBufferTime));
        }
        if (minimumUpdatePeriod != 0) {
            sj.add(String.format("minimum-update-period=%d", minimumUpdatePeriod));
        }
        if (mpdBaseUrl != null) {
            sj.add(String.format("mpd-base-url=%s", mpdBaseUrl));
        }
        if (!mpdFileName.equals("dash.mpd")) {
            sj.add(String.format("mpd-file-name=%s", mpdFileName));
        }
        if (muxer == Muxer.MP4) {
            sj.add(String.format("muxer=%d", muxer.getValue()));
        }
        if (!periodDuration.toString().equals("18446744073709551615")) {
            sj.add(String.format("period-duration=%s", periodDuration));
        }
        if (!sendKeyframeRequests) {
            sj.add("send-keyframe-requests=false");
        }
        if (targetDuration != 15) {
            sj.add(String.format("target-duration=%d", targetDuration));
        }
        if (useSegmentList) {
            sj.add("use-segment-list=true");
        }
        return sj.toString();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Muxer {
        TS(0), MP4(1);
        private final int value;
    }
}
