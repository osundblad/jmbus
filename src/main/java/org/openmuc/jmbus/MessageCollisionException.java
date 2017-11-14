package org.openmuc.jmbus;

import java.io.IOException;

public class MessageCollisionException extends IOException {

    private final byte[] skipBytes;

    public MessageCollisionException(final byte[] skipedBytes) {
        super("skipped " + skipedBytes.length + " bytes");
        this.skipBytes = skipedBytes;
    }

    public byte[] getSkipBytes() {
        return skipBytes;
    }

}
