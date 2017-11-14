package org.openmuc.jmbus;

import java.io.IOException;

public class PartialMessageException extends IOException {
    public PartialMessageException(final String s) {
        super(s);
    }
}
