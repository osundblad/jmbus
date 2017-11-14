package org.openmuc.jmbus;

import org.junit.Test;

import static org.junit.Assert.*;

public class HexUtilTest {

    @Test
    public void toHexString() {
        final String hexString = HexUtil.toHexString(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}, 18);

        assertEquals(
                "0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x08  0x09 0x0a 0x0b 0x0c 0x0d 0x0e 0x0f 0x10\n" +
                        "0x11 0x12", hexString);
    }
}