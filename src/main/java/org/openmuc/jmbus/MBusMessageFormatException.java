package org.openmuc.jmbus;

public class MBusMessageFormatException extends RuntimeException {

    /**
     * Throw when the message bytes in a long message (0x68) differ.
     * @param length1
     * @param length2
     */
    public MBusMessageFormatException(final int length1, final int length2) {
        super(String.format("Length bytes differ: %d and %d", length1, length2));
    }
}
