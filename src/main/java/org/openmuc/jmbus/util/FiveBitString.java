package org.openmuc.jmbus.util;

import java.nio.ByteBuffer;

public final class FiveBitString {

    private static final int START_OF_LETTERS = 'A' - 1;

    private FiveBitString() {
    }

    public static String decodeManufacturerId(final byte[] buffer) {
        return decodeManufacturerId(buffer, 0);
    }

    public static String decodeManufacturerId(final byte[] buffer, final int offset) {
        final int manufacturerIdAsInt = Byte.toUnsignedInt(buffer[offset]) + (buffer[offset + 1] << 8);
        final char c2 = (char) (((manufacturerIdAsInt) & 0x1f) + START_OF_LETTERS);
        final char c1 = (char) (((manufacturerIdAsInt >>> 5) & 0x1f) + START_OF_LETTERS);
        final char c0 = (char) (((manufacturerIdAsInt >>> 10) & 0x1f) + START_OF_LETTERS);
        return "" + c0 + c1 + c2;
    }

    public static byte[] encodeManufacturerIdString(final String manufactureId) {
        if (manufactureId == null || manufactureId.isEmpty()) {
            return new byte[]{0, 0};
        }
        if (manufactureId.length() != 3) {
            throw new IllegalArgumentException("ManufacturerId should be 3 characters (or null/empty string)!");
        }

        final byte[] chars = manufactureId.toUpperCase().getBytes();
        final int asInt =  ((chars[0] - 64) << 10) | ((chars[1] - 64) << 5) |  (chars[2] - 64);

        return ByteBuffer.allocate(2).put((byte) (asInt & 0xFF)).put((byte) (asInt >> 8)).array();
    }

}
