package org.openmuc.jmbus;

public class HexUtil {

    private static final int BYTES_BEFORE_LINEBREAK = 16;
    private static final int BYTES_BEFORE_EXTRA_SPACE = 8;

    public static String toHexString(final byte[] byteArray, final int length) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (i != 0) {
                if (i % BYTES_BEFORE_LINEBREAK == 0) {
                    builder.append('\n');
                } else if (i % BYTES_BEFORE_EXTRA_SPACE == 0) {
                    builder.append("  ");
                } else {
                    builder.append(' ');
                }
            }
            builder.append(toHexString(byteArray[i]));
        }

        return builder.toString();
    }

    public static String toHexString(final byte b) {
        return String.format("0x%02x", b);
    }

}
