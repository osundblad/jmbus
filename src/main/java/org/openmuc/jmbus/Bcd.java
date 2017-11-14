/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus;

import java.util.Arrays;

/**
 * This class represents a binary-coded decimal (BCD) number as defined by the M-Bus standard. The class provides
 * methods to convert the BCD to other types such as <code>double</code>, <code>int</code> or <code>String</code>.
 */
public class Bcd extends Number {

    private static final long serialVersionUID = 790515601507532939L;
    private static final int LOW_4_MASK = 0x0f;
    private static final int HIGH_4_MASK = 0xf0;

    private final byte[] value;

    /**
     * Constructs a <code>Bcd</code> from the given int.
     *
     * @param bcd the bcd as an int
     * @return the Bcd
     */
    public static Bcd from(final int bcd) {
        final byte[] bytes = new byte[4];
        for (int factor = 1, i = 0; i < 4; i++) {
            final int pair = (bcd / factor) % 100;
            bytes[i] = (byte) (pair / 10 * 0x10 + (pair % 10));
            factor *= 100;
        }
        return new Bcd(bytes);
    }

    /**
     * Constructs a <code>Bcd</code> from the given bytes. The constructed Bcd will create a new byte array for
     * internal storage of its value, so no defensive copying is required.
     *
     * @param bcdBytes the byte array to be used for construction of the <code>Bcd</code>
     */
    public static Bcd from(final byte[] bcdBytes) {
        return from(bcdBytes, 0, bcdBytes.length);
    }

    /**
     * Constructs a 4 byte length <code>Bcd</code> from the given bytes. The constructed Bcd will create
     * a new byte array for internal storage of its value, so no defensive copying is required.
     *
     * @param bcdBytes the byte array to be used for construction of the <code>Bcd</code>
     * @param offset   the offset to the bcd in the byte array
     */
    public static Bcd from(final byte[] bcdBytes, final int offset) {
        return from(bcdBytes, offset, 4);
    }

    /**
     * Constructs a <code>Bcd</code> from the given bytes. The constructed Bcd will create a new byte array for
     * internal storage of its value, so no defensive copying is required.
     *
     * @param bcdBytes the byte array to be used for construction of the <code>Bcd</code>
     * @param offset   the offset to the bcd in the byte array
     * @param length   the length of the Bcd (1, 2, 3, 4, or 6 bytes)
     */
    public static Bcd from(final byte[] bcdBytes, final int offset, final int length) {
        return new Bcd(Arrays.copyOfRange(bcdBytes, offset, offset + length));
    }

    private Bcd(final byte[] bcdBytes) {
        value = bcdBytes;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public String toString() {
        final byte[] bytes = new byte[value.length * 2];
        int c = 0;

        if ((value[value.length - 1] & HIGH_4_MASK) == HIGH_4_MASK) {
            bytes[c++] = 0x2d;
        } else {
            bytes[c++] = (byte) (((value[value.length - 1] >> 4) & LOW_4_MASK) + 48);
        }

        bytes[c++] = (byte) ((value[value.length - 1] & LOW_4_MASK) + 48);

        for (int i = value.length - 2; i >= 0; i--) {
            bytes[c++] = (byte) (((value[i] & HIGH_4_MASK) >> 4) + 48);
            bytes[c++] = (byte) ((value[i] & LOW_4_MASK) + 48);
        }

        return new String(bytes);
    }

    /**
     * Returns the value of this <code>Bcd</code> as a double.
     */
    @Override
    public double doubleValue() {
        return longValue();
    }

    /**
     * Returns the value of this <code>Bcd</code> as a float.
     */
    @Override
    public float floatValue() {
        return longValue();
    }

    /**
     * Returns the value of this <code>Bcd</code> as an int.
     */
    @Override
    public int intValue() {
        int result = 0;
        int factor = 1;

        for (int i = 0; i < (value.length - 1); i++) {
            result += (value[i] & LOW_4_MASK) * factor;
            factor = factor * 10;
            result += ((value[i] & HIGH_4_MASK) >> 4) * factor;
            factor = factor * 10;
        }

        result += (value[value.length - 1] & LOW_4_MASK) * factor;
        factor = factor * 10;

        if ((value[value.length - 1] & HIGH_4_MASK) == HIGH_4_MASK) {
            result = result * -1;
        } else {
            result += ((value[value.length - 1] & HIGH_4_MASK) >> 4) * factor;
        }

        return result;
    }

    /**
     * Returns the value of this <code>Bcd</code> as a long.
     */
    @Override
    public long longValue() {
        long result = 0L;
        long factor = 1L;

        for (int i = 0; i < (value.length - 1); i++) {
            result += (value[i] & LOW_4_MASK) * factor;
            factor = factor * 10L;
            result += ((value[i] & HIGH_4_MASK) >> 4) * factor;
            factor = factor * 10L;
        }

        result += (value[value.length - 1] & LOW_4_MASK) * factor;
        factor = factor * 10L;

        if ((value[value.length - 1] & HIGH_4_MASK) == HIGH_4_MASK) {
            result = result * -1;
        } else {
            result += ((value[value.length - 1] & HIGH_4_MASK) >> 4) * factor;
        }

        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Bcd bcd = (Bcd) o;

        return Arrays.equals(value, bcd.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
