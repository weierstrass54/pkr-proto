package com.ckontur.pkr.gstreamer.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@RequiredArgsConstructor
public class RtspSource implements Source {
    @Getter
    private final String factoryName = "rtspsrc";
    @Getter
    private final String name;

    private final String protocols;
    private final String location;
    private final int latency;

    public RtspSource(String name, String location) {
        this(name, "tcp", location, 100);
    }

    @Override
    public String gstDescription() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.format("rtspsrc location=\"%s\" name=%s", location, name));
        if (!protocols.equals("tcp+udp-mcast+udp")) {
            sj.add(String.format("protocols=%s", protocols));
        }
        if (latency != 2000) {
            sj.add(String.format("latency=%d", latency));
        }
        return sj.toString();
    }
}
