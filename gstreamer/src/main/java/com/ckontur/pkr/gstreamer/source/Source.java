package com.ckontur.pkr.gstreamer.source;

import com.ckontur.pkr.gstreamer.pipeline.MediaPipelineElement;

public interface Source extends MediaPipelineElement {
    String getFactoryName();
    String getName();

}
