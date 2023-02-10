package com.ct.ctcamera.video.encoding;

import com.ct.ctcamera.internal.Pool;

/**
 * A simple {@link Pool(int, Factory)} implementation for input buffers.
 */
class InputBufferPool extends Pool<InputBuffer> {

    InputBufferPool() {
        super(Integer.MAX_VALUE, new Factory<InputBuffer>() {
            @Override
            public InputBuffer create() {
                return new InputBuffer();
            }
        });
    }
}
