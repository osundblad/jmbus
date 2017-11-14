package org.openmuc.jmbus;

import java.io.IOException;

public class NoMessageException extends IOException {

    public NoMessageException() {
        super("No message.");
    }

}
